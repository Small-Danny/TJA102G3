package com.tibafit.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibafit.config.filter.ReCaptchaAuthenticationFilter;
import com.tibafit.service.user.AdminDetailsService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security 核心配置類。
 * 這裡是整個應用程式安全機制的總控制中心，負責定義認證、授權、登入登出、CSRF、記住我等所有安全相關的策略。
 * 該配置同時處理了前台（用戶端）和後台（管理端）兩種不同的安全需求。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- Autowired 依賴注入區 ---
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler; // 自定義登出成功處理器

    @Autowired
    private ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter; // Google reCAPTCHA 驗證過濾器

    @Autowired
    private AdminDetailsService adminDetailsService; // 後台管理員用戶服務

    @Autowired
    private PasswordEncoder passwordEncoder; // 全局密碼加密器

    @Value("${app.security.remember-me-key}")
    private String rememberMeKey; // "記住我" 功能的密鑰，從 application.properties 讀取

    @Autowired
    private AuthenticationEntryPoint delegatingAuthenticationEntryPoint; // 認證入口點分發器

    @Autowired
    @Qualifier("customUserAuthenticationProvider")
    private AuthenticationProvider customUserAuthenticationProvider; // 前台用戶認證Provider

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService; // 前台用戶服務

    /**
     * 認證入口點分發器 (AuthenticationEntryPoint Delegator)。
     * 當一個「未經認證」的用戶嘗試訪問受保護資源時，這個 Bean 會被觸發，決定該如何回應。
     * 這是實現前後台分離、API與頁面不同處理方式的關鍵。
     * 使用 LinkedHashMap 來確保規則的匹配順序。
     * * @return AuthenticationEntryPoint 分發器實例
     */
    @Bean
    public static AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

        // 規則1: 如果請求路徑是 /api/**，代表是前端 SPA 發來的 AJAX 請求。
        //        此時不應重定向頁面，而是返回 HTTP 401 Unauthorized 狀態碼和 JSON 錯誤訊息。
        entryPoints.put(new AntPathRequestMatcher("/api/**"), (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("error", "未登入或憑證無效");
            errorDetails.put("message", "請先登入以存取此資源");
            errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
        });

        // 規則2: 如果請求路徑是 /admin/**，代表是訪問後台管理頁面。
        //        此時應重定向到後台的登入頁面。
        entryPoints.put(new AntPathRequestMatcher("/admin/**"), 
            new LoginUrlAuthenticationEntryPoint("/admin/login"));

        // 規則3: 如果請求路徑是 /frontend-template/**，代表是訪問前台頁面。
        //        此時應重定向到前台的登入頁面。
        entryPoints.put(new AntPathRequestMatcher("/frontend-template/**"), 
            new LoginUrlAuthenticationEntryPoint("/login.html"));

        // 預設規則: 對於其他所有未匹配到的請求，默認重定向到前台登入頁。
        final LoginUrlAuthenticationEntryPoint defaultEntryPoint = new LoginUrlAuthenticationEntryPoint("/login.html");

        final DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        delegatingEntryPoint.setDefaultEntryPoint(defaultEntryPoint);
        return delegatingEntryPoint;
    }

    /**
     * 後台管理員認證 Provider (AuthenticationProvider)。
     * 負責處理後台管理員的登入認證邏輯。
     * @return DaoAuthenticationProvider 實例
     */
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService); // 指定使用 AdminDetailsService 來查詢管理員用戶
        authProvider.setPasswordEncoder(passwordEncoder);       // 指定使用全局的 PasswordEncoder 來比對密碼
        return authProvider;
    }

    /**
     * 前台用戶 "記住我" (RememberMe) 服務。
     * 負責生成和解析前台用戶的 remember-me cookie。
     * @return RememberMeServices 實例
     */
    @Bean("userRememberMeServices")
    public RememberMeServices userRememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14); // 設置 token 有效期為 14 天
        // 關鍵設定！因為我們是通過 API 自定義登入，而不是傳統表單提交，
        // 必須設置為 true，讓 RememberMeServices 信任我們的外部調用，而不是自己去檢查請求參數。
        rememberMe.setAlwaysRemember(true);
        return rememberMe;
    }

    /**
     * 後台管理員 "記住我" (RememberMe) 服務。
     * 負責生成和解析後台管理員的 remember-me cookie。
     * @return RememberMeServices 實例
     */
    @Bean("adminRememberMeServices")
    public RememberMeServices adminRememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, adminDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14); // 設置 token 有效期為 14 天
        return rememberMe;
    }

    /**
     * "記住我" 服務分發器 (RememberMeServices Delegator)。
     * 由於系統有兩套 "記住我" 服務（前台 vs 後台），這個 Bean 負責根據當前請求的 URL，
     * 將 remember-me 的相關操作（如自動登入、登入成功處理）分發給正確的服務。
     * @param userRememberMeServices 前台服務
     * @param adminRememberMeServices 後台服務
     * @return 統一的 RememberMeServices 接口實現
     */
    @Bean
    @Primary // 標記為主要的 RememberMeServices Bean
    public RememberMeServices delegatingRememberMeServices(
            @Qualifier("userRememberMeServices") RememberMeServices userRememberMeServices,
            @Qualifier("adminRememberMeServices") RememberMeServices adminRememberMeServices) {
        
          return new RememberMeServices() {
                // 處理自動登入邏輯
                @Override
                public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/admin/")) {
                        return adminRememberMeServices.autoLogin(request, response);
                    }
                    // 其他所有情況（包括 API 和前台頁面）都由 userRememberMeServices 處理
                    return userRememberMeServices.autoLogin(request, response);
                }

                // 處理登入成功後的邏輯
                @Override
                public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
                    if (request.getRequestURI().startsWith("/admin/")) {
                        adminRememberMeServices.loginSuccess(request, response, auth);
                    } else {
                        userRememberMeServices.loginSuccess(request, response, auth);
                    }
                }

                // 處理登入失敗後的邏輯
                @Override
                public void loginFail(HttpServletRequest request, HttpServletResponse response) {
                    if (request.getRequestURI().startsWith("/admin/")) {
                        adminRememberMeServices.loginFail(request, response);
                    } else {
                        userRememberMeServices.loginFail(request, response);
                    }
                }
        };
    }

    /**
     * 配置 HTTP 防火牆，以允許 URL 中包含分號 (;) 等特殊字符。
     * Spring Security 預設的防火牆非常嚴格，可能會拒絕包含某些字符的合法請求。
     * @return HttpFirewall 實例
     */
    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true); // 也允許 URL 編碼的斜線
        return firewall;
    }
    
    /**
     * 將自定義的 HTTP 防火牆應用到 Web 安全性中。
     * @return WebSecurityCustomizer 實例
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowSemicolonHttpFirewall());
    }

    /**
     * 核心安全過濾器鏈配置 (The Security Filter Chain)。
     * 這是整個安全配置的心臟，定義了所有請求如何被過濾和處理。
     * @param http HttpSecurity 配置對象
     * @return SecurityFilterChain 實例
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 註冊我們自定義的前後台認證 Provider
            .authenticationProvider(adminAuthenticationProvider())
            .authenticationProvider(customUserAuthenticationProvider)

            // 2. 授權規則 (URL權限控制)，規則順序至關重要！
            .authorizeHttpRequests(authorize -> authorize
                // 規則 2.1: 優先放行後台登入頁，防止重定向循環
                .requestMatchers("/admin/login").permitAll()
                // 規則 2.2: 放行所有公開資源、靜態文件和無需登入的 API
                .requestMatchers("/", "/index.html", "/login.html", "/register.html",
                        "/css/**", "/js/**", "/images/**", "/adminlte/**", "/frontend-template/**",
                        "/api/users/register", "/api/users/login", "/api/users/send-code",
                        "/api/users/request-password-reset", "/api/csrf-token","/login"
                ).permitAll()
                // 規則 2.3: 所有 /admin/ 路徑下的請求，都必須擁有 "ADMIN" 角色
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 規則 2.4: 所有 /api/ 路徑下的請求，都必須經過認證 (登入)
                .requestMatchers("/api/**").authenticated()
                // 規則 2.5: 除了以上規則，其他所有請求都必須經過認證 (最嚴格的規則放在最後)
                .anyRequest().authenticated()
            )

            // 3. 後台表單登錄配置
            .formLogin(form -> form
                .loginPage("/admin/login")         // 指定後台登入頁的 URL
                .loginProcessingUrl("/admin/login") // 指定處理登入請求的 URL
                .defaultSuccessUrl("/admin/dashboard", true) // 登入成功後強制跳轉到後台主頁
                .failureUrl("/admin/login?error=true") // 登入失敗後跳轉的 URL
            )

            // 4. 異常處理：當認證失敗時，使用我們自定義的入口點分發器
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(delegatingAuthenticationEntryPoint)
            )

            // 5. "記住我" 功能：使用我們自定義的服務分發器
            .rememberMe(remember -> remember
                .key(rememberMeKey)
                .rememberMeServices(delegatingRememberMeServices(
                    userRememberMeServices(), 
                    adminRememberMeServices()
                ))
            )

            // 6. 登出配置
            .logout(logout -> logout
                // 匹配 /admin/logout 和 /api/users/logout 等請求
                .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout", "POST")) 
                .logoutSuccessHandler(customLogoutSuccessHandler) // 使用自定義的登出成功處理器
                .invalidateHttpSession(true) // 登出後使 HttpSession 無效
                .clearAuthentication(true)   // 清除認證訊息
                .deleteCookies("JSESSIONID", "XSRF-TOKEN", "remember-me") // 清除所有相關 Cookie
            )

            // 7. CSRF (跨站請求偽造) 保護配置
            .csrf(csrf -> csrf
                // 將 CSRF token 存儲在 Cookie 中，這是現代前後端分離架構（如 React, Vue）的推薦做法
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            // 8. 自定義過濾器鏈
            // 在處理用戶名密碼認證之前，先插入我們的 reCAPTCHA 驗證過濾器
            .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}