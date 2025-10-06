package com.tibafit.dto.sporttypeitem;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tibafit.dto.sport.SportResponseDTO;

public class SportTypeItemResponseDTO {
    private Integer sportTypeItemId;
    private Integer sportTypeId;
    
    private Integer sportId;
    private SportResponseDTO sportResponseDTO;
    
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;

    
    public Integer getSportTypeItemId() {
        return sportTypeItemId;
    }
    public void setSportTypeItemId(Integer sportTypeItemId) {
        this.sportTypeItemId = sportTypeItemId;
    }

    
    public Integer getSportTypeId() {
        return sportTypeId;
    }
    public void setSportTypeId(Integer sportTypeId) {
        this.sportTypeId = sportTypeId;
    }

    
    public Integer getSportId() {
        return sportId;
    }
    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }
    public SportResponseDTO getSportResponseDTO() {
		return sportResponseDTO;
	}
	public void setSportResponseDTO(SportResponseDTO sportResponseDTO) {
		this.sportResponseDTO = sportResponseDTO;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreateDatetime() {
        return createDatetime;
    }
    public void setCreateDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
    }

    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getUpdateDatetime() {
        return updateDatetime;
    }
    public void setUpdateDatetime(LocalDateTime updateDatetime) {
        this.updateDatetime = updateDatetime;
    }
    
    
    @Override
    public String toString() {
        return "SportTypeItemResponseDTO {" +
               "sportTypeItemId=" + sportTypeItemId + 
               ", sportTypeId=" + sportTypeId + 
               ", sportId=" + sportId + 
               ", createDatetime=" + createDatetime + 
               ", updateDatetime=" + updateDatetime + 
               "}";
    }      
}
