package com.tibafit.repository.article;

import java.util.List;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tibafit.dto.article.ArticleCollectionDTO;
import com.tibafit.dto.article.ArticleDetailDTO;
import com.tibafit.dto.article.ArticlePageDTO;
import com.tibafit.dto.article.ArticleSibebarInfoDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

	// 自訂查詢，只選擇發布日期、標題、作者名稱
	@Query("""
			    SELECT a.createTime, a.title, a.user.name
			    FROM Article a
			    WHERE a.isDeleted = false
			    ORDER BY a.createTime DESC
			""")
	List<Object[]> findAllArticlesWithAuthor();

	// 側邊欄最新文章查詢 (前5)
	@Query("""
			    SELECT new com.tibafit.dto.article.ArticleSibebarInfoDTO(
			        a.articleId, a.coverImageUrl, a.title, a.user.nickName, a.user.email,a.createTime
			    )
			    FROM Article a
			    WHERE a.isDeleted = false
			    ORDER BY a.createTime DESC
			""")
	List<ArticleSibebarInfoDTO> findLatestArticles(Pageable pageable);

	// 查詢文章列表 (分頁，可用於文章列表頁)
	@Query("""
			    SELECT new com.tibafit.dto.article.ArticlePageDTO(
			        a.articleId, a.title, a.user.nickName, a.user.email,a.views, a.createTime,
			        a.forumType.forumTypeName, a.coverImageUrl
			    )
			    FROM Article a
			    WHERE a.isDeleted = false
			    AND (:forumTypeId IS NULL OR a.forumType.forumTypeId = :forumTypeId)
			    AND (:keyword IS NULL OR a.title LIKE %:keyword%)
			    ORDER BY a.createTime DESC
			""")
	List<ArticlePageDTO> findPageList(@Param("forumTypeId") Integer forumTypeId, @Param("keyword") String keyword,
			Pageable pageable);

	// 查詢文章總數 (可用於分頁)
	@Query("""
			    SELECT COUNT(a)
			    FROM Article a
			    WHERE a.isDeleted = false
			    AND (:forumTypeId IS NULL OR a.forumType.forumTypeId = :forumTypeId)
			    AND (:keyword IS NULL OR a.title LIKE %:keyword%)
			""")
	long findPageCount(@Param("forumTypeId") Integer forumTypeId, @Param("keyword") String keyword);

	@Query("""
			    SELECT new com.tibafit.dto.article.ArticleDetailDTO(
			    	u.userId,
			    	u.profilePicture,
			    	u.email,
			    	u.nickName,
			        a.articleId,
			        a.title,
			        a.content,
			        a.coverImageUrl,
			        a.articleAttribute,
			        f.forumTypeName,
			        u.name,
			        a.views,
			        a.updateTime
			    )
			    FROM Article a
			    JOIN a.forumType f
			    JOIN a.user u
			    WHERE a.articleId = :articleId
			""")
	ArticleDetailDTO findArticleDetailById(@Param("articleId") Integer articleId);

	//瀏覽量增加
	@Modifying
	@Query("UPDATE Article a SET a.views = a.views + 1 WHERE a.articleId = :articleId")
	int incrementViews(@Param("articleId") Integer articleId);
	
	
	// 側邊欄最新收藏文章查詢（前5）
	@Query("""
	    SELECT new com.tibafit.dto.article.ArticleSibebarInfoDTO(
	        ac.article.articleId,
	        ac.article.coverImageUrl,
	        ac.article.title,
	        ac.article.user.nickName,
	        ac.article.user.email,
	        ac.article.createTime
	    )
	    FROM ArticleCollection ac
	    WHERE ac.user = :user
	    AND ac.collectionStatus = 1
	    AND ac.article.isDeleted = false
	    ORDER BY ac.collectTime DESC
	""")
	List<ArticleSibebarInfoDTO> findLatestUserFavorites(@Param("user") User user, Pageable pageable);



	// 取得某個使用者的文章列表（不包含已刪除）
	List<Article> findByUserAndIsDeleted(User user, boolean b);
    

}
