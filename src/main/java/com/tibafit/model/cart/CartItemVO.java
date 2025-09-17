package com.tibafit.model.cart;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItemVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id")
	private Integer cartItemId;
	@Column(name = "product_id", nullable = false)
	private Integer productId;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "cart_item_quantity", nullable = false)
	private Integer cartItemQuantity;
	
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
