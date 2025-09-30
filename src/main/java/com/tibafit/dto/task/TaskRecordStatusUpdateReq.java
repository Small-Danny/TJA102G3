// src/main/java/com/task/api/dto/TaskRecordStatusUpdateReq.java
package com.tibafit.dto.task;

import jakarta.validation.constraints.NotNull;

public class TaskRecordStatusUpdateReq {
  /** TaskRecordStatusVO 的主鍵（Integer） */
  @NotNull
  public Integer statusId;
}
