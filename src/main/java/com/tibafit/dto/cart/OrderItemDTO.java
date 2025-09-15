package com.tibafit.dto.cart;

import com.tibafit.model.cart.OrderItemVO;

public class OrderItemDTO {
	private Integer productId;
	private Integer quantity;
	private Integer buyPrice;
	private Integer itemTotalPrice;
	private String orderItemCode;
	
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

	public Integer getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Integer buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Integer getItemTotalPrice() {
		return itemTotalPrice;
	}

	public void setItemTotalPrice(Integer itemTotalPrice) {
		this.itemTotalPrice = itemTotalPrice;
	}

	public String getOrderItemCode() {
		return orderItemCode;
	}

	public void setOrderItemCode(String orderItemCode) {
		this.orderItemCode = orderItemCode;
	}

	public static OrderItemDTO from(OrderItemVO oiv) {
		var oid = new OrderItemDTO();
		oid.setProductId(oiv.getProduct().getProductId());
		oid.setQuantity(oiv.getOrderItemQuantity());
		oid.setBuyPrice(oiv.getBuyPrice());
	    oid.setItemTotalPrice(oiv.getItemTotalPrice());
	    oid.setOrderItemCode(oiv.getOrderItemCode());
	    return oid;
	}
}
