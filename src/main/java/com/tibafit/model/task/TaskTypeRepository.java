package com.tibafit.model.task;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTypeRepository extends JpaRepository<TaskTypeVO, Integer> {
    Optional<TaskTypeVO> findByTaskTypeName(String taskTypeName);
    boolean existsByTaskTypeName(String taskTypeName);
}
