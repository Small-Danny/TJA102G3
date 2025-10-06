package com.tibafit.dto.workoutplan;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class WorkoutPlanRequestDTO implements Serializable {
    private Integer workoutPlanId;
    private String workoutPlanName;
    private Integer userId;
    private String sportFrom;
    private Integer sportId;
    private Integer customSportId;
//    private Integer workoutPlanStatus;
    private String workoutPlanDate;
    private String workoutPlanTime;
    private Integer workoutPlanIsNotify;
    private Integer workoutPlanExpectedDuration;
//    private Integer actualTotalCount;
//    private Integer actualTotalDuration;
//    private Integer actualTotalCalories;
//    private Integer workoutPlanDataStatus;
//    private Integer taskRecordId;
    
    
    public WorkoutPlanRequestDTO() {
		super();
	}


    public Integer getWorkoutPlanId() {
        return workoutPlanId;
    }
    public void setWorkoutPlanId(Integer workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }
    
    
    @NotBlank(message = "計畫名稱: 不可為空")
    @Size(max = 50, message = "計畫名稱: 長度不可超過 50")
    public String getWorkoutPlanName() {
		return workoutPlanName;
	}
	public void setWorkoutPlanName(String workoutPlanName) {
		this.workoutPlanName = workoutPlanName;
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

    
//    public Integer getWorkoutPlanStatus() {
//        return workoutPlanStatus;
//    }
//    public void setWorkoutPlanStatus(Integer workoutPlanStatus) {
//        this.workoutPlanStatus = workoutPlanStatus;
//    }

    
    @NotNull(message = "計畫安排日期: 不可為空")
    public String getWorkoutPlanDate() {
        return workoutPlanDate;
    }
    public void setWorkoutPlanDate(String workoutPlanDate) {
        this.workoutPlanDate = workoutPlanDate;
    }

    public String getWorkoutPlanTime() {
        return workoutPlanTime;
    }
    public void setWorkoutPlanTime(String workoutPlanTime) {
        this.workoutPlanTime = workoutPlanTime;
    }

    @NotNull(message = "是否提醒: 不可為空")
    public Integer getWorkoutPlanIsNotify() {
        return workoutPlanIsNotify;
    }
    public void setWorkoutPlanIsNotify(Integer workoutPlanIsNotify) {
        this.workoutPlanIsNotify = workoutPlanIsNotify;
    }

    @NotNull(message = "計畫預計時長: 不可為空")
    @Positive(message = "計畫預計時長: 必須大於 0")
    public Integer getWorkoutPlanExpectedDuration() {
        return workoutPlanExpectedDuration;
    }
    public void setWorkoutPlanExpectedDuration(Integer workoutPlanExpectedDuration) {
        this.workoutPlanExpectedDuration = workoutPlanExpectedDuration;
    }

//    @PositiveOrZero(message = "實際執行總次數: 必須 >= 0")
//    public Integer getActualTotalCount() {
//        return actualTotalCount;
//    }
//    public void setActualTotalCount(Integer actualTotalCount) {
//        this.actualTotalCount = actualTotalCount;
//    }
//
//    @PositiveOrZero(message = "實際執行總時長: 必須 >= 0")
//    public Integer getActualTotalDuration() {
//        return actualTotalDuration;
//    }
//    public void setActualTotalDuration(Integer actualTotalDuration) {
//        this.actualTotalDuration = actualTotalDuration;
//    }
//
//    @PositiveOrZero(message = "實際執行總卡路里: 必須 >= 0")
//    public Integer getActualTotalCalories() {
//        return actualTotalCalories;
//    }
//    public void setActualTotalCalories(Integer actualTotalCalories) {
//        this.actualTotalCalories = actualTotalCalories;
//    }


//    public Integer getWorkoutPlanDataStatus() {
//        return workoutPlanDataStatus;
//    }
//    public void setWorkoutPlanDataStatus(Integer workoutPlanDataStatus) {
//        this.workoutPlanDataStatus = workoutPlanDataStatus;
//    }

//    public Integer getTaskRecordId() {
//        return taskRecordId;
//    }
//    public void setTaskRecordId(Integer taskRecordId) {
//        this.taskRecordId = taskRecordId;
//    }

    @Override
    public String toString() {
        return "WorkoutPlanRequestDTO {" +
                "workoutPlanId=" + workoutPlanId +
                ", workoutPlanName=" + workoutPlanName +
                ", userId=" + userId +
                ", sportFrom=" + sportFrom +
                ", sportId=" + sportId +
                ", customSportId=" + customSportId +
//                ", workoutPlanStatus=" + workoutPlanStatus +
                ", workoutPlanDate=" + workoutPlanDate +
                ", workoutPlanTime=" + workoutPlanTime +
                ", workoutPlanIsNotify=" + workoutPlanIsNotify +
                ", workoutPlanExpectedDuration=" + workoutPlanExpectedDuration +
//                ", actualTotalCount=" + actualTotalCount +
//                ", actualTotalDuration=" + actualTotalDuration +
//                ", actualTotalCalories=" + actualTotalCalories +
//                ", workoutPlanDataStatus=" + workoutPlanDataStatus +
//                ", taskRecordId=" + taskRecordId +
                "}";
    }
}
