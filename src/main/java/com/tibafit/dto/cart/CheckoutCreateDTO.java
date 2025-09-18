package com.tibafit.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

//	userId（建議改由後端會話取得）
//	收件資訊：recipientName、recipientPhone（手機或市話格式）、recipientAddress
//	usedPoints（可為 0 或不填，後端視為 0）
//	內建 jakarta.validation 會在進入 Controller 前先驗證必填、長度與電話格式，避免不合法資料進入服務層。

public class CheckoutCreateDTO {

	@NotNull(message = "使用者ID必填")
	@Min(value = 1, message = "使用者ID不合法")
	private Integer userId; // ⚠ 正式環境建議從登入會話取得 userId，避免前端任意指定造成越權

	@NotBlank(message = "收貨人姓名必填")
	@Size(min = 2, max = 40, message = "收貨人姓名需 2–40 字")
	private String recipientName; // 收貨人姓名

	@NotBlank(message = "收貨人電話必填")
	@Pattern(
			// 手機：09xxxxxxxx；台北市話：02-xxxxxxxx；其他市話：0[3-8]-xxxxxxx/xxxxxxxx
			// ✅ 修正：使用 \\d 表示數字類別（原本的 \\\\d 會變成「\d」字面量，無法匹配數字）
			regexp = "^(09\\d{8}|02-?\\d{8}|0[3-8]-?\\d{7,8})$", message = "收貨人電話格式不正確（手機 09xxxxxxxx 或市話 0x-xxxxxxx / 0xx-xxxxxxx）")
	private String recipientPhone; // 聯絡電話

	@NotBlank(message = "收貨人地址必填")
	@Size(min = 6, max = 120, message = "收貨人地址需 6–120 字")
	private String recipientAddress; // 收貨地址

	@PositiveOrZero(message = "使用點數不得為負")
	private Integer usedPoints; // 使用點數（可為 null；服務層可將 null 視為 0）

	// ===== Getter / Setter =====

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
