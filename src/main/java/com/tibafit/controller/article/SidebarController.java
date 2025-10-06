//package com.tibafit.controller.article;

//
//import org.springframework.web.bind.annotation.GetMapping;
//
//
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import com.tibafit.dto.article.ArticleDetailDTO;
//import com.tibafit.dto.article.ArticleQueryDTO;
//import com.tibafit.dto.article.ArticleSibebarInfoDTO;
//import com.tibafit.model.article.ForumType;
//import com.tibafit.model.user.User;
//import com.tibafit.service.article.ArticleService;
//import com.tibafit.service.article.ForumTypeService;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@RestController
//public class SidebarController {
//
//	private final ForumTypeService forumTypeService;
//	private final ArticleService articleService;
//
//	private final List<String> defaultImages = Arrays.asList("/images/1.jpg", "/images/2.jpg", "/images/3.jpg",
//			"/images/4.jpg", "/images/5.jpg");
//
//	public SidebarController(ForumTypeService forumTypeService, ArticleService articleService) {
//		this.forumTypeService = forumTypeService;
//		this.articleService = articleService;
//
//	}
//
//	// 側邊欄資料
//	@GetMapping("/api/sidebar")
//	public Map<String, Object> getSidebarData() {
//		Map<String, Object> response = new HashMap<>();
//
//		Map<String, Object> sessionData = sessionController.getSession();
//		boolean isLoggedIn = (boolean) sessionData.getOrDefault("isLoggedIn", false);
//		User currentUser = (User) sessionData.get("user");
//
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		Random random = new Random();
//
//		// 取得側邊欄資料（最新文章 + 最新收藏）
//		Map<String, List<ArticleSibebarInfoDTO>> sidebarData = articleService.getSidebarData(currentUser);
//
//		// 最新文章
//		List<Map<String, Object>> recentPosts = new ArrayList<>();
//		for (ArticleSibebarInfoDTO article : sidebarData.get("latestArticles")) {
//			LocalDateTime createTime = article.getCreateTime().toLocalDateTime();
//			String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
//					? article.getCoverImageUrl()
//					: defaultImages.get(random.nextInt(defaultImages.size()));
//
//			recentPosts.add(Map.of("id", article.getId(), "author", article.getAuthorName(), "title",
//					article.getTitle(), "date", createTime.format(formatter), "imageUrl", imageUrl));
//		}
//		response.put("recentPosts", recentPosts);
//
//		// 最新收藏文章（前5筆）
//		List<Map<String, Object>> latestFavorites = new ArrayList<>();
//		if (isLoggedIn && currentUser != null) {
//			for (ArticleSibebarInfoDTO article : sidebarData.get("latestFavorites")) {
//				LocalDateTime collectTime = article.getCreateTime().toLocalDateTime();
//				String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
//						? article.getCoverImageUrl()
//						: defaultImages.get(random.nextInt(defaultImages.size()));
//
//				latestFavorites.add(Map.of("id", article.getId(), "author", article.getAuthorName(), "title",
//						article.getTitle(), "date", collectTime.format(formatter), "imageUrl", imageUrl));
//			}
//		}
//		response.put("latestFavorites", latestFavorites);
//
//		// 分類
//		List<ForumType> forumTypes = forumTypeService.getAllForumTypes();
//		List<Map<String, Object>> categories = new ArrayList<>();
//		for (ForumType type : forumTypes) {
//			categories.add(Map.of("id", type.getForumTypeId(), "name", type.getForumTypeName()));
//		}
//		response.put("categories", categories);
//
//		// Meta links
//		response.put("metaLinks",
//				List.of(Map.of("name", "登入", "url", "/login"), Map.of("name", "註冊", "url", "/register")));
//
//		return response;
//	}
//
//	// 文章分頁查詢
//	@GetMapping("/api/posts")
//	public Map<String, Object> getPosts(@RequestParam(value = "type", required = false) Integer type,
//			@RequestParam(value = "keyword", required = false) String keyword,
//			@RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
//			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
//
//		ArticleQueryDTO dto = new ArticleQueryDTO();
//		dto.setType(type);
//		dto.setKeyWords(keyword);
//		dto.setPageNum(pageNum);
//		dto.setPageSize(pageSize);
//
//		return articleService.getArticlesPageMap(dto);
//	}
//
//	// 文章詳細頁
//	@GetMapping("/api/posts/{articleId}")
//	public ArticleDetailDTO getArticleDetail(@PathVariable Integer articleId) {
//		return articleService.getArticleDetailById(articleId);
//	}
//}
//
//package com.tibafit.controller.article;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//
//import com.tibafit.dto.article.ArticleDetailDTO;
//import com.tibafit.dto.article.ArticleQueryDTO;
//import com.tibafit.dto.article.ArticleSibebarInfoDTO;
//import com.tibafit.model.article.ForumType;
//import com.tibafit.model.user.User;
//import com.tibafit.service.article.ArticleService;
//import com.tibafit.service.article.ForumTypeService;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@RestController
//public class SidebarController {
//
//    private final ForumTypeService forumTypeService;
//    private final ArticleService articleService;
//
//    private final List<String> defaultImages = Arrays.asList(
//            "/images/1.jpg", "/images/2.jpg", "/images/3.jpg",
//            "/images/4.jpg", "/images/5.jpg"
//    );
//
//    public SidebarController(ForumTypeService forumTypeService, ArticleService articleService) {
//        this.forumTypeService = forumTypeService;
//        this.articleService = articleService;
//    }
//
//    // 側邊欄資料
//    @GetMapping("/api/sidebar")
//    public Map<String, Object> getSidebarData(@AuthenticationPrincipal User currentUser) {
//        Map<String, Object> response = new HashMap<>();
//        boolean isLoggedIn = (currentUser != null);
//        if(currentUser != null) {
//            System.out.println("id = " + currentUser.getUserId());
//        }
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        Random random = new Random();
//
//        // 取得側邊欄資料（最新文章 + 最新收藏）
//        Map<String, List<ArticleSibebarInfoDTO>> sidebarData = articleService.getSidebarData(currentUser);
//
//        // 最新文章
//        List<Map<String, Object>> recentPosts = new ArrayList<>();
//        for (ArticleSibebarInfoDTO article : sidebarData.getOrDefault("latestArticles", List.of())) {
//            LocalDateTime createTime = article.getCreateTime().toLocalDateTime();
//            String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
//                    ? article.getCoverImageUrl()
//                    : defaultImages.get(random.nextInt(defaultImages.size()));
//
//            recentPosts.add(Map.of(
//                    "id", article.getId(),
//                    "author", article.getAuthorName(),
//                    "title", article.getTitle(),
//                    "date", createTime.format(formatter),
//                    "imageUrl", imageUrl
//            ));
//        }
//        response.put("recentPosts", recentPosts);
//
//        // 最新收藏文章（前5筆）
//        List<Map<String, Object>> latestFavorites = new ArrayList<>();
//        if (isLoggedIn) {
//            for (ArticleSibebarInfoDTO article : sidebarData.getOrDefault("latestFavorites", List.of())) {
//                LocalDateTime collectTime = article.getCreateTime().toLocalDateTime();
//                String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
//                        ? article.getCoverImageUrl()
//                        : defaultImages.get(random.nextInt(defaultImages.size()));
//
//                latestFavorites.add(Map.of(
//                        "id", article.getId(),
//                        "author", article.getAuthorName(),
//                        "title", article.getTitle(),
//                        "date", collectTime.format(formatter),
//                        "imageUrl", imageUrl
//                ));
//            }
//        }
//        response.put("latestFavorites", latestFavorites);
//
//        // 分類
//        List<ForumType> forumTypes = forumTypeService.getAllForumTypes();
//        List<Map<String, Object>> categories = new ArrayList<>();
//        for (ForumType type : forumTypes) {
//            categories.add(Map.of(
//                    "id", type.getForumTypeId(),
//                    "name", type.getForumTypeName()
//            ));
//        }
//        response.put("categories", categories);
//
//        // Meta links
//        response.put("metaLinks", List.of(
//                Map.of("name", "登入", "url", "/login"),
//                Map.of("name", "註冊", "url", "/register")
//        ));
//
//        // 🔹 新增 currentUser 回傳
//        response.put("currentUser", currentUser);
//
//        return response;
//    }
//
//    // 文章分頁查詢
//    @GetMapping("/api/posts")
//    public Map<String, Object> getPosts(@RequestParam(value = "type", required = false) Integer type,
//                                        @RequestParam(value = "keyword", required = false) String keyword,
//                                        @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
//                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
//
//        ArticleQueryDTO dto = new ArticleQueryDTO();
//        dto.setType(type);
//        dto.setKeyWords(keyword);
//        dto.setPageNum(pageNum);
//        dto.setPageSize(pageSize);
//
//        return articleService.getArticlesPageMap(dto);
//    }
//
//    // 文章詳細頁
//    @GetMapping("/api/posts/{articleId}")
//    public ArticleDetailDTO getArticleDetail(@PathVariable Integer articleId) {
//        return articleService.getArticleDetailById(articleId);
//    }
//}
package com.tibafit.controller.article;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.tibafit.dto.article.ArticleDetailDTO;
import com.tibafit.dto.article.ArticleQueryDTO;
import com.tibafit.dto.article.ArticleSibebarInfoDTO;
import com.tibafit.model.article.ForumType;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.article.ArticleService;
import com.tibafit.service.article.ForumTypeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class SidebarController {

    private final ForumTypeService forumTypeService;
    private final ArticleService articleService;
    private final UserRepository userRepository;

    private final List<String> defaultImages = Arrays.asList(
            "/images/1.jpg", "/images/2.jpg", "/images/3.jpg",
            "/images/4.jpg", "/images/5.jpg"
    );

    public SidebarController(ForumTypeService forumTypeService,
                             ArticleService articleService,
                             UserRepository userRepository) {
        this.forumTypeService = forumTypeService;
        this.articleService = articleService;
        this.userRepository = userRepository;
    }

    // ===== 側邊欄資料 =====
    @GetMapping("/api/sidebar")
    public Map<String, Object> getSidebarData() {
        System.out.println(">>> Sidebar API 被呼叫 <<<");

        Map<String, Object> response = new HashMap<>();
//<取得登入資訊>
        //從 SecurityContext 取得登入使用者
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = auth.getPrincipal();
//        User currentUser = null;
//
//        if (principal instanceof UserDetails) {
//            String email = ((UserDetails) principal).getUsername();
//            currentUser = userRepository.findByEmail(email).orElse(null);
//        } else if (principal instanceof String) {
//            currentUser = userRepository.findByEmail((String) principal).orElse(null);
//        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                currentUser = userRepository.findByEmail(email).orElse(null);
            } else if (principal instanceof String str && !"anonymousUser".equals(str)) {
                currentUser = userRepository.findByEmail(str).orElse(null);
            }
        }



        boolean isLoggedIn = currentUser != null;
        System.out.println("currentUser: " + currentUser);
//<取得登入資訊
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Random random = new Random();

        // 取得側邊欄資料（最新文章 + 最新收藏）
        Map<String, List<ArticleSibebarInfoDTO>> sidebarData = articleService.getSidebarData(currentUser);

        // 最新文章
        List<Map<String, Object>> recentPosts = new ArrayList<>();
        for (ArticleSibebarInfoDTO article : sidebarData.getOrDefault("latestArticles", List.of())) {
            LocalDateTime createTime = article.getCreateTime().toLocalDateTime();
            String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
                    ? article.getCoverImageUrl()
                    : defaultImages.get(random.nextInt(defaultImages.size()));

            recentPosts.add(Map.of(
                    "id", article.getId(),
                    "author", article.getAuthorName(),
                    "title", article.getTitle(),
                    "date", createTime.format(formatter),
                    "email", article.getEmail(),
                    "imageUrl", imageUrl
            ));
        }
        response.put("recentPosts", recentPosts);

        // 最新收藏文章
        List<Map<String, Object>> latestFavorites = new ArrayList<>();
        if (isLoggedIn) {
            for (ArticleSibebarInfoDTO article : sidebarData.getOrDefault("latestFavorites", List.of())) {
                LocalDateTime collectTime = article.getCreateTime().toLocalDateTime();
                String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
                        ? article.getCoverImageUrl()
                        : defaultImages.get(random.nextInt(defaultImages.size()));

                latestFavorites.add(Map.of(
                        "id", article.getId(),
                        "author", article.getAuthorName(),
                        "title", article.getTitle(),
                        "date", collectTime.format(formatter),
                        "imageUrl", imageUrl,
                        "email",article.getEmail()
                ));
            }
        }
        response.put("latestFavorites", latestFavorites);

        // 分類
        List<ForumType> forumTypes = forumTypeService.getAllForumTypes();
        List<Map<String, Object>> categories = new ArrayList<>();
        for (ForumType type : forumTypes) {
            categories.add(Map.of(
                    "id", type.getForumTypeId(),
                    "name", type.getForumTypeName()
            ));
        }
        response.put("categories", categories);

     // Meta links
        List<Map<String,Object>> metaLinks = new ArrayList<>();
        if(currentUser != null){
            // 已登入 → 顯示登出
            metaLinks.add(Map.of("name", "登出", "url", "/logout"));
        } else {
            // 未登入 → 顯示登入 / 註冊
            metaLinks.add(Map.of("name", "登入", "url", "/login"));
            metaLinks.add(Map.of("name", "註冊", "url", "/register"));
        }
        response.put("metaLinks", metaLinks);


        response.put("currentUser", currentUser); // 回傳登入使用者資訊

        return response;
    }

    // ===== 文章分頁查詢 =====
    @GetMapping("/api/posts")
    public Map<String, Object> getPosts(@RequestParam(value = "type", required = false) Integer type,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        ArticleQueryDTO dto = new ArticleQueryDTO();
        dto.setType(type);
        dto.setKeyWords(keyword);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);

        return articleService.getArticlesPageMap(dto);
    }

    // ===== 文章詳細頁 =====
    @GetMapping("/api/posts/{articleId}")
    public ArticleDetailDTO getArticleDetail(@PathVariable Integer articleId) {
        return articleService.getArticleDetailById(articleId);
    }
}



