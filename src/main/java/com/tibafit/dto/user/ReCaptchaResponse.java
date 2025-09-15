package com.tibafit.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReCaptchaResponse {
	
	//接收 Google reCAPTCHA 驗證伺服器回傳給我們的 JSON 結果。
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    private String hostname;

    @JsonProperty("error-codes")
    private String[] errorCodes;
}
