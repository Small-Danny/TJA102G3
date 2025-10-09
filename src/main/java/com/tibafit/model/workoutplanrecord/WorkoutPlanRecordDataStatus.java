package com.tibafit.model.workoutplanrecord;

public enum WorkoutPlanRecordDataStatus {
    DELETE(0, "刪除"), EXIST(1, "存在");

    private final Integer codeNum;
    private final String displayName;

    private WorkoutPlanRecordDataStatus(Integer codeNum, String displayName) {
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
    	String defultStr = String.valueOf(codeNum);
    	
        if (codeNum == null) {
        	return defultStr;
        }
        for (WorkoutPlanRecordDataStatus status : WorkoutPlanRecordDataStatus.values()) {
            if (Integer.valueOf(status.codeNum).equals(codeNum)) {
                return status.displayName;
            }
        }

        return defultStr;
    }
}
