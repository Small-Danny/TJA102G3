// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.tibafit.repository.task;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.task.TaskVO;

public interface TaskRepository extends JpaRepository<TaskVO, Integer> {

	

}