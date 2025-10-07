package com.tibafit.model.workoutplan;

public enum WorkoutPlanStatus {
	NOTYET(0, "未執行"), DONE(1, "已執行");

	private final Integer codeNum;
	private final String displayName;

	public Integer getCodeNum() {
		return codeNum;
	}
	public String getDisplayName() {
		return displayName;
	}

	private WorkoutPlanStatus(Integer codeNum, String displayName) {
		this.codeNum = codeNum;
		this.displayName = displayName;
	}

	public static String getDisplayNameByCodeNum(Integer codeNum) {
		String defultStr = String.valueOf(codeNum);
		
		if (codeNum == null) {
			return defultStr;
		}
		for (WorkoutPlanStatus status : WorkoutPlanStatus.values()) {
			if (status.getCodeNum().equals(codeNum)) {
				return status.getDisplayName();
			}
		}
		
		return defultStr;

	}
}
