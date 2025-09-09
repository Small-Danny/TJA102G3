package com.tibafit.dto.user;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;

import com.tibafit.model.user.User;

import lombok.Data;
@Data
public class LoginRequest {
	private String email;
	private String password;
	private String captcha;
	//Dto專門用來接收資料，特意省略Getter跟setter,練習用@Data加入depency依賴與Lombok插件
}
