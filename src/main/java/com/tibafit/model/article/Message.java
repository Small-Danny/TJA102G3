package com.tibafit.model.article;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "sender_user_id")
    private Integer senderUserId; // NULL 表示系統/管理員

    @Column(name = "receiver_user_id", nullable = false)
    private Integer receiverUserId;

    @Column(name = "related_article_id")
    private Integer relatedArticleId;

    @Column(name = "related_report_id")
    private Integer relatedReportId;

    @Column(name = "message_type", nullable = false)
    private String messageType; // ENUM('會員訊息','系統通知','檢舉通知')

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_deleted_by_sender", nullable = false)
    private Boolean isDeletedBySender = false;

    @Column(name = "is_deleted_by_receiver", nullable = false)
    private Boolean isDeletedByReceiver = false;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Integer senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Integer getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Integer receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public Integer getRelatedArticleId() {
        return relatedArticleId;
    }

    public void setRelatedArticleId(Integer relatedArticleId) {
        this.relatedArticleId = relatedArticleId;
    }

    public Integer getRelatedReportId() {
        return relatedReportId;
    }

    public void setRelatedReportId(Integer relatedReportId) {
        this.relatedReportId = relatedReportId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsDeletedBySender() {
        return isDeletedBySender;
    }

    public void setIsDeletedBySender(Boolean isDeletedBySender) {
        this.isDeletedBySender = isDeletedBySender;
    }

    public Boolean getIsDeletedByReceiver() {
        return isDeletedByReceiver;
    }

    public void setIsDeletedByReceiver(Boolean isDeletedByReceiver) {
        this.isDeletedByReceiver = isDeletedByReceiver;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

