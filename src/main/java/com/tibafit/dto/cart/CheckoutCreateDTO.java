package com.tibafit.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CheckoutCreateDTO {

	@NotNull(message = "使用者ID必填")
	@Min(value = 1, message = "使用者ID不合法")
	private Integer userId;

	@NotBlank(message = "收貨人姓名必填")
	@Size(min = 2, max = 40, message = "收貨人姓名需 2–40 字")
	private String recipientName;

	@NotBlank(message = "收貨人電話必填")
	@Pattern(regexp = "^(09\\d{8}|02-?\\d{8}|0[3-8]-?\\\\d{7,8})$", message = "收貨人電話格式不正確（手機 09xxxxxxxx 或市話 0x-xxxxxxx / 0xx-xxxxxxx）")
	private String recipientPhone;

	@NotBlank(message = "收貨人地址必填")
	@Size(min = 6, max = 120, message = "收貨人地址需 6–120 字")
	private String recipientAddress;

	@PositiveOrZero(message = "使用點數不得為負")
	private Integer usedPoints;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getUsedPoints() {
		return usedPoints;
	}

	public void setUsedPoints(Integer usedPoints) {
		this.usedPoints = usedPoints;
	}

}
