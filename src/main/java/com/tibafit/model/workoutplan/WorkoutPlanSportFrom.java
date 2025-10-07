package com.tibafit.model.workoutplan;

public enum WorkoutPlanSportFrom {
	SYSTEM("system", "系統運動"), CUSTOM("custom", "自訂義運動");

	private final String codeName;
	private final String displayName;

	public String getCodeName() {
		return codeName;
	}
	public String getDisplayName() {
		return displayName;
	}

	private WorkoutPlanSportFrom(String codeName, String displayName) {
		this.codeName = codeName;
		this.displayName = displayName;
	}

	public static String getDisplayNameByCodeName(String codeName) {
		String defultStr = "無來源";

		if (codeName == null) {
			return defultStr;
		}
		for (WorkoutPlanSportFrom status : WorkoutPlanSportFrom.values()) {
			if (status.getCodeName().equals(codeName)) {
				return status.getDisplayName();
			}
		}

		return defultStr;

	}
}
