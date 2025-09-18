package com.tibafit.controller.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.dto.cart.PaymentMockDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.service.cart.PaymentService;

import jakarta.validation.Valid;

//	MockPaymentController 提供假金流回傳 API（POST /api/payments/mock）。前端送入 userId / orderId /
//	success 來模擬支付成功或失敗；控制器呼叫 PaymentService.mockPay(...) 更新訂單的付款狀態（並可寫
//	入付款時間、調整訂單狀態），最後以 OrdersDTO 回傳更新後的訂單。

@RestController // 這是一支 REST 控制器，方法回傳值會序列化為 JSON
@RequestMapping("/api/payments") // 統一路徑前綴（後面再接 /mock）
public class MockPaymentController {
	private final PaymentService paymentService; // 金流服務（這裡用「假金流」實作，更新付款/訂單狀態）

	@Autowired // 建構子注入：交由 Spring 注入 PaymentService
	public MockPaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	/**
	 * 假金流的支付結果回傳（前端模擬用） 例：POST /api/payments/mock body = { "userId":101,
	 * "orderId":123, "success":true } 成功時通常會把 orders.paymentStatus=1 並寫入
	 * paymentTime； 也可依流程同步調整 orderStatus（例如：待處理→處理中）。
	 */
	@PostMapping("/mock")
	public OrdersDTO mock(@RequestBody @Valid PaymentMockDTO req) {
		// ⚠︎ 實務上建議「userId」不要由前端傳，而是從登入會話取得，避免越權操作
		OrdersVO ov = paymentService.mockPay(req.getUserId(), // 使用者 ID（建議改由 session 取得）
				req.getOrderId(), // 訂單 ID
				req.isSuccess()); // 付款結果（true=成功 / false=失敗）

		// 轉為乾淨的回傳模型，避免直接曝露 Entity
		return OrdersDTO.from(ov);
	}
}
