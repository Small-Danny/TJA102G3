package com.tibafit.model.workoutplanrecord;

import com.tibafit.model.workoutplan.WorkoutPlanDataStatus;

public enum WorkoutPlanRecordCalorieCountMethod {
    RAWCOUNT("估算", 1), KGCOUNT("體重計算", 2), FILLIN("自填", 3);

    private final String displayName;
    private final Integer codeNum;

    private WorkoutPlanRecordCalorieCountMethod(String displayName, Integer codeNum) {
        this.displayName = displayName;
        this.codeNum = codeNum;
    }

    public String getDisplayName() { 
    	return displayName; 
    }
    public int getCodeNum() {
    	return codeNum; 
    }
    
    public static String getDisplayNameByCodeNum(Integer codeNum) {
    	String defultStr = "無";
    	
        if (codeNum == null) {
        	return defultStr;
        }
        for (WorkoutPlanRecordCalorieCountMethod method : WorkoutPlanRecordCalorieCountMethod.values()) {
            if (Integer.valueOf(method.codeNum).equals(codeNum)) {
                return method.displayName;
            }
        }

        return defultStr;
    }
}
