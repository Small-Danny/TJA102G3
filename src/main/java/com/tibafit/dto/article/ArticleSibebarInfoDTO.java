package com.tibafit.dto.article;

import java.sql.Timestamp;

public class ArticleSibebarInfoDTO {

	private Integer id;
	private String coverImageUrl;
	private String title;
	private String authorName;
	private Timestamp createTime;

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

	@Override
	public String toString() {
		return "ArticleInfoDTO [id=" + id + ", coverImageUrl=" + coverImageUrl + ", title=" + title + ", authorName="
				+ authorName + ", createTime=" + createTime + "]";
	}

	public ArticleSibebarInfoDTO(Integer id, String coverImageUrl, String title, String authorName, Timestamp createTime) {
		super();
		this.id = id;
		this.coverImageUrl = coverImageUrl;
		this.title = title;
		this.authorName = authorName;
		this.createTime = createTime;
	}

	public ArticleSibebarInfoDTO() {
		super();
	}

}
