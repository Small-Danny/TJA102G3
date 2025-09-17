package com.tibafit.dto.admin;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


//	orderId：要更新的訂單（若路徑已有 /{id}，建議移除此欄或於後端忽略 body 的 orderId，以免不一致）
//	orderStatus：訂單狀態（可為 null＝不變更；建議統一常數/Enum）
//	paymentStatus：付款狀態（可為 null＝不變更；當改為「已付款=1」時，後端通常要同步設定 paymentTime）
//	paymentTime：付款時間（可由前端帶或後端自動 now()；注意 LocalDateTime 沒有時區資訊，建議後端統一處理）

@NoArgsConstructor // Lombok：無參數建構子
@AllArgsConstructor // Lombok：全參數建構子
@Builder // Lombok：builder()；若使用需注意欄位名一致
public class UpdateStatusDTO {

	@NotNull(message = "訂單ID必填")
	private Integer orderId; // 目標訂單 ID
								// ⚠ 若 API 路徑已有 /orders/{id}，此欄可能「重複」；建議改由 path 指定，DTO 不帶 orderId。

	/**
	 * 訂單狀態（範例：0 新訂單、1 處理中/已付款、2 已出貨、3 完成、4 取消 …） 建議集中管理成常數或 Enum，避免魔術數字散落各處。
	 */
	private Integer orderStatus; // 可為 null：表示不變更

	/**
	 * 付款狀態（0 未付款、1 已付款） 若設定為 1，通常應同步寫入 paymentTime（或由後端自動 now()）。
	 */
	private Integer paymentStatus; // 可為 null：表示不變更

	/**
	 * 付款時間 - 若設為已付款可一併帶時間；不帶則由後端以 now() 補上。 - 型別 LocalDateTime
	 * 無時區資訊：序列化/儲存時請注意時區（建議後端統一以系統時區或 UTC）。
	 */
	private LocalDateTime paymentTime;

	// ===== Getter / Setter（遵循 JavaBean 規範，便於 Spring/Jackson 綁定）=====

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
