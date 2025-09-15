package com.tibafit.dto.cart;

import jakarta.validation.constraints.NotNull;

public class CartSetQuantityDTO {

	@NotNull
	private Integer productId;
	
	@NotNull
	private Integer qty;  // <= 0 視為刪除
	
	@NotNull
	private Integer usertId;
	
	
	public Integer getUsertId() {
		return usertId;
	}

	public void setUsertId(Integer usertId) {
		this.usertId = usertId;
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
