package com.tibafit.dto.user;

import lombok.Data;

@Data
public class LoginRequest {
	private String email;
	private String password;
	private String recaptchaToken;

	private boolean rememberMe;
	// 一個明確的映射橋樑。JsonProperty
}
