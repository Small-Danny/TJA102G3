package com.tibafit.controller.article;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.article.MemberInfoArticlePostDTO;
import com.tibafit.dto.article.forumMemberDetailDTO;
import com.tibafit.service.article.ForumMemberService;

@RestController
@RequestMapping("/api/forum/member")
public class ForumMemberController {

	@Autowired
	private ForumMemberService forumMemberService;

	@GetMapping("/{userId}")
	public forumMemberDetailDTO getMemberDetail(@PathVariable Integer userId) {
		return forumMemberService.getMemberDetail(userId);
	}
	
	// 取得會員的文章列表
    @GetMapping("/articles/{userId}")
    public List<MemberInfoArticlePostDTO> getMemberArticles(@PathVariable Integer userId) {
        return forumMemberService.getMemberArticles(userId);
    }
    
 // 取得會員的精選文章（每個分類中瀏覽數最高的文章）
    @GetMapping("/featured/{userId}")
    public List<MemberInfoArticlePostDTO> getFeaturedArticles(@PathVariable Integer userId) {
        return forumMemberService.getFeaturedArticles(userId);
    }

}
