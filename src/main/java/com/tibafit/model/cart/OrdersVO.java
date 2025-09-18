package com.tibafit.model.cart;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.*;

//	OrdersVO 是訂單主檔的 JPA 實體，記錄下單者、下單時間、收件資訊、使用點數、總價、付款狀態與付款時
//	間（未付款為 null），以及與多筆 OrderItemVO 的關聯。
//	建立訂單時寫入主檔與明細；付款流程更新 paymentStatus 與 paymentTime。

@Entity // JPA 實體：對應資料表 orders（訂單主檔）
@Table(name = "orders")
public class OrdersVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主鍵（MySQL AUTO_INCREMENT）
	@Column(name = "order_id")
	private Integer orderId;

	@Column(name = "user_id", nullable = false) // 下單者 ID
	private Integer userId;

	@CreationTimestamp // 由 Hibernate 在 INSERT 時自動填入建立時間
	@Column(name = "order_date", nullable = false)
	private LocalDateTime orderDate;

	@Column(name = "order_status", nullable = false) // 訂單狀態（整數碼；由服務層控制流程）
	private Integer orderStatus = 0;

	@Column(name = "recipient_name", nullable = false, length = 50) // 收件人姓名
	private String recipientName = "未指定";

	@Column(name = "recipient_phone", nullable = false, length = 20) // 收件人電話
	private String recipientPhone = "0000000000";

	@Column(name = "recipient_address", nullable = false, length = 255) // 收件地址
	private String recipientAddress = "未指定";

	@Column(name = "used_points_amount") // 使用點數（可為 null）
	private Integer usedPointsAmount;

	@Column(name = "total_price", nullable = false) // 訂單總價（由明細加總後寫入）
	private Integer totalPrice;

	@Column(name = "payment_time", nullable = true) // ✅ 可為 null（未付款時不寫入時間）
	private LocalDateTime paymentTime;

	@Column(name = "payment_status", nullable = false) // ✅ 付款狀態（0 未付 / 1 已付 / 2 失敗）
	private Integer paymentStatus = 0;

	@Column(name = "order_code", nullable = false, unique = true, length = 50) // 訂單代碼/流水號
	private String orderCode;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // 一對多：orders(1) -> order_item(N)
	private List<OrderItemVO> orderItems = new ArrayList<>(); // 維持父子生命週期一致（cascade+orphanRemoval）

	// ===== Getter / Setter =====

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
