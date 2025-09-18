package com.tibafit.controller.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.CheckoutCreateDTO;
import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.service.cart.CheckoutService;

import jakarta.validation.Valid;

//	CheckoutController 提供結帳建立訂單的入口 API（POST /api/checkout）。接收前端送來的收件資訊與點
//	數，呼叫 CheckoutService 將目前使用者在 Redis 的購物車轉成 MySQL 訂單（同時檢庫、扣庫存、建立
//	orders/order_item、清空購物車），最後以 OrdersDTO 回傳新訂單內容。建議實務上改由後端從登入會話
//	取得 userId，避免由前端任意指定。

@RestController // 宣告為 REST 控制器：方法回傳物件會自動轉成 JSON
@RequestMapping("/api/checkout") // 本控制器的共同路徑（前端呼叫 /api/checkout）
public class CheckoutController {
	private final CheckoutService checkoutService; // 結帳服務：把 Redis 購物車轉成 MySQL 訂單、扣庫存、清空購物車

	@Autowired // 建構子注入：Spring 會自動注入 CheckoutService Bean
	public CheckoutController(CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	/**
	 * 建立訂單（由購物車產生） 前端傳入收件資料與（可選）使用點數； 服務層會：檢查購物車 → 檢庫/扣庫存 → 建 orders / order_item →
	 * 清空購物車。
	 */
	@PostMapping // POST /api/checkout
	public OrdersDTO create(@RequestBody @Valid CheckoutCreateDTO req) {
		// 1) 取出必要欄位；字串做 trim 防止前後空白
		// 2) usedPoints 若為 null 則當 0 處理（避免 NPE）
		OrdersVO ov = checkoutService.createOrderFromCart(req.getUserId(), // ⚠ 實務上建議從登入會話取得 userId，比較安全
				req.getRecipientName().trim(), // 收件人姓名
				req.getRecipientPhone().trim(), // 收件人電話（格式驗證在 DTO 的 @Valid）
				req.getRecipientAddress().trim(), // 收件人地址
				req.getUsedPoints() == null ? 0 : req.getUsedPoints() // 使用點數（可為 0）
		);

		// 回傳乾淨的 DTO（避免直接曝露 Entity）
		return OrdersDTO.from(ov);
	}
}
