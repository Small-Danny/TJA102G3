package com.tibafit.controller.article;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.article.MemberInfoArticlePostDTO;
import com.tibafit.dto.article.forumMemberDetailDTO;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.article.ForumMemberService;

@RestController
@RequestMapping("/api/forum/member")
public class ForumMemberController {
	
	@Autowired
	private final UserRepository userRepository;
	private final ForumMemberService forumMemberService;

	@Autowired
	public ForumMemberController(UserRepository userRepository, ForumMemberService forumMemberService) {
	    this.userRepository = userRepository;
	    this.forumMemberService = forumMemberService;
	}

	

	@GetMapping("/{userId}")
	public forumMemberDetailDTO getMemberDetail(@PathVariable Integer userId) {
		
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
