package com.tibafit.model.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 若你有用 Hibernate，可用這兩個註解做自動時間戳
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "task_type",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_task_type_name", columnNames = "task_type_name")
    },
    indexes = {
        @Index(name = "idx_task_type_name", columnList = "task_type_name")
    }
)
public class TaskTypeVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_type_id")
    private Integer taskTypeId;                    // PK AUTO_INCREMENT

    @NotBlank
    @Size(max = 50)
    @Column(name = "task_type_name", length = 50, nullable = false)
    private String taskTypeName;                   // NOT NULL + UNIQUE

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;             // 建立時間

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;             // 更新時間

    // 反向關聯（被控端）：多方(TaskVO)才是擁有端/維護端
    @OneToMany(mappedBy = "taskTypeVO", fetch = FetchType.LAZY) //mappedBy = "taskTypeVO" 要與TaskVO @ManyToOne 註記的一致
    @OrderBy("task_id asc")
    private List<TaskVO> tasks = new ArrayList<>();

    // ====== Getter / Setter ======
    public Integer getTaskTypeId() { return taskTypeId; }
    public void setTaskTypeId(Integer taskTypeId) { this.taskTypeId = taskTypeId; }

    public String getTaskTypeName() { return taskTypeName; }
    public void setTaskTypeName(String taskTypeName) { this.taskTypeName = taskTypeName; }

    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }

    public List<TaskVO> getTasks() { return tasks; }

    // 方便雙向維護（選用）：呼叫後會同時設置多方的外鍵
//    public void addTask(TaskVO task) {
//        tasks.add(task);
//        task.setTaskType(this); // 多方是擁有端
//    }

    // equals / hashCode 以主鍵為準
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskTypeVO)) return false;
        TaskTypeVO other = (TaskTypeVO) o;
        return taskTypeId != null && taskTypeId.equals(other.taskTypeId);
    }
    @Override
    public int hashCode() { return 31; }
}
