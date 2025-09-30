package com.tibafit.repository.task;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.task.TaskTypeVO;

public interface TaskTypeRepository extends JpaRepository<TaskTypeVO, Integer> {
    Optional<TaskTypeVO> findByTaskTypeName(String taskTypeName);
    boolean existsByTaskTypeName(String taskTypeName);
}
