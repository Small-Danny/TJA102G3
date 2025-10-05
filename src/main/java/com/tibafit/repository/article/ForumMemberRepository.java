package com.tibafit.repository.article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;

@Repository
public interface ForumMemberRepository extends JpaRepository<User, Integer> {
	// 根據 userId 查詢 user所有資訊
	User findByUserId(Integer userId);

	// 查詢指定使用者的文章（未刪除），按發布時間倒序
	@Query("SELECT a FROM Article a WHERE a.user.userId = :userId AND a.isDeleted = false ORDER BY a.createTime DESC")
	List<Article> findArticlesByUserId(@Param("userId") Integer userId);

	// 查詢該會員每個分類中瀏覽數最高的文章（未刪除）
	@Query("""
			SELECT a FROM Article a
			WHERE a.user.userId = :userId
			AND a.isDeleted = false
			AND a.views = (
			    SELECT MAX(a2.views) FROM Article a2
			    WHERE a2.user.userId = :userId
			    AND a2.isDeleted = false
			    AND a2.forumType.forumTypeId = a.forumType.forumTypeId
			)
			ORDER BY a.createTime DESC
			""")
	List<Article> findFeaturedArticlesByUserId(@Param("userId") Integer userId);

}
