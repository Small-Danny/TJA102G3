package com.tibafit.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginRequest {
	private String email;
	private String password;
	private String recaptchaToken;
	@JsonProperty("remember-me")
	private boolean rememberMe;
	// 一個明確的映射橋樑。JsonProperty
}
