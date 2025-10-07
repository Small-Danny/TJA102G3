package com.tibafit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibafit.config.filter.ReCaptchaAuthenticationFilter;
import com.tibafit.service.user.AdminDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
 * <p>
 * 各位團隊成員請注意：
 * 這份檔案是整個應用程式的【安全中樞】，定義了所有關於使用者認證(Authentication)、
 * 授權(Authorization)、CSRF保護、CORS設定、以及各種安全性的細節。
 * <p>
 * 任何對此檔案的修改都可能影響到：
 * 1. 使用者登入、登出流程
 * 2. 頁面與API的存取權限
 * 3. 跨站請求偽造(CSRF)的防護
 * 4. 金流串接的穩定性
 * <p>
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
    // ▼▼▼ 【新增區塊】 IV. 安全上下文儲存庫 (Security Context Repositories) ▼▼▼
    // =======================================================================================

    /**
     * 前台專用的「警衛室」。它會將登入資訊存到 Session 的 "SPRING_SECURITY_CONTEXT_USER" 屬性中。
     */
    @Bean
    @Qualifier("userSecurityContextRepository")
    public SecurityContextRepository userSecurityContextRepository() {
        HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        repository.setSpringSecurityContextKey("SPRING_SECURITY_CONTEXT_USER");
        return repository;
    }

    /**
     * 後台專用的「警衛室」。它會將登入資訊存到 Session 的 "SPRING_SECURITY_CONTEXT_ADMIN" 屬性中。
     */
    @Bean
    @Qualifier("adminSecurityContextRepository")
    public SecurityContextRepository adminSecurityContextRepository() {
        HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        repository.setSpringSecurityContextKey("SPRING_SECURITY_CONTEXT_ADMIN");
        return repository;
    }


    // =======================================================================================
    // IV. "記住我" 服務 (Remember-Me Services)
    // ---------------------------------------------------------------------------------------
    // 這裡同樣做了精巧的設計，讓前台和後台的 "記住我" 功能可以獨立運作，
    // 使用不同的 UserDetailsService，確保不會混淆使用者身份。
    //=======================================================================================

    @Bean("userRememberMeServices")
    public RememberMeServices userRememberMeServices() {

        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14);
        rememberMe.setCookieName("user-remember-me-cookie");
        rememberMe.setParameter("user-remember-me");
        return rememberMe;
    }

    @Bean("adminRememberMeServices")
    public RememberMeServices adminRememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, adminDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14); // cookie 有效期 14 天
        rememberMe.setCookieName("admin-remember-me-cookie");
        rememberMe.setParameter("admin-remember-me");
        return rememberMe;
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

    // =======================================================================================
    // VII. 【【【 核心修改：拆分 SecurityFilterChain 為兩個獨立的 Bean 】】】
    // =======================================================================================

    /**
     * 後台安全過濾器鏈 (Admin Security Filter Chain)
     * - @Order(1)：優先級最高，Spring Security 會先檢查此鏈的規則。
     * - securityMatcher("/admin/**")：明確指定此鏈只處理 /admin/ 開頭的請求。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
                                                        @Qualifier("adminSecurityContextRepository") SecurityContextRepository adminSecurityContextRepository,
                                                        // ▼▼▼ 注入主要的委派服務 ▼▼▼
                                                        @Qualifier("adminRememberMeServices") RememberMeServices adminRememberMeServices) throws Exception {
        http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/admin/**"),
                        new AntPathRequestMatcher("/adminlte/**"),// 讓後台過濾器也處理 adminlte 的請求
                        new AntPathRequestMatcher("/uploads/**"),
                        new AntPathRequestMatcher("/plugins/**"), // 任務
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/webjars/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**"),
                        new AntPathRequestMatcher("/tasks/**"),
                        new AntPathRequestMatcher("/dMain/bd/**"),  // 運動計畫
                        new AntPathRequestMatcher("/sportSidebar/api/bd/**"),
                        new AntPathRequestMatcher("/sport/api/**"),
                        new AntPathRequestMatcher("/sportType/api/bd/**"),
                        new AntPathRequestMatcher("/sportTypeItem/api/**"),
                        new AntPathRequestMatcher("/fileImg/api/bd/**"),
                        new AntPathRequestMatcher("/sportPics/img/bd/**")
                ))
                .authenticationProvider(adminAuthenticationProvider()) // 指定後台驗證邏輯
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/adminlte/**", "/uploads/**", "/plugins/**", "/images/**", "/webjars/**"
                                , "/css/**", "/js/**", "/tasks/**","/api/analytics/workouts/**").permitAll()
                        .requestMatchers("/admin/login").permitAll()  // 開放後台登入頁
                        .anyRequest().hasRole("ADMIN") // 其他 /admin/ 路徑皆需 ADMIN 角色
                )
                .formLogin(form -> form // 使用 Spring Security 內建的表單登入
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                )
                .securityContext(context -> context
                        .securityContextRepository(adminSecurityContextRepository) // ★ 關鍵2: 使用後台專用的 SecurityContextRepository
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout", "POST"))
                        .logoutSuccessUrl("/admin/login?logout") // 使用您自訂的登出成功處理器
                        .invalidateHttpSession(false) // ★ 關鍵3: 登出時【不】銷毀整個Session，只清除自己的認證
                        .deleteCookies("admin-remember-me", "XSRF-TOKEN")
                )
                .rememberMe(remember -> remember
                        .rememberMeServices(adminRememberMeServices) // 使用後台專用的 "記住我" 服務
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                );

        return http.build();
    }

    /**
     * 前台與API安全過濾器鏈 (User & API Security Filter Chain)
     * - @Order(2)：優先級較低，只有不匹配 /admin/** 的請求才會進入此鏈。
     * - securityMatcher("/**")：處理所有剩下的請求。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http,
                                                       ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter,
                                                       AuthenticationEntryPoint delegatingAuthenticationEntryPoint,
                                                       @Qualifier("userSecurityContextRepository") SecurityContextRepository userSecurityContextRepository,
                                                       // ▼▼▼ 注入主要的委派服務 ▼▼▼
                                                       @Qualifier("userRememberMeServices") RememberMeServices userRememberMeServices) throws Exception {
        http
                .securityMatcher("/**") // ★ 關鍵1: 作用於所有其他請求
                .authenticationProvider(customUserAuthenticationProvider) // 指定前台驗證邏輯
                .authorizeHttpRequests(authorize -> authorize
                        // --- 規則 A: 公開訪問區 ---
                        .requestMatchers(
                                "/", "/frontend-template/index.html", "/frontend-template/login.html",
                                "/frontend-template/register.html", "/frontend-template/reset-set-password.html",
                                "/frontend-template/register-success.html", "/frontend-template/register-fail.html",
                                "/frontend-template/cart.html", "/frontend-template/cart_order.html",
                                "/frontend-template/pay_success.html", "/shop/products", "/shop/product/**",
                                "/frontend-template/forum.html", "/frontend-template/article-detail.html","/frontend-template/forum/member-detail.html"
                                ,"/dMain/fd/workoutPlan_main", "/sportPics/publicImg/fd/sportCommon/**"
                        ).permitAll()
                        // --- 靜態資源 ---
                        .requestMatchers("/frontend-template/**", "/images/**").permitAll()
                        // --- 公開 API ---
                        .requestMatchers(
                                "/api/users/register", "/api/users/login", "/api/users/send-code",
                                "/api/users/request-password-reset", "/api/users/reset-password-with-token",
                                "/api/csrf-token", "/api/products/**", "/api/cart/**", "/shop/api/**",
                                "/api/ecpay/callback", "/payment/ecpay/return", "/api/line-pay/confirm",
                                "/api/posts/**", "/api/sidebar", "/api/categories", "/api/report-types"
                                , "/api/ai/**","/api/forum/member/**","/api/messages/**" 
                                ,"/sportSidebar/api/fd/**"
                        ).permitAll()
                        // --- 規則 B: 其他所有請求，只要登入即可 ---
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(delegatingAuthenticationEntryPoint))
                .securityContext(context -> context
                        .securityContextRepository(userSecurityContextRepository) // ★ 關鍵2: 使用前台專用的 SecurityContextRepository
                )
                .rememberMe(remember -> remember
                        .rememberMeServices(userRememberMeServices) // 使用前台專用的 "記住我" 服務
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/users/logout", "POST"))
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .invalidateHttpSession(false) // ★ 關鍵3: 登出時同樣【不】銷毀整個Session
                        .clearAuthentication(true)
                        .deleteCookies("user-remember-me", "XSRF-TOKEN")
                )
                .cors(withDefaults()) // 啟用 CORS
                .csrf(csrf -> csrf // 設定 CSRF
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers( // 忽略金流和部分API的CSRF檢查
                                "/ecpay/callback", "/payment/ecpay", "/payment/ecpay/return",
                                "/api/line-pay/confirm", "/api/line-pay/callback", "/api/line-pay/notification",
                                "/api/posts/**", "/api/mycollection/**", "/api/myarticles/**", "/api/cart/**",
                                "/api/ai/**","/api/forum/member/**", "/api/messages/**" 
                        )
                )
                .headers(headers -> headers // 設定安全標頭
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "script-src 'self' https://payment-stage.ecpay.com.tw https://www.google.com " +
                                        "https://www.gstatic.com https://cdn.jsdelivr.net 'unsafe-inline'; " +
                                        "frame-src 'self' https://www.google.com;"
                        ))
                )
                .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 加入 reCAPTCHA 過濾器
                // 前台是透過 API 登入，所以要禁用內建的表單登入頁
                .formLogin(form -> form.disable());

        return http.build();
    }
}