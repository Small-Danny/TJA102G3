package com.tibafit.model.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "task_record_status_code",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_task_record_status_name", columnNames = "status_name")
    }
)
public class TaskRecordStatusVO {

    /** 主鍵：狀態碼（例如 0=未完成、1=已完成） */
    @Id
    @Column(name = "task_record_status")
    private Integer taskRecordStatus;

    /** 狀態名稱 */
    @NotBlank
    @Size(max = 20)
    @Column(name = "status_name", length = 20, nullable = false)
    private String statusName;

    // === 方便使用的常數（可選） ===
    public static final int INCOMPLETE = 0;
    public static final int COMPLETED  = 1;

    // ===== Getter / Setter =====
    public Integer getTaskRecordStatus() { return taskRecordStatus; }
    public void setTaskRecordStatus(Integer taskRecordStatus) { this.taskRecordStatus = taskRecordStatus; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    // equals/hashCode 以主鍵為準
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskRecordStatusVO)) return false;
        TaskRecordStatusVO other = (TaskRecordStatusVO) o;
        return taskRecordStatus != null && taskRecordStatus.equals(other.taskRecordStatus);
    }
    @Override
    public int hashCode() { return 31; }
}
