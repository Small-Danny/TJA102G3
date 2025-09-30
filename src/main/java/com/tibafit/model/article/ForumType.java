package com.tibafit.model.article;

import jakarta.persistence.*;


import java.util.List;

import com.tibafit.model.article.Article;



@Entity
@Table(name = "forum_type")
public class ForumType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_type_id")
    private Integer forumTypeId;

    @Column(name = "forum_type_name", nullable = false, length = 50, unique = true)
    private String forumTypeName;

    // 反向關聯 Article（選擇性，可雙向）
    @OneToMany(mappedBy = "forumType", fetch = FetchType.LAZY)
    private List<Article> articles;

    public ForumType() {
    }

    public ForumType(Integer forumTypeId, String forumTypeName) {
        this.forumTypeId = forumTypeId;
        this.forumTypeName = forumTypeName;
    }

    public Integer getForumTypeId() {
        return forumTypeId;
    }

    public void setForumTypeId(Integer forumTypeId) {
        this.forumTypeId = forumTypeId;
    }

    public String getForumTypeName() {
        return forumTypeName;
    }

    public void setForumTypeName(String forumTypeName) {
        this.forumTypeName = forumTypeName;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "ForumType [forumTypeId=" + forumTypeId + ", forumTypeName=" + forumTypeName + "]";
    }
}
