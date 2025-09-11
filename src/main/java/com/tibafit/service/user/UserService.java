package com.tibafit.service.user;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tibafit.dto.user.ChangePasswordRequest;
import com.tibafit.dto.user.LoginRequest;
import com.tibafit.dto.user.PerformResetRequest;
import com.tibafit.dto.user.RegisterRequest;
import com.tibafit.dto.user.UpdateProfileRequest;
import com.tibafit.model.user.User;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	public abstract User register(RegisterRequest req, HttpServletRequest httpServletRequest);

	public abstract void sendVerificationCode(String email);

	public abstract User login(LoginRequest loginRequest, HttpServletRequest request);

	public abstract User updateProfile(Integer userId, UpdateProfileRequest updateRequest);

	public abstract String updateProfilePicture(Integer userId, MultipartFile profilePicture);

	public abstract String changePassword(Integer userId, ChangePasswordRequest changepasswordrequest);

	public abstract void sendPasswordResetToken(String email, String captchaInput, HttpServletRequest request);

	public abstract String resetPasswordWithToken(PerformResetRequest request);

	public abstract List<User> findAll();
	
	public abstract User findById(Integer userId);
	
	public abstract List<User> serchUser(String keyword);

}
