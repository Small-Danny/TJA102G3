package com.tibafit.model.task;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.tibafit.model.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "task_record",
    indexes = {
        @Index(name = "idx_task_record_user",   columnList = "user_id"),
        @Index(name = "idx_task_record_task",   columnList = "task_id"),
        @Index(name = "idx_task_record_status", columnList = "task_record_status")
    }
)
public class TaskRecordVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_record_id")
    private Integer taskRecordId;   // PK AUTO_INCREMENT

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_task_record_user")
    )
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "task_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_task_record_task")
    )
    private TaskVO taskVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "task_record_status",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_task_record_status_code")
    )
    private TaskRecordStatusVO taskRecordStatusVO;

    @NotNull
    @Column(name = "user_start_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime userStartTime;

    @Column(name = "user_end_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime userEndTime;

    // ===== Getter / Setter =====
    public Integer getTaskRecordId() { return taskRecordId; }
    public void setTaskRecordId(Integer id) { this.taskRecordId = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
   
    
    public TaskRecordStatusVO getTaskRecordStatusVO() {
		return taskRecordStatusVO;
	}
	public void setTaskRecordStatusVO(TaskRecordStatusVO taskRecordStatusVO) {
		this.taskRecordStatusVO = taskRecordStatusVO;
	}
	
	public TaskVO getTaskVO() { return taskVO; }
    public void setTaskVO(TaskVO taskVO) { this.taskVO = taskVO; }

    public LocalDateTime getUserStartTime() { return userStartTime; }
    public void setUserStartTime(LocalDateTime t) { this.userStartTime = t; }

    public LocalDateTime getUserEndTime() { return userEndTime; }
    public void setUserEndTime(LocalDateTime t) { this.userEndTime = t; }
}
