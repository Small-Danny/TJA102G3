package com.tibafit.model.workoutplan;

public enum WorkoutPlanNotifyStatus {
    NOTYET(0, "未寄"),
    SENT(1, "已寄"),
    MAKEUP(2, "補寄");

    private final Integer codeNum;
    private final String displayName;

    WorkoutPlanNotifyStatus(Integer codeNum, String displayName) {
        this.codeNum = codeNum;
        this.displayName = displayName;
    }

    public Integer getCodeNum() {
        return codeNum;
    }
    public String getDisplayName() {
        return displayName;
    }

    public static String getDisplayNameByCodeNum(Integer codeNum) {
    	String defultStr = "無寄信狀態";
    	
        if (codeNum == null) {
        	return defultStr;
        }
        
        for (WorkoutPlanNotifyStatus status : WorkoutPlanNotifyStatus.values()) {
            if (Integer.valueOf(status.codeNum).equals(codeNum)) {
                return status.displayName;
            }
        }

        return defultStr;
    }
}
