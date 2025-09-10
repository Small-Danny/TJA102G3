package com.tibafit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 這個 Bean 專門用來處理「完全不需要」安全檢查的靜態資源。
     * 效能最好，因為它直接繞過了 Spring Security 的過濾鏈。
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 【修正】同時忽略後台和前台的靜態資源資料夾
        return (web) -> web.ignoring().requestMatchers(
            "/adminlte/**",          // 忽略後台樣板的資源
            "/frontend-template/**", // 忽略前台樣板的資源 (CSS, JS等)
            "/images/**"             // 忽略共用的圖片資源
        );
    }
    /**
     * 這個 Bean 專門用來設定需要「動態權限」的 API 路徑。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
            		.requestMatchers(
                // 【修正】將 /api/captcha 也明確地加入公開 API 清單
                    // --- 前台靜態頁面 (請根據你的檔名修改) ---
                    "/", "/index.html", "/login.html", "/register.html", 
                    "/forgot-password.html", "/reset-set-password.html",

                    // --- 後台儀表板 ---
                    "/admin/dashboard",

                    // --- 公開的 API ---
                    "/api/captcha",
                    "/api/users/register",
                    "/api/users/send-code",
                    "/api/users/login",
                    "/api/users/request-password-reset",
                    "/api/users/reset-password-with-token"
                ).permitAll()
                // 除了上面清單中的 API，任何其他的 API 都需要登入後才能訪問
                .anyRequest().authenticated()
            );

        // 功能開關（維持不變）
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable());

        return http.build();
    }
}