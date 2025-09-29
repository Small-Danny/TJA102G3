package com.tibafit.repository.task;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.task.TaskRecordStatusVO;

public interface TaskRecordStatusRepository extends JpaRepository<TaskRecordStatusVO, Integer> {
    boolean existsByStatusName(String statusName);
}
