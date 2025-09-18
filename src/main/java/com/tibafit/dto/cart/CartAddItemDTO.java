package com.tibafit.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

//	CartAddItemDTO 是前台加入購物車的請求模型，包含 userId、productId、qty(≥1)。控制器端配合
//	@Valid 就能自動驗證。實務上建議後端從登入會話取得 userId，避免被惡意指定他人帳號。

public class CartAddItemDTO {

	@NotNull
	private Integer userId; // 使用者 ID（⚠ 正式環境建議由後端登入會話取得，避免前端任意指定）

	@NotNull
	private Integer productId; // 要加入購物車的商品 ID

	@NotNull
	@Min(1)
	private Integer qty; // 加入數量（至少 1）

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}
}
