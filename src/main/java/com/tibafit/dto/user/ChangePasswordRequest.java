package com.tibafit.dto.user;

import lombok.Data;
@Data
public class ChangePasswordRequest {
	   private String currentPassword; // 原密碼
	    private String newPassword;     // 新密碼
	    private String confirmPassword; // 確認新密碼
}
