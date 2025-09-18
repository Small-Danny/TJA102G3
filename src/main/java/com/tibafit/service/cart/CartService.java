package com.tibafit.service.cart;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.CartItemVO;
import com.tibafit.repository.cart.CartItemDAO;

//	key：cart:{userId}
//	field：{productId}（字串）
//	value：qty（整數）提供讀取、加入/累加、設定數量、刪除單品與清空整車；每次寫入會把購物車 key 的 TTL 續期為 30 天。

@Service // 服務層：專責操作 Redis 中的購物車（Hash 結構）
public class CartService {

	private final RedisTemplate<String, Object> redis; // 由 CartRedisConfig 提供（key:String / value:Object）

	@Autowired
	public CartService(RedisTemplate<String, Object> redis) {
		this.redis = redis;
	}

	/** 產生使用者購物車的 Redis key：格式 cart:{userId} */
	private String key(Integer userId) {
		return "cart:" + userId;
	}

	/** 讀取購物車（Redis Hash → Map<Object,Object>） */
	public Map<Object, Object> getCart(Integer userId) {
		return redis.opsForHash().entries(key(userId)); // HGETALL cart:{userId}
	}

	/**
	 * 新增 / 累加商品數量 規則： - 若原本不存在：視為 0，再加上 quantity - 若加總後 <= 0：視為刪除該商品 - 每次操作後刷新 TTL
	 * 30 天
	 */
	public void addItem(Integer userId, Integer productId, Integer quantity) {
		HashOperations<String, Object, Object> ops = redis.opsForHash();
		Integer cur = (Integer) ops.get(key(userId), productId.toString()); // HGET
		int newQty = (cur == null ? 0 : cur) + quantity;
		if (newQty <= 0) {
			ops.delete(key(userId), productId.toString()); // HDEL
		} else {
			ops.put(key(userId), productId.toString(), newQty); // HSET
		}
		redis.expire(key(userId), Duration.ofDays(30)); // EXPIRE（滾動 TTL）
		// ⚠ 併發下更穩的作法可改用 ops.increment(hashKey, field, delta) 以確保原子性（HINCRBY）
	}

	/**
	 * 設定商品數量（idempotent） 規則： - quantity <= 0 視為刪除該商品 - 否則直接覆寫數量並刷新 TTL 30 天
	 */
	public void setQuantity(Integer userId, Integer productId, Integer quantity) {
		HashOperations<String, Object, Object> ops = redis.opsForHash();
		if (quantity == null || quantity <= 0) {
			ops.delete(key(userId), productId.toString()); // HDEL
		} else {
			ops.put(key(userId), productId.toString(), quantity); // HSET
			redis.expire(key(userId), Duration.ofDays(30)); // EXPIRE
		}
	}

	/** 移除單一商品 */
	public void removeItem(Integer userId, Integer productId) {
		redis.opsForHash().delete(key(userId), productId.toString()); // HDEL
	}

	/** 清空整車（直接刪除 key） */
	public void clear(Integer userId) {
		redis.delete(key(userId)); // DEL cart:{userId}
	}
}
