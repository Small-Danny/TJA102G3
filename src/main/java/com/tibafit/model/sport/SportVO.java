package com.tibafit.model.sport;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tibafit.model.user.Admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sport")
public class SportVO implements Serializable {
	private Integer sportId;
	private String sportName;
	private String sportDescription;
    private BigDecimal sportMets;
	private Integer sportEstimatedCalories;
	private String sportLevel;
	private String sportPic;
	private Integer sportDataStatus;
	
    private Integer adminId;
    private Admin adminVO;
    
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
	
	
	public SportVO() {
		super();
	}


	public SportVO(Integer sportId, String sportName, String sportDescription, BigDecimal sportMets, Integer sportEstimatedCalories,
			String sportLevel, String sportPic, Integer sportDataStatus, Integer adminId) {
		super();
		this.sportId = sportId;
		this.sportName = sportName;
		this.sportDescription = sportDescription;
		this.sportMets = sportMets;
		this.sportEstimatedCalories = sportEstimatedCalories;
		this.sportLevel = sportLevel;
		this.sportPic = sportPic;
		this.sportDataStatus = sportDataStatus;
		this.adminId = adminId;
	}

	@Id
	@Column(name = "sport_id", updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】 
	public Integer getSportId() {
		return sportId;
	}
	public void setSportId(Integer sportId) {
		this.sportId = sportId;
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

	
	@Column(name = "sport_mets", nullable = false, precision = 4, scale = 2)
	public BigDecimal getSportMets() {
		return sportMets;
	}
	public void setSportMets(BigDecimal sportMets) {
		this.sportMets = sportMets;
	}

	
	@Column(name = "sport_estimated_calories", nullable = false)
	public Integer getSportEstimatedCalories() {
		return sportEstimatedCalories;
	}
	public void setSportEstimatedCalories(Integer sportEstimatedCalories) {
		this.sportEstimatedCalories = sportEstimatedCalories;
	}

	
	@Column(name = "sport_level", nullable = false, length = 20)
	public String getSportLevel() {
		return sportLevel;
	}
	public void setSportLevel(String sportLevel) {
		this.sportLevel = sportLevel;
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

	
	@Column(name = "admin_id", nullable = false)
	public Integer getAdminId() {
		return adminId;
	}
	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", referencedColumnName = "admin_id", nullable=false, insertable = false, updatable = false)
	public Admin getAdminVO() {
		return adminVO;
	}
	public void setAdminVO(Admin adminVO) {
		this.adminVO = adminVO;
	}


	// 由 MySQL 自動 insert
	@Column(name = "create_datetime", insertable = false, updatable = false)
	public LocalDateTime getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(LocalDateTime createDatetime) {
		this.createDatetime = createDatetime;
	}

	
	// 由 MySQL 自動 insert/update
	@Column(name = "update_datetime", insertable = false, updatable = false)
	public LocalDateTime getUpdateDatetime() {
		return updateDatetime;
	}
	public void setUpdateDatetime(LocalDateTime updateDatetime) {
		this.updateDatetime = updateDatetime;
	}


	@Override
	public String toString() {
	    return "SportVO {" +
	            "sportId=" + sportId +
	            ", sportName=" + sportName +
	            ", sportDescription=" + sportDescription +
	            ", sportMets=" + sportMets +
	            ", sportEstimatedCalories=" + sportEstimatedCalories +
	            ", sportLevel=" + sportLevel +
	            ", sportPic=" + sportPic +
	            ", sportDataStatus=" + sportDataStatus +
	            ", adminId=" + adminId +
	            ", createDatetime=" + createDatetime +
	            ", updateDatetime=" + updateDatetime +
	            "}";
	}	
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	    	return true;
	    }
	    if (!(obj instanceof SportVO)) {
	    	return false;
	    }
	    SportVO other = (SportVO) obj;
	    
	    if (this.sportId != null && other.sportId != null) {
	        return this.sportId.equals(other.sportId);
	    }
	    // 兩物件都是 new 時，用記憶體位址判斷
	    return this == other;
	}

	@Override
	public int hashCode() {
	    return sportId != null ? sportId.hashCode() : System.identityHashCode(this);
	}
}
