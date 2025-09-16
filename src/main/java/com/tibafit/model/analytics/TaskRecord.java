package com.tibafit.model.analytics;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_record")
public class TaskRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_record_id")
	private Integer id;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "task_id", nullable = false)
	private Integer taskId;

	@Column(name = "task_record_status", nullable = false)
	private Integer taskRecordStatus;

	@Column(name = "user_start_time", nullable = false)
	private LocalDateTime userStartTime;

	@Column(name = "user_end_time")
	private LocalDateTime userEndTime;

	// Getters / Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getTaskRecordStatus() {
		return taskRecordStatus;
	}

	public void setTaskRecordStatus(Integer taskRecordStatus) {
		this.taskRecordStatus = taskRecordStatus;
	}

	public LocalDateTime getUserStartTime() {
		return userStartTime;
	}

	public void setUserStartTime(LocalDateTime userStartTime) {
		this.userStartTime = userStartTime;
	}

	public LocalDateTime getUserEndTime() {
		return userEndTime;
	}

	public void setUserEndTime(LocalDateTime userEndTime) {
		this.userEndTime = userEndTime;
	}
}
