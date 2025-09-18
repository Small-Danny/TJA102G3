package com.tibafit.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

//	quantity：新的數量（>0）
//	內建 jakarta.validation 驗證，控制器用 @Valid 就能自動檢查。

@NoArgsConstructor // Lombok：產生無參數建構子
@AllArgsConstructor // Lombok：產生全參數建構子
@Builder // Lombok：產生 builder()（若要用，欄位名需與此類一致）
public class UpdateOrderItemQtyDTO {

	@NotNull // 必填
	@Positive(message = "數量需大於 0") // 驗證規則：> 0
	private Integer quantity; // 新的數量

	// Getter / Setter（JavaBean 命名，方便 Spring/Jackson 綁定）
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}