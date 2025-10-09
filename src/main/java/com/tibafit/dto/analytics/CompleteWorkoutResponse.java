package com.tibafit.dto.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompleteWorkoutResponse {

	private final Integer taskRecordId;
	private final Integer minutesSaved;
	private final String message;

	public CompleteWorkoutResponse(Integer taskRecordId, Integer minutesSaved, String message) {
		this.taskRecordId = taskRecordId;
		this.minutesSaved = minutesSaved;
		this.message = message;
	}

	public Integer getTaskRecordId() {
		return taskRecordId;
	}

	public Integer getMinutesSaved() {
		return minutesSaved;
	}

	public String getMessage() {
		return message;
	}
}
