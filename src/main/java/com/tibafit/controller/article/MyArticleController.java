package com.tibafit.controller.article;

import com.tibafit.dto.article.ArticleFormDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;
import com.tibafit.service.article.ForumTypeService;
import com.tibafit.service.article.MyArticleService;
import com.tibafit.repository.user.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/myarticles")
public class MyArticleController {

    private final MyArticleService myArticleService;
    private final ForumTypeService forumTypeService;
    private final UserRepository userRepository;

    public MyArticleController(MyArticleService myArticleService,
                               ForumTypeService forumTypeService,
                               UserRepository userRepository) {
        this.myArticleService = myArticleService;
        this.forumTypeService = forumTypeService;
        this.userRepository = userRepository;
    }

    /** 捕捉單檔或整個請求過大 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public Map<String, Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", "檔案大小超過限制（最大 10MB）");
        return resp;
    }

    /** 取得登入使用者的文章列表 */
    @GetMapping
    public List<Map<String, Object>> getMyArticles() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        return myArticleService.getArticlesByUser(currentUser);
    }

    /** 刪除(軟刪除)文章 */
    @DeleteMapping("/{articleId}")
    public Map<String, Object> deleteArticle(@PathVariable Integer articleId) {
        Map<String, Object> resp = new HashMap<>();
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            resp.put("success", false);
            resp.put("message", "尚未登入");
            return resp;
        }

        boolean result = myArticleService.deleteArticle(articleId, currentUser);
        resp.put("success", result);
        resp.put("message", result ? "刪除成功" : "刪除失敗");
        return resp;
    }

    /** 新增文章 */
    @PostMapping
    public Map<String, Object> createArticle(@RequestParam("title") String title,
                                             @RequestParam(value = "forumTypeId", required = false) Integer forumTypeId,
                                             @RequestParam("content") String content,
                                             @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        Map<String, Object> resp = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        User currentUser = getCurrentUser();

        if (currentUser == null) {
            errors.put("login", "尚未登入");
            resp.put("success", false);
            resp.put("errors", errors);
            return resp;
        }

        // 後端驗證欄位
        if (title == null || title.isBlank()) errors.put("title", "請輸入文章標題");
        if (forumTypeId == null) errors.put("forumTypeId", "請選擇文章分類");

        boolean hasTextOrImage = content != null
                && (!content.replaceAll("<[^>]+>", "").trim().isEmpty() || content.contains("<img"));
        if (!hasTextOrImage) errors.put("content", "文章內容不可為空");

        if (!errors.isEmpty()) {
            resp.put("success", false);
            resp.put("errors", errors);
            return resp;
        }

        try {
            ArticleFormDTO dto = new ArticleFormDTO();
            dto.setTitle(title);
            dto.setForumTypeId(forumTypeId);
            dto.setContent(content);
            dto.setUserId(currentUser.getUserId());

            try {
                Article article = myArticleService.createArticle(dto, coverImage);
                resp.put("success", true);
                resp.put("articleId", article.getArticleId());
                return resp;
            } catch (IllegalArgumentException e) {
                resp.put("success", false);
                resp.put("message", e.getMessage());
                return resp;
            }

        } catch (Exception e) {
            errors.put("exception", "文章新增失敗: " + e.getMessage());
            resp.put("success", false);
            resp.put("errors", errors);
            return resp;
        }
    }

    /** 取得指定文章的內容（編輯時用） */
    @GetMapping("/{articleId}")
    public Map<String, Object> getArticle(@PathVariable Integer articleId) {
        Map<String, Object> resp = new HashMap<>();
        User currentUser = getCurrentUser();
//        if (currentUser == null) {
//            resp.put("success", false);
//            resp.put("message", "尚未登入");
//            return resp;
//        }

        Article article = myArticleService.getArticleById(articleId, currentUser);
        if (article == null) {
            resp.put("success", false);
            resp.put("message", "文章不存在或無權限");
            return resp;
        }

        ArticleFormDTO dto = new ArticleFormDTO();
        dto.setArticleId(article.getArticleId());
        dto.setUserId(article.getUser().getUserId());
        dto.setForumTypeId(article.getForumType().getForumTypeId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setCoverImageUrl(article.getCoverImageUrl());

        resp.put("success", true);
        resp.put("data", dto);
        return resp;
    }

    /** 更新文章（標題、分類、內容、封面圖片） */
    @PutMapping("/{articleId}")
    public Map<String, Object> updateArticle(@PathVariable Integer articleId,
                                             @RequestParam("title") String title,
                                             @RequestParam("forumTypeId") Integer forumTypeId,
                                             @RequestParam("content") String content,
                                             @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        Map<String, Object> resp = new HashMap<>();
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            resp.put("success", false);
            resp.put("message", "尚未登入");
            return resp;
        }

        try {
            ArticleFormDTO dto = new ArticleFormDTO();
            dto.setTitle(title);
            dto.setForumTypeId(forumTypeId);
            dto.setContent(content);
            dto.setUserId(currentUser.getUserId());

            Article updated = myArticleService.updateArticle(articleId, dto, coverImage);
            resp.put("success", true);
            resp.put("articleId", updated.getArticleId());
            return resp;
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "文章更新失敗：" + e.getMessage());
            return resp;
        }
    }

    /** 取得目前登入使用者 */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email).orElse(null);
        } else if (principal instanceof String) {
            return userRepository.findByEmail((String) principal).orElse(null);
        }
        return null;
    }
}
