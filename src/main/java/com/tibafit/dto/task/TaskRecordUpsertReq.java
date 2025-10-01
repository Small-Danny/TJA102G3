package com.tibafit.dto.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskRecordUpsertReq {
	public Integer userId;
	public Integer taskId;
	public String userStartTime; // "yyyy-MM-dd'T'HH:mm:ss"
	public String userEndTime; // 可為 null（依你的驗證）
	public Integer statusId; // 可為 null（建立時可不給）
	

	private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	public void setUserStartTime(LocalDateTime t) {
        this.userStartTime = (t == null) ? null : t.format(FMT);
    }
	
}
