package com.tibafit.service.article;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tibafit.dto.article.MemberInfoArticlePostDTO;
import com.tibafit.dto.article.forumMemberDetailDTO;
import com.tibafit.model.article.Article;
import com.tibafit.model.user.User;
import com.tibafit.repository.article.ForumMemberRepository;

@Service
public class ForumMemberService {

    @Autowired
    private ForumMemberRepository forumMemberRepository;
    
 // 五張預設圖片列表
    private final List<String> defaultCoverImages = Arrays.asList(
        "/images/1.jpg",
        "/images/2.jpg",
        "/images/3.jpg",
        "/images/4.jpg",
        "/images/5.jpg"
    );

    private final Random random = new Random();

    private String getRandomCoverImage() {
        return defaultCoverImages.get(random.nextInt(defaultCoverImages.size()));
    }

    public forumMemberDetailDTO getMemberDetail(Integer userId) {
        User user = forumMemberRepository.findByUserId(userId);
        if(user == null) {
            return null; // 或丟出自訂例外
        }

        forumMemberDetailDTO dto = new forumMemberDetailDTO();
        dto.setUserId(user.getUserId());
        dto.setNickName(user.getNickName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "/frontend-template/assets/images/profile-picture-default.jpg");
        dto.setCreateTime(user.getCreateTime());

        return dto;
    }

    public List<MemberInfoArticlePostDTO> getMemberArticles(Integer userId) {
        List<Article> articles = forumMemberRepository.findArticlesByUserId(userId);
        List<MemberInfoArticlePostDTO> dtoList = new ArrayList<>();

        for (Article article : articles) {
            MemberInfoArticlePostDTO dto = new MemberInfoArticlePostDTO();
            dto.setUserId(article.getUser().getUserId());
            dto.setArticleId(article.getArticleId());
            // 如果 coverImageUrl 為 null，就隨機選一張預設圖
            dto.setCoverImageUrl(article.getCoverImageUrl() != null ? article.getCoverImageUrl() : getRandomCoverImage());
            dto.setContent(article.getContent());
            dto.setForumTypeName(article.getForumType().getForumTypeName());
            dto.setCreateTime(article.getCreateTime());
            dto.setViews(article.getViews());
            dto.setTitle(article.getTitle());
            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<MemberInfoArticlePostDTO> getFeaturedArticles(Integer userId) {
        List<Article> articles = forumMemberRepository.findFeaturedArticlesByUserId(userId);
        List<MemberInfoArticlePostDTO> dtoList = new ArrayList<>();

        for (Article article : articles) {
            MemberInfoArticlePostDTO dto = new MemberInfoArticlePostDTO();
            dto.setUserId(article.getUser().getUserId());
            dto.setArticleId(article.getArticleId());
            dto.setCoverImageUrl(article.getCoverImageUrl() != null ? article.getCoverImageUrl() : getRandomCoverImage());
            dto.setContent(article.getContent());
            dto.setForumTypeName(article.getForumType().getForumTypeName());
            dto.setCreateTime(article.getCreateTime());
            dto.setViews(article.getViews());
            dto.setTitle(article.getTitle());
            dtoList.add(dto);
        }

        return dtoList;
    }

}
