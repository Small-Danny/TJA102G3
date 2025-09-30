package com.tibafit.controller.user;

import com.tibafit.dto.user.*;
import com.tibafit.exception.ResourceNotFoundException;
import com.tibafit.model.user.User;
import com.tibafit.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody RegisterRequest req, HttpServletRequest httpServletRequest) {
		User registeredUser = userService.register(req, httpServletRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
	}

	@PostMapping("/send-code")
	public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestBody Map<String, String> payload) { // ★ 修正回傳類型
		String email = payload.get("email");
		userService.sendVerificationCode(email);
		// ★ 修正回傳內容為 JSON
		return ResponseEntity.ok(Map.of("message", "驗證碼已成功發送"));
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request,
			HttpServletResponse response) {
		   User loggedInUser = userService.login(loginRequest, request, response);
	        return ResponseEntity.ok(loggedInUser);
	}
    
    // 這個 API 會由 profile.html 在載入時呼叫，用來驗證 session 是否有效。
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        User user = userService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者: " + userEmail));
        return ResponseEntity.ok(user);
    }
    
	@PutMapping("/{userId}")
	public ResponseEntity<User> updateProfile(@PathVariable Integer userId,
			@RequestBody UpdateProfileRequest updateRequest) {
		User updatedUser = userService.updateProfile(userId, updateRequest);
		return ResponseEntity.ok(updatedUser);
	}

	@PutMapping("/{userId}/profile-picture")
	public ResponseEntity<Map<String, String>> updateProfilePicture(@PathVariable Integer userId,
			@RequestParam("profilePicture") MultipartFile profilePicture) {
		String newPicturePath = userService.updateProfilePicture(userId, profilePicture);
		return ResponseEntity.ok(Map.of("newPicturePath", newPicturePath));
	}

	@PutMapping("/{userId}/password")
	public ResponseEntity<Map<String, String>> changePassword(@PathVariable Integer userId, @RequestBody ChangePasswordRequest request) { // ★ 修正回傳類型
		String message = userService.changePassword(userId, request);
		// ★ 修正回傳內容為 JSON
		return ResponseEntity.ok(Map.of("message", message));
	}

	@PostMapping("/request-password-reset")
	public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestBody PasswordResetRequest req) { // ★ 修正回傳類型
		   userService.sendPasswordResetToken(req);
		   // ★ 修正回傳內容為 JSON
		   return ResponseEntity.ok(Map.of("message", "密碼重設連結已發送至您的電子郵件，請於15分鐘內使用。"));
	}

	@PostMapping("/reset-password-with-token")
	public ResponseEntity<Map<String, String>> performPasswordReset(@RequestBody PerformResetRequest req) { // ★ 修正回傳類型
		String message = userService.resetPasswordWithToken(req);
		// ★ 修正回傳內容為 JSON
		return ResponseEntity.ok(Map.of("message", message));
	}

	/**
	 * 處理使用者訂閱電子報的請求
	 * @param payload 包含 email 的請求內文
	 * @return 回傳成功訊息
	 */
	@PostMapping("/subscribe")
	public ResponseEntity<Map<String, String>> subscribeNewsletter(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");
		String message = userService.subscribeNewsletter(email); // 我們等下會在 Service 中建立這個方法
		return ResponseEntity.ok(Map.of("message", message));
	}
}