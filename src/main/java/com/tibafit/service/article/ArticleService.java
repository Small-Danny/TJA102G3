package com.tibafit.service.article;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tibafit.dto.article.ArticleDetailDTO;
import com.tibafit.dto.article.ArticlePageDTO;
import com.tibafit.dto.article.ArticleQueryDTO;
import com.tibafit.dto.article.ArticleSibebarInfoDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;
import com.tibafit.repository.article.ArticleCollectionRepository;
import com.tibafit.repository.article.ArticleRepository;

@Service
public class ArticleService {

	private final ArticleRepository articleRepository;

	// 建構子注入
	public ArticleService(ArticleRepository articleRepository,
			ArticleCollectionRepository articleCollectionRepository) {
		this.articleRepository = articleRepository;
	}

	public List<Object[]> getAllArticlesWithAuthor() {
		return articleRepository.findAllArticlesWithAuthor();
	}

	public Map<String, List<ArticleSibebarInfoDTO>> getSidebarData(User user) {
		Map<String, List<ArticleSibebarInfoDTO>> sidebarMap = new HashMap<>();

		// 最新五篇文章
		List<ArticleSibebarInfoDTO> latestArticles = articleRepository.findLatestArticles(PageRequest.of(0, 5));
		sidebarMap.put("latestArticles", latestArticles);

		// 最新五筆收藏文章（如果 user 不為 null）
		List<ArticleSibebarInfoDTO> latestFavorites = List.of();
		if (user != null) {
			latestFavorites = articleRepository.findLatestUserFavorites(user, PageRequest.of(0, 5));
		}
		sidebarMap.put("latestFavorites", latestFavorites);

		return sidebarMap;
	}

	// 模糊查詢+分頁
	public Map<String, Object> getArticlesPageMap(ArticleQueryDTO articleQueryDTO) {
		Integer forumTypeId = articleQueryDTO.getType();
		String keyword = articleQueryDTO.getKeyWords();
		int pageNum = articleQueryDTO.getPageNum() != null ? articleQueryDTO.getPageNum() : 0;
		int pageSize = articleQueryDTO.getPageSize() != null ? articleQueryDTO.getPageSize() : 10;

		Pageable pageable = PageRequest.of(pageNum, pageSize);

		// 查詢分頁資料
		List<ArticlePageDTO> pageData = articleRepository.findPageList(forumTypeId, keyword, pageable);

		// 隨機補圖片
		String[] defaults = { "/images/1.jpg", "/images/2.jpg", "/images/3.jpg","/images/4.jpg","/images/5.jpg" };
		java.util.Random random = new java.util.Random();

		for (ArticlePageDTO dto : pageData) {
			if (dto.getCoverImageUrl() == null || dto.getCoverImageUrl().isBlank()) {
				dto.setCoverImageUrl(defaults[random.nextInt(defaults.length)]);
			}
		}

		// 查詢總數
		long totalSize = articleRepository.findPageCount(forumTypeId, keyword);
		long totalPage = (totalSize + pageSize - 1) / pageSize;

		// 包裝結果
		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("pageNum", pageNum);
		pageInfo.put("pageSize", pageSize);
		pageInfo.put("totalSize", totalSize);
		pageInfo.put("totalPage", totalPage);
		pageInfo.put("pageData", pageData);

		return pageInfo;
	}

	// 單篇文章明細查詢
	@Transactional
	public ArticleDetailDTO getArticleDetailById(Integer articleId) {
		ArticleDetailDTO dto = articleRepository.findArticleDetailById(articleId);
		// 先增加瀏覽量
		articleRepository.incrementViews(articleId);
		// 同步更新 DTO 的 views
		dto.setViews(dto.getViews() + 1);
		if (dto != null) {
			// 預設圖片清單
			String[] defaults = { "/images/1.jpg", "/images/2.jpg", "/images/3.jpg", "/images/4.jpg", "/images/5.jpg" };
			java.util.Random random = new java.util.Random();

			// 如果封面圖為空，補隨機預設圖
			if (dto.getCoverImageUrl() == null || dto.getCoverImageUrl().isBlank()) {
				dto.setCoverImageUrl(defaults[random.nextInt(defaults.length)]);
			}
		}
		String authorAvatar = dto.getProfilePicture();
		 if(authorAvatar == null || authorAvatar.isBlank()) {
		        dto.setProfilePicture("/frontend-template/assets/images/profile-picture-default.jpg");
		    } else if(!authorAvatar.startsWith("http://") && !authorAvatar.startsWith("https://") && !authorAvatar.startsWith("/")) {
		        dto.setProfilePicture("/" + authorAvatar);
		    }
		return dto;
	}

	public Article getArticleById(Integer articleId) {
		return articleRepository.findById(articleId).orElse(null);
	}

	

}
