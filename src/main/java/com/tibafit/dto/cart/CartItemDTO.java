package com.tibafit.dto.cart;

//	CartItemDTO 是購物車內單一品項的資料模型，只包含 productId 與 quantity。它通常由後端把 Redis 的
//	{ productId → qty } 轉成陣列時使用（搭配 CartDTO.items），前端再依 productId 去顯示名稱、單價與
//	小計。若未來要在同一筆明細回傳單價/小計，可擴充欄位如 unitPrice、subtotal。

public class CartItemDTO {
	private Integer productId; // 商品 ID（用來到產品表查名稱/價格/圖片等）
	private Integer quantity;  // 該商品在購物車中的數量

	// 便利建構子：一次指定商品與數量（常在把 Redis Hash 轉 DTO 時使用）
	public CartItemDTO(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
