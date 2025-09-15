package com.tibafit.config;
import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // 主要讓前後台按登出確認對應的邏輯應該往哪
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // 獲取觸發登出的頁面 URL (Referer Header)
        String refererUrl = request.getHeader("Referer");

        // 檢查 Referer 是否存在，並且是否包含 "/admin/" 路徑
        if (refererUrl != null && refererUrl.contains("/admin/")) {
            // 如果是從後台頁面登出的，就導向後台登入頁
            response.sendRedirect(request.getContextPath() + "/admin/login?logout=true");
        } else {
            // 否則 (來自前台或任何其他地方)，一律導向到前台首頁
            response.sendRedirect(request.getContextPath() + "/frontend-template/index.html");
        }
    }
}