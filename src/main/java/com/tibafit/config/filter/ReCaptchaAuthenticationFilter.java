package com.tibafit.config.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tibafit.exception.RecaptchaValidationException; // 【修正一】引入我們新的例外
import com.tibafit.service.user.ReCaptchaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ReCaptchaAuthenticationFilter extends OncePerRequestFilter {

    private final ReCaptchaService reCaptchaService;
    private final AuthenticationFailureHandler failureHandler;

    @Autowired
    public ReCaptchaAuthenticationFilter(ReCaptchaService reCaptchaService) {
        this.reCaptchaService = reCaptchaService;
        this.failureHandler = new SimpleUrlAuthenticationFailureHandler("/admin/login?error=recaptcha");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

    	// 只攔截 POST /admin/login (提交表單)，不攔截 GET
        if ("/admin/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            
            String recaptchaToken = request.getParameter("g-recaptcha-response");

            if (!reCaptchaService.validateToken(recaptchaToken)) {
                System.out.println("reCAPTCHA 驗證失敗！");
                failureHandler.onAuthenticationFailure(
                    request,
                    response,
                    new RecaptchaValidationException("reCAPTCHA validation failed")
                );
                return;
            }
            System.out.println("reCAPTCHA 驗證成功！");
        }
        
        filterChain.doFilter(request, response);
    }
}