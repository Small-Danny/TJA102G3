package com.tibafit.dto.article;

import java.sql.Timestamp;

public class MemberInfoArticlePostDTO {

	private Integer userId;  // 使用者 ID
	private Integer articleId;
    private String coverImageUrl;     // 文章封面
    private String content;           // 文章內容
    private String forumTypeName;     // 文章分類名稱
    private Timestamp createTime;     // 發布時間
    private Integer views;            // 瀏覽數
    private String title;             // 文章標題
    
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getCoverImageUrl() {
		return coverImageUrl;
	}
	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getForumTypeName() {
		return forumTypeName;
	}
	public void setForumTypeName(String forumTypeName) {
		this.forumTypeName = forumTypeName;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Integer getViews() {
		return views;
	}
	public void setViews(Integer views) {
		this.views = views;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public MemberInfoArticlePostDTO() {
		super();
	}
	public MemberInfoArticlePostDTO(Integer userId, Integer articleId, String coverImageUrl, String content,
			String forumTypeName, Timestamp createTime, Integer views, String title) {
		super();
		this.userId = userId;
		this.articleId = articleId;
		this.coverImageUrl = coverImageUrl;
		this.content = content;
		this.forumTypeName = forumTypeName;
		this.createTime = createTime;
		this.views = views;
		this.title = title;
	}
	@Override
	public String toString() {
		return "MemberInfoArticlePostDTO [userId=" + userId + ", articleId=" + articleId + ", coverImageUrl="
				+ coverImageUrl + ", content=" + content + ", forumTypeName=" + forumTypeName + ", createTime="
				+ createTime + ", views=" + views + ", title=" + title + "]";
	}
	
    
    
}
