package com.tibafit.dto.cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.tibafit.model.cart.OrdersVO;

//	OrdersDTO 是整張訂單的回傳模型。把資料庫的 OrdersVO（主檔 + 明細）轉成前端需要的欄位格式，包含
//	訂單基本資訊與 items 明細列表（由 OrderItemDTO 組成）。在 from(OrdersVO) 中也做了明細為 null 的防
//	呆，避免沒有明細時發生 NPE。

public class OrdersDTO {
	private Integer orderId; // 訂單主鍵
	private Integer userId; // 使用者 ID
	private LocalDateTime orderDate; // 下單時間（注意：無時區資訊，前端顯示需留意時區）
	private Integer orderStatus; // 訂單狀態（建議用常數/Enum 統一管理）
	private Integer paymentStatus; // 付款狀態（0 未付款 / 1 已付款…）
	private LocalDateTime paymentTime; // 付款時間（若已付款）
	private Integer totalPrice; // 訂單總價（通常為所有明細小計加總）
	private String orderCode; // 訂單代碼/流水號（若有）
	private List<OrderItemDTO> items; // 訂單明細清單

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====
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

	/**
	 * 將 JPA Entity（OrdersVO）轉為前端友善的 DTO。 - 直接映射主檔欄位（id、userId、時間、狀態、總價、代碼） - 明細列表以
	 * stream 將 OrderItemVO → OrderItemDTO - 加上空值防護：若明細為 null，回傳空清單避免 NPE
	 */
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

		// 明細轉換（null → 空清單，避免 NullPointerException）
		var orderItems = ov.getOrderItems();
		otd.setItems(orderItems == null ? java.util.Collections.emptyList()
				: orderItems.stream().map(OrderItemDTO::from).collect(Collectors.toList()));
		return otd;
	}
}
