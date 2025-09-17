package com.tibafit.model.cart;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "orders")
public class OrdersVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@CreationTimestamp
	@Column(name = "order_date", nullable = false)
	private LocalDateTime orderDate;

	@Column(name = "order_status", nullable = false)
	private Integer orderStatus = 0;

	@Column(name = "recipient_name", nullable = false, length = 50)
	private String recipientName = "未指定";
	
	@Column(name = "recipient_phone", nullable = false, length = 20)
	private String recipientPhone = "0000000000";
	
	@Column(name = "recipient_address", nullable = false, length = 255)
	private String recipientAddress = "未指定";

	@Column(name = "used_points_amount")
	private Integer usedPointsAmount;
	
	@Column(name = "total_price", nullable = false)
	private Integer totalPrice;

	@Column(name = "payment_time", nullable = false)
	private LocalDateTime paymentTime = LocalDateTime.now();
	
	@Column(name = "payment_status", nullable = false)
	private Integer paymentStatus = 0; // 0未付 1已付 2失敗
	
	@Column(name = "order_code", nullable = false, unique = true, length = 50)
	private String orderCode;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // orders (1) -> order_item (N)
	private List<OrderItemVO> orderItems = new ArrayList<>();

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

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}

	public String getRecipientAddress() {
		return recipientAddress;
	}

	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}

	public Integer getUsedPointsAmount() {
		return usedPointsAmount;
	}

	public void setUsedPointsAmount(Integer usedPointsAmount) {
		this.usedPointsAmount = usedPointsAmount;
	}

	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public LocalDateTime getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(LocalDateTime paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Integer getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public List<OrderItemVO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemVO> orderItems) {
		this.orderItems = orderItems;
	}
	
	
}
