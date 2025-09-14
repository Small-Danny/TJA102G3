package com.tibafit.controller.user;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CsrfController {

    /**
     * 提供一個公開的 API 端點，用於獲取當前的 CSRF Token。
     * 靜態前端頁面 (如 login.html) 可以透過 JavaScript 呼叫此 API 來取得 Token，
     * 以便在後續的 POST (如登入) 請求中攜帶。
     */
    @GetMapping("/api/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        // Spring Security 會自動將 CsrfToken 物件附加到每個請求上，我們只需將其取出並回傳
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}
