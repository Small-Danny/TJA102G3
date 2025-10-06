package com.tibafit.repository.analytics;

import com.tibafit.model.analytics.WorkoutPlanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsWorkoutPlanRecordRepository extends JpaRepository<WorkoutPlanRecord, Integer> {

    // 依 planId 查所有紀錄
    List<WorkoutPlanRecord> findByWorkoutPlanId(Integer workoutPlanId);

    // 依時間區間查紀錄（如果你有用到）
    List<WorkoutPlanRecord> findByActualRecordDatetimeGreaterThanEqualAndActualRecordDatetimeLessThan(
        LocalDateTime startInclusive, LocalDateTime endExclusive
    );
}
