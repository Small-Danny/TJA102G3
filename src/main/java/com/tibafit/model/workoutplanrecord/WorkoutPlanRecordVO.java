package com.tibafit.model.workoutplanrecord;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tibafit.model.customsport.CustomSportVO;
import com.tibafit.model.sport.SportVO;
import com.tibafit.model.workoutplan.WorkoutPlanVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "workout_plan_record")
public class WorkoutPlanRecordVO implements Serializable {
	private Integer workoutPlanRecordId;
	
	private Integer workoutPlanId;
	private WorkoutPlanVO workoutPlanVO;
	
	private String sportFrom;
	private Integer sportId;
	private SportVO sportVO;
	private Integer customSportId;
	private CustomSportVO customSportVO;
	
	
	private Integer actualCalories;
	private Integer calorieCountMethod;
	
	private LocalDateTime actualStartTime;
	private LocalDateTime actualEndTime;
	private Integer actualDuration;
	private LocalDateTime actualRecordDatetime;
	private Integer workoutPlanRecordDataStatus;
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	
    private Integer userId;
	

	public WorkoutPlanRecordVO() {
		super();
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workout_plan_record_id")
	public Integer getWorkoutPlanRecordId() {
		return workoutPlanRecordId;
	}
	public void setWorkoutPlanRecordId(Integer workoutPlanRecordId) {
		this.workoutPlanRecordId = workoutPlanRecordId;
	}

	
	@Column(name = "workout_plan_id", nullable = false)
	public Integer getWorkoutPlanId() {
		return workoutPlanId;
	}
	public void setWorkoutPlanId(Integer workoutPlanId) {
		this.workoutPlanId = workoutPlanId;
	}
	// 雙向關聯
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "workout_plan_id", referencedColumnName = "workout_plan_id", insertable = false, updatable = false)
	@JsonBackReference
	public WorkoutPlanVO getWorkoutPlanVO() {
		return workoutPlanVO;
	}
	public void setWorkoutPlanVO(WorkoutPlanVO workoutPlanVO) {
		this.workoutPlanVO = workoutPlanVO;
	}


	@Column(name = "sport_from", nullable = false, length = 20)
	public String getSportFrom() {
		return sportFrom;
	}
	public void setSportFrom(String sportFrom) {
		this.sportFrom = sportFrom;
	}

	
	@Column(name = "sport_id")
	public Integer getSportId() {
		return sportId;
	}
	public void setSportId(Integer sportId) {
		this.sportId = sportId;
	}
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "sport_id", referencedColumnName = "sport_id", insertable = false, updatable = false)
	public SportVO getSportVO() {
		return sportVO;
	}
	public void setSportVO(SportVO sportVO) {
		this.sportVO = sportVO;
	}

	
	@Column(name = "custom_sport_id")
	public Integer getCustomSportId() {
		return customSportId;
	}
	public void setCustomSportId(Integer customSportId) {
		this.customSportId = customSportId;
	}
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "custom_sport_id", referencedColumnName = "custom_sport_id", insertable = false, updatable = false)
	public CustomSportVO getCustomSportVO() {
		return customSportVO;
	}
	public void setCustomSportVO(CustomSportVO customSportVO) {
		this.customSportVO = customSportVO;
	}

	
	@Column(name = "actual_calories", nullable = false)
	public Integer getActualCalories() {
		return actualCalories;
	}
	public void setActualCalories(Integer actualCalories) {
		this.actualCalories = actualCalories;
	}
	
	@Column(name = "calorie_count_method", nullable = false)
	public Integer getCalorieCountMethod() {
		return calorieCountMethod;
	}
	public void setCalorieCountMethod(Integer calorieCountMethod) {
		this.calorieCountMethod = calorieCountMethod;
	}


	@Column(name = "actual_start_time", nullable = false)
	public LocalDateTime getActualStartTime() {
		return actualStartTime;
	}
	public void setActualStartTime(LocalDateTime actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	
	@Column(name = "actual_end_time", nullable = false)
	public LocalDateTime getActualEndTime() {
		return actualEndTime;
	}
	public void setActualEndTime(LocalDateTime actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	
	@Column(name = "actual_duration", nullable = false)
	public Integer getActualDuration() {
		return actualDuration;
	}
	public void setActualDuration(Integer actualDuration) {
		this.actualDuration = actualDuration;
	}

	
	@Column(name = "actual_record_datetime", nullable = false, insertable=false, updatable = false)
	public LocalDateTime getActualRecordDatetime() {
		return actualRecordDatetime;
	}
	public void setActualRecordDatetime(LocalDateTime actualRecordDatetime) {
		this.actualRecordDatetime = actualRecordDatetime;
	}

	
	@Column(name = "workout_plan_record_data_status", nullable = false)
	public Integer getWorkoutPlanRecordDataStatus() {
		return workoutPlanRecordDataStatus;
	}
	public void setWorkoutPlanRecordDataStatus(Integer workoutPlanRecordDataStatus) {
		this.workoutPlanRecordDataStatus = workoutPlanRecordDataStatus;
	}

	
	@Column(name = "create_datetime", nullable = false, insertable=false, updatable = false)
	public LocalDateTime getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(LocalDateTime createDatetime) {
		this.createDatetime = createDatetime;
	}

	
	@Column(name = "update_datetime", nullable = false, insertable=false, updatable = false)
	public LocalDateTime getUpdateDatetime() {
		return updateDatetime;
	}
	public void setUpdateDatetime(LocalDateTime updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
	// 僅暫存request userId，與資料庫、response無關
	@Transient
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	@Override
	public String toString() {
	    return "WorkoutPlanRecordVO {" +
	            "workoutPlanRecordId=" + workoutPlanRecordId +
	            ", workoutPlanId=" + workoutPlanId +
	            ", sportFrom=" + sportFrom +
	            ", sportId=" + sportId +
	            ", customSportId=" + customSportId +
	            ", actualCalories=" + actualCalories +
	            ", calorieCountMethod=" + calorieCountMethod +
	            ", actualStartTime=" + actualStartTime +
	            ", actualEndTime=" + actualEndTime +
	            ", actualDuration=" + actualDuration +
	            ", actualRecordDatetime=" + actualRecordDatetime +
	            ", workoutPlanRecordDataStatus=" + workoutPlanRecordDataStatus +
	            ", createDatetime=" + createDatetime +
	            ", updateDatetime=" + updateDatetime +
	            ", userId=" + userId +
	            "}";
	}
	
	
	
}
