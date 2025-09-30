package com.tibafit.dto.article;


/**
 * Article 對應 DTO，用於新增/修改文章
 */
public class ArticleFormDTO {

 

	public ArticleFormDTO(Integer articleId, Integer userId, Integer forumTypeId, String title, String content,
			String coverImageUrl) {
		super();
		this.articleId = articleId;
		this.userId = userId;
		this.forumTypeId = forumTypeId;
		this.title = title;
		this.content = content;
		this.coverImageUrl = coverImageUrl;
	}

	// 修改文章時使用，新增時可不填
    private Integer articleId;

    // 對應 Article.user
    private Integer userId;

    // 對應 Article.forumType
    private Integer forumTypeId;

    // 對應 Article.title
    private String title;

    // 對應 Article.content
    private String content;

    // 對應 Article.coverImageUrl，前端可傳 URL 或 null
    private String coverImageUrl;

    public ArticleFormDTO() {}

    // getter / setter
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

    public Integer getForumTypeId() {
        return forumTypeId;
    }

    public void setForumTypeId(Integer forumTypeId) {
        this.forumTypeId = forumTypeId;
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

    @Override
    public String toString() {
        return "ArticleFormDTO{" +
                "articleId=" + articleId +
                ", userId=" + userId +
                ", forumTypeId=" + forumTypeId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                '}';
    }
}
