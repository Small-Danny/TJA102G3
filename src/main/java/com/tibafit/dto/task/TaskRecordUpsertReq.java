package com.tibafit.dto.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskRecordUpsertReq {
	public Integer userId;
	public Integer taskId;
	public String userStartTime;
	public String userEndTime;
	public Integer statusId;
	

	private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	public void setUserStartTime(LocalDateTime t) {
        this.userStartTime = (t == null) ? null : t.format(FMT);
    }
	
}
