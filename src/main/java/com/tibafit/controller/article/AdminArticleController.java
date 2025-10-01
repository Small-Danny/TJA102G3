package com.tibafit.controller.article;

import com.tibafit.dto.article.ArticleListDTO;
import com.tibafit.service.article.AdminArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
 
@Controller
@RequestMapping("/admin/community/posts")
public class AdminArticleController {

    @Autowired
    private AdminArticleService adminArticleService;
    
    @GetMapping
    public String listArticles(@RequestParam(required = false) String keyword, Model model) {
        List<ArticleListDTO> articles = adminArticleService.getAllArticles(keyword);
        model.addAttribute("articles", articles);
        model.addAttribute("keyword", keyword);
        return "admin/article/articleList"; // 對應 Thymeleaf 模板
    }
    
    @GetMapping("/{id}")
    public String viewArticle(@PathVariable Integer id, Model model) {
        ArticleListDTO article = adminArticleService.getArticleById(id); // 從 Service 拿 DTO
        model.addAttribute("article", article);
        return "admin/article/articleDetailAdmin"; // 對應 Thymeleaf 詳細頁
    }

    @PostMapping("/{id}/update")
    public String updateArticle(@PathVariable Integer id,
                                @RequestParam String articleAttribute,
                                @RequestParam boolean isPinned,
                                @RequestParam boolean isDeleted) {
        adminArticleService.updateArticleFields(id, articleAttribute, isPinned, isDeleted);
        return "redirect:/admin/community/posts";
    }

}
