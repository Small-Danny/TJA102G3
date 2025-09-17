package com.tibafit.test.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication(scanBasePackages = "com.tibafit") // 啟動一個可執行的 Spring Boot 應用，掃描 com.tibafit 以建立 Bean（含 Redis 設定）
public class RedisTest implements CommandLineRunner { // 實作 CommandLineRunner：容器啟動完成後自動執行 run(...) 一次

	@Autowired
	private RedisTemplate<String, Object> redis; // 由 CartRedisConfig 註冊的 RedisTemplate（key: String / value: Object）

	public static void main(String[] args) {
		SpringApplication.run(RedisTest.class, args); // 啟動 Spring 應用（執行後會觸發 run(...)）
	}

	@Override
	public void run(String... args) {
		String key = "cart:1"; // 模擬使用者 1 的購物車 key（Hash）
		// 寫入兩筆 Hash 欄位：field=商品ID（"5"/"6"），value=數量（10）
		// 依你的 RedisTemplate 設定：HashKey 以 String 序列化，HashValue 以 JSON 序列化（數字 10）
		redis.opsForHash().put(key, "5", 10); // productId=5, qty=10
		redis.opsForHash().put(key, "6", 10);

		// 設定購物車 key 的 TTL：30 天（每次寫入可視需求刷新）
		redis.expire(key, Duration.ofDays(30));

		System.out.println("🚀 已在 Redis(db8) 寫入測試資料 cart:1"); // 純提示文字；實際 DB index 取決於 spring.data.redis.database
	}
}
