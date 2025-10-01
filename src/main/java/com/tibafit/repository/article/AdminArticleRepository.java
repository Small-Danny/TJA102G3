package com.tibafit.repository.article;

import com.tibafit.dto.article.ArticleListDTO;
import com.tibafit.model.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminArticleRepository extends JpaRepository<Article, Integer> {

    // 查詢文章列表 DTO
    @Query("""
        SELECT new com.tibafit.dto.article.ArticleListDTO(
            a.articleId,
            a.title,
            a.coverImageUrl,
            a.content,
            u.nickName,
            f.forumTypeName,
            a.articleAttribute,
            a.isPinned,
            a.isDeleted,
            a.views,
            a.createTime,
            a.updateTime
        )
        FROM Article a
        LEFT JOIN a.user u
        LEFT JOIN a.forumType f
        WHERE (:keyword IS NULL OR :keyword = '' 
               OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY a.createTime DESC
    """)
    List<ArticleListDTO> findAllArticlesDTO(@Param("keyword") String keyword);

    // 查詢單篇文章 DTO
    @Query("""
        SELECT new com.tibafit.dto.article.ArticleListDTO(
            a.articleId,
            a.title,
            a.coverImageUrl,
            a.content,
            u.nickName,
            f.forumTypeName,
            a.articleAttribute,
            a.isPinned,
            a.isDeleted,
            a.views,
            a.createTime,
            a.updateTime
        )
        FROM Article a
        LEFT JOIN a.user u
        LEFT JOIN a.forumType f
        WHERE a.articleId = :id
    """)
    Optional<ArticleListDTO> findArticleDTOById(@Param("id") Integer id);

    // 直接拿 Entity 更新欄位
    Optional<Article> findByArticleId(Integer id);
}
