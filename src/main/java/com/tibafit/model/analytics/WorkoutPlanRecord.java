package com.tibafit.model.analytics;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_plan_record")
public class WorkoutPlanRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workout_plan_record_id")
	private Integer id;

	@Column(name = "workout_plan_id", nullable = false)
	private Integer workoutPlanId;

	@Column(name = "sport_from", nullable = false, length = 20)
	private String sportFrom;

	@Column(name = "sport_id")
	private Integer sportId;

	@Column(name = "custom_sport_id")
	private Integer customSportId;

	@Column(name = "actual_calories", nullable = false)
	private Integer actualCalories;

	@Column(name = "actual_start_time", nullable = false)
	private LocalDateTime actualStartTime;

	@Column(name = "actual_end_time", nullable = false)
	private LocalDateTime actualEndTime;

	@Column(name = "actual_duration", nullable = false)
	private Integer actualDuration;

	@Column(name = "actual_record_datetime", nullable = false)
	private LocalDateTime actualRecordDatetime;

	@Column(name = "workout_plan_record_data_status", nullable = false)
	private Integer workoutPlanRecordDataStatus;

	// ===== Getters / Setters =====
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWorkoutPlanId() {
		return workoutPlanId;
	}

	public void setWorkoutPlanId(Integer workoutPlanId) {
		this.workoutPlanId = workoutPlanId;
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

	public Integer getActualCalories() {
		return actualCalories;
	}

	public void setActualCalories(Integer actualCalories) {
		this.actualCalories = actualCalories;
	}

	public LocalDateTime getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(LocalDateTime actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public LocalDateTime getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(LocalDateTime actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	public Integer getActualDuration() {
		return actualDuration;
	}

	public void setActualDuration(Integer actualDuration) {
		this.actualDuration = actualDuration;
	}

	public LocalDateTime getActualRecordDatetime() {
		return actualRecordDatetime;
	}

	public void setActualRecordDatetime(LocalDateTime actualRecordDatetime) {
		this.actualRecordDatetime = actualRecordDatetime;
	}

	public Integer getWorkoutPlanRecordDataStatus() {
		return workoutPlanRecordDataStatus;
	}

	public void setWorkoutPlanRecordDataStatus(Integer workoutPlanRecordDataStatus) {
		this.workoutPlanRecordDataStatus = workoutPlanRecordDataStatus;
	}
}
