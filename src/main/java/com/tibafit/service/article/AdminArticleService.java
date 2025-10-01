package com.tibafit.service.article;

import com.tibafit.dto.article.ArticleListDTO;
import com.tibafit.model.article.Article;
import com.tibafit.repository.article.AdminArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminArticleService {

    @Autowired
    private AdminArticleRepository articleRepository;

    // 查詢文章列表
    public List<ArticleListDTO> getAllArticles(String keyword) {
        return articleRepository.findAllArticlesDTO(keyword);
    }

    // 查詢單篇文章
    public ArticleListDTO getArticleById(Integer id) {
        ArticleListDTO articleDTO = articleRepository.findArticleDTOById(id).orElse(null);
        if (articleDTO == null) {
            throw new RuntimeException("文章不存在，ID=" + id);
        }
        return articleDTO;
    }

    // 更新可編輯欄位
    @Transactional
    public void updateArticleFields(Integer id, String articleAttribute, boolean isPinned, boolean isDeleted) {
        Article article = articleRepository.findByArticleId(id).orElse(null);
        if (article == null) {
            throw new RuntimeException("文章不存在，ID=" + id);
        }

     // String -> Enum
        try {
            Article.ArticleAttribute attributeEnum = Article.ArticleAttribute.valueOf(articleAttribute);
            article.setArticleAttribute(attributeEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("無效的文章屬性：" + articleAttribute);
        }
        article.setIsPinned(isPinned);
        article.setIsDeleted(isDeleted);

        articleRepository.save(article); // @Transactional 會自動 flush，但保留 save 也可以
    }
}
