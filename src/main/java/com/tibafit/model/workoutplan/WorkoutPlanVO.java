package com.tibafit.model.workoutplan;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tibafit.model.customsport.CustomSportVO;
import com.tibafit.model.sport.SportVO;
import com.tibafit.model.user.User;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "workout_plan")
public class WorkoutPlanVO implements Serializable {
	private Integer workoutPlanId;
    private String workoutPlanName;
    
	private Integer userId;
	private User userVO;
	
	private String sportFrom;
	private Integer sportId;
	private SportVO sportVO;
	private Integer customSportId;
	private CustomSportVO customSportVO;
	
	private Set<WorkoutPlanRecordVO> workoutPlanRecordVOs;
	
	private Integer workoutPlanStatus;
	private LocalDate workoutPlanDate;
	private LocalTime workoutPlanTime;
	private Integer workoutPlanIsNotify;
	private Integer workoutPlanExpectedDuration;
	private Integer actualTotalCount;
	private Integer actualTotalDuration;
	private Integer actualTotalCalories;
	private Integer workoutPlanDataStatus;
	private Integer taskRecordId;
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	
	private Integer workoutPlanNotifyStatus;

	public WorkoutPlanVO() {
		super();
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workout_plan_id")
	public Integer getWorkoutPlanId() {
		return workoutPlanId;
	}
	public void setWorkoutPlanId(Integer workoutPlanId) {
		this.workoutPlanId = workoutPlanId;
	}
	
	
	@Column(name = "workout_plan_name", nullable = false, length = 50)
    public String getWorkoutPlanName() {
		return workoutPlanName;
	}
	public void setWorkoutPlanName(String workoutPlanName) {
		this.workoutPlanName = workoutPlanName;
	}

	
	@Column(name = "user_id", nullable = false)
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
	public User getUserVO() {
		return userVO;
	}
	public void setUserVO(User userVO) {
		this.userVO = userVO;
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
	
	// 雙向關聯
	@OneToMany(mappedBy = "workoutPlanVO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("actualStartTime ASC")
	@JsonManagedReference
	public Set<WorkoutPlanRecordVO> getWorkoutPlanRecordVOs() {
		return workoutPlanRecordVOs;
	}
	public void setWorkoutPlanRecordVOs(Set<WorkoutPlanRecordVO> workoutPlanRecordVOs) {
		this.workoutPlanRecordVOs = workoutPlanRecordVOs;
	}


	@Column(name = "workout_plan_status", nullable = false)
	public Integer getWorkoutPlanStatus() {
		return workoutPlanStatus;
	}
	public void setWorkoutPlanStatus(Integer workoutPlanStatus) {
		this.workoutPlanStatus = workoutPlanStatus;
	}

	
	@Column(name = "workout_plan_date", nullable = false)
	public LocalDate getWorkoutPlanDate() {
		return workoutPlanDate;
	}
	public void setWorkoutPlanDate(LocalDate workoutPlanDate) {
		this.workoutPlanDate = workoutPlanDate;
	}

	
	@Column(name = "workout_plan_time")
	public LocalTime getWorkoutPlanTime() {
		return workoutPlanTime;
	}
	public void setWorkoutPlanTime(LocalTime workoutPlanTime) {
		this.workoutPlanTime = workoutPlanTime;
	}

	
	@Column(name = "workout_plan_is_notify", nullable = false)
	public Integer getWorkoutPlanIsNotify() {
		return workoutPlanIsNotify;
	}
	public void setWorkoutPlanIsNotify(Integer workoutPlanIsNotify) {
		this.workoutPlanIsNotify = workoutPlanIsNotify;
	}

	
	@Column(name = "workout_plan_expected_duration", nullable = false)
	public Integer getWorkoutPlanExpectedDuration() {
		return workoutPlanExpectedDuration;
	}
	public void setWorkoutPlanExpectedDuration(Integer workoutPlanExpectedDuration) {
		this.workoutPlanExpectedDuration = workoutPlanExpectedDuration;
	}

	
	@Column(name = "actual_total_count", nullable = false)
	public Integer getActualTotalCount() {
		return actualTotalCount;
	}
	public void setActualTotalCount(Integer actualTotalCount) {
		this.actualTotalCount = actualTotalCount;
	}

	
	@Column(name = "actual_total_duration", nullable = false)
	public Integer getActualTotalDuration() {
		return actualTotalDuration;
	}
	public void setActualTotalDuration(Integer actualTotalDuration) {
		this.actualTotalDuration = actualTotalDuration;
	}

	
	@Column(name = "actual_total_calories", nullable = false)
	public Integer getActualTotalCalories() {
		return actualTotalCalories;
	}
	public void setActualTotalCalories(Integer actualTotalCalories) {
		this.actualTotalCalories = actualTotalCalories;
	}

	
	@Column(name = "workout_plan_data_status", nullable = false)
	public Integer getWorkoutPlanDataStatus() {
		return workoutPlanDataStatus;
	}
	public void setWorkoutPlanDataStatus(Integer workoutPlanDataStatus) {
		this.workoutPlanDataStatus = workoutPlanDataStatus;
	}

	
	@Column(name = "task_record_id")
	public Integer getTaskRecordId() {
		return taskRecordId;
	}
	public void setTaskRecordId(Integer taskRecordId) {
		this.taskRecordId = taskRecordId;
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
	

	@Column(name = "workout_plan_notify_status", nullable = false)
	public Integer getWorkoutPlanNotifyStatus() {
		return workoutPlanNotifyStatus;
	}
	public void setWorkoutPlanNotifyStatus(Integer workoutPlanNotifyStatus) {
		this.workoutPlanNotifyStatus = workoutPlanNotifyStatus;
	}


	@Override
	public String toString() {
	    return "WorkoutPlanVO {" +
	            "workoutPlanId=" + workoutPlanId +
	            ", workoutPlanName=" + workoutPlanName +
	            ", userId=" + userId +
	            ", sportFrom=" + sportFrom +
	            ", sportId=" + sportId +
	            ", customSportId=" + customSportId +
	            ", workoutPlanStatus=" + workoutPlanStatus +
	            ", workoutPlanDate=" + workoutPlanDate +
	            ", workoutPlanTime=" + workoutPlanTime +
	            ", workoutPlanIsNotify=" + workoutPlanIsNotify +
	            ", workoutPlanExpectedDuration=" + workoutPlanExpectedDuration +
	            ", actualTotalCount=" + actualTotalCount +
	            ", actualTotalDuration=" + actualTotalDuration +
	            ", actualTotalCalories=" + actualTotalCalories +
	            ", workoutPlanDataStatus=" + workoutPlanDataStatus +
	            ", taskRecordId=" + taskRecordId +
	            ", createDatetime=" + createDatetime +
	            ", updateDatetime=" + updateDatetime +
	            ", updateDatetime=" + workoutPlanNotifyStatus +
	            "}";
	}
}
