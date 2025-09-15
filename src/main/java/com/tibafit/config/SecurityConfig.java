package com.tibafit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.tibafit.config.filter.ReCaptchaAuthenticationFilter;
import com.tibafit.service.user.AdminDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    
    // 注入一個自定義的認證過濾器，用於在處理登入前進行 ReCaptcha 驗證。
    @Autowired
    private ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter;

    // 注入後台管理員專用的 UserDetailsService，用於從資料庫載入管理員使用者資訊。
    @Autowired
    private AdminDetailsService adminDetailsService;
    
    // 從 application.properties 或其他配置檔中讀取 Remember Me 功能所需的密鑰。
    @Value("${app.security.remember-me-key}")
    private String rememberMeKey;
    
    /**
     * @Bean 方法: passwordEncoder()
     * 作用: 提供一個密碼編碼器（BCryptPasswordEncoder），用於對使用者密碼進行安全雜湊。
     * 這是 Spring Security 推薦的密碼儲存方式。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * @Bean 方法: authenticationManager()
     * 作用: 負責管理整個認證流程。這個 Bean 會被自動注入到其他需要進行認證的服務中。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    /**
     * @Bean 方法: adminAuthenticationProvider()
     * 作用: 建立一個專門用於後台管理員認證的 DaoAuthenticationProvider。
     * 它明確指定了使用 adminDetailsService 和 passwordEncoder 來處理後台登入。
     * 這是一個良好的實踐，可以將前台與後台的認證邏輯分開。
     */
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService); //手動注入，避免自動有問題
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * @Bean 方法: rememberMeServices()
     * 作用: 此 Bean 專門為「前台」會員登入的 Remember Me 功能提供服務。
     * 它使用了 TokenBasedRememberMeServices，並設定了有效期為 14 天 (86400 * 14)。
     * 註解說明此 Bean 專門給前台的 UserServiceImpl 注入使用。
     */
    @Bean
    public RememberMeServices rememberMeServices(@Qualifier("userDetailsService") UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14);
        return rememberMe;
    }
    
    /**
     * @Bean 方法: securityFilterChain()
     * 作用: 這是整個應用程式的核心安全配置，定義了所有 API 或頁面的權限規則。
     * @param http HttpSecurity 配置介面
     * @return SecurityFilterChain 物件
     * @throws Exception 拋出配置異常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(adminAuthenticationProvider()) // ★ 關鍵：明確指定使用後台的認證提供者
           .authorizeHttpRequests(authorize -> authorize
                // 具體範例: 團隊協作時，新功能的程式碼應新增在此區塊。
                // 為了確保規則被正確匹配，新的、更精確的規則應放置於較通用的規則之前。
                // 例如，為後台管理功能新增的規則，應放在公開 API 規則之前。
                
                // (A) 公開 API 與靜態資源：任何人皆可訪問。
                // 這些路徑無需身份驗證，通常用於首頁、登入、註冊、靜態資源、以及公開的 API。
                // 程式碼應在此處統一列出所有公開路徑。
               .requestMatchers(
                    "/", "/index.html", "/login.html", "/register.html", "/forgot-password.html",
                    "/reset-set-password.html", "/css/**", "/js/**", "/images/**", "/fonts/**", "/assets/**",
                    "/adminlte/**", "/frontend-template/**", "/avatars/**", "/api/**"
                ).permitAll()
                
                // (B) 管理員專屬頁面：需要 'ADMIN' 角色才能訪問。
               .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // (C) 管制 API：除了上述公開 API 外，任何其他請求都必須已登入 (authenticated)。
                // 這是一個重要的安全策略，也被稱為「預設拒絕」。
                // 任何沒有明確被permitAll()或其他規則豁免的請求，都會被此規則攔截。
               .anyRequest().authenticated()
            )
           .formLogin(form -> form
                // 設定後台登入頁面與處理 URL，與前台分開，避免衝突。
               .loginPage("/admin/login")
               .loginProcessingUrl("/admin/login")
               .defaultSuccessUrl("/admin/dashboard", true)
               .failureUrl("/admin/login?error=true")
               .permitAll() // 登入頁面必須允許所有人訪問
            )
           .rememberMe(remember -> remember
                // 【修正】為後台的 rememberMe 建立一個「獨立」的設定，不再共用 Bean
               .key(rememberMeKey) // 使用我們注入的密鑰
               .userDetailsService(adminDetailsService) // ★★★ 關鍵：明確指定後台要用 AdminDetailsService
               .tokenValiditySeconds(86400 * 14) // 設定有效期
            )
           .logout(logout -> logout
               .logoutUrl("/admin/logout") // 設定登出 URL
               .logoutSuccessHandler(customLogoutSuccessHandler) // 使用自定義登出處理器
            )
            // 將自定義的 ReCaptcha 過濾器新增到 Spring Security 預設的認證過濾器之前。
            // 這確保了在驗證使用者名稱和密碼之前，會先執行 ReCaptcha 驗證。
           .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
