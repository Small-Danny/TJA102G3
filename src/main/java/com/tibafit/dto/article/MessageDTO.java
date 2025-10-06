package com.tibafit.dto.article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageDTO {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private String senderAvatar;
    private Integer receiverId;
    private String receiverName;
    private String type;  // 會員訊息 / 系統通知 / 檢舉通知
    private String title;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    private String createTimeStr; // 新增字串格式時間

    public MessageDTO() {}

    public MessageDTO(Integer id, Integer senderId, String senderName, String senderAvatar, Integer receiverId,
			String receiverName, String type, String title, String content, Boolean isRead, LocalDateTime createTime) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderAvatar = senderAvatar;
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.type = type;
		this.title = title;
		this.content = content;
		this.isRead = isRead;
		setCreateTime(createTime); // 使用 setter 生成 createTimeStr
	}

    // ===== Getter / Setter =====
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreateTime() { return createTime; }

    public void setCreateTime(LocalDateTime createTime) { 
        this.createTime = createTime;
        if (createTime != null) {
            this.createTimeStr = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            this.createTimeStr = "";
        }
    }

    public String getCreateTimeStr() { return createTimeStr; }
}
