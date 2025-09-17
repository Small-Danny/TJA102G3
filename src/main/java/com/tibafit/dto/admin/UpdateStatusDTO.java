package com.tibafit.dto.admin;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusDTO {

    @NotNull(message = "訂單ID必填")
    private Integer orderId;

    /** 例如：0 新訂單、1 已付款、2 已出貨、3 完成、4 取消 ... */
    private Integer orderStatus;

    /** 0 未付款、1 已付款 */
    private Integer paymentStatus;

    /** 若設為已付款可一併帶付款時間，不帶則由後端自動 now() */
    private LocalDateTime paymentTime;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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
    
    
}
