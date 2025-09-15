package com.tibafit.service.user;

public interface ReCaptchaService {

	  /**
     * 驗證前端傳來的 reCAPTCHA token 是否有效
     * @param recaptchaToken 前端 reCAPTCHA 元件產生的 token
     * @return true 如果驗證成功，false 如果失敗
     */
    boolean validateToken(String recaptchaToken);

}
