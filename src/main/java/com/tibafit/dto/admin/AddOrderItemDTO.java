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

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddOrderItemDTO {

    @NotNull(message = "訂單ID必填")
    private Integer orderId;

    @NotNull(message = "產品ID必填")
    private Integer productId;

    /** 建議下單時快照名稱與單價，以免商品改名改價影響歷史訂單 */
    @NotBlank 
    @Size(max = 255)
    private String productName;

    @NotNull 
    @PositiveOrZero
    private Integer BuyPrice;

    @NotNull 
    @Positive(message = "數量需大於 0")
    private Integer quantity;

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

	public Integer getBuyPrice() {
		return BuyPrice;
	}

	public void setBuyPrice(Integer BuyPrice) {
		this.BuyPrice = BuyPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
    
    
}
