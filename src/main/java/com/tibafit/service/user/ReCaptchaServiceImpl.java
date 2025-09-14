package com.tibafit.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.tibafit.dto.user.ReCaptchaResponse;

@Service
public class ReCaptchaServiceImpl implements ReCaptchaService{
	
	// Google reCAPTCHA 驗證的官方網址
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // 從 application.properties 中讀取我們儲存的密鑰
    @Value("${google.recaptcha.secret-key}")
    private String secretKey;
    
    // 使用 RestTemplate 來發送 HTTP的外部請求,是Spring用的
    private final RestTemplate restTemplate;

    public ReCaptchaServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

	@Override
	public boolean validateToken(String recaptchaToken) {
		if(recaptchaToken == null || recaptchaToken.isEmpty()) {
		return false;
	}
		  try {
	            // 準備要發送給 Google 的參數，對應RestTemplate
			  	// MultiValueMap允許一個單字（Key）擁有多個解釋（Value）的特殊字典
	            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
	            requestMap.add("secret", secretKey); // 我們的密鑰
	            requestMap.add("response", recaptchaToken); // 前端傳來的 token
	            // 發送 POST 請求，並接收 Google 的回應
	            ReCaptchaResponse response = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, requestMap, ReCaptchaResponse.class);

	            // 如果 Google 回應的 DTO 物件存在，且裡面的 success 欄位為 true，就代表驗證成功
	            return response != null && response.isSuccess();

	        } catch (Exception e) {
	            // 如果在與 Google 溝通的過程中發生任何錯誤，都視為驗證失敗
	            e.printStackTrace();
	            return false;
	        }
	    }
}
