package com.tibafit.config.cartredis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

//  定義一個專案共用的 RedisTemplate<String,Object> Bean，供 Service（例如 CartService）直接注入使用。
//  將 Key/Hash Key 設為 StringRedisSerializer，Value/Hash Value 設為 GenericJackson2JsonRedisSerializer，
//  讓 Redis 裡的資料可讀、好除錯、跨語言相容。

@Configuration
@EnableCaching
public class CartRedisConfig {

	@Bean // 將此方法回傳的 RedisTemplate 放進 Spring 容器，供其他類別注入使用
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory rf) {
		// 建立一個 RedisTemplate，Key 型別為 String、Value 型別為 Object（泛型不會限制 Hash 內部的型別）
		RedisTemplate<String, Object> rt = new RedisTemplate<>();

		// 指定連線工廠（Spring Boot 依 application.yml 中 spring.redis.* 幫我們建立）
		rt.setConnectionFactory(rf);

		// 準備序列化器：
		// 1) Key/Hash Key 用「字串序列化」：在 redis-cli 看到的 key/field 會是可讀文字（避免 JDK 預設二進位）
		var key = new StringRedisSerializer();

		// 2) Value/Hash Value 用「通用 JSON 序列化」：跨語言、可讀性高，含類型資訊（避免還原失敗）
		var val = new GenericJackson2JsonRedisSerializer();

		// 指定各種序列化策略（非常重要，否則會用 JDK 預設序列化，資料看起來像亂碼）
		rt.setKeySerializer(key); // 針對「鍵」的序列化（如：cart:101）
		rt.setHashKeySerializer(key); // 針對「Hash 的欄位名」序列化（如 Hash 裡的 productId）
		rt.setValueSerializer(val); // 針對一般「值」序列化
		rt.setHashValueSerializer(val); // 針對「Hash 的值」序列化（如每個 productId 對應的 qty）

		// 完成屬性設定，初始化內部狀態
		rt.afterPropertiesSet();
		return rt;
	}
}
