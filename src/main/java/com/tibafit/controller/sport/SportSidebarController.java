package com.tibafit.controller.sport;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.exception.ResourceNotFoundException;
import com.tibafit.model.user.Admin;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.AdminRepository;
import com.tibafit.repository.user.UserRepository;

@RestController
@RequestMapping("/sportSidebar/api")
public class SportSidebarController {

	private final UserRepository userRepo;
	private final AdminRepository adminRepo;

	@Autowired
	public SportSidebarController(UserRepository userRepo, AdminRepository adminRepo) {
		// 唯一初始化
	    this.userRepo = userRepo;
	    this.adminRepo = adminRepo;
	}

    @PostMapping("/fd/sidebar")
    public ApiResponseDTO<Map<String, Object>> getCurrentFdSidebar(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponseDTO.loginError("SportSidebarController error: Not yet, status: " + HttpStatus.UNAUTHORIZED.value());
        }

        String userEmail = authentication.getName();
        // PO
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("SportSidebarController not found error: em:" + userEmail));

        Map<String, Object> information = Map.of(
                "uudd", user.getUserId(),
                "uuee", user.getEmail(),
                "authorities", authentication.getAuthorities()
        );

        return ApiResponseDTO.success(information);
    }
    
    @PostMapping("/bd/sidebar")
    public ApiResponseDTO<Map<String, Object>> getCurrentBdSidebar(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponseDTO.loginError("SportSidebarController error: Not yet, status: " + HttpStatus.UNAUTHORIZED.value());
        }

        String adminAccount = authentication.getName();
        // PO
        Admin admin = adminRepo.findByAccount(adminAccount)
                .orElseThrow(() -> new ResourceNotFoundException("SportSidebarController not found error: em:" + adminAccount));

        Map<String, Object> information = Map.of(
                "uudd", admin.getAdminId(),
                "uuacc", admin.getAccount(),
                "authorities", authentication.getAuthorities()
        );

        return ApiResponseDTO.success(information);
    }
}
