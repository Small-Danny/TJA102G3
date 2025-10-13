// src/main/java/com/task/api/dto/TaskRecordDTO.java
package com.tibafit.dto.task;

import java.time.format.DateTimeFormatter;

import com.tibafit.model.task.TaskRecordVO;

public class TaskRecordDTO {
  public Integer taskRecordId;
  public Integer userId;
  public Integer taskId;
  public String  userStartTime;    // ISO_LOCAL_DATE_TIME
  public String  userEndTime;      // ISO_LOCAL_DATE_TIME
  public Integer statusId;         // 來自 TaskRecordStatusVO 的 PK
  public String  statusName;       // 若有名稱/代碼就帶，沒有可為 null
  public String  taskName;
  public Integer targetValue;
  public String  unit;
  public Integer points;

  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public static TaskRecordDTO from(TaskRecordVO vo) {
    TaskRecordDTO d = new TaskRecordDTO();
    d.taskRecordId = vo.getTaskRecordId();
    d.userId = (vo.getUser() == null)? null : vo.getUser().getUserId();
    d.taskId = (vo.getTaskVO() == null)? null : vo.getTaskVO().getTaskId();
    d.userStartTime = (vo.getUserStartTime()==null)? null : vo.getUserStartTime().format(ISO);
    d.userEndTime   = (vo.getUserEndTime()==null)? null : vo.getUserEndTime().format(ISO);

    if (vo.getTaskRecordStatusVO() != null) {
      d.statusId = vo.getTaskRecordStatusVO().getTaskRecordStatus();
      try {
        
        d.statusName = (String) vo.getTaskRecordStatusVO().getClass()
          .getMethod("getStatusName").invoke(vo.getTaskRecordStatusVO());
      } catch (Exception ignore) {}
    }
    d.taskName = vo.getTaskVO().getTaskName();
    d.targetValue = vo.getTaskVO().getTargetValue();
    d.unit = vo.getTaskVO().getUnit();
    d.points = vo.getTaskVO().getPoints();
    return d;
  }
}
