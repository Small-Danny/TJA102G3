package com.tibafit.model.workoutplan;

public enum WorkoutPlanDataStatus {
    DELETE(0, "刪除"), EXIST(1, "存在");

    private final Integer codeNum;
    private final String displayName;

    private WorkoutPlanDataStatus(Integer codeNum, String displayName) {
        this.codeNum = codeNum;
        this.displayName = displayName;
    }

    public int getCodeNum() {
    	return codeNum; 
    }
    public String getDisplayName() { 
    	return displayName; 
    }

    public static String getDisplayNameByCodeNum(Integer codeNum) {
    	String defultStr = "無資料狀態";
    	
        if (codeNum == null) {
        	return defultStr;
        }
        for (WorkoutPlanDataStatus status : WorkoutPlanDataStatus.values()) {
            if (Integer.valueOf(status.codeNum).equals(codeNum)) {
                return status.displayName;
            }
        }

        return defultStr;
    }
}
