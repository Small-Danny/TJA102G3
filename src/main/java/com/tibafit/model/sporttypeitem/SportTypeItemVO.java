package com.tibafit.model.sporttypeitem;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.tibafit.model.sporttype.SportTypeVO;
import com.tibafit.model.sport.SportVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "sport_type_item")
public class SportTypeItemVO implements Serializable {
    private Integer sportTypeItemId;
    
    private Integer sportTypeId;
    private SportTypeVO sportTypeVO;
    
    private Integer sportId;
    private SportVO sportVO;
    
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_type_item_id", nullable = false)
    public Integer getSportTypeItemId() {
        return sportTypeItemId;
    }
    public void setSportTypeItemId(Integer sportTypeItemId) {
        this.sportTypeItemId = sportTypeItemId;
    }

    
    @Column(name = "sport_type_id", nullable = false)
    public Integer getSportTypeId() {
        return sportTypeId;
    }
    public void setSportTypeId(Integer sportTypeId) {
        this.sportTypeId = sportTypeId;
    }
    // 雙向關聯
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "sport_type_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    public SportTypeVO getSportTypeVO() {
        return sportTypeVO;
    }
    public void setSportTypeVO(SportTypeVO sportTypeVO) {
        this.sportTypeVO = sportTypeVO;
    }

    
    @Column(name = "sport_id", nullable = false)
    public Integer getSportId() {
        return sportId;
    }
    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "sport_id", nullable = false, insertable = false, updatable = false)
    public SportVO getSportVO() {
        return sportVO;
    }
    public void setSportVO(SportVO sportVO) {
        this.sportVO = sportVO;
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
        return "SportTypeItemVO {" +
               "sportTypeItemId=" + sportTypeItemId + 
               ", sportTypeId=" + sportTypeId + 
               ", sportId=" + sportId + 
               ", createDatetime=" + createDatetime + 
               ", updateDatetime=" + updateDatetime + 
               "}";
    }
}
