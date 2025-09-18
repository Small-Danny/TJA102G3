package com.tibafit.repository.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.cart.CartItemVO;

//	CartItemDAO 是 cart_item 表的 Spring Data JPA 存取介面。除了繼承 JpaRepository 取得通用 CRUD
//	外，還定義了幾個以命名規約產生的查詢/刪除/存在性方法，方便以 userId / productId 維度操作購物車快
//	照資料。適用於後台檢視、除錯或與 Redis 同步的情境。

public interface CartItemDAO extends JpaRepository<CartItemVO, Integer> {
	// 介面繼承 JpaRepository<實體, 主鍵型別>，自帶 CRUD、分頁、排序等常用方法

	List<CartItemVO> findByUserId(Integer userId);
	// 依使用者 ID 取得該用戶購物車所有記錄（通常用於後台檢視或快照功能）

	Optional<CartItemVO> findByUserIdAndProductId(Integer userId, Integer productId);
	// 取得某用戶某商品的單筆購物車記錄（若不存在回傳 Optional.empty()）

	void deleteByUserId(Integer userId);
	// 刪除某用戶的整車（物理刪除；若僅使用 Redis，可作為快照清理）

	void deleteByUserIdAndProductId(Integer userId, Integer productId);
	// 刪除某用戶購物車中的特定商品

	boolean existsByUserIdAndProductId(Integer userId, Integer productId);
	// 檢查某用戶購物車是否已存在某商品（用於避免重複新增、決定改為更新數量）
}
