package com.tibafit.dto.workoutplan;

import java.io.Serializable;
import java.util.List;

import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordResponseDTO;

public class WorkoutPlanResponseDTO implements Serializable {
    private Integer workoutPlanId;
    private String workoutPlanName;
    private Integer userId;
    
    private String sportFrom;
    private String sportFromText;
    
    private Integer sportId;
    private Integer customSportId;
    private String sportName;
    
    private String sportLevel;
    private String sportLevelText;
    
    private Integer sportCalories;
    private List<WorkoutPlanRecordResponseDTO> workoutPlanRecordResponseDTOs;
    
    private Integer workoutPlanStatus;
    private String workoutPlanStatusText;
    
    
    private String workoutPlanDate;
    private String workoutPlanTime;
    
    private Integer workoutPlanIsNotify;
    private String workoutPlanIsNotifyText;
    
    private Integer workoutPlanExpectedDuration;
    private String workoutPlanExpectedDurationText;
    
    private Integer actualTotalCount;
    
    private Integer actualTotalDuration;
    private String actualTotalDurationText;
    
    private Integer actualTotalCalories;
    
    private Integer workoutPlanDataStatus;
    private Integer taskRecordId;
    private String createDatetime;
    private String updateDatetime;
    
    
    public WorkoutPlanResponseDTO() {
		super();
	}

  
    public Integer getWorkoutPlanId() {
        return workoutPlanId;
    }
    public void setWorkoutPlanId(Integer workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }
    
    
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
    
    public String getSportFromText() {
		return sportFromText;
	}
	public void setSportFromText(String sportFromText) {
		this.sportFromText = sportFromText;
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
    
	public String getSportName() {
		return sportName;
	}
	public void setSportName(String sportName) {
		this.sportName = sportName;
	}


    public String getSportLevel() {
		return sportLevel;
	}
	public void setSportLevel(String sportLevel) {
		this.sportLevel = sportLevel;
	}
	public String getSportLevelText() {
		return sportLevelText;
	}
	public void setSportLevelText(String sportLevelText) {
		this.sportLevelText = sportLevelText;
	}
	
	
	public Integer getSportCalories() {
		return sportCalories;
	}
	public void setSportCalories(Integer sportCalories) {
		this.sportCalories = sportCalories;
	}


	public List<WorkoutPlanRecordResponseDTO> getWorkoutPlanRecordResponseDTOs() {
		return workoutPlanRecordResponseDTOs;
	}
	public void setWorkoutPlanRecordResponseDTOs(List<WorkoutPlanRecordResponseDTO> workoutPlanRecordResponseDTOs) {
		this.workoutPlanRecordResponseDTOs = workoutPlanRecordResponseDTOs;
	}


	public Integer getWorkoutPlanStatus() {
        return workoutPlanStatus;
    }
    public void setWorkoutPlanStatus(Integer workoutPlanStatus) {
        this.workoutPlanStatus = workoutPlanStatus;
    }
    
	public String getWorkoutPlanStatusText() {
		return workoutPlanStatusText;
	}
	public void setWorkoutPlanStatusText(String workoutPlanStatusText) {
		this.workoutPlanStatusText = workoutPlanStatusText;
	}

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

    public Integer getWorkoutPlanIsNotify() {
        return workoutPlanIsNotify;
    }
    public void setWorkoutPlanIsNotify(Integer workoutPlanIsNotify) {
        this.workoutPlanIsNotify = workoutPlanIsNotify;
    }
    
	public String getWorkoutPlanIsNotifyText() {
		return workoutPlanIsNotifyText;
	}
	public void setWorkoutPlanIsNotifyText(String workoutPlanIsNotifyText) {
		this.workoutPlanIsNotifyText = workoutPlanIsNotifyText;
	}

    public Integer getWorkoutPlanExpectedDuration() {
        return workoutPlanExpectedDuration;
    }
    public void setWorkoutPlanExpectedDuration(Integer workoutPlanExpectedDuration) {
        this.workoutPlanExpectedDuration = workoutPlanExpectedDuration;
    }
    
    public String getWorkoutPlanExpectedDurationText() {
		return workoutPlanExpectedDurationText;
	}
	public void setWorkoutPlanExpectedDurationText(String workoutPlanExpectedDurationText) {
		this.workoutPlanExpectedDurationText = workoutPlanExpectedDurationText;
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
    
    public String getActualTotalDurationText() {
		return actualTotalDurationText;
	}
	public void setActualTotalDurationText(String actualTotalDurationText) {
		this.actualTotalDurationText = actualTotalDurationText;
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

    public Integer getTaskRecordId() {
        return taskRecordId;
    }
    public void setTaskRecordId(Integer taskRecordId) {
        this.taskRecordId = taskRecordId;
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
        return "WorkoutPlanResponseDTO [" +
                "workoutPlanId=" + workoutPlanId +
                ", workoutPlanName=" + workoutPlanName +
                ", userId=" + userId +
                ", sportFrom=" + sportFrom +
                ", sportFromText=" + sportFromText +
                ", sportId=" + sportId +
                ", customSportId=" + customSportId +
                ", sportName=" + sportName +
                ", sportLevel=" + sportLevel +
                ", sportLevelText=" + sportLevelText +
                ", sportCalories=" + sportCalories +
                ", workoutPlanStatus=" + workoutPlanStatus +
                ", workoutPlanStatusText=" + workoutPlanStatusText +
                ", workoutPlanDate=" + workoutPlanDate +
                ", workoutPlanTime=" + workoutPlanTime +
                ", workoutPlanIsNotify=" + workoutPlanIsNotify +
                ", workoutPlanIsNotifyText=" + workoutPlanIsNotifyText +
                ", workoutPlanExpectedDuration=" + workoutPlanExpectedDuration +
                ", workoutPlanExpectedDurationText=" + workoutPlanExpectedDurationText +
                ", actualTotalCount=" + actualTotalCount +
                ", actualTotalDuration=" + actualTotalDuration +
                ", actualTotalDurationText=" + actualTotalDurationText +
                ", actualTotalCalories=" + actualTotalCalories +
                ", workoutPlanDataStatus=" + workoutPlanDataStatus +
                ", taskRecordId=" + taskRecordId +
                ", createDatetime=" + createDatetime +
                ", updateDatetime=" + updateDatetime +
                "]";
    }
    
    
}
