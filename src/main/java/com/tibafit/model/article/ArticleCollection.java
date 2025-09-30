package com.tibafit.model.article;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;


@Entity
@Table(name = "article_collection",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "article_id"})})
public class ArticleCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Integer collectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // 使用 LocalDateTime + @PrePersist 自動填時間
    @Column(name = "collect_time", nullable = false, updatable = false)
    private LocalDateTime collectTime;

    @Column(name = "collection_status", nullable = false)
    private Integer collectionStatus = 1; // 1=收藏，0=取消收藏

    public ArticleCollection() {}

    public ArticleCollection(User user, Article article) {
        this.user = user;
        this.article = article;
    }

    @PrePersist
    public void prePersist() {
        if (collectTime == null) {
            collectTime = LocalDateTime.now();
        }
    }

    // getter / setter
    public Integer getCollectionId() { return collectionId; }
    public void setCollectionId(Integer collectionId) { this.collectionId = collectionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public LocalDateTime getCollectTime() { return collectTime; }
    public void setCollectTime(LocalDateTime collectTime) { this.collectTime = collectTime; }

    public Integer getCollectionStatus() { return collectionStatus; }
    public void setCollectionStatus(Integer collectionStatus) { this.collectionStatus = collectionStatus; }

    @Override
    public String toString() {
        return "ArticleCollection [collectionId=" + collectionId + ", user=" + user + ", article=" + article
                + ", collectTime=" + collectTime + ", collectionStatus=" + collectionStatus + "]";
    }
}
