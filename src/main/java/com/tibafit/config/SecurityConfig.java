package com.tibafit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibafit.config.filter.ReCaptchaAuthenticationFilter;
import com.tibafit.service.user.AdminDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * =======================================================================================
 * [核心安全組態] SecurityConfig.java
 * =======================================================================================
 *
 * 各位團隊成員請注意：
 * 這份檔案是整個應用程式的【安全中樞】，定義了所有關於使用者認證(Authentication)、
 * 授權(Authorization)、CSRF保護、CORS設定、以及各種安全性的細節。
 *
 * 任何對此檔案的修改都可能影響到：
 * 1. 使用者登入、登出流程
 * 2. 頁面與API的存取權限
 * 3. 跨站請求偽造(CSRF)的防護
 * 4. 金流串接的穩定性
 *
 * 這份設定經過多次迭代與調整，才達到目前能同時處理「前台使用者」、「後台管理員」及
 * 「API請求」三種不同情境的穩定狀態。請在修改前務必了解每個區塊的功能。
 *
 * @author [你的名字]
 * @version 2.0 (經過多次重構與功能增強)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // =======================================================================================
    // I. 依賴注入 (Dependencies Injection)
    // ---------------------------------------------------------------------------------------
    // 這裡注入了所有安全設定會用到的服務與組件。
    // =======================================================================================

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private AdminDetailsService adminDetailsService; // 後台管理員專用的 UserDetailsService

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.security.remember-me-key}")
    private String rememberMeKey; // 用於 "記住我" 功能的密鑰，存放在 application.properties

    @Autowired
    @Qualifier("customUserAuthenticationProvider")
    private AuthenticationProvider customUserAuthenticationProvider; // 前台使用者專用的 Provider

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService; // 前台使用者專用的 UserDetailsService


    // =======================================================================================
    // II. 認證入口點 (Authentication Entry Point)
    // ---------------------------------------------------------------------------------------
    // 這是整個安全機制的「第一道防線」。當一個【未登入】的使用者嘗試存取需要權限的
    // 資源時，這個 EntryPoint 會決定該如何回應。
    //
    // 設計亮點：
    // 我們使用了 DelegatingAuthenticationEntryPoint，它像一個「路由器」，
    // 能根據請求的 URL，智慧地決定不同的處理方式，而不是所有請求都導向同一個登入頁。
    //=======================================================================================

    /**
     * 處理未認證請求的「智慧分流器」。
     * - API 請求 (/api/**) → 回傳 401 Unauthorized JSON 錯誤訊息，讓前端能明確處理。
     * - 後台請求 (/admin/**) → 重新導向至後台登入頁 (/admin/login)。
     * - 前台請求 (/frontend-template/**) → 重新導向至前台登入頁 (/frontend-template/login.html)。
     * - 其他所有請求 → 預設導向至前台登入頁。
     * 這種設計對於前後端分離的架構至關重要。
     */
    @Bean
    public static AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

        // 規則1: API - 回傳 JSON
        entryPoints.put(new AntPathRequestMatcher("/api/**"), (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "未登入或憑證無效");
            error.put("message", "請先登入以存取此資源");
            error.put("status", HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        });

        // 規則2: 後台 - 導向後台登入頁
        entryPoints.put(new AntPathRequestMatcher("/admin/**"), new LoginUrlAuthenticationEntryPoint("/admin/login"));

        // 規則3: 前台 - 導向前台登入頁
        entryPoints.put(AntPathRequestMatcher.antMatcher("/frontend-template/**"), new LoginUrlAuthenticationEntryPoint("/frontend-template/login.html"));

        // 預設規則
        final LoginUrlAuthenticationEntryPoint defaultEntryPoint = new LoginUrlAuthenticationEntryPoint("/frontend-template/login.html");

        final DelegatingAuthenticationEntryPoint delegating = new DelegatingAuthenticationEntryPoint(entryPoints);
        delegating.setDefaultEntryPoint(defaultEntryPoint);
        return delegating;
    }


    // =======================================================================================
    // III. 認證管理器與提供者 (Authentication Manager & Providers)
    // ---------------------------------------------------------------------------------------
    // 定義了「如何驗證使用者」的具體邏輯。我們有兩套驗證機制：
    // 1. 前台使用者 (customUserAuthenticationProvider): 走我們自訂的驗證流程。
    // 2. 後台管理員 (adminAuthenticationProvider): 使用 Spring Security 標準的 DAO 驗證流程。
    //=======================================================================================

    /**
     * 前台使用者的身份驗證管理器。
     * 它只包含我們自訂的 customUserAuthenticationProvider。
     */
    @Bean("userAuthenticationManager")
    public AuthenticationManager user
    (@Qualifier("customUserAuthenticationProvider") AuthenticationProvider customUserAuthenticationProvider) {
        return new ProviderManager(customUserAuthenticationProvider);
    }

    /**
     * 後台管理員的身份驗證提供者。
     * 使用標準的 DaoAuthenticationProvider，它會比對資料庫中的帳號密碼。
     */
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService); // 指定從 AdminDetailsService 獲取管理員資料
        authProvider.setPasswordEncoder(passwordEncoder); // 指定密碼加密器
        return authProvider;
    }


    // =======================================================================================
    // IV. "記住我" 服務 (Remember-Me Services)
    // ---------------------------------------------------------------------------------------
    // 這裡同樣做了精巧的設計，讓前台和後台的 "記住我" 功能可以獨立運作，
    // 使用不同的 UserDetailsService，確保不會混淆使用者身份。
    //=======================================================================================

    @Bean("userRememberMeServices")
    public RememberMeServices userRememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService) {

            // ▼▼▼ 【 關鍵的最終修正 】 ▼▼▼
            // 覆寫這個方法，讓它永遠回傳 true。
            // 因為我們是從 Service 手動調用 loginSuccess，我們自己已經確認過使用者想要「記住我」，
            // 所以我們在這裡直接告訴 Spring Security 不用再去檢查 HTTP 請求參數了。
            @Override
            protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
                return true;
            }
            // ▲▲▲ 【 關鍵的最終修正 】 ▲▲▲

            // 以下的日誌可以暫時保留，用來確認最終的執行情況
            @Override
            public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
                super.onLoginSuccess(request, response, successfulAuthentication);
            }

            @Override
            protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
                super.setCookie(tokens, maxAge, request, response);
            }
        };

        rememberMe.setTokenValiditySeconds(86400 * 14);
        return rememberMe;
    }

    @Bean("adminRememberMeServices")
    public RememberMeServices adminRememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, adminDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14); // cookie 有效期 14 天
        return rememberMe;
    }

    /**
     * 同樣使用「委派模式」，根據請求路徑判斷要使用哪個 RememberMeServices。
     * - /admin/** 的請求會觸發 adminRememberMeServices。
     * - 其他請求則觸發 userRememberMeServices。
     */
    @Bean
    @Primary
    public RememberMeServices delegatingRememberMeServices(@Qualifier("userRememberMeServices") RememberMeServices userRememberMeServices, @Qualifier("adminRememberMeServices") RememberMeServices adminRememberMeServices) {
        // 這是一個 (Anonymous Class) 的寫法，用來動態切換兩種服務
        return new RememberMeServices() {
            @Override
            public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
                String uri = request.getRequestURI();
                if (uri.startsWith("/admin/")) {
                    return adminRememberMeServices.autoLogin(request, response);
                } else {
                    return userRememberMeServices.autoLogin(request, response);
                }
            }

            @Override
            public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
                if (request.getRequestURI().startsWith("/admin/")) {
                    adminRememberMeServices.loginSuccess(request, response, auth);
                } else {
                    userRememberMeServices.loginSuccess(request, response, auth);
                }
            }

            @Override
            public void loginFail(HttpServletRequest request, HttpServletResponse response) {
                // loginFail 通常不需要做特別處理
            }
        };
    }


    // =======================================================================================
    // V. Web 安全性微調 (Web Security Fine-tuning)
    // ---------------------------------------------------------------------------------------
    // 包含防火牆、CORS 等針對 HTTP 請求本身的安全性設定。
    //=======================================================================================

    /**
     * 設定 HTTP 防火牆，以允許 URL 中包含分號 (;) 和編碼過的斜線 (%2F)。
     * 某些特殊的 URL 結構或參數可能會需要這個設定。
     */
    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowSemicolonHttpFirewall());
    }

    /**
     * 設定 CORS (跨來源資源共用)。
     * 允許來自任何來源(*)的任何請求方法(*)和標頭(*)。
     * 【注意】：在生產環境中，為了安全性，'setAllowedOrigins' 應該設定為前端的具體域名，
     * 而不是 "*"，例如：.setAllowedOrigins(Arrays.asList("https://www.yourfrontend.com"))
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // 開發階段使用 "*", 生產環境應指定域名
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // =======================================================================================
    // VI. 安全過濾器鏈 (Security Filter Chain)
    // ---------------------------------------------------------------------------------------
    //  ★★★★★ 這是整個設定檔【最核心】的部分 ★★★★★
    // 它像一條「加工流水線」，定義了每個 HTTP 請求需要經過哪些安全檢查。
    // 順序非常重要！
    //=======================================================================================

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter, AuthenticationEntryPoint delegatingAuthenticationEntryPoint) throws Exception {
        http
                // 步驟 1: 註冊我們定義的兩種身份驗證提供者
                .authenticationProvider(adminAuthenticationProvider())
                .authenticationProvider(customUserAuthenticationProvider)

                // 步驟 2: HTTP 請求授權規則 (Authorization Rules)
                .authorizeHttpRequests(authorize -> authorize
                        // ----------------------------------------------------------------
                        // 【規則 A: 公開訪問區 - 不需要登入】
                        // ----------------------------------------------------------------
                        .requestMatchers(
                                // --- 核心頁面 ---
                                "/",
                                "/frontend-template/index.html",
                                "/frontend-template/login.html",
                                "/frontend-template/register.html",
                                "/frontend-template/header.html",

                                // --- 購物流程頁面 (關鍵修改，確保未登入用戶也能瀏覽) ---
                                "/frontend-template/cart.html",
                                "/frontend-template/cart_order.html",
                                "/frontend-template/pay_success.html", // 付款成功頁

                                // --- 商品相關頁面 (關鍵修改) ---
                                "/shop/products",      // 商品列表頁
                                "/shop/product/**",     // 所有商品詳情頁

                                // 文章區頁面
                                "/frontend-template/forum.html",
                                "/frontend-template/article-detail.html"
//                                "/frontend-template/myarticles.html",
//                                "/frontend-template/mycollection.html"
//                                "/frontend-template/post-article.html"
                        ).permitAll()

                        // --- 靜態資源 (CSS, JS, 圖片等，必須開放) ---
                        .requestMatchers(
                                "/frontend-template/**",
                                "/adminlte/**",
                                "/images/**"


                        ).permitAll()

                        // --- 公開 API (無需登入即可呼叫) ---
                        .requestMatchers(
                                // 使用者註冊/登入相關
                                "/api/users/register",
                                "/api/users/login",
                                "/api/users/send-code",
                                "/api/users/profile",
                                "/api/users/request-password-reset",
                                "/api/csrf-token",

                                // 購物流程相關 API (關鍵修改，允許匿名用戶加到購物車)
                                "/api/products/**",      // 查詢商品資訊
                                "/api/cart/**",          // 操作購物車
                                "/shop/api/**",          // 查詢庫存

                                // 金流 Callback API (必須公開，因為是金流方主動呼叫)
                                "/api/ecpay/callback",       // 綠界 Server-to-Server
                                "/payment/ecpay/return",     // 綠界 前端跳轉
                                "/api/line-pay/confirm",    // LINE Pay Server-to-Server

                                //公開文章區 API
                                "/api/posts/**",
                                "/api/sidebar",
                                "/api/categories",
                                "/api/report-types",
                                "/api/ai/**"
                        ).permitAll()
                        .requestMatchers("/admin/login").permitAll()
                        // ----------------------------------------------------------------
                        // 【規則 B: 管理員權限區 - 需要 ADMIN 角色】
                        // ----------------------------------------------------------------
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ----------------------------------------------------------------
                        // 【規則 C: 其他所有請求 - 只要登入即可】
                        // ----------------------------------------------------------------
                        .anyRequest().authenticated()
                )

                // 步驟 3: 設定後台的表單登入頁面
                .formLogin(form -> form
                        .loginPage("/admin/login") // 指定登入頁 URL
                        .loginProcessingUrl("/admin/login") // 處理登入請求的 URL
                        .defaultSuccessUrl("/admin/dashboard", true) // 登入成功後導向
                        .failureUrl("/admin/login?error=true") // 登入失敗導向
                )

                // 步驟 4: 處理未認證的請求 (使用上面定義的智慧分流器)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(delegatingAuthenticationEntryPoint))

                // 步驟 5: 啟用 "記住我" 功能 (使用上面定義的委派服務)
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .rememberMeServices(delegatingRememberMeServices(userRememberMeServices(), adminRememberMeServices()))
                )

                // 步驟 6: 登出設定
                .logout(logout -> logout
                        // 監聽前台和後台的登出路徑
                        .logoutRequestMatcher(new OrRequestMatcher(
                                new AntPathRequestMatcher("/api/users/logout", "POST"),
                                new AntPathRequestMatcher("/admin/logout", "POST")
                        ))
                        // ★★★ 關鍵修正：使用你原本就寫好的 CustomLogoutSuccessHandler ★★★
                        // 它能智慧地判斷要重新導向到前台還是後台
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me", "XSRF-TOKEN") // 清理所有 cookie
                        .permitAll()
                )

                // 步驟 7: 啟用 CORS (使用上面定義的 corsConfigurationSource)
                .cors(withDefaults())

                // 步驟 8: CSRF 保護設定
                .csrf(csrf -> csrf
                        // 將 CSRF token 存放在 cookie 中，讓前端 JS 可以讀取
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 【重要】金流 callback 必須排除在 CSRF 保護之外，因為它們不是由我們的前端發起
                        .ignoringRequestMatchers(
                                "/ecpay/callback",
                                "/payment/ecpay",
                                "/payment/ecpay/return",
                                "/api/line-pay/confirm",// Line Pay 也需要排除
                                "/api/line-pay/callback",
                                "/api/line-pay/notification",
                                "/api/posts/**",
                                "/api/mycollection/**",
                                "/api/myarticles/**",
                                "/api/ai/**"
                        )
                )

                // 步驟 9: 安全標頭 (Security Headers) 設定，特別是 CSP
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // 防止點擊劫持 (Clickjacking)
                        // Content-Security-Policy (CSP) 是防止 XSS 攻擊的重要防線
                        // 它限制了瀏覽器只能從我們信任的來源載入資源 (腳本、圖片、樣式等)
                        // 這個設定非常繁瑣，需要不斷測試與調整
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "script-src 'self' " +
                                        "https://payment-stage.ecpay.com.tw " + // 綠界支付
                                        "https://www.google.com " +             // reCAPTCHA
                                        "https://www.gstatic.com " +            // reCAPTCHA
                                        "https://cdn.jsdelivr.net " + // ✨ [新增] 允許來自 jsdelivr CDN 的腳本
                                        "'unsafe-inline'; " +                   // 允許 inline script (某些舊套件可能需要)
                                        "frame-src 'self' https://www.google.com;"      // 允許 reCAPTCHA 的 iframe
                                // 這裡可以繼續添加其他指令如 style-src, img-src 等
                        ))
                )

                // 步驟 10: 在標準的帳號密碼驗證前，先執行我們的 reCAPTCHA 驗證過濾器
                .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}