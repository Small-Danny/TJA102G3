package com.tibafit.dto.admin;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

//	orderId：目標訂單
//	productId：要加入的商品
//	productName / buyPrice：下單當下的名稱與單價快照（避免日後商品改名改價影響歷史訂單）
//	quantity：購買數量（> 0）
//	內含 jakarta.validation 驗證註解，可由控制器的 @Valid 自動檢查

@NoArgsConstructor // Lombok：產生無參數建構子
@AllArgsConstructor // Lombok：產生全參數建構子
@Builder // Lombok：產生 builder()（若要用，欄位名需與此類一致）
public class AddOrderItemDTO {

	@NotNull(message = "訂單ID必填")
	private Integer orderId; // 要加進哪一筆訂單（若路徑已有 /orders/{orderId}，此欄可能冗餘）

	@NotNull(message = "產品ID必填")
	private Integer productId; // 指定要加入的商品

	/** 下單當下的名稱快照（避免商品改名影響歷史訂單） */
	@NotBlank
	@Size(max = 255)
	private String productName;

	/** 下單當下的成交單價快照（避免商品改價影響歷史訂單） */
	@NotNull
	@PositiveOrZero
	private Integer buyPrice; // 金額若需小數建議改 BigDecimal；目前以整數元儲存

	@NotNull
	@Positive(message = "數量需大於 0")
	private Integer quantity; // 購買數量（必須 > 0）

	// ===== Getter / Setter（依 JavaBean 規範命名，便於框架與 JSON 映射）=====

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	// ⚠ 修正命名：getBuyPrice（B 要大寫）
	// - 影響 JSON 映射與框架（例如 Spring、Jackson）辨識屬性名稱為 "buyPrice"
	public Integer getBuyPrice() {
		return buyPrice;
	}

	// 參數命名用小寫開頭（慣例），避免和欄位名混淆
	public void setBuyPrice(Integer buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
