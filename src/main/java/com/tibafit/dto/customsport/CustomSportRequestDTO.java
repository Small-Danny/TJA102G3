package com.tibafit.dto.customsport;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public class CustomSportRequestDTO {
    private Integer customSportId;
    private String sportName;
    private String sportDescription;
    private Integer sportEstimatedCalories;
    private String sportPic;
    private Integer sportDataStatus;
    private Integer userId;

    public Integer getCustomSportId() {
        return customSportId; 
    }
    public void setCustomSportId(Integer customSportId) {
        this.customSportId = customSportId; 
    }

    
	@NotBlank(message = "名稱: 不可為空")
    @Size(max = 50, message = "名稱: 長度不可超過 50")
    public String getSportName() {
        return sportName; 
    }
    public void setSportName(String sportName) {
        this.sportName = sportName; 
    }

    
    @NotBlank(message = "描述: 不可為空")
    @Size(max = 255, message = "描述: 長度不可超過 255")
    public String getSportDescription() {
        return sportDescription; 
    }
    public void setSportDescription(String sportDescription) {
        this.sportDescription = sportDescription; 
    }

    
    @NotNull(message = "卡路里: 不可為空")
	@Positive(message="卡路里: 請輸入大於0的正整數數字，最多5位整數")
	@Digits(integer = 5, fraction = 0, message = "卡路里: 請輸入大於0的正整數數字，最多5位整數")
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

    
    @NotNull(message = "建立會員ID: 不可為空")
    public Integer getUserId() {
        return userId; 
    }
    public void setUserId(Integer userId) {
        this.userId = userId; 
    }
    
    
    @Override
    public String toString() {
        return "CustomSportRequestDTO {" +
                "customSportId=" + customSportId +
                ", sportName=" + sportName +
                ", sportDescription=" + sportDescription +
                ", sportEstimatedCalories=" + sportEstimatedCalories +
                ", sportPic=" + sportPic +
                ", sportDataStatus=" + sportDataStatus +
                ", userId=" + userId +
                "}";
    }
}