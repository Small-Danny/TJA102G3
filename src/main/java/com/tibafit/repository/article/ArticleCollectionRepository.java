package com.tibafit.repository.article;

import com.tibafit.dto.article.ArticleCollectionDTO;

import com.tibafit.model.article.Article;
import com.tibafit.model.article.ArticleCollection;
import com.tibafit.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleCollectionRepository extends JpaRepository<ArticleCollection, Integer> {

	// 查詢單篇文章是否被指定使用者收藏
	ArticleCollection findByUserAndArticle(User user, Article article);

	// 查詢使用者收藏的文章列表
	@Query("""
				SELECT new com.tibafit.dto.article.ArticleCollectionDTO(
							    ac.article.articleId,
							    ac.article.coverImageUrl,
							    ac.article.title,
							    ac.article.user.name,
							    ac.article.createTime,
							    ac.article.isDeleted
							)
							FROM ArticleCollection ac
							WHERE ac.user = :user
							AND ac.collectionStatus = 1
							AND ac.article.isDeleted = false

							ORDER BY ac.collectTime DESC

										""")
	List<ArticleCollectionDTO> findUserFavorites(@Param("user") User user);

	// 刪除指定使用者收藏的文章
	void deleteByUserAndArticle(User user, Article article);
}
