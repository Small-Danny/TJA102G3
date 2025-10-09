package com.tibafit.service.analytics;

import com.tibafit.dto.analytics.CompleteWorkoutRequest;
import com.tibafit.dto.analytics.SeriesResponse;
import com.tibafit.model.analytics.TaskRecord;
import com.tibafit.model.analytics.WorkoutPlan;
import com.tibafit.model.analytics.WorkoutPlanRecord;
import com.tibafit.repository.analytics.AnalyticsTaskRecordRepository;
import com.tibafit.repository.analytics.AnalyticsWorkoutPlanRepository;
import com.tibafit.repository.analytics.AnalyticsWorkoutPlanRecordRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class AnalyticsService {

    private final AnalyticsTaskRecordRepository taskRepo;
    private final AnalyticsWorkoutPlanRepository workoutPlanRepo;
    private final AnalyticsWorkoutPlanRecordRepository workoutPlanRecordRepo;

    private static final ZoneId ZONE_TW = ZoneId.of("Asia/Taipei");

    // === 時間窗 ===
    private static final class Window {
        final ZonedDateTime start; // [start, end)
        final ZonedDateTime end;
        final boolean perHour;     // today = 每小時；week/month = 每日
        Window(ZonedDateTime start, ZonedDateTime end, boolean perHour) {
            this.start = start;
            this.end = end;
            this.perHour = perHour;
        }
    }

    public AnalyticsService(AnalyticsTaskRecordRepository taskRepo,
                            AnalyticsWorkoutPlanRepository workoutPlanRepo,
                            AnalyticsWorkoutPlanRecordRepository workoutPlanRecordRepo) {
        this.taskRepo = taskRepo;
        this.workoutPlanRepo = workoutPlanRepo;
        this.workoutPlanRecordRepo = workoutPlanRecordRepo;
    }

    /** 回傳圖表序列資料（支援 planId 可選） */
    public SeriesResponse buildSeries(String metric, String range, long uid, Long planId) {
        Window w = window(range);
        boolean perHour = w.perHour;

        List<String> labels = perHour
                ? hourLabels()
                : dateLabels(w.start.toLocalDate(), w.end.minusDays(1).toLocalDate());
        double[] data = new double[labels.size()];

        switch (metric) {
            case "workout-time" -> {
                LocalDateTime startLdt = w.start.toLocalDateTime();
                LocalDateTime endLdt   = w.end.toLocalDateTime();

                // 1) 取「當前使用者」在視窗內的 plans（若 planId != null → 只留那張）
                LocalDate startDate = w.start.toLocalDate();
                LocalDate endDateEx = w.end.toLocalDate();
                List<WorkoutPlan> userPlans =
                        workoutPlanRepo.findByUserIdAndWorkoutPlanDateGreaterThanEqualAndWorkoutPlanDateLessThanOrderByWorkoutPlanDateAsc(
                                (int) uid, startDate, endDateEx);
                if (planId != null) {
                    userPlans.removeIf(p -> {
                        Long pid = getPlanId(p);
                        return pid == null || !pid.equals(planId);
                    });
                }
                Set<Long> userPlanIds = new HashSet<>();
                for (WorkoutPlan p : userPlans) {
                    Long pid = getPlanId(p);
                    if (pid != null) userPlanIds.add(pid);
                }

                // 2) 撈出視窗內所有紀錄 → 過濾成屬於該使用者 plans 的紀錄
                List<WorkoutPlanRecord> recsAll =
                        workoutPlanRecordRepo.findByActualRecordDatetimeGreaterThanEqualAndActualRecordDatetimeLessThan(
                                startLdt, endLdt);
                List<WorkoutPlanRecord> recs = new ArrayList<>();
                for (WorkoutPlanRecord r : recsAll) {
                    Long rid = getRecordPlanId(r);
                    if (rid != null && userPlanIds.contains(rid)) recs.add(r);
                }

                // 3) 用「紀錄」時數累加（today 也跨小時切段）
                if (perHour) accumulateRecordMinutesPerHourSplit(recs, w, data);
                else         accumulateRecordMinutesPerDay(recs,  w, data);

                // 4) 若沒有任何紀錄分鐘數 → 退回 TaskRecord（完成=1）
                if (Arrays.stream(data).sum() == 0.0) {
                    List<TaskRecord> rows = taskRepo
                            .findByUserIdAndTaskRecordStatusAndUserStartTimeGreaterThanEqualAndUserStartTimeLessThanOrderByUserStartTime(
                                    uid, 1, startLdt, endLdt);
                    if (planId != null) {
                        rows.removeIf(t -> {
                            Long tidPlan = getTaskWorkoutPlanId(t);
                            return tidPlan == null || !tidPlan.equals(planId);
                        });
                    }
                    if (perHour) accumulateMinutesPerHour(rows, w, data);
                    else         accumulateMinutesPerDay(rows,  w, data);
                }

                // **不再做任何 Plan 補位：沒有紀錄/任務 → 直接 0**
            }
            case "tasks" -> {
                List<TaskRecord> rows = taskRepo
                        .findByUserIdAndTaskRecordStatusAndUserStartTimeGreaterThanEqualAndUserStartTimeLessThanOrderByUserStartTime(
                                uid, 1, w.start.toLocalDateTime(), w.end.toLocalDateTime());
                if (perHour) accumulateCountPerHour(rows, w, data);
                else         accumulateCountPerDay(rows,  w, data);
            }
            case "calories" -> {
                LocalDateTime startLdt = w.start.toLocalDateTime();
                LocalDateTime endLdt   = w.end.toLocalDateTime();

                // 先抓使用者在視窗內的 plans（若 planId != null 只留那張）
                LocalDate startDate = w.start.toLocalDate();
                LocalDate endDateEx = w.end.toLocalDate();
                List<WorkoutPlan> userPlans =
                        workoutPlanRepo.findByUserIdAndWorkoutPlanDateGreaterThanEqualAndWorkoutPlanDateLessThanOrderByWorkoutPlanDateAsc(
                                (int) uid, startDate, endDateEx);
                if (planId != null) {
                    userPlans.removeIf(p -> {
                        Long pid = getPlanId(p);
                        return pid == null || !pid.equals(planId);
                    });
                }
                Map<Long, WorkoutPlan> planMap = new HashMap<>();
                Set<Long> userPlanIds = new HashSet<>();
                for (WorkoutPlan p : userPlans) {
                    Long pid = getPlanId(p);
                    if (pid != null) {
                        userPlanIds.add(pid);
                        planMap.put(pid, p);
                    }
                }

                // 撈出視窗內的所有紀錄 → 過濾成屬於當前使用者計畫的紀錄
                List<WorkoutPlanRecord> recsAll =
                        workoutPlanRecordRepo.findByActualRecordDatetimeGreaterThanEqualAndActualRecordDatetimeLessThan(
                                startLdt, endLdt);
                List<WorkoutPlanRecord> recs = new ArrayList<>();
                for (WorkoutPlanRecord r : recsAll) {
                    Long rid = getRecordPlanId(r);
                    if (rid != null && userPlanIds.contains(rid)) recs.add(r);
                }

                if (!recs.isEmpty()) {
                    // 有紀錄：只用紀錄算熱量（精準）
                    if (perHour) accumulateCaloriesFromRecordsPerHour(recs, planMap, w, data);
                    else         accumulateCaloriesFromRecordsPerDay (recs, planMap, w, data);
                } else {
                    // 無紀錄：單次使用 plan.actualTotalCalories（與卡片一致）
                    if (perHour) accumulateCaloriesFromPlansPerHour(userPlans, w, data);
                    else         accumulateCaloriesFromPlansPerDay (userPlans, w, data);
                }
            }
            default -> { /* 未知 metric → 維持全 0 */ }
        }

        String unit = switch (metric) {
            case "workout-time" -> "分鐘";
            case "tasks"        -> "次";
            case "calories"     -> "大卡";
            default             -> "";
        };
        double total = Arrays.stream(data).sum();

        List<Number> dataList = new ArrayList<>(data.length);
        for (double v : data) dataList.add(v);

        return new SeriesResponse(labels, dataList, unit, total, metric, range);
    }

    // === 完成運動（保留） ===
    @Transactional
    public TaskRecord completeWorkout(CompleteWorkoutRequest req, Long fallbackUserId) {
        Long uidL = (req.getUserId() != null) ? req.getUserId().longValue() : fallbackUserId;
        if (uidL == null) throw new IllegalArgumentException("userId 不可為空");
        Integer userId = uidL.intValue();

        if (req.getStartAt() == null && (req.getMinutes() == null || req.getMinutes() <= 0)) {
            throw new IllegalArgumentException("startAt 與 minutes 至少擇一提供");
        }

        Integer minutes = req.getMinutes();
        LocalDateTime start = req.getStartAt();
        LocalDateTime end = req.getEndAt();

        if (minutes == null) {
            if (end == null) end = LocalDateTime.now();
            if (start == null) throw new IllegalArgumentException("startAt 不可為空");
            long diff = Duration.between(start, end).toMinutes();
            if (diff <= 0) throw new IllegalArgumentException("結束時間需晚於開始時間");
            minutes = (int) diff;
        }

        if (minutes > 600) minutes = 600;
        if (minutes < 1)   minutes = 1;

        TaskRecord tr = new TaskRecord();
        tr.setUserId(userId);
        tr.setTaskRecordStatus(1);
        tr.setUserStartTime(start != null ? start : LocalDateTime.now().minusMinutes(minutes));
        tr.setUserEndTime(end != null ? end : tr.getUserStartTime().plusMinutes(minutes));

        if (req.getWorkoutPlanId() != null) {
            try {
                Method m = TaskRecord.class.getMethod("setWorkoutPlanId", Long.class);
                m.invoke(tr, req.getWorkoutPlanId().longValue());
            } catch (Exception ignore) {}
        }

        return taskRepo.save(tr);
    }

    // === 時間窗 / 標籤 ===
    private Window window(String range) {
        ZonedDateTime now = ZonedDateTime.now(ZONE_TW);
        ZonedDateTime startDay = now.truncatedTo(ChronoUnit.DAYS);
        return switch (range) {
            case "today" -> new Window(startDay,               startDay.plusDays(1), true);
            case "week"  -> new Window(startDay.minusDays(6),  startDay.plusDays(1), false);
            case "month" -> new Window(startDay.minusDays(30), startDay.plusDays(1), false);
            default      -> new Window(startDay.minusDays(6),  startDay.plusDays(1), false);
        };
    }
    private List<String> hourLabels() {
        return IntStream.range(0, 24).mapToObj(h -> String.format("%02d", h)).toList();
    }
    private List<String> dateLabels(LocalDate from, LocalDate toInclusive) {
        List<String> ls = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(toInclusive); d = d.plusDays(1)) ls.add(d.toString());
        return ls;
    }

    // === WorkoutPlanRecord：分鐘累加（today 也做跨小時切段） ===
    private void accumulateRecordMinutesPerHourSplit(List<WorkoutPlanRecord> recs, Window w, double[] out) {
        for (WorkoutPlanRecord r : recs) {
            LocalDateTime sLdt = null, eLdt = null;
            try {
                sLdt = (LocalDateTime) r.getClass().getMethod("getActualStartTime").invoke(r);
                eLdt = (LocalDateTime) r.getClass().getMethod("getActualEndTime").invoke(r);
            } catch (Exception ignore) {}

            if (sLdt != null && eLdt != null && eLdt.isAfter(sLdt)) {
                ZonedDateTime s = clampToWindow(sLdt, w);
                ZonedDateTime e = clampToWindow(eLdt, w);
                if (!e.isAfter(s)) continue;

                ZonedDateTime cur = s;
                while (cur.isBefore(e)) {
                    ZonedDateTime endOfHour = cur.truncatedTo(ChronoUnit.HOURS).plusHours(1);
                    ZonedDateTime segEnd    = e.isBefore(endOfHour) ? e : endOfHour;
                    long minutes = ChronoUnit.MINUTES.between(cur, segEnd);
                    out[cur.getHour()] += minutes;
                    cur = segEnd;
                }
                continue;
            }

            LocalDateTime when = getRecordTime(r);
            if (when == null) continue;
            ZonedDateTime z = when.atZone(ZONE_TW);
            if (z.isBefore(w.start) || !z.isBefore(w.end)) continue;

            int mins = getRecordMinutes(r);
            if (mins <= 0) continue;
            out[z.getHour()] += mins;
        }
    }
    private void accumulateRecordMinutesPerDay(List<WorkoutPlanRecord> recs, Window w, double[] out) {
        LocalDate d0 = w.start.toLocalDate();
        for (WorkoutPlanRecord r : recs) {
            LocalDateTime when = getRecordTime(r);
            if (when == null) continue;
            ZonedDateTime z = when.atZone(ZONE_TW);
            if (z.isBefore(w.start) || !z.isBefore(w.end)) continue;

            int mins = getRecordMinutes(r);
            if (mins <= 0) continue;

            int idx = (int) ChronoUnit.DAYS.between(d0, z.toLocalDate());
            if (0 <= idx && idx < out.length) out[idx] += mins;
        }
    }
    private LocalDateTime getRecordTime(WorkoutPlanRecord r) {
        try {
            Object v = r.getClass().getMethod("getActualRecordDatetime").invoke(r);
            if (v instanceof LocalDateTime ldt) return ldt;
        } catch (Exception ignore) {}
        try {
            Object v = r.getClass().getMethod("getActualStartTime").invoke(r);
            if (v instanceof LocalDateTime ldt) return ldt;
        } catch (Exception ignore) {}
        return null;
    }
    private int getRecordMinutes(WorkoutPlanRecord r) {
        try {
            Object v = r.getClass().getMethod("getActualDuration").invoke(r);
            if (v instanceof Number n && n.intValue() > 0) return Math.min(n.intValue(), 600);
        } catch (Exception ignore) {}
        try {
            Object vs = r.getClass().getMethod("getActualStartTime").invoke(r);
            Object ve = r.getClass().getMethod("getActualEndTime").invoke(r);
            if (vs instanceof LocalDateTime s && ve instanceof LocalDateTime e) {
                long diff = Duration.between(s, e).toMinutes();
                if (diff > 0) return (int) Math.min(diff, 600);
            }
        } catch (Exception ignore) {}
        return 0;
    }

    // === Calories：以紀錄為主，無紀錄才用 plan.actualTotalCalories ===
    private void accumulateCaloriesFromRecordsPerHour(List<WorkoutPlanRecord> recs,
                                                      Map<Long, WorkoutPlan> planMap,
                                                      Window w, double[] out) {
        for (WorkoutPlanRecord r : recs) {
            LocalDateTime when = getRecordTime(r);
            if (when == null) continue;
            ZonedDateTime z = when.atZone(ZONE_TW);
            if (z.isBefore(w.start) || !z.isBefore(w.end)) continue;

            double kcal = recordCalories(r, planMap);
            if (kcal <= 0) continue;

            out[z.getHour()] += kcal;
        }
    }
    private void accumulateCaloriesFromRecordsPerDay(List<WorkoutPlanRecord> recs,
                                                     Map<Long, WorkoutPlan> planMap,
                                                     Window w, double[] out) {
        LocalDate d0 = w.start.toLocalDate();
        for (WorkoutPlanRecord r : recs) {
            LocalDateTime when = getRecordTime(r);
            if (when == null) continue;
            ZonedDateTime z = when.atZone(ZONE_TW);
            if (z.isBefore(w.start) || !z.isBefore(w.end)) continue;

            double kcal = recordCalories(r, planMap);
            if (kcal <= 0) continue;

            int idx = (int) ChronoUnit.DAYS.between(d0, z.toLocalDate());
            if (0 <= idx && idx < out.length) out[idx] += kcal;
        }
    }
    /** 由紀錄取得熱量：先讀 record.actualCalories / burnedCalories / calories；再以 duration×plan(kcal/hr)；否則 0 */
    private double recordCalories(WorkoutPlanRecord r, Map<Long, WorkoutPlan> planMap) {
        // 1) 紀錄本身的熱量欄位
        try {
            Object v = r.getClass().getMethod("getActualCalories").invoke(r);
            if (v instanceof Number n && n.doubleValue() > 0) return n.doubleValue();
        } catch (Exception ignore) {}
        try {
            Object v = r.getClass().getMethod("getBurnedCalories").invoke(r);
            if (v instanceof Number n && n.doubleValue() > 0) return n.doubleValue();
        } catch (Exception ignore) {}
        try {
            Object v = r.getClass().getMethod("getCalories").invoke(r);
            if (v instanceof Number n && n.doubleValue() > 0) return n.doubleValue();
        } catch (Exception ignore) {}

        // 2) duration × 計畫的 kcal/hr
        Integer durMin = null;
        try {
            Object v = r.getClass().getMethod("getActualDuration").invoke(r);
            if (v instanceof Number n && n.intValue() > 0) durMin = Math.min(n.intValue(), 600);
        } catch (Exception ignore) {}
        if (durMin == null) {
            try {
                Object vs = r.getClass().getMethod("getActualStartTime").invoke(r);
                Object ve = r.getClass().getMethod("getActualEndTime").invoke(r);
                if (vs instanceof LocalDateTime s && ve instanceof LocalDateTime e) {
                    long diff = Duration.between(s, e).toMinutes();
                    if (diff > 0) durMin = (int) Math.min(diff, 600);
                }
            } catch (Exception ignore) {}
        }

        if (durMin != null) {
            Double rate = null; // kcal per hour
            Long pid = getRecordPlanId(r);
            WorkoutPlan plan = (pid != null) ? planMap.get(pid) : null;

            if (plan != null) {
                try {
                    Object v = plan.getClass().getMethod("getCaloriesPerHour").invoke(plan);
                    if (v instanceof Number n && n.doubleValue() > 0) rate = n.doubleValue();
                } catch (Exception ignore) {}
                try {
                    Object v = plan.getClass().getMethod("getWorkoutPlanCaloriesPerHour").invoke(plan);
                    if (v instanceof Number n && n.doubleValue() > 0) rate = n.doubleValue();
                } catch (Exception ignore) {}
            }
            if (rate != null && rate > 0) {
                return rate * (durMin / 60.0);
            }
        }

        // 3) 沒有 → 0
        return 0.0;
    }

    /** 無紀錄時：按「通知時間小時」放入 plan.actualTotalCalories（無通知 → 12:00） */
    private void accumulateCaloriesFromPlansPerHour(List<WorkoutPlan> plans, Window w, double[] out) {
        for (WorkoutPlan p : plans) {
            Double kcal = planActualCalories(p);
            if (kcal == null || kcal <= 0) continue;

            LocalDate d = p.getWorkoutPlanDate();
            LocalTime t = Optional.ofNullable(p.getWorkoutPlanNotifyTime()).orElse(LocalTime.NOON);
            ZonedDateTime when = ZonedDateTime.of(d, t, ZONE_TW);
            if (when.isBefore(w.start) || !when.isBefore(w.end)) continue;

            out[when.getHour()] += kcal;
        }
    }
    /** 無紀錄時：按日期放入 plan.actualTotalCalories */
    private void accumulateCaloriesFromPlansPerDay(List<WorkoutPlan> plans, Window w, double[] out) {
        LocalDate start = w.start.toLocalDate();
        LocalDate endEx = w.end.toLocalDate();
        for (WorkoutPlan p : plans) {
            Double kcal = planActualCalories(p);
            if (kcal == null || kcal <= 0) continue;

            LocalDate d = p.getWorkoutPlanDate();
            if (d.isBefore(start) || !d.isBefore(endEx)) continue;

            int idx = (int) ChronoUnit.DAYS.between(start, d);
            if (0 <= idx && idx < out.length) out[idx] += kcal;
        }
    }
    /** 只讀取 plan.actualTotalCalories（讀不到就回 null） */
    private Double planActualCalories(WorkoutPlan p) {
        try {
            Object v = p.getClass().getMethod("getActualTotalCalories").invoke(p);
            if (v instanceof Number n) return n.doubleValue();
        } catch (Exception ignore) {}
        return null;
    }

    // === TaskRecord（原本邏輯） ===
    private void accumulateMinutesPerHour(List<TaskRecord> rows, Window w, double[] out) {
        for (TaskRecord r : rows) {
            ZonedDateTime s = clampToWindow(r.getUserStartTime(), w);
            ZonedDateTime e = clampToWindow(r.getUserEndTime(),   w);
            if (!e.isAfter(s)) continue;
            ZonedDateTime cur = s;
            while (cur.isBefore(e)) {
                ZonedDateTime endOfHour = cur.truncatedTo(ChronoUnit.HOURS).plusHours(1);
                ZonedDateTime segEnd    = e.isBefore(endOfHour) ? e : endOfHour;
                long minutes = ChronoUnit.MINUTES.between(cur, segEnd);
                out[cur.getHour()] += minutes;
                cur = segEnd;
            }
        }
    }
    private void accumulateMinutesPerDay(List<TaskRecord> rows, Window w, double[] out) {
        LocalDate startDate = w.start.toLocalDate();
        for (TaskRecord r : rows) {
            ZonedDateTime s = clampToWindow(r.getUserStartTime(), w);
            ZonedDateTime e = clampToWindow(r.getUserEndTime(),   w);
            if (!e.isAfter(s)) continue;
            ZonedDateTime cur = s;
            while (cur.isBefore(e)) {
                ZonedDateTime endOfDay = cur.truncatedTo(ChronoUnit.DAYS).plusDays(1);
                ZonedDateTime segEnd   = e.isBefore(endOfDay) ? e : endOfDay;
                long minutes = ChronoUnit.MINUTES.between(cur, segEnd);
                int idx = (int) ChronoUnit.DAYS.between(startDate, cur.toLocalDate());
                if (0 <= idx && idx < out.length) out[idx] += minutes;
                cur = segEnd;
            }
        }
    }
    private void accumulateCountPerHour(List<TaskRecord> rows, Window w, double[] out) {
        for (TaskRecord r : rows) {
            ZonedDateTime s = clampToWindow(r.getUserStartTime(), w);
            if (s.isBefore(w.start) || !s.isBefore(w.end)) continue;
            out[s.getHour()] += 1;
        }
    }
    private void accumulateCountPerDay(List<TaskRecord> rows, Window w, double[] out) {
        LocalDate startDate = w.start.toLocalDate();
        for (TaskRecord r : rows) {
            ZonedDateTime s = clampToWindow(r.getUserStartTime(), w);
            if (s.isBefore(w.start) || !s.isBefore(w.end)) continue;
            int idx = (int) ChronoUnit.DAYS.between(startDate, s.toLocalDate());
            if (0 <= idx && idx < out.length) out[idx] += 1;
        }
    }

    // === 反射工具 ===
    private Long getPlanId(WorkoutPlan p) {
        try {
            Object v = p.getClass().getMethod("getWorkoutPlanId").invoke(p);
            if (v instanceof Long l) return l;
            if (v instanceof Integer i) return i.longValue();
        } catch (Exception ignore) {}
        try {
            Object v = p.getClass().getMethod("getId").invoke(p);
            if (v instanceof Long l) return l;
            if (v instanceof Integer i) return i.longValue();
        } catch (Exception ignore) {}
        return null;
    }
    private Long getRecordPlanId(WorkoutPlanRecord r) {
        try {
            Object v = r.getClass().getMethod("getWorkoutPlanId").invoke(r);
            if (v instanceof Long l) return l;
            if (v instanceof Integer i) return i.longValue();
        } catch (Exception ignore) {}
        try {
            Object wp = r.getClass().getMethod("getWorkoutPlan").invoke(r);
            if (wp != null) {
                try {
                    Object pid = wp.getClass().getMethod("getWorkoutPlanId").invoke(wp);
                    if (pid instanceof Long l) return l;
                    if (pid instanceof Integer i) return i.longValue();
                } catch (Exception ignore) {}
                try {
                    Object pid = wp.getClass().getMethod("getId").invoke(wp);
                    if (pid instanceof Long l) return l;
                    if (pid instanceof Integer i) return i.longValue();
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
        return null;
    }
    private Long getTaskWorkoutPlanId(TaskRecord t) {
        try {
            Object v = t.getClass().getMethod("getWorkoutPlanId").invoke(t);
            if (v instanceof Long l) return l;
            if (v instanceof Integer i) return i.longValue();
        } catch (Exception ignore) {}
        return null;
    }

    /** 把 DB 的 LocalDateTime 轉為 Asia/Taipei 並夾住 [w.start,w.end) 的端點 */
    private ZonedDateTime clampToWindow(LocalDateTime ldt, Window w) {
        ZonedDateTime z = ldt.atZone(ZONE_TW);
        if (z.isBefore(w.start)) return w.start;
        if (z.isAfter(w.end))    return w.end;
        return z;
    }
}
