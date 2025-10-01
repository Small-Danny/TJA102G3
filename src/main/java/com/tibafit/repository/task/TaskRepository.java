// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.tibafit.repository.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tibafit.model.task.TaskVO;

public interface TaskRepository extends JpaRepository<TaskVO, Integer> {

	// 排除「曾經建立過 TaskRecord（任何狀態）」的任務
	@Query("""
			  SELECT t
			    FROM TaskVO t
			   WHERE NOT EXISTS (
			          SELECT 1 FROM TaskRecordVO tr
			           WHERE tr.taskVO = t
			             AND tr.user.userId = :userId
			        )
			""")
	List<TaskVO> findNeverJoined(@Param("userId") Integer userId);

}