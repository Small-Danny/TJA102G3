package com.tibafit.service.analytics;

import com.tibafit.dto.analytics.SeriesResponse;
import com.tibafit.model.analytics.TaskRecord;
import com.tibafit.model.analytics.WorkoutPlan;
import com.tibafit.repository.analytics.TaskRecordRepository;
import com.tibafit.repository.analytics.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class AnalyticsService {

    private final TaskRecordRepository taskRepo;
    private final WorkoutPlanRepository workoutPlanRepo;

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

    public AnalyticsService(TaskRecordRepository taskRepo,
                            WorkoutPlanRepository workoutPlanRepo) {
        this.taskRepo = taskRepo;
        this.workoutPlanRepo = workoutPlanRepo;
    }

    /** 對外主方法 */
    public SeriesResponse buildSeries(String metric, String range, long uid) {
        Window w = window(range);
        boolean perHour = w.perHour;

        // 先準備 labels 與資料陣列
        List<String> labels = perHour
                ? hourLabels()
                : dateLabels(w.start.toLocalDate(), w.end.minusDays(1).toLocalDate());
        double[] data = new double[labels.size()];

        switch (metric) {
            case "workout-time" -> {
                // 以 task_record 為資料源（完成狀態=1）
                List<TaskRecord> rows = taskRepo
                        .findByUserIdAndTaskRecordStatusAndUserStartTimeGreaterThanEqualAndUserStartTimeLessThanOrderByUserStartTime(
                                uid, 1, w.start.toLocalDateTime(), w.end.toLocalDateTime());
                if (perHour) accumulateMinutesPerHour(rows, w, data);
                else         accumulateMinutesPerDay(rows,  w, data);
            }
            case "tasks" -> {
                List<TaskRecord> rows = taskRepo
                        .findByUserIdAndTaskRecordStatusAndUserStartTimeGreaterThanEqualAndUserStartTimeLessThanOrderByUserStartTime(
                                uid, 1, w.start.toLocalDateTime(), w.end.toLocalDateTime());
                if (perHour) accumulateCountPerHour(rows, w, data);
                else         accumulateCountPerDay(rows,  w, data);
            }
            case "calories" -> {
                // 以 workout_plan 為資料源（用日期窗）
                LocalDate startDate = w.start.toLocalDate();
                LocalDate endDateEx = w.end.toLocalDate();
                List<WorkoutPlan> plans =
                        workoutPlanRepo.findByUserIdAndWorkoutPlanDateGreaterThanEqualAndWorkoutPlanDateLessThanOrderByWorkoutPlanDateAsc(
                                (int) uid, startDate, endDateEx);

                if (perHour) accumulateCaloriesPerHour(plans, w, data);
                else         accumulateCaloriesPerDay(plans,  w, data);
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

        // 裝箱成 List<Number>
        List<Number> dataList = new ArrayList<>(data.length);
        for (double v : data) dataList.add(v);

        return new SeriesResponse(labels, dataList, unit, total, metric, range);
    }

    // ---------------- 窗口與標籤 ----------------

    /** today=小時桶；week/month=日桶；皆用 [start,end) 以 Asia/Taipei 計算 */
    private Window window(String range) {
        ZonedDateTime now = ZonedDateTime.now(ZONE_TW);
        ZonedDateTime startDay = now.truncatedTo(ChronoUnit.DAYS);
        return switch (range) {
            case "today" -> new Window(startDay,                startDay.plusDays(1), true);
            case "week"  -> new Window(startDay.minusDays(6),   startDay.plusDays(1), false); // 含今天共7天
            case "month" -> new Window(startDay.minusDays(30),  startDay.plusDays(1), false); // 含今天共31天
            default      -> new Window(startDay.minusDays(6),   startDay.plusDays(1), false);
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

    // ---------------- 運動時數（來源：task_record） ----------------

    /** 精準把每筆紀錄的分鐘數分配到它跨越的各小時，且夾住 [w.start,w.end) */
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

    /** 每筆紀錄的分鐘數按日期分配，且夾住 [w.start,w.end) */
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

    // ---------------- 任務完成次數（來源：task_record） ----------------

    /** 任務數（today）：以開始時間所屬小時 +1（跨時段不拆） */
    private void accumulateCountPerHour(List<TaskRecord> rows, Window w, double[] out) {
        for (TaskRecord r : rows) {
            ZonedDateTime s = clampToWindow(r.getUserStartTime(), w);
            if (s.isBefore(w.start) || !s.isBefore(w.end)) continue;
            out[s.getHour()] += 1;
        }
    }

    /** 任務數（week/month）：以開始時間所屬日期 +1（跨日不拆） */
    private void accumulateCountPerDay(List<TaskRecord> rows, Window w, double[] out) {
        LocalDate startDate = w.start.toLocalDate();
        for (TaskRecord r : rows) {
            ZonedDateTime s = clampToWindow(r.getUserStartTime(), w);
            if (s.isBefore(w.start) || !s.isBefore(w.end)) continue;
            int idx = (int) ChronoUnit.DAYS.between(startDate, s.toLocalDate());
            if (0 <= idx && idx < out.length) out[idx] += 1;
        }
    }

    // ---------------- 卡路里（來源：workout_plan） ----------------

    /** 取熱量（Integer → double；null 則 0） */
    private double planCalories(WorkoutPlan p) {
        Integer act = p.getActualTotalCalories();
        return (act != null) ? act.doubleValue() : 0.0;
    }

    /** today：以「通知時間」所在小時歸桶；若無通知時間，預設 12:00 */
    private void accumulateCaloriesPerHour(List<WorkoutPlan> plans, Window w, double[] out) {
        for (WorkoutPlan p : plans) {
            LocalDate d = p.getWorkoutPlanDate();
            LocalTime t = Optional.ofNullable(p.getWorkoutPlanNotifyTime()).orElse(LocalTime.NOON);
            ZonedDateTime when = ZonedDateTime.of(d, t, ZONE_TW);

            if (when.isBefore(w.start) || !when.isBefore(w.end)) continue;

            int hourIdx = when.getHour();
            out[hourIdx] += planCalories(p);
        }
    }

    /** week/month：以「計畫日期」歸桶（每天一格） */
    private void accumulateCaloriesPerDay(List<WorkoutPlan> plans, Window w, double[] out) {
        LocalDate start = w.start.toLocalDate();
        LocalDate endEx = w.end.toLocalDate();

        for (WorkoutPlan p : plans) {
            LocalDate d = p.getWorkoutPlanDate();
            if (d.isBefore(start) || !d.isBefore(endEx)) continue;

            int idx = (int) ChronoUnit.DAYS.between(start, d);
            if (0 <= idx && idx < out.length) out[idx] += planCalories(p);
        }
    }

    // ---------------- 工具 ----------------

    /** 把 DB 的 LocalDateTime 轉為 Asia/Taipei 並夾住 [w.start,w.end) 的端點 */
    private ZonedDateTime clampToWindow(LocalDateTime ldt, Window w) {
        ZonedDateTime z = ldt.atZone(ZONE_TW);
        if (z.isBefore(w.start)) return w.start;
        if (z.isAfter(w.end))    return w.end;
        return z;
    }
}
