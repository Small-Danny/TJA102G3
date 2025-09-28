package com.tibafit.service.user;

import com.tibafit.dto.user.*;
import com.tibafit.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public abstract User register(RegisterRequest req, HttpServletRequest httpServletRequest);

    public abstract void sendVerificationCode(String email);

    public abstract User login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);

    public abstract User updateProfile(Integer userId, UpdateProfileRequest updateRequest);

    public abstract String updateProfilePicture(Integer userId, MultipartFile profilePicture);

    public abstract String changePassword(Integer userId, ChangePasswordRequest changepasswordrequest);

    public abstract void sendPasswordResetToken(PasswordResetRequest req);

    public abstract String resetPasswordWithToken(PerformResetRequest request);

    public abstract List<User> findAll();

    public abstract User findById(Integer userId);

    public abstract List<User> searchUser(String keyword);

    public abstract void toggleAccountStatus(Integer userId);

    public abstract Optional<User> findUserByEmail(String email);

    String subscribeNewsletter(String email);
}
