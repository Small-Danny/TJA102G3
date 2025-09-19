package com.tibafit.test.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication(scanBasePackages = "com.tibafit")
public class RedisTest implements CommandLineRunner {
	
	@Autowired
	private RedisTemplate<String, Object> redis;
	
	public static void main(String[] args) {
        SpringApplication.run(RedisTest.class, args);
    }

    @Override
    public void run(String... args) {
        String key = "cart:1";
        redis.opsForHash().put(key, "5", 10); // productId=5, qty=10
        redis.opsForHash().put(key, "6", 10);
        redis.expire(key, Duration.ofDays(30));

        System.out.println("🚀 已在 Redis(db8) 寫入測試資料 cart:1");
    }
}
