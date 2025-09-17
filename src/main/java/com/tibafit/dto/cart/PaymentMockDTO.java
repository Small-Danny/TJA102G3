package com.tibafit.dto.cart;

import jakarta.validation.constraints.NotNull;

public class PaymentMockDTO {
	@NotNull
	private Integer userId;
	@NotNull
	private Integer orderId;
	private boolean success = true;
	
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
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
