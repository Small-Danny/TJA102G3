package com.tibafit.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.user.ChangePasswordRequest;
import com.tibafit.exception.ValidationException;
import com.tibafit.model.user.Admin;
import com.tibafit.repository.user.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService{
	
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public AdminServiceImpl(AdminRepository adminRepository,PasswordEncoder passwordEncoder) {
		
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	
	@Override
	@Transactional
	public void changeAdminPassword(String currentAdminAccount, ChangePasswordRequest request) {
		// 1. 查詢使用者是否存在
		Admin admin = adminRepository.findByAccount(currentAdminAccount)
				.orElseThrow(() -> new ValidationException("changePassword", "找不到使用者"));

		// 2. 取得請求中的密碼
		String currentPassword = request.getCurrentPassword();
		String newPassword = request.getNewPassword();
		String confirmPassword = request.getConfirmPassword();

		// 3. 呼叫輔助方法，進行非空驗證
		validateNotEmpty(currentPassword, "currentPassword", "請輸入當前密碼");
		validateNotEmpty(newPassword, "newPassword", "請輸入新密碼");
		validateNotEmpty(confirmPassword, "confirmPassword", "請確認輸入新密碼");

		// 4. 驗證新密碼和確認密碼是否一致
		if (!newPassword.equals(confirmPassword)) {
			throw new ValidationException("confirmPassword", "兩次輸入的密碼不一致");
		}

		// 5. 驗證當前密碼是否正確
		// 這裡需要用 passwordEncoder.matches() 來比對原始密碼與資料庫中的加密密碼
		if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
			throw new ValidationException("currentPassword", "當前密碼不正確");
		}

		// 6. 驗證新密碼強度和是否與舊密碼相同
		validatePasswordStrength(newPassword);

		// 7. 確保新密碼不能與原密碼相同
		if (passwordEncoder.matches(newPassword, admin.getPassword())) {
			throw new ValidationException("newPassword", "新密碼不能與原密碼相同");
		}

		// 8. 加密新密碼並保存
		admin.setPassword(passwordEncoder.encode(newPassword));
		adminRepository.save(admin);

	}
		
	
	/**
	 * @param 非空驗證
	 */
	private void validateNotEmpty(String value, String field, String message) {
		if (value == null || value.trim().isEmpty()) {
			throw new ValidationException(field, message);
		}
	}

	/**
	 * @param passworrd密碼強度驗證 長度+大小寫+數字+特殊符號
	 */
	private void validatePasswordStrength(String password) {
		if (password.length() < 8) {
			throw new ValidationException("newPassword", "密碼長度至少8位");
		}
		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		if (!password.matches(passwordRegex)) {
			throw new ValidationException("newPassword", "密码必须包含大小写字母、数字和特殊符号");
		}
	}
	}

