package com.tibafit.model.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRecordStatusRepository extends JpaRepository<TaskRecordStatusVO, Integer> {
    boolean existsByStatusName(String statusName);
}
