package com.tibafit.dto.cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.tibafit.model.cart.OrdersVO;

public class OrdersDTO {
	private Integer orderId;
	private Integer userId;
	private LocalDateTime orderDate;
	private Integer orderStatus;
	private Integer paymentStatus;
	private LocalDateTime paymentTime;
	private Integer totalPrice;
	private String orderCode;
	private List<OrderItemDTO> items;
	
	
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(LocalDateTime paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public List<OrderItemDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}

	public static OrdersDTO from(OrdersVO ov) {
		OrdersDTO otd = new OrdersDTO();
		otd.setOrderId(ov.getOrderId());
		otd.setUserId(ov.getUserId());
		otd.setOrderDate(ov.getOrderDate());
		otd.setOrderStatus(ov.getOrderStatus());
		otd.setPaymentStatus(ov.getPaymentStatus());
		otd.setPaymentTime(ov.getPaymentTime());
		otd.setTotalPrice(ov.getTotalPrice());
		otd.setOrderCode(ov.getOrderCode());
		otd.setItems(ov.getOrderItems().stream().map(OrderItemDTO::from).collect(Collectors.toList()));
		return otd;
	}
}
