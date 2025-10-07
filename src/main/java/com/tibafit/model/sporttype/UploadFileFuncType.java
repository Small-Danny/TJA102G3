package com.tibafit.model.sporttype;

public enum UploadFileFuncType {
	sport("系統運動"), customSport("自訂義運動"), sportType("運動分類"); 
	
    private final String displayName;

    UploadFileFuncType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
