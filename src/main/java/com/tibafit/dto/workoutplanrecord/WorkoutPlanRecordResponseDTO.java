package com.tibafit.dto.workoutplanrecord;

import java.io.Serializable;

public class WorkoutPlanRecordResponseDTO implements Serializable {
    private Integer workoutPlanRecordId;
    private Integer workoutPlanId;
    private String sportFrom;
    private Integer sportId;
    private Integer customSportId;
    
    private Integer actualCalories;
    private Integer calorieCountMethod;
    private String calorieCountMethodText;
    
    private String actualStartTime;
    private String actualEndTime;
    
    private Integer actualDuration;
    private String actualDurationText;
    
    private String actualRecordDatetime;
    
    private Integer workoutPlanRecordDataStatus;
    private String createDatetime;
    private String updateDatetime;
    

    public WorkoutPlanRecordResponseDTO() {
		super();
	}
    
    
	public Integer getWorkoutPlanRecordId() {
        return workoutPlanRecordId;
    }
    public void setWorkoutPlanRecordId(Integer workoutPlanRecordId) {
        this.workoutPlanRecordId = workoutPlanRecordId;
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
    
    
    public Integer getCalorieCountMethod() {
		return calorieCountMethod;
	}
	public void setCalorieCountMethod(Integer calorieCountMethod) {
		this.calorieCountMethod = calorieCountMethod;
	}
	public String getCalorieCountMethodText() {
		return calorieCountMethodText;
	}
	public void setCalorieCountMethodText(String calorieCountMethodText) {
		this.calorieCountMethodText = calorieCountMethodText;
	}


	public String getActualStartTime() {
        return actualStartTime;
    }
    public void setActualStartTime(String actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    
    public String getActualEndTime() {
        return actualEndTime;
    }
    public void setActualEndTime(String actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    
    public Integer getActualDuration() {
        return actualDuration;
    }
    public void setActualDuration(Integer actualDuration) {
        this.actualDuration = actualDuration;
    }
	public String getActualDurationText() {
		return actualDurationText;
	}
	public void setActualDurationText(String actualDurationText) {
		this.actualDurationText = actualDurationText;
	}

	
    public String getActualRecordDatetime() {
        return actualRecordDatetime;
    }
    public void setActualRecordDatetime(String actualRecordDatetime) {
        this.actualRecordDatetime = actualRecordDatetime;
    }

    
    public Integer getWorkoutPlanRecordDataStatus() {
        return workoutPlanRecordDataStatus;
    }
    public void setWorkoutPlanRecordDataStatus(Integer workoutPlanRecordDataStatus) {
        this.workoutPlanRecordDataStatus = workoutPlanRecordDataStatus;
    }

    
    public String getCreateDatetime() {
        return createDatetime;
    }
    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    
    public String getUpdateDatetime() {
        return updateDatetime;
    }
    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    
    @Override
    public String toString() {
        return "WorkoutPlanRecordResponseDTO {" +
                "workoutPlanRecordId=" + workoutPlanRecordId +
                ", workoutPlanId=" + workoutPlanId +
                ", sportFrom=" + sportFrom +
                ", sportId=" + sportId +
                ", customSportId=" + customSportId +
                ", actualCalories=" + actualCalories +
                ", calorieCountMethod=" + calorieCountMethod +
                ", calorieCountMethodText=" + calorieCountMethodText +
                ", actualStartTime=" + actualStartTime +
                ", actualEndTime=" + actualEndTime +
                ", actualDuration=" + actualDuration +
                ", actualDurationText=" + actualDurationText +
                ", actualRecordDatetime=" + actualRecordDatetime +
                ", workoutPlanRecordDataStatus=" + workoutPlanRecordDataStatus +
                ", createDatetime=" + createDatetime +
                ", updateDatetime=" + updateDatetime +
                "}";
    }
}
