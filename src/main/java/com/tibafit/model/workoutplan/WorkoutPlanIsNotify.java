package com.tibafit.model.workoutplan;

public enum WorkoutPlanIsNotify {
	NONOTIFY(0, "已關閉"), YESNOTIFY(1, "已開啟");

	private final Integer codeNum;
	private final String displayName;

	public Integer getCodeNum() {
		return codeNum;
	}
	public String getDisplayName() {
		return displayName;
	}

	private WorkoutPlanIsNotify(Integer codeNum, String displayName) {
		this.codeNum = codeNum;
		this.displayName = displayName;
	}

	public static String getDisplayNameByCodeNum(Integer codeNum) {
		String defultStr = "無提醒";

		if (codeNum == null) {
			return defultStr;
		}
		for (WorkoutPlanIsNotify status : WorkoutPlanIsNotify.values()) {
			if (status.getCodeNum().equals(codeNum)) {
				return status.getDisplayName();
			}
		}

		return defultStr;

	}
}
