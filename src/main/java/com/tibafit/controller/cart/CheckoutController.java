package com.tibafit.controller.cart;

import com.tibafit.dto.cart.CheckoutCreateDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository; // 步驟 1: 匯入你的 UserRepository
import com.tibafit.service.cart.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 建議匯入
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
	private final CheckoutService checkoutService;
	private final UserRepository userRepository; // 步驟 1: 新增 UserRepository 成員變數

	@Autowired
	public CheckoutController(CheckoutService checkoutService, UserRepository userRepository) { // 步驟 2: 更新建構子
		this.checkoutService = checkoutService;
		this.userRepository = userRepository; // 步驟 2: 初始化
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Valid CheckoutCreateDTO req, Authentication authentication) {

		// =================== 步驟 3: 修改這裡的邏輯 ===================
		// 1. 【安全】從 Authentication 物件取得使用者帳號 (email)
		String userEmail = authentication.getName();

		// 2. 【可靠】使用帳號去資料庫撈取完整的 User 物件，確保資料一致性
		User currentUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("找不到已驗證的使用者: " + userEmail));

		// 3. 從撈出的 User 物件取得 userId
		Integer userId = currentUser.getUserId();
		// =============================================================

		try {
			// 呼叫 Service 的邏輯完全不變
			OrdersVO newOrder = checkoutService.createOrderFromCart(
					userId,
					req.getRecipientName(),
					req.getRecipientPhone(),
					req.getRecipientAddress(),
					req.getUsedPoints()
			);
			// 回傳一個包含 orderId 和 orderCode 的物件
			// 這樣前端才知道要用哪個 ID 去呼叫付款 API
			Map<String, Object> response = Map.of(
					"message", "訂單已成功建立",
					"orderId", newOrder.getOrderId(),
					"orderCode", newOrder.getOrderCode()
			);
			return ResponseEntity.ok(Map.of(
					"message", "訂單已成功建立",
					"orderId", newOrder.getOrderId(),
					"orderCode", newOrder.getOrderCode()
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("message", "建立訂單時發生錯誤: " + e.getMessage()));
		}
	}
}