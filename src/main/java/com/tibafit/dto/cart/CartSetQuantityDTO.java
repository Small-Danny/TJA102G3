package com.tibafit.dto.cart;

import jakarta.validation.constraints.NotNull;

//	CartSetQuantityDTO 是前台設定購物車中某商品數量時用的請求模型，包含 productId、qty（約定 <=0
//	表刪除）、userId。控制器配合 @Valid 可先做非空檢查；修正 setter 後，userId 才能正確由 JSON 綁定
//	到物件。

public class CartSetQuantityDTO {

	@NotNull
	private Integer productId; // 要設定數量的商品 ID

	@NotNull
	private Integer qty; // 新數量（約定：<= 0 代表刪除此商品）

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====


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
