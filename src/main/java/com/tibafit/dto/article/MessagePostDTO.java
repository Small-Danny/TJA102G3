package com.tibafit.dto.article;

public class MessagePostDTO {

    private Integer messageId;        // 訊息ID
    private Integer senderUserId;     // 發送者ID
    private Integer receiverUserId;   // 接收者會員ID
    private Integer relatedArticleId; // 相關文章ID
    private String title;             // 訊息標題
    private String content;           // 訊息內容
    private String messageType;
   

    // Getter 與 Setter
    
    public Integer getMessageId() {
        return messageId;
    }

    public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
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

	public MessagePostDTO(Integer messageId, Integer senderUserId, Integer receiverUserId, Integer relatedArticleId,
			String title, String content, String messageType) {
		super();
		this.messageId = messageId;
		this.senderUserId = senderUserId;
		this.receiverUserId = receiverUserId;
		this.relatedArticleId = relatedArticleId;
		this.title = title;
		this.content = content;
		this.messageType = messageType;
	}

	public MessagePostDTO() {
		super();
	}

	@Override
	public String toString() {
		return "MessageDTO [messageId=" + messageId + ", senderUserId=" + senderUserId + ", receiverUserId="
				+ receiverUserId + ", relatedArticleId=" + relatedArticleId + ", title=" + title + ", content="
				+ content + ", messageType=" + messageType + "]";
	}

	

}
