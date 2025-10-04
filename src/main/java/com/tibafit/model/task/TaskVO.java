package com.tibafit.model.task;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "task",
    indexes = {
        @Index(name = "idx_task_type_id", columnList = "task_type_id"),
        @Index(name = "idx_admin_id", columnList = "admin_id"),
        @Index(name = "idx_start_end", columnList = "start_time,end_time")
    }
)
public class TaskVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

//    @NotNull
//    @Column(name = "task_type_id", nullable = false)
//    private Integer taskTypeId;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_type_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_task_task_type"))
    private TaskTypeVO taskTypeVO;


    @NotBlank
    @Size(max = 100)
    @Column(name = "task_name", length = 100, nullable = false)
    private String taskName;

    @NotNull
    @Positive
    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @NotBlank
    @Size(max = 20)
    @Column(name = "unit", length = 20, nullable = false)
    private String unit;

    @NotNull
    @Column(name = "start_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @NotNull
    @Min(0)
    @Max(127)
    @Column(name = "points", nullable = false)
    private Integer points = 0;

//    @NotNull
//    @Size(max = 2083)
//    @Column(name = "task_icon", length = 2083, nullable = false)
//    private String taskIcon;
    
    @Lob
    @Basic(fetch = FetchType.LAZY) // LAZY 可避免列表時把整張圖拉出來
    @Column(name = "task_icon", columnDefinition = "BLOB")
    private byte[] taskIcon;

    @NotNull
    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    // ====== Getter/Setter ======

    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    public TaskTypeVO getTaskTypeVO() {
		return taskTypeVO;
	}
	public void setTaskTypeVO(TaskTypeVO taskTypeVO) {
		this.taskTypeVO = taskTypeVO;
	}
	public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDate getStartTime() { return startTime; }
    public void setStartTime(LocalDate startTime) { this.startTime = startTime; }

    public LocalDate getEndTime() { return endTime; }
    public void setEndTime(LocalDate endTime) { this.endTime = endTime; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

//    public String getTaskIcon() { return taskIcon; }
//    public void setTaskIcon(String taskIcon) { this.taskIcon = taskIcon; }
    
    public byte[] getTaskIcon() {
		return taskIcon;
	}
	public void setTaskIcon(byte[] taskIcon) {
		this.taskIcon = taskIcon;
	}
	
	public Integer getAdminId() { return adminId; }
	public void setAdminId(Integer adminId) { this.adminId = adminId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskVO)) return false;
        TaskVO other = (TaskVO) o;
        return taskId != null && taskId.equals(other.taskId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}