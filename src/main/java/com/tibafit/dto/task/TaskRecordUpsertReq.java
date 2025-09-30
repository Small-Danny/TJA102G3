package com.tibafit.dto.task;

public class TaskRecordUpsertReq {
	public Integer userId;
	public Integer taskId;
	public String userStartTime; // "yyyy-MM-dd'T'HH:mm:ss"
	public String userEndTime; // 可為 null（依你的驗證）
	public Integer statusId; // 可為 null（建立時可不給）
}
