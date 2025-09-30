package com.tibafit.service.article;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tibafit.dto.article.ArticleCollectionDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.article.ArticleCollection;
import com.tibafit.model.user.User;
import com.tibafit.repository.article.ArticleCollectionRepository;


@Service
public class ArticleCollectionService {

    private final ArticleCollectionRepository articleCollectionRepository;

    public ArticleCollectionService(ArticleCollectionRepository articleCollectionRepository) {
        this.articleCollectionRepository = articleCollectionRepository;
    }

    // 找使用者收藏的文章列表
    public List<ArticleCollectionDTO> getUserFavorites(User user) {
        if (user == null) return List.of();
        return articleCollectionRepository.findUserFavorites(user);
    }

    // 使用者收藏文章
    @Transactional
    public boolean addArticleToFavorites(User user, Article article) {
        if (user == null || article == null) return false;

        ArticleCollection existing = articleCollectionRepository.findByUserAndArticle(user, article);

        if (existing != null) {
            existing.setCollectionStatus(1);
            articleCollectionRepository.save(existing);
        } else {
            ArticleCollection newCollection = new ArticleCollection();
            newCollection.setUser(user);
            newCollection.setArticle(article);
            newCollection.setCollectionStatus(1);
            articleCollectionRepository.save(newCollection);
        }
        return true;
    }

    // 檢查使用者是否收藏過文章
    public boolean isCollected(User user, Article article) {
        if (user == null || article == null) return false;

        ArticleCollection collection = articleCollectionRepository.findByUserAndArticle(user, article);
        return collection != null && collection.getCollectionStatus() == 1;
    }

    // 切換收藏/取消收藏
    @Transactional
    public boolean toggleArticleFavorite(User user, Article article) {
        if (user == null || article == null) return false;

        ArticleCollection collection = articleCollectionRepository.findByUserAndArticle(user, article);

        if (collection != null) {
            collection.setCollectionStatus(collection.getCollectionStatus() == 1 ? 0 : 1);
            articleCollectionRepository.save(collection);
        } else {
            ArticleCollection newCollection = new ArticleCollection();
            newCollection.setUser(user);
            newCollection.setArticle(article);
            newCollection.setCollectionStatus(1);
            articleCollectionRepository.save(newCollection);
        }
        return true;
    }

    // 在 mycollection 中刪除收藏
    @Transactional
    public boolean removeFavorite(User user, Article article) {
        if (user == null || article == null) return false;

        ArticleCollection collection = articleCollectionRepository.findByUserAndArticle(user, article);
        if (collection != null && collection.getCollectionStatus() == 1) {
            collection.setCollectionStatus(0);
            articleCollectionRepository.save(collection);
            return true;
        }
        return false;
    }
}

