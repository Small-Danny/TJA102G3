package com.tibafit.dto.article;

import java.sql.Timestamp;

public class ArticleCollectionDTO {

	private Integer id;
	private String coverImageUrl;
	private String title;
	private String authorName;
	private String email;
	private Timestamp createTime;
	private Boolean isDeleted;

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	

	@Override
	public String toString() {
		return "ArticleCollectionDTO [id=" + id + ", coverImageUrl=" + coverImageUrl + ", title=" + title
				+ ", authorName=" + authorName + ", email=" + email + ", createTime=" + createTime + ", isDeleted="
				+ isDeleted + "]";
	}

	public ArticleCollectionDTO(Integer id, String coverImageUrl, String title, String authorName, String email,
			Timestamp createTime, Boolean isDeleted) {
		super();
		this.id = id;
		this.coverImageUrl = coverImageUrl;
		this.title = title;
		this.authorName = authorName;
		this.email = email;
		this.createTime = createTime;
		this.isDeleted = isDeleted;
	}

	public ArticleCollectionDTO() {
		super();
	}

	


}