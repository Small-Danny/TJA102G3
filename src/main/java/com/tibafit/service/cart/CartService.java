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

@Service
public class CartService {

    private final RedisTemplate<String, Object> redis;

    @Autowired
    public CartService(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    private String key(Integer userId) {
        return "cart:" + userId;
    }

    // 讀取購物車
    public Map<Object, Object> getCart(Integer userId) {
        return redis.opsForHash().entries(key(userId));
    }

    // 新增 / 累加
    public void addItem(Integer userId, Integer productId, Integer quantity) {
        HashOperations<String, Object, Object> ops = redis.opsForHash();
        Integer cur = (Integer) ops.get(key(userId), productId.toString());
        int newQty = (cur == null ? 0 : cur) + quantity;
        if (newQty <= 0) {
            ops.delete(key(userId), productId.toString());
        } else {
            ops.put(key(userId), productId.toString(), newQty);
        }
        redis.expire(key(userId), Duration.ofDays(30));
    }

    // 設定數量（<=0 視為刪除）
    public void setQuantity(Integer userId, Integer productId, Integer quantity) {
        HashOperations<String, Object, Object> ops = redis.opsForHash();
        if (quantity == null || quantity <= 0) {
            ops.delete(key(userId), productId.toString());
        } else {
            ops.put(key(userId), productId.toString(), quantity);
            redis.expire(key(userId), Duration.ofDays(30));
        }
    }

    // 刪除單一商品
    public void removeItem(Integer userId, Integer productId) {
        redis.opsForHash().delete(key(userId), productId.toString());
    }

    // 清空購物車
    public void clear(Integer userId) {
        redis.delete(key(userId));
    }
}