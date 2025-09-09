package com.tibafit.dto.user;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;

import com.tibafit.model.user.User;

import lombok.Data;

@Data
public class RegisterRequest {
	private String email;
	private String password;
	private String name;
	private String code;
	private String captcha; 
}
