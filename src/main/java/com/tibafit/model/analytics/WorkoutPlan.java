package com.tibafit.model.analytics;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "workout_plan")
public class WorkoutPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workout_plan_id")
	private Integer id;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "sport_from", nullable = false, length = 20)
	private String sportFrom;

	@Column(name = "sport_id")
	private Integer sportId;

	@Column(name = "custom_sport_id")
	private Integer customSportId;

	@Column(name = "workout_plan_status", nullable = false)
	private Integer workoutPlanStatus;

	@Column(name = "workout_plan_date", nullable = false)
	private LocalDate workoutPlanDate;

	@Column(name = "workout_plan_notify_time")
	private LocalTime workoutPlanNotifyTime;

	@Column(name = "workout_plan_expected_duration", nullable = false)
	private Integer workoutPlanExpectedDuration;

	@Column(name = "actual_total_count", nullable = false)
	private Integer actualTotalCount;

	@Column(name = "actual_total_duration", nullable = false)
	private Integer actualTotalDuration;

	@Column(name = "actual_total_calories", nullable = false)
	private Integer actualTotalCalories;

	@Column(name = "workout_plan_data_status", nullable = false)
	private Integer workoutPlanDataStatus;

	@Column(name = "workout_plan_update_datetime")
	private LocalDateTime workoutPlanUpdateDatetime;

	@Column(name = "task_record_id")
	private Integer taskRecordId;

	// ===== Getters / Setters =====
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

	public String getSportFrom() {
		return sportFrom;
	}

	public void setSportFrom(String sportFrom) {
		this.sportFrom = sportFrom;
	}

	public Integer getSportId() {
		return sportId;
	}

	public void setSportId(Integer sportId) {
		this.sportId = sportId;
	}

	public Integer getCustomSportId() {
		return customSportId;
	}

	public void setCustomSportId(Integer customSportId) {
		this.customSportId = customSportId;
	}

	public Integer getWorkoutPlanStatus() {
		return workoutPlanStatus;
	}

	public void setWorkoutPlanStatus(Integer workoutPlanStatus) {
		this.workoutPlanStatus = workoutPlanStatus;
	}

	public LocalDate getWorkoutPlanDate() {
		return workoutPlanDate;
	}

	public void setWorkoutPlanDate(LocalDate workoutPlanDate) {
		this.workoutPlanDate = workoutPlanDate;
	}

	public LocalTime getWorkoutPlanNotifyTime() {
		return workoutPlanNotifyTime;
	}

	public void setWorkoutPlanNotifyTime(LocalTime workoutPlanNotifyTime) {
		this.workoutPlanNotifyTime = workoutPlanNotifyTime;
	}

	public Integer getWorkoutPlanExpectedDuration() {
		return workoutPlanExpectedDuration;
	}

	public void setWorkoutPlanExpectedDuration(Integer workoutPlanExpectedDuration) {
		this.workoutPlanExpectedDuration = workoutPlanExpectedDuration;
	}

	public Integer getActualTotalCount() {
		return actualTotalCount;
	}

	public void setActualTotalCount(Integer actualTotalCount) {
		this.actualTotalCount = actualTotalCount;
	}

	public Integer getActualTotalDuration() {
		return actualTotalDuration;
	}

	public void setActualTotalDuration(Integer actualTotalDuration) {
		this.actualTotalDuration = actualTotalDuration;
	}

	public Integer getActualTotalCalories() {
		return actualTotalCalories;
	}

	public void setActualTotalCalories(Integer actualTotalCalories) {
		this.actualTotalCalories = actualTotalCalories;
	}

	public Integer getWorkoutPlanDataStatus() {
		return workoutPlanDataStatus;
	}

	public void setWorkoutPlanDataStatus(Integer workoutPlanDataStatus) {
		this.workoutPlanDataStatus = workoutPlanDataStatus;
	}

	public LocalDateTime getWorkoutPlanUpdateDatetime() {
		return workoutPlanUpdateDatetime;
	}

	public void setWorkoutPlanUpdateDatetime(LocalDateTime workoutPlanUpdateDatetime) {
		this.workoutPlanUpdateDatetime = workoutPlanUpdateDatetime;
	}

	public Integer getTaskRecordId() {
		return taskRecordId;
	}

	public void setTaskRecordId(Integer taskRecordId) {
		this.taskRecordId = taskRecordId;
	}
}
