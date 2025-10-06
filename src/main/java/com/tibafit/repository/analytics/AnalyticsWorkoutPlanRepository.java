package com.tibafit.repository.analytics;

import com.tibafit.model.analytics.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsWorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
    List<WorkoutPlan> findByUserIdAndWorkoutPlanDateGreaterThanEqualAndWorkoutPlanDateLessThanOrderByWorkoutPlanDateAsc(
        Integer userId, LocalDate startInclusive, LocalDate endExclusive
    );
}