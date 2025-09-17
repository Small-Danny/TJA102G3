package com.tibafit.dto.cart;

import jakarta.validation.constraints.NotNull;

//	PaymentMockDTO 是假金流回傳用的請求模型，攜帶 userId、orderId 與 success（預設 true）。控制器
//	MockPaymentController 會用它來呼叫 PaymentService.mockPay(...)，依結果更新訂單的付款狀態；正式環
//	境建議改由後端從登入會話取得 userId，避免越權。

public class PaymentMockDTO {
	@NotNull
	private Integer userId; // 使用者 ID（⚠ 正式建議從後端登入會話取得，避免前端任意指定）

	@NotNull
	private Integer orderId; // 要模擬付款的訂單 ID

	private boolean success = true; // 模擬結果：true=成功 / false=失敗（預設成功）

	// ===== Getter / Setter =====
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public boolean isSuccess() { // boolean 慣例使用 isXxx 作為 getter
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
