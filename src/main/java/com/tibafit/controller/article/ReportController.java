//package com.tibafit.controller.article;
//
//import java.util.HashMap;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import com.tibafit.dto.article.ReportTypeDTO;
//import com.tibafit.model.article.Article;
//import com.tibafit.service.article.ArticleReportService;
//import com.tibafit.service.article.ArticleService;
//
//
//@RestController
//public class ReportController {
//
//    private final ArticleReportService reportService;
//    private final ArticleService articleService;
//    private final SessionController sessionController;
//
//    public ReportController(ArticleReportService reportService,
//                            ArticleService articleService,
//                            SessionController sessionController) {
//        this.reportService = reportService;
//        this.articleService = articleService;
//        this.sessionController = sessionController;
//    }
//
//    // 取得檢舉類型列表
//    @GetMapping("/api/report-types")
//    public List<ReportTypeDTO> getReportTypes() {
//        return reportService.getAllReportTypes();
//    }
//
//    // 送出檢舉文章
//    @PostMapping("/api/posts/{articleId}/report")
//    public Map<String, Object> reportArticle(@PathVariable Integer articleId,
//                                             @RequestParam Integer reportTypeId,
//                                             @RequestParam(required = false) String reason) {
//        Map<String, Object> response = new HashMap<>();
//
//        // 取得登入使用者
//        Map<String, Object> sessionData = sessionController.getSession();
//        boolean isLoggedIn = (boolean) sessionData.getOrDefault("isLoggedIn", false);
//        User currentUser = (User) sessionData.get("user");
//
//        if (!isLoggedIn || currentUser == null) {
//            response.put("success", false);
//            response.put("message", "尚未登入");
//            return response;
//        }
//
//        Article article = articleService.getArticleById(articleId);
//        if (article == null) {
//            response.put("success", false);
//            response.put("message", "文章不存在");
//            return response;
//        }
//
//        try {
//            reportService.createArticleReport(article, reportTypeId, reason, currentUser);
//            response.put("success", true);
//            response.put("message", "檢舉已送出，感謝您的回報");
//        } catch (IllegalArgumentException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//        }
//
//        return response;
//    }
//}
package com.tibafit.controller.article;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tibafit.dto.article.ReportTypeDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.article.ArticleReportService;
import com.tibafit.service.article.ArticleService;


@RestController
public class ReportController {

    private final ArticleReportService reportService;
    private final ArticleService articleService;
    private final UserRepository userRepository;

    public ReportController(ArticleReportService reportService,
                            ArticleService articleService,UserRepository userRepository
                           ) {
        this.reportService = reportService;
        this.articleService = articleService;
        this.userRepository = userRepository;
    }

    // 取得檢舉類型列表
    @GetMapping("/api/report-types")
    public List<ReportTypeDTO> getReportTypes() {
        return reportService.getAllReportTypes();
    }

    // 送出檢舉文章
    @PostMapping("/api/posts/{articleId}/report")
    public Map<String, Object> reportArticle(@PathVariable Integer articleId,
                                             @RequestParam Integer reportTypeId,
                                             @RequestParam(required = false) String reason) {
        Map<String, Object> response = new HashMap<>();

        // 取得登入使用者
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

        try {
            reportService.createArticleReport(article, reportTypeId, reason, currentUser);
            response.put("success", true);
            response.put("message", "檢舉已送出，感謝您的回報");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }
}

