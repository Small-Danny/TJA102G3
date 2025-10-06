package com.tibafit.model.sporttype;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tibafit.model.sporttypeitem.SportTypeItemVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "sport_type")
public class SportTypeVO implements Serializable {
    private Integer sportTypeId;
    
    private Set<SportTypeItemVO> sportTypeItemVOs;
    
    private String sportTypeName;
    private String sportTypePic;
    private Integer sportTypeDataStatus;
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_type_id", nullable = false)
    public Integer getSportTypeId() {
        return sportTypeId;
    }
    public void setSportTypeId(Integer sportTypeId) {
        this.sportTypeId = sportTypeId;
    }
    // 雙向關聯
    @OneToMany(mappedBy = "sportTypeVO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("sportId DESC")
    @JsonManagedReference
    public Set<SportTypeItemVO> getSportTypeItemVOs() {
        return sportTypeItemVOs;
    }
    public void setSportTypeItemVOs(Set<SportTypeItemVO> sportTypeItemVOs) {
        this.sportTypeItemVOs = sportTypeItemVOs;
    }

    
    @Column(name = "sport_type_name", nullable = false, length = 50)
    public String getSportTypeName() {
        return sportTypeName;
    }
    public void setSportTypeName(String sportTypeName) {
        this.sportTypeName = sportTypeName;
    }

    
    @Column(name = "sport_type_pic", length = 255)
    public String getSportTypePic() {
        return sportTypePic;
    }
    public void setSportTypePic(String sportTypePic) {
        this.sportTypePic = sportTypePic;
    }

    @Column(name = "sport_type_data_status", nullable = false)
    public Integer getSportTypeDataStatus() {
		return sportTypeDataStatus;
	}
	public void setSportTypeDataStatus(Integer sportTypeDataStatus) {
		this.sportTypeDataStatus = sportTypeDataStatus;
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
        return "SportTypeVO {" +
               "sportTypeId=" + sportTypeId + 
               ", sportTypeName=" + sportTypeName + 
               ", sportTypePic=" + sportTypePic + 
               ", sportTypeDataStatus=" + sportTypeDataStatus + 
               ", createDatetime=" + createDatetime + 
               ", updateDatetime=" + updateDatetime + 
               "}";
    }
}
