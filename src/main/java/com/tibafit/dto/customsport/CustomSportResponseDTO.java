package com.tibafit.dto.customsport;

import java.time.LocalDateTime;


public class CustomSportResponseDTO {
	private Integer customSportId;
	private String sportName;
	private String sportDescription;
	private Integer sportEstimatedCalories;
	private String sportPic;
	
	private Integer sportDataStatus;
	private String sportDataStatusText;
	
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;

	private Integer userId;
	private String userIdText;
	

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
	
	public String getSportDescription() {
		return sportDescription;
	}
	public void setSportDescription(String sportDescription) {
		this.sportDescription = sportDescription;
	}

	public Integer getSportEstimatedCalories() {
		return sportEstimatedCalories;
	}
	public void setSportEstimatedCalories(Integer sportEstimatedCalories) {
		this.sportEstimatedCalories = sportEstimatedCalories;
	}

	public String getSportPic() {
		return sportPic;
	}
	public void setSportPic(String sportPic) {
		this.sportPic = sportPic;
	}

	public Integer getSportDataStatus() {
		return sportDataStatus;
	}
	public void setSportDataStatus(Integer sportDataStatus) {
		this.sportDataStatus = sportDataStatus;
	}

	public String getSportDataStatusText() {
		return sportDataStatusText;
	}
	public void setSportDataStatusText(String sportDataStatusText) {
		this.sportDataStatusText = sportDataStatusText;
	}

	public LocalDateTime getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(LocalDateTime createDatetime) {
		this.createDatetime = createDatetime;
	}

	public LocalDateTime getUpdateDatetime() {
		return updateDatetime;
	}
	public void setUpdateDatetime(LocalDateTime updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getUserIdText() {
		return userIdText;
	}
	public void setUserIdText(String userIdText) {
		this.userIdText = userIdText;
	}
	
	
}
