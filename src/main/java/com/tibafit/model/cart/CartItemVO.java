package com.tibafit.model.cart;

import jakarta.persistence.*;

//	CartItemVO 是對應資料表 cart_item 的 JPA 實體，描述「某使用者購物車中的某商品與其數量」。雖然專
//	案以 Redis 儲存購物車為主，這張表可作為快照/稽核/後台檢視用途（例如 CartAdminController 的查詢）。

@Entity // JPA 實體：對應資料表 cart_item（本專案以 Redis 為主，這張表多用於快照/稽核或後台檢視）
@Table(name = "cart_item")
public class CartItemVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主鍵（對應 MySQL AUTO_INCREMENT）
	@Column(name = "cart_item_id")
	private Integer cartItemId;

	@Column(name = "product_id", nullable = false) // 商品 ID（未建立關聯；若常需要商品資訊，可考慮建立 @ManyToOne）
	private Integer productId;

	@Column(name = "user_id", nullable = false) // 使用者 ID
	private Integer userId;

	@Column(name = "cart_item_quantity", nullable = false) // 數量（>0；可在 Service/DB 層加約束）
	private Integer cartItemQuantity;

	// ===== Getter / Setter（JavaBean 命名，便於 JPA/Spring/Jackson 使用）=====
	public Integer getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(Integer cartItemId) {
		this.cartItemId = cartItemId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCartItemQuantity() {
		return cartItemQuantity;
	}

	public void setCartItemQuantity(Integer cartItemQuantity) {
		this.cartItemQuantity = cartItemQuantity;
	}
}
