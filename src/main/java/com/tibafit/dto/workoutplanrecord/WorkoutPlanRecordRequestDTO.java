package com.tibafit.dto.workoutplanrecord;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class WorkoutPlanRecordRequestDTO  implements Serializable {
    private Integer workoutPlanRecordId;
    private Integer workoutPlanId;
    private String sportFrom;
    private Integer sportId;
    private Integer customSportId;
    
//    private Integer actualCalories;
    
    private String actualStartTime;
    private String actualEndTime;
//    private Integer actualDuration;
//    private LocalDateTime actualRecordDatetime;
//    private Integer workoutPlanRecordDataStatus;
    private Integer userId;
    
    
    public WorkoutPlanRecordRequestDTO() {
		super();
	}

    
    public Integer getWorkoutPlanRecordId() {
        return workoutPlanRecordId;
    }
    public void setWorkoutPlanRecordId(Integer workoutPlanRecordId) {
        this.workoutPlanRecordId = workoutPlanRecordId;
    }

    @NotNull(message = "計畫ID: 不可為空")
    public Integer getWorkoutPlanId() {
        return workoutPlanId;
    }
    public void setWorkoutPlanId(Integer workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }

    @NotBlank(message = "運動來源: 不可為空")
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

//    @NotNull(message = "實際執行卡路里: 不可為空")
//    @PositiveOrZero(message = "actualCalories 必須 >= 0")
//    public Integer getActualCalories() {
//        return actualCalories;
//    }
//    public void setActualCalories(Integer actualCalories) {
//        this.actualCalories = actualCalories;
//    }

    @NotNull(message = "實際開始時間: 不可為空")
    public String getActualStartTime() {
        return actualStartTime;
    }
    public void setActualStartTime(String actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    @NotNull(message = "實際結束時間: 不可為空")
    public String getActualEndTime() {
        return actualEndTime;
    }
    public void setActualEndTime(String actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

//    @NotNull(message = "實際時長: 不可為空")
//    @Positive(message = "實際時長: 必須 > 0")
//    public Integer getActualDuration() {
//        return actualDuration;
//    }
//    public void setActualDuration(Integer actualDuration) {
//        this.actualDuration = actualDuration;
//    }

//    @NotNull(message = "實際紀錄日期時間:不可為空")
//    public LocalDateTime getActualRecordDatetime() {
//        return actualRecordDatetime;
//    }
//    public void setActualRecordDatetime(LocalDateTime actualRecordDatetime) {
//        this.actualRecordDatetime = actualRecordDatetime;
//    }

//    @NotNull(message = "資料狀態: 不可為空")
//    public Integer getWorkoutPlanRecordDataStatus() {
//        return workoutPlanRecordDataStatus;
//    }
//    public void setWorkoutPlanRecordDataStatus(Integer workoutPlanRecordDataStatus) {
//        this.workoutPlanRecordDataStatus = workoutPlanRecordDataStatus;
//    }
    
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
    
    

    @Override
    public String toString() {
        return "WorkoutPlanRecordRequestDTO {" +
                "workoutPlanRecordId=" + workoutPlanRecordId +
                ", workoutPlanId=" + workoutPlanId +
                ", sportFrom=" + sportFrom +
                ", sportId=" + sportId +
                ", customSportId=" + customSportId +
//                ", actualCalories=" + actualCalories +
                ", actualStartTime=" + actualStartTime +
                ", actualEndTime=" + actualEndTime +
//                ", actualDuration=" + actualDuration +
//                ", actualRecordDatetime=" + actualRecordDatetime +
//                ", workoutPlanRecordDataStatus=" + workoutPlanRecordDataStatus +
                "}";
    }
}
