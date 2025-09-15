package com.tibafit.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecipientDTO {

    @NotNull(message = "訂單ID必填")
    private Integer orderId;

    @NotBlank
    @Size(max = 50)
    private String recipientName;

    @NotBlank 
    @Size(max = 100)
    private String recipientAddress;

    @NotBlank
    @Pattern(regexp = "^[0-9+\\-() ]{8,20}$", message = "連絡電話格式不正確")
    private String recipientPhone;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 300)
    private String note;

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

	public void setRecipientAddress(String recipienAddress) {
		this.recipientAddress = recipienAddress;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipienPhone) {
		this.recipientPhone = recipienPhone;
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
