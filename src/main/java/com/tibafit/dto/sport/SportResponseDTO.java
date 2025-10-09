package com.tibafit.dto.sport;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SportResponseDTO {
	private Integer sportId;
	private String sportName;
	private String sportDescription;
    private BigDecimal sportMets;
	private Integer sportEstimatedCalories;
	
	private String sportLevel;
	private String sportLevelText;
	
	private String sportPic;
	
	private Integer sportDataStatus;
	private String sportDataStatusText;
	
    private Integer adminId;
    private String adminIdText;
    
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
	
	
	public SportResponseDTO() {
		super();
	}


	public SportResponseDTO(Integer sportId, String sportName, String sportDescription, BigDecimal sportMets,
			Integer sportEstimatedCalories, String sportLevel, String sportLevelText, String sportPic,
			Integer sportDataStatus, String sportDataStatusText, Integer adminId, String adminIdText) {
		super();
		this.sportId = sportId;
		this.sportName = sportName;
		this.sportDescription = sportDescription;
		this.sportMets = sportMets;
		this.sportEstimatedCalories = sportEstimatedCalories;
		this.sportLevel = sportLevel;
		this.sportLevelText = sportLevelText;
		this.sportPic = sportPic;
		this.sportDataStatus = sportDataStatus;
		this.sportDataStatusText = sportDataStatusText;
		this.adminId = adminId;
		this.adminIdText = adminIdText;
	}


	public Integer getSportId() {
		return sportId;
	}


	public void setSportId(Integer sportId) {
		this.sportId = sportId;
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


	public BigDecimal getSportMets() {
		return sportMets;
	}


	public void setSportMets(BigDecimal sportMets) {
		this.sportMets = sportMets;
	}


	public Integer getSportEstimatedCalories() {
		return sportEstimatedCalories;
	}


	public void setSportEstimatedCalories(Integer sportEstimatedCalories) {
		this.sportEstimatedCalories = sportEstimatedCalories;
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


	public Integer getAdminId() {
		return adminId;
	}


	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}


	public String getAdminIdText() {
		return adminIdText;
	}
	
	
	public void setAdminIdText(String adminIdText) {
		this.adminIdText = adminIdText;
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
	
	
	@Override
	public String toString() {
	    return "SportResponseDTO {" +
	            "sportId=" + sportId +
	            ", sportName='" + sportName + '\'' +
	            ", sportDescription='" + sportDescription + '\'' +
	            ", sportMets=" + sportMets +
	            ", sportEstimatedCalories=" + sportEstimatedCalories +
	            ", sportLevel='" + sportLevel + '\'' +
	            ", sportLevelText='" + sportLevelText + '\'' +
	            ", sportPic='" + sportPic + '\'' +
	            ", sportDataStatus=" + sportDataStatus +
	            ", sportDataStatusText='" + sportDataStatusText + '\'' +
	            ", adminId=" + adminId +
	            ", adminIdText='" + adminIdText + '\'' +
	            ", createDatetime=" + createDatetime +
	            ", updateDatetime=" + updateDatetime +
	            '}';
	}
	
}


