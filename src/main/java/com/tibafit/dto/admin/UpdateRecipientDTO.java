package com.tibafit.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

//	UpdateRecipientDTO 是後台更新訂單收件資訊的請求模型，包含姓名、地址、電話（＋可選 email、備
//	註）。內建 jakarta.validation 驗證：必填/長度/電話格式/Email 格式。若你的 API 路徑本身就帶
//	/{id}，建議把 DTO 的 orderId 拿掉，避免 path 與 body 重複而可能不一致。

@NoArgsConstructor // Lombok：無參數建構子
@AllArgsConstructor // Lombok：全參數建構子
@Builder // Lombok：builder() 建構器（若使用，欄位名需與本類一致）
public class UpdateRecipientDTO {

	@NotNull(message = "訂單ID必填")
	private Integer orderId; // 目標訂單 ID
								// ⚠ 如果你的 API 路徑已經是 /orders/{id}，此欄就會顯得「冗餘」；
								// 建議可移除，避免 path 與 body 內的 id 不一致（維持單一真相）

	@NotBlank
	@Size(max = 50)
	private String recipientName; // 收件人姓名

	@NotBlank
	@Size(max = 100)
	private String recipientAddress; // 收件地址

	@NotBlank
	@Pattern(regexp = "^[0-9+\\-() ]{8,20}$", message = "連絡電話格式不正確")
	private String recipientPhone; // 聯絡電話
									// 📌 若要台灣市話（一般）可用：0\\d{1,2}-?\\d{6,8}
									// 📌 僅 02 開頭：^02-?\\d{8}$
									// 📌 台灣手機：^09\\d{8}$（依需求擇一或前端/後端分流驗證）

	@Email
	@Size(max = 100)
	private String email; // Email（可為空，但若有就必須符合 Email 格式）

	@Size(max = 300)
	private String note; // 備註（可空）

	// ===== Getter / Setter（遵循 JavaBean 命名，以利 Spring/Jackson 綁定）=====

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientAddress() {
		return recipientAddress;
	}

	public void setRecipientAddress(String recipientAddress) { // 修正參數名稱
		this.recipientAddress = recipientAddress;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) { // 修正參數名稱
		this.recipientPhone = recipientPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}