package com.tibafit.repository.analytics;

import com.tibafit.model.analytics.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsTaskRecordRepository extends JpaRepository<TaskRecord, Long> {

 
  List<TaskRecord> findByUserIdAndTaskRecordStatusAndUserStartTimeGreaterThanEqualAndUserStartTimeLessThanOrderByUserStartTime(
      long userId, int taskRecordStatus, LocalDateTime start, LocalDateTime end
  );
}
