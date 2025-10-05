package com.tibafit.controller.article;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tibafit.dto.article.ArticleCollectionDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;
import com.tibafit.service.article.ArticleCollectionService;
import com.tibafit.service.article.ArticleService;
import com.tibafit.repository.user.UserRepository;

@RestController
public class CollectionController {

	private final ArticleService articleService;
	private final ArticleCollectionService collectionService;
	private final UserRepository userRepository;
	private final List<String> defaultImages = Arrays.asList("/images/1.jpg", "/images/2.jpg", "/images/3.jpg",
			"/images/4.jpg", "/images/5.jpg");

	public CollectionController(ArticleService articleService, ArticleCollectionService collectionService,UserRepository userRepository) {
		this.articleService = articleService;
		this.collectionService = collectionService;
		this.userRepository = userRepository;

	}

	// 收藏 / 取消收藏功能
	@PostMapping("/api/posts/{articleId}/favorite/toggle")
	public Map<String, Object> toggleFavorite(@PathVariable Integer articleId) {
		Map<String, Object> response = new HashMap<>();
//        Map<String, Object> sessionData = sessionController.getSession();
//        boolean isLoggedIn = (boolean) sessionData.getOrDefault("isLoggedIn", false);
//        User currentUser = (User) sessionData.get("user");
		
		// ====================================取得登入資訊====================================
		// 從 SecurityContext 取得登入使用者
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();
		User currentUser = null;

		if (principal instanceof UserDetails) {
			String email = ((UserDetails) principal).getUsername();
			currentUser = userRepository.findByEmail(email).orElse(null);
		} else if (principal instanceof String) {
			currentUser = userRepository.findByEmail((String) principal).orElse(null);
		}

		boolean isLoggedIn = currentUser != null;
		System.out.println("currentUser: " + currentUser);
		// ====================================取得登入資訊====================================

		if (!isLoggedIn || currentUser == null) {
			response.put("success", false);
			response.put("message", "尚未登入");
			return response;
		}

		Article article = articleService.getArticleById(articleId);
		if (article == null) {
			response.put("success", false);
			response.put("message", "文章不存在");
			return response;
		}

		boolean result = collectionService.toggleArticleFavorite(currentUser, article);
		boolean isCollected = collectionService.isCollected(currentUser, article);

		response.put("success", result);
		response.put("isCollected", isCollected);
		response.put("message", isCollected ? "已加入收藏" : "已取消收藏");

		return response;
	}

	// 取得收藏狀態
	@GetMapping("/api/posts/{articleId}/favorite/status")
	public Map<String, Object> getFavoriteStatus(@PathVariable Integer articleId) {
//		Map<String, Object> sessionData = sessionController.getSession();
//		boolean isLoggedIn = (boolean) sessionData.getOrDefault("isLoggedIn", false);
//		User currentUser = (User) sessionData.get("user");

		// ====================================取得登入資訊====================================
				// 從 SecurityContext 取得登入使用者
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Object principal = auth.getPrincipal();
				User currentUser = null;

				if (principal instanceof UserDetails) {
					String email = ((UserDetails) principal).getUsername();
					currentUser = userRepository.findByEmail(email).orElse(null);
				} else if (principal instanceof String) {
					currentUser = userRepository.findByEmail((String) principal).orElse(null);
				}

				boolean isLoggedIn = currentUser != null;
				System.out.println("currentUser: " + currentUser);
		// ====================================取得登入資訊====================================

		Map<String, Object> response = new HashMap<>();
		response.put("isLoggedIn", isLoggedIn);

		if (isLoggedIn && currentUser != null) {
			Article article = articleService.getArticleById(articleId);
			boolean isCollected = collectionService.isCollected(currentUser, article);
			response.put("isCollected", isCollected);
		} else {
			response.put("isCollected", false);
		}

		return response;
	}

	// 取得目前使用者收藏的文章清單
	@GetMapping("/api/mycollection")
	public List<Map<String, Object>> getMyCollection() {
//		Map<String, Object> sessionData = sessionController.getSession();
//		boolean isLoggedIn = (boolean) sessionData.getOrDefault("isLoggedIn", false);
//		User currentUser = (User) sessionData.get("user");
		
	// ====================================取得登入資訊====================================
        //從 SecurityContext 取得登入使用者
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        User currentUser = null;

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            currentUser = userRepository.findByEmail(email).orElse(null);
        } else if (principal instanceof String) {
            currentUser = userRepository.findByEmail((String) principal).orElse(null);
        }

        boolean isLoggedIn = currentUser != null;
        System.out.println("currentUser: " + currentUser);
     // ====================================取得登入資訊====================================

		if (!isLoggedIn || currentUser == null) {
			return List.of();
		}

		List<ArticleCollectionDTO> favorites = collectionService.getUserFavorites(currentUser);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Random random = new Random();
		List<Map<String, Object>> result = new ArrayList<>();

		for (ArticleCollectionDTO fav : favorites) {
			String date = "";
			if (fav.getCreateTime() != null) {
				LocalDateTime time = fav.getCreateTime().toLocalDateTime();
				date = time.format(formatter);
			}

			String imageUrl = (fav.getCoverImageUrl() != null && !fav.getCoverImageUrl().isBlank())
					? fav.getCoverImageUrl()
					: defaultImages.get(random.nextInt(defaultImages.size()));

			Map<String, Object> map = new HashMap<>();
			map.put("articleId", fav.getId());
			map.put("title", fav.getTitle());
			map.put("authorName", fav.getAuthorName());
			map.put("email", fav.getEmail());
			map.put("createTime", date);
			map.put("coverImageUrl", imageUrl);

			result.add(map);
		}

		return result;
	}

	// 刪除收藏文章
	@DeleteMapping("/api/mycollection/{articleId}")
	public Map<String, Object> removeFavorite(@PathVariable Integer articleId) {
		Map<String, Object> response = new HashMap<>();

//		Map<String, Object> sessionData = sessionController.getSession();
//		boolean isLoggedIn = (boolean) sessionData.getOrDefault("isLoggedIn", false);
//		User currentUser = (User) sessionData.get("user");
		
	// ====================================取得登入資訊====================================
        //從 SecurityContext 取得登入使用者
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        User currentUser = null;

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            currentUser = userRepository.findByEmail(email).orElse(null);
        } else if (principal instanceof String) {
            currentUser = userRepository.findByEmail((String) principal).orElse(null);
        }

        boolean isLoggedIn = currentUser != null;
        System.out.println("currentUser: " + currentUser);
     // ====================================取得登入資訊====================================

		if (!isLoggedIn || currentUser == null) {
			response.put("success", false);
			response.put("message", "尚未登入");
			return response;
		}

		Article article = articleService.getArticleById(articleId);
		if (article == null) {
			response.put("success", false);
			response.put("message", "文章不存在");
			return response;
		}

		boolean result = collectionService.removeFavorite(currentUser, article);
		response.put("success", result);
		response.put("message", result ? "已取消收藏" : "取消收藏失敗");

		return response;
	}
}
