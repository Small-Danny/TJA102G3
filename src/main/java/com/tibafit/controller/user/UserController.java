package com.tibafit.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tibafit.dto.user.ChangePasswordRequest;
import com.tibafit.dto.user.LoginRequest;
import com.tibafit.dto.user.PasswordResetRequest;
import com.tibafit.dto.user.PerformResetRequest;
import com.tibafit.dto.user.RegisterRequest;
import com.tibafit.dto.user.UpdateProfileRequest;
import com.tibafit.model.user.User;
import com.tibafit.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

	// 宣告需要一個UserService
	private final UserService userService;

	// 建構子注入

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody RegisterRequest req, HttpServletRequest httpServletRequest) { // 【增加參數】
		// 這裡的註解是HTTP請求主體Body,裡面尋找JSON資料，然後自動轉換成一個 RegisterRequest 物件
	    User registeredUser = userService.register(req, httpServletRequest);
		// 把得到的 User 物件回傳201 Created狀態碼並放入body,200代表成功

		return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
	}

	// <?>代表可以回傳任何類型的內容
	@PostMapping("/send-code")
	public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> payload) {
		// 從 payload 中取得 email
		String email = payload.get("email");
		// 呼叫 UserService 來處理發送驗證碼的邏輯
		userService.sendVerificationCode(email);
		// 回傳一個成功的訊息
		return ResponseEntity.ok().body("驗證碼已成功發送");
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) { // 【增加參數】
	    // 將 request 一起傳遞給 service
	    User loggedInUser = userService.login(loginRequest, request);
	    return ResponseEntity.ok(loggedInUser);
	}

	// API 1: 專門用來更新「文字」資料
	@PutMapping("/{userId}") // 我們改用 PUT 來表示「更新」，語意更清晰
	public ResponseEntity<User> updateProfile(@PathVariable Integer userId,
			@RequestBody UpdateProfileRequest updateRequest) {

		User updatedUser = userService.updateProfile(userId, updateRequest);
		return ResponseEntity.ok(updatedUser);
	}

	// API 2: 專門用來更新「頭像圖片」
	@PutMapping("/{userId}/profile-picture")
	public ResponseEntity<Map<String, String>> updateProfilePicture(@PathVariable Integer userId,
			@RequestParam("profilePicture") MultipartFile profilePicture) {
		
		String newPicturePath = userService.updateProfilePicture(userId, profilePicture);
		// 回傳新圖片的路徑給前端
		return ResponseEntity.ok(Map.of("newPicturePath", newPicturePath));
	}
	//用來確認修改密碼接口
	@PutMapping("/{userId}/password")
	public String changePassword(@PathVariable Integer userId, @RequestBody ChangePasswordRequest request) {
	    return userService.changePassword(userId, request);
	}
	

	   // 【第一步：請求重設】這個 API 給 forgot-password.html 使用
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest req, HttpServletRequest httpServletRequest) {
        userService.sendPasswordResetToken(req.getEmail(), req.getCaptcha(), httpServletRequest);
        return ResponseEntity.ok().body("密碼重設連結已發送至您的電子郵件，請於15分鐘內使用。");
    }

    // 【第二步：執行重設】這個 API 給 reset-set-password.html 使用
    @PostMapping("/reset-password-with-token")
    public ResponseEntity<String> performPasswordReset(@RequestBody PerformResetRequest req) {
        // 呼叫正確的 Service 方法
        String message = userService.resetPasswordWithToken(req); 
        // 回傳成功訊息
        return ResponseEntity.ok(message);
    }
}
