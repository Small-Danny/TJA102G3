package com.tibafit.model.article;

import jakarta.persistence.*;
import java.sql.Timestamp;
import com.tibafit.model.user.User;




@Entity
@Table(name = "article")
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "article_id")
	private Integer articleId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // 對應 users 表

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forum_type_id", nullable = false)
	private ForumType forumType; // 對應 forum_type 表

	@Column(nullable = false, length = 200)
	private String title;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String content;


	@Column(name = "cover_image_url", length = 255)
	private String coverImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "article_attribute", nullable = false, columnDefinition = "ENUM('一般文章','公告')")
	private ArticleAttribute articleAttribute; // '一般文章' 或 '公告'

	@Column(name = "is_pinned", nullable = false)
	private Boolean isPinned = false;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "create_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createTime;

	@Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp updateTime;

	@Column(name = "views", nullable = false)
	private Integer views = 0;

	public Article() {
	}

	// getter / setter
	public Integer getArticleId() {
		return articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ForumType getForumType() {
		return forumType;
	}

	public void setForumType(ForumType forumType) {
		this.forumType = forumType;
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

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "Article [articleId=" + articleId + ", user=" + user + ", forumType=" + forumType + ", title=" + title
				+ ", content=" + content + ", coverImageUrl=" + coverImageUrl + ", articleAttribute=" + articleAttribute
				+ ", isPinned=" + isPinned + ", isDeleted=" + isDeleted + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", views=" + views + "]";
	}

	public Article(User user, ForumType forumType, String title, String content, ArticleAttribute articleAttribute,
			Integer views) {
		this.user = user;
		this.forumType = forumType;
		this.title = title;
		this.content = content;
		this.articleAttribute = articleAttribute;
		this.views = views;
	}

	// Enum 對應資料表 enum
	public enum ArticleAttribute {
		一般文章, 公告
	}
}
