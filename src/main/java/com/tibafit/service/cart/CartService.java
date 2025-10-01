package com.tibafit.service.cart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;

@Service
public class CartService {

	private final RedisTemplate<String, Object> redis;
	private static final Logger log = LoggerFactory.getLogger(CartService.class);

	@Autowired
	public CartService(RedisTemplate<String, Object> redis) {
		this.redis = redis;
	}

	private String key(String cartId) {
		return "cart:" + cartId;
	}

	// --- 為了兼容舊程式碼，保留所有接收 Integer userId 的方法 ---
	public Map<Object, Object> getCart(Integer userId) {
		return getCart(String.valueOf(userId));
	}

	public void addItem(Integer userId, Integer productId, Integer quantity) {
		addItem(String.valueOf(userId), productId, quantity);
	}

	public void setQuantity(Integer userId, Integer productId, Integer quantity) {
		if (userId == null)
			return;
		setQuantity(String.valueOf(userId), productId, quantity); // 呼叫下面的新方法
	}

	public void removeItem(Integer userId, Integer productId) {
		if (userId == null)
			return;
		removeItem(String.valueOf(userId), productId); // 呼叫下面的新方法
	}

	public void clear(Integer userId) {
		if (userId == null)
			return;
		clear(String.valueOf(userId)); // 呼叫下面的新方法
	}

	// --- 新的、接收 String cartId 的核心方法 ---
	public Map<Object, Object> getCart(String cartId) {
		if (cartId == null || cartId.isBlank())
			return Map.of();
		return redis.opsForHash().entries(key(cartId));
	}

	public void addItem(String cartId, Integer productId, Integer quantity) {
		if (cartId == null || cartId.isBlank())
			return;
		HashOperations<String, Object, Object> ops = redis.opsForHash();
		ops.increment(key(cartId), String.valueOf(productId), quantity);
		redis.expire(key(cartId), Duration.ofDays(30));
	}

	public void setQuantity(String cartId, Integer productId, Integer quantity) {
		if (cartId == null || cartId.isBlank())
			return;
		HashOperations<String, Object, Object> ops = redis.opsForHash();
		String productField = String.valueOf(productId);
		if (quantity == null || quantity <= 0) {
			ops.delete(key(cartId), productField);
		} else {
			ops.put(key(cartId), productField, quantity);
			redis.expire(key(cartId), Duration.ofDays(30));
		}
	}

	public void removeItem(String cartId, Integer productId) {
		if (cartId == null || cartId.isBlank())
			return;
		redis.opsForHash().delete(key(cartId), String.valueOf(productId));
	}

	public void clear(String cartId) {
		if (cartId == null || cartId.isBlank())
			return;
		redis.delete(key(cartId));
	}

	/**
	 * 【核心邏輯】合併訪客購物車到會員購物車
	 */
	@Transactional
	public void mergeCart(String sessionId, Integer userId) {
		// 3. 【★★★ 在方法開頭加入 Log ★★★】
		log.info("mergeCart 方法被呼叫，傳入 sessionId: {}, userId: {}", sessionId, userId);

		String sessionCartId = sessionId;
		String userCartId = String.valueOf(userId);

		if (sessionCartId == null || sessionCartId.isBlank() || sessionCartId.equals(userCartId)) {
			log.warn("sessionId 為空或與 userId 相同，取消合併。");
			return;
		}

		Map<Object, Object> sessionCart = getCart(sessionCartId);

		// 4. 【★★★ 在找到購物車後加入 Log ★★★】
		if (sessionCart.isEmpty()) {
			log.warn("在 Redis 中找不到 sessionId 為 {} 的訪客購物車，或者購物車是空的。無需合併。", sessionId);
			return;
		}

		log.info("成功找到訪客購物車 (sessionId: {})，內容為: {}", sessionId, sessionCart);
		log.info("準備將商品合併到會員購物車 (userId: {})", userId);

		for (Map.Entry<Object, Object> entry : sessionCart.entrySet()) {
			try {
				Integer productId = Integer.valueOf(entry.getKey().toString());
				Integer quantity = Integer.parseInt(entry.getValue().toString());
				if (productId != null && quantity > 0) {
					addItem(userCartId, productId, quantity);
				}
			} catch (NumberFormatException e) {
				System.err.println("合併購物車時解析商品資料失敗: " + entry);
			}
		}
		clear(sessionCartId);
		log.info("商品合併完成，已清除訪客購物車 (sessionId: {})。", sessionId);
	}
}