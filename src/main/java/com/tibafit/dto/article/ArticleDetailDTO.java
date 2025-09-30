package com.tibafit.dto.article;

import java.sql.Timestamp;


import com.tibafit.model.article.Article;
import com.tibafit.model.article.Article.ArticleAttribute;

public class ArticleDetailDTO {
	private Integer articleId;
	private String title;
	private String content;
	private String coverImageUrl;
	private Article.ArticleAttribute articleAttribute;
	private String forumTypeName;
	private String userName;
	private Integer views;
	private Timestamp updateTime;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public ArticleAttribute getArticleAttribute() {
		return articleAttribute;
	}

	public void setArticleAttribute(ArticleAttribute articleAttribute) {
		this.articleAttribute = articleAttribute;
	}

	public String getForumTypeName() {
		return forumTypeName;
	}

	public void setForumTypeName(String forumTypeName) {
		this.forumTypeName = forumTypeName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public ArticleDetailDTO(Integer articleId, String title, String content, String coverImageUrl,
			ArticleAttribute articleAttribute, String forumTypeName, String userName, Integer views,
			Timestamp updateTime) {
		super();
		this.articleId = articleId;
		this.title = title;
		this.content = content;
		this.coverImageUrl = coverImageUrl;
		this.articleAttribute = articleAttribute;
		this.forumTypeName = forumTypeName;
		this.userName = userName;
		this.views = views;
		this.updateTime = updateTime;
	}

	public ArticleDetailDTO() {
		super();
	}

	@Override
	public String toString() {
		return "ArticleDetailDTO [articleId=" + articleId + ", title=" + title + ", content=" + content
				+ ", coverImageUrl=" + coverImageUrl + ", articleAttribute=" + articleAttribute + ", forumTypeName="
				+ forumTypeName + ", userName=" + userName + ", views=" + views + ", updateTime=" + updateTime + "]";
	}

	

	

	

}
