package com.tibafit.dto.article;

import java.sql.Timestamp;


import com.tibafit.model.article.Article;
import com.tibafit.model.article.Article.ArticleAttribute;


public class ArticleListDTO {

	private Integer articleId;
	private String title;
	private String coverImageUrl;
	private String content;
	private String name;
	private String forumTypeName;
	private Article.ArticleAttribute articleAttribute;
	private Boolean isPinned;
	private Boolean isDeleted;
	private Integer views;
	private Timestamp createTime;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getForumTypeName() {
		return forumTypeName;
	}
	public void setForumTypeName(String forumTypeName) {
		this.forumTypeName = forumTypeName;
	}
	public Article.ArticleAttribute getArticleAttribute() {
		return articleAttribute;
	}
	public void setArticleAttribute(Article.ArticleAttribute articleAttribute) {
		this.articleAttribute = articleAttribute;
	}
	public Boolean getIsPinned() {
		return isPinned;
	}
	public void setIsPinned(Boolean isPinned) {
		this.isPinned = isPinned;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public ArticleListDTO(Integer articleId, String title, String coverImageUrl, String content, String name,
			String forumTypeName, ArticleAttribute articleAttribute, Boolean isPinned, Boolean isDeleted, Integer views,
			Timestamp createTime, Timestamp updateTime) {
		super();
		this.articleId = articleId;
		this.title = title;
		this.coverImageUrl = coverImageUrl;
		this.content = content;
		this.name = name;
		this.forumTypeName = forumTypeName;
		this.articleAttribute = articleAttribute;
		this.isPinned = isPinned;
		this.isDeleted = isDeleted;
		this.views = views;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	public ArticleListDTO() {
		super();
	}
	

	
}
