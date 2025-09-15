package com.tibafit.config.cartredis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
@EnableCaching
public class CartRedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory rf){
		RedisTemplate<String, Object> rt = new RedisTemplate<>();
		rt.setConnectionFactory(rf);
		var key = new StringRedisSerializer();
		var val = new GenericJackson2JsonRedisSerializer();
		rt.setKeySerializer(key); 
		rt.setHashKeySerializer(key);
		rt.setValueSerializer(val);
		rt.setHashValueSerializer(val);
	    rt.afterPropertiesSet();
		return rt;
	}
}
