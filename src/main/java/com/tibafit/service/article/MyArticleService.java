package com.tibafit.service.article;

import com.cloudinary.Cloudinary;


import com.cloudinary.utils.ObjectUtils;
import com.tibafit.dto.article.ArticleFormDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.article.ForumType;
import com.tibafit.model.user.User;
import com.tibafit.repository.article.ArticleRepository;
import com.tibafit.utils.ContentImageUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MyArticleService {

    private final ArticleRepository articleRepository;
    private final Cloudinary cloudinary;
    private final ContentImageUploader contentImageUploader;

    // 預設圖片清單
    private final List<String> defaultImages = Arrays.asList(
            "/images/1.jpg", "/images/2.jpg", "/images/3.jpg",
            "/images/4.jpg", "/images/5.jpg"
    );

    public MyArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;

        // 直接在 Service 內初始化 Cloudinary
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "doxg5hdim",
                "api_key", "748718811281865",
                "api_secret", "O8D8bdZ3wvb3KV7vClIcPEOvOB4"
        ));

        // 將 Cloudinary 傳給 ContentImageUploader
        this.contentImageUploader = new ContentImageUploader(cloudinary);
    }

    /** 取得使用者的文章列表（處理時間＋預設封面圖） */
    public List<Map<String, Object>> getArticlesByUser(User user) {
        List<Article> articles = articleRepository.findByUserAndIsDeleted(user, false);
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Random random = new Random();

        for (Article article : articles) {
            Map<String, Object> map = new HashMap<>();
            map.put("articleId", article.getArticleId());
            map.put("title", article.getTitle());
            map.put("views", article.getViews());

            // 時間處理
            String date = "";
            if (article.getCreateTime() != null) {
                date = article.getCreateTime().toLocalDateTime().format(formatter);
            }
            map.put("createTime", date);

            // 封面圖處理
            String imageUrl = (article.getCoverImageUrl() != null && !article.getCoverImageUrl().isBlank())
                    ? article.getCoverImageUrl()
                    : defaultImages.get(random.nextInt(defaultImages.size()));
            map.put("coverImageUrl", imageUrl);

            result.add(map);
        }
        return result;
    }

    /** 刪除文章（軟刪除） */
    public boolean deleteArticle(Integer articleId, User user) {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) return false;
        if (!article.getUser().getUserId().equals(user.getUserId())) return false;

        article.setIsDeleted(true); // 軟刪除
        articleRepository.save(article);
        return true;
    }

    /** 新增文章 */
    public Article createArticle(ArticleFormDTO dto, MultipartFile coverImage) throws IOException {
        Article article = new Article();

        User user = new User();
        user.setUserId(dto.getUserId());
        article.setUser(user);

        ForumType forumType = new ForumType();
        forumType.setForumTypeId(dto.getForumTypeId());
        article.setForumType(forumType);

        article.setTitle(dto.getTitle());
        article.setArticleAttribute(Article.ArticleAttribute.一般文章);
//--------------------------------------------------------------------------------------------------
     // 封面圖片大小上限 10MB
        long MAX_COVER_SIZE = 10 * 1024 * 1024; 
        if (coverImage != null && coverImage.getSize() > MAX_COVER_SIZE) {
            throw new IllegalArgumentException("封面圖片大小不能超過 10MB");
        }

        // 文章內容大小上限 10MB
        long MAX_CONTENT_SIZE = 10 * 1024 * 1024;
        if (dto.getContent() != null && dto.getContent().getBytes().length > MAX_CONTENT_SIZE) {
            throw new IllegalArgumentException("文章內容大小不能超過 10MB");
        }
//--------------------------------------------------------------------------------------------------
        
        // 處理文章內容內的 Base64 圖片
        String processedContent = contentImageUploader.uploadContentImages(dto.getContent());
        article.setContent(processedContent);

        // 設定時間
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        article.setCreateTime(now);
        article.setUpdateTime(now);

        // 處理封面圖片
        if (coverImage != null && !coverImage.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(
                    coverImage.getBytes(),
                    ObjectUtils.asMap("folder", "forum/covers")
            );
            String url = (String) uploadResult.get("secure_url");
            article.setCoverImageUrl(url);
        } else {
            article.setCoverImageUrl(null); // 沒有上傳 → 後續取預設圖
        }

        return articleRepository.save(article);
    }
    
    /** 取得單篇文章（編輯用） */
    public Article getArticleById(Integer articleId, User user) {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null || !article.getUser().getUserId().equals(user.getUserId())) {
            return null; // 理論上不會發生，因為列表點進來才可編輯
        }
        return article;
    }

    /** 更新文章（標題、分類、內容、封面圖片） */
    public Article updateArticle(Integer articleId, ArticleFormDTO dto, MultipartFile coverImage) throws IOException {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null || !article.getUser().getUserId().equals(dto.getUserId())) {
            return null;
        }

        article.setTitle(dto.getTitle());
        ForumType forumType = new ForumType();
        forumType.setForumTypeId(dto.getForumTypeId());
        article.setForumType(forumType);
        article.setContent(contentImageUploader.uploadContentImages(dto.getContent()));
        article.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        // 如果有重新上傳封面 → 上傳雲端並更新 URL
        if (coverImage != null && !coverImage.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(
                    coverImage.getBytes(),
                    ObjectUtils.asMap("folder", "forum/covers")
            );
            String url = (String) uploadResult.get("secure_url");
            article.setCoverImageUrl(url);
        }

        return articleRepository.save(article);
    }

}
