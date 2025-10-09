package com.tibafit.dto.analytics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompleteWorkoutRequest {

	/** 若你要從登入態取 userId，這個欄位可不傳 */
	private Integer userId;

	/** 對應被完成的運動計畫 ID（可選，但建議帶） */
	private Integer workoutPlanId;

	/** 使用者實際開始時間（與 minutes 至少擇一提供） */
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startAt;

	/** 使用者實際結束時間（可選；若沒帶會用 now 或用 minutes 推算） */
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endAt;

	/** 總分鐘數（可選；若沒帶則用 startAt/endAt 計算） */
	private Integer minutes;

	public CompleteWorkoutRequest() {
	} // 給 Jackson 反序列化用

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getWorkoutPlanId() {
		return workoutPlanId;
	}

	public void setWorkoutPlanId(Integer workoutPlanId) {
		this.workoutPlanId = workoutPlanId;
	}

	public LocalDateTime getStartAt() {
		return startAt;
	}

	public void setStartAt(LocalDateTime startAt) {
		this.startAt = startAt;
	}

	public LocalDateTime getEndAt() {
		return endAt;
	}

	public void setEndAt(LocalDateTime endAt) {
		this.endAt = endAt;
	}

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}
}
