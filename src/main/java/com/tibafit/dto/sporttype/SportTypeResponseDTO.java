package com.tibafit.dto.sporttype;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tibafit.dto.sport.SportResponseDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemResponseDTO;

public class SportTypeResponseDTO {
    private Integer sportTypeId;
    private String sportTypeName;
    private String sportTypePic;
    
    private Integer sportTypeDataStatus;
    private String sportTypeDataStatusText;
    
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;

    private List<SportTypeItemResponseDTO> sportTypeItemResponseDTOs;
    
    
    public Integer getSportTypeId() {
        return sportTypeId;
    }
    public void setSportTypeId(Integer sportTypeId) {
        this.sportTypeId = sportTypeId;
    }

    
    public String getSportTypeName() {
        return sportTypeName;
    }
    public void setSportTypeName(String sportTypeName) {
        this.sportTypeName = sportTypeName;
    }

    
    public String getSportTypePic() {
        return sportTypePic;
    }
    public void setSportTypePic(String sportTypePic) {
        this.sportTypePic = sportTypePic;
    }

    
    public Integer getSportTypeDataStatus() {
		return sportTypeDataStatus;
	}
	public void setSportTypeDataStatus(Integer sportTypeDataStatus) {
		this.sportTypeDataStatus = sportTypeDataStatus;
	}
	
	
	public String getSportTypeDataStatusText() {
		return sportTypeDataStatusText;
	}
	public void setSportTypeDataStatusText(String sportTypeDataStatusText) {
		this.sportTypeDataStatusText = sportTypeDataStatusText;
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

    
    public List<SportTypeItemResponseDTO> getSportTypeItemResponseDTOs() {
        return sportTypeItemResponseDTOs;
    }
    public void setSportTypeItemResponseDTOs(List<SportTypeItemResponseDTO> sportTypeItemResponseDTOs) {
        this.sportTypeItemResponseDTOs = sportTypeItemResponseDTOs;
    }

    
    @Override
    public String toString() {
        return "SportTypeResponseDTO {" +
               "sportTypeId=" + sportTypeId +
               ", sportTypeName=" + sportTypeName +
               ", sportTypePic=" + sportTypePic +
               ", sportTypeDataStatus=" + sportTypeDataStatus + 
               ", sportTypeDataStatusText=" + sportTypeDataStatusText + 
               ", createDatetime=" + createDatetime +
               ", updateDatetime=" + updateDatetime +
               "}";
    }
}