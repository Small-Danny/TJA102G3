package com.tibafit.dto.article;

import java.sql.Timestamp;

public class ArticlePageDTO {

	// 文章ID
	private Integer articleId;

	// 標題
	private String title;

	// 作者名稱
	private String authorName;

	// 作者email
	private String email;

	// 瀏覽量
	private Integer views;

	// 建立日期
	private Timestamp createTime;

	// 文章分類名稱
	private String forumName;

	// 封面圖片
	private String coverImageUrl;

	// ======== Constructor ========
	public ArticlePageDTO() {
	}

	public ArticlePageDTO(Integer articleId, String title, String authorName, String email, Integer views,
			Timestamp createTime, String forumName, String coverImageUrl) {
		super();
		this.articleId = articleId;
		this.title = title;
		this.authorName = authorName;
		this.email = email;
		this.views = views;
		this.createTime = createTime;
		this.forumName = forumName;
		this.coverImageUrl = coverImageUrl;
	}

	// ======== Getter / Setter ========

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getArticleId() {
		return articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	@Override
	public String toString() {
		return "ArticlePageDTO [articleId=" + articleId + ", title=" + title + ", authorName=" + authorName + ", email="
				+ email + ", views=" + views + ", createTime=" + createTime + ", forumName=" + forumName
				+ ", coverImageUrl=" + coverImageUrl + "]";
	}

}
