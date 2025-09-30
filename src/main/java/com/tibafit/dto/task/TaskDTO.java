// DTO
package com.tibafit.dto.task;

import com.tibafit.model.task.TaskVO;

public class TaskDTO {
  public Integer taskId;
  public String taskName;
  public Integer targetValue;
  public String unit;
  public String startTime;
  public String endTime;
  public Integer points;
  public byte[] taskIcon;

  public static TaskDTO from(TaskVO vo) {
    TaskDTO d = new TaskDTO();
    d.taskId = vo.getTaskId();
    d.taskName = vo.getTaskName();
    d.targetValue = vo.getTargetValue();
    d.unit = vo.getUnit();
    d.startTime = vo.getStartTime().toString();
    d.endTime = vo.getEndTime().toString();
    d.points = vo.getPoints();
    d.taskIcon = vo.getTaskIcon();
    return d;
  }
}
