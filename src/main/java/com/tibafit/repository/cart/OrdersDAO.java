package com.tibafit.repository.cart;

import com.tibafit.model.cart.OrdersVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//	findByUserIdOrderByOrderDateDesc(...)：查某使用者的訂單列表（可分頁，時間新→舊）。
//	existsByOrderCode(...)：檢查訂單碼是否重複，方便你在產生訂單碼時做唯一性驗證。

public interface OrdersDAO extends JpaRepository<OrdersVO, Integer> {

	// 以 userId 查詢該使用者的訂單清單（依下單時間「新到舊」排序），並支援分頁/排序參數
	// 使用方式範例：
	// Page<OrdersVO> p = ordersDAO.findByUserIdOrderByOrderDateDesc(userId,
	// PageRequest.of(page, size));
	Page<OrdersVO> findByUserIdOrderByOrderDateAsc(Integer userId, Pageable pageable);

	List<OrdersVO> findByUserIdOrderByOrderDateAsc(Integer userId);

	// 檢查訂單碼是否已存在（用於產生新訂單碼時的唯一性校驗）
	boolean existsByOrderCode(String orderCode);
	Optional<OrdersVO> findByOrderCode(String orderCode);

}
