package com.tibafit.model.customsport;

import java.time.LocalDateTime;

import com.tibafit.model.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_sport")
public class CustomSportVO {

	private Integer customSportId;
	private String sportName;
	private String sportDescription;
	private Integer sportEstimatedCalories;
	private String sportPic;
	private Integer sportDataStatus;
	
	private Integer userId;
	private User userVO;
	
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	

	public CustomSportVO() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custom_sport_id")
	public Integer getCustomSportId() {
		return customSportId;
	}
	public void setCustomSportId(Integer customSportId) {
		this.customSportId = customSportId;
	}

	
	@Column(name = "sport_name", nullable = false, length = 50)
	public String getSportName() {
		return sportName;
	}
	public void setSportName(String sportName) {
		this.sportName = sportName;
	}

	
	@Column(name = "sport_description", length = 255)
	public String getSportDescription() {
		return sportDescription;
	}
	public void setSportDescription(String sportDescription) {
		this.sportDescription = sportDescription;
	}

	
	@Column(name = "sport_estimated_calories", nullable = false)
	public Integer getSportEstimatedCalories() {
		return sportEstimatedCalories;
	}
	public void setSportEstimatedCalories(Integer sportEstimatedCalories) {
		this.sportEstimatedCalories = sportEstimatedCalories;
	}

	
	@Column(name = "sport_pic", length = 255)
	public String getSportPic() {
		return sportPic;
	}
	public void setSportPic(String sportPic) {
		this.sportPic = sportPic;
	}

	
	@Column(name = "sport_data_status", nullable = false)
	public Integer getSportDataStatus() {
		return sportDataStatus;
	}
	public void setSportDataStatus(Integer sportDataStatus) {
		this.sportDataStatus = sportDataStatus;
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

	
	@Column(name = "create_datetime", nullable = false, insertable = false, updatable = false)
	public LocalDateTime getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(LocalDateTime createDatetime) {
		this.createDatetime = createDatetime;
	}

	
	@Column(name = "update_datetime", nullable = false, insertable = false, updatable = false)
	public LocalDateTime getUpdateDatetime() {
		return updateDatetime;
	}
	public void setUpdateDatetime(LocalDateTime updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	
	@Override
	public String toString() {
	    return "CustomSportVO {" +
	           "customSportId=" + customSportId + 
	           ", sportName=" + sportName + 
	           ", sportDescription=" + sportDescription + 
	           ", sportEstimatedCalories=" + sportEstimatedCalories + 
	           ", sportPic=" + sportPic + 
	           ", sportDataStatus=" + sportDataStatus + 
	           ", userId=" + userId + 
	           ", createDatetime=" + createDatetime + 
	           ", updateDatetime=" + updateDatetime + 
	           "}";
	}

}