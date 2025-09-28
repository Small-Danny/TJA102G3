package com.tibafit.dto.cart;

import com.fasterxml.jackson.annotation.JsonProperty; // ★★★ 1. 記得 import 這個 ★★★
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartAddItemDTO {

	// ★★★ 修正 #1：完全刪除 userId 欄位 ★★★
	// 我們不再信任前端傳來的 userId，而是由 Controller 從登入狀態判斷。

	@NotNull
	private Integer productId;

	@NotNull
	@Min(1)
	@JsonProperty("quantity") // ★★★ 修正 #2：告訴 Spring，前端傳來的 "quantity" 欄位要對應到這個 "qty" 變數 ★★★
	private Integer qty;

	// ===== Getter / Setter (保持不變) =====
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