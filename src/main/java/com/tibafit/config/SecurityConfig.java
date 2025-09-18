// 聲明該類所在的套件
package com.tibafit.config;

// 引入所需的 Java 和 Spring Framework 類
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
// @EnableWebSecurity 啟用 Spring Security 的 Web 安全性功能
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    // 自動注入 reCAPTCHA 驗證過濾器
    @Autowired
    private ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter;

    // 自動注入後台管理員用戶詳細資訊服務
    @Autowired
    private AdminDetailsService adminDetailsService;

    // 自動注入密碼編碼器，用於密碼的加密與比對
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 從 application.properties 中讀取 '記住我' 功能的密鑰
    @Value("${app.security.remember-me-key}")
    private String rememberMeKey;

    // 自動注入委派認證入口點，用於處理未認證的請求
    @Autowired
    private AuthenticationEntryPoint delegatingAuthenticationEntryPoint;

    // 自動注入名為 "customUserAuthenticationProvider" 的自定義用戶認證提供者
    @Autowired
    @Qualifier("customUserAuthenticationProvider")
    private AuthenticationProvider customUserAuthenticationProvider;

    // 自動注入名為 "userDetailsService" 的用戶詳細資訊服務
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    /* =====================  未認證導向規則（API 回 401；後台/前台導向對應登入頁）  ===================== */
    @Bean
    public static AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
        // 使用 LinkedHashMap 保持插入順序，確保請求匹配規則的優先級
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

        // API：未登入回 401 + JSON
        entryPoints.put(new AntPathRequestMatcher("/api/**"), (request, response, authException) -> {
            // 設置 HTTP 狀態碼為 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 設置響應內容類型為 JSON
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "未登入或憑證無效");
            error.put("message", "請先登入以存取此資源");
            error.put("status", HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        });

        // 後台未認證 → /admin/login
        entryPoints.put(new AntPathRequestMatcher("/admin/**"),
                new LoginUrlAuthenticationEntryPoint("/admin/login"));

        // 前台未認證 → /frontend-template/login.html
        entryPoints.put(AntPathRequestMatcher.antMatcher("/frontend-template/**"),
                new LoginUrlAuthenticationEntryPoint("/frontend-template/login.html"));

        // 其他 → 一律導向前台登入頁
        final LoginUrlAuthenticationEntryPoint defaultEntryPoint =
                new LoginUrlAuthenticationEntryPoint("/frontend-template/login.html");

        final DelegatingAuthenticationEntryPoint delegating =
                new DelegatingAuthenticationEntryPoint(entryPoints);
        delegating.setDefaultEntryPoint(defaultEntryPoint);
        return delegating;
    }

    /* =====================  AuthManager / Providers  ===================== */

    @Bean
    public AuthenticationManager userAuthenticationManager(
            @Qualifier("customUserAuthenticationProvider")
            AuthenticationProvider customUserAuthenticationProvider) {
        return new ProviderManager(customUserAuthenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        // DaoAuthenticationProvider 是 Spring Security 提供的一個基於 DAO 的認證提供者
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /* =====================  Remember-Me（前/後台分流）  ===================== */

    @Bean("userRememberMeServices")
    public RememberMeServices userRememberMeServices() {
        TokenBasedRememberMeServices rememberMe =
                new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14);
        rememberMe.setAlwaysRemember(true);
        return rememberMe;
    }

    @Bean("adminRememberMeServices")
    public RememberMeServices adminRememberMeServices() {
        TokenBasedRememberMeServices rememberMe =
                new TokenBasedRememberMeServices(rememberMeKey, adminDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14);
        return rememberMe;
    }

    @Bean
    @Primary
    public RememberMeServices delegatingRememberMeServices(
            @Qualifier("userRememberMeServices") RememberMeServices userRememberMeServices,
            @Qualifier("adminRememberMeServices") RememberMeServices adminRememberMeServices) {

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
                if (request.getRequestURI().startsWith("/admin/")) {
                    adminRememberMeServices.loginFail(request, response);
                } else {
                    userRememberMeServices.loginFail(request, response);
                }
            }
        };
    }

    /* =====================  Firewall（允許 ; 與 %2F）  ===================== */

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        // StrictHttpFirewall 是 Spring Security 默認的防火牆實現
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // 設置為 true 以允許 URL 中包含分號 (;)
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowSemicolonHttpFirewall());
    }

    /* =====================  Security Filter Chain  ===================== */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 兩種 Provider
            .authenticationProvider(adminAuthenticationProvider())
            .authenticationProvider(customUserAuthenticationProvider)

            // 路由授權（你的白名單為主）
            .authorizeHttpRequests(authorize -> authorize
                // 後台登入頁
                .requestMatchers("/admin/login").permitAll()

                // 前台公開頁與靜態資源
                .requestMatchers("/", "/index.html", "/login.html", "/register.html",
                        "/css/**", "/js/**", "/images/**", "/adminlte/**", "/frontend-template/**",
                        "/assets/**", "/webjars/**", "/font/**", "/fonts/**").permitAll()

                // 商店頁（前台）
                .requestMatchers("/shop/**").permitAll()

                // 錯誤與 well-known
                .requestMatchers("/error", "/error/**", "/.well-known/**").permitAll()

                // 你已開放的匿名 API
                .requestMatchers(HttpMethod.GET,
                        "/api/analytics/**", "/analytics", "/analytics/**",
                        "/products", "/shop/products", "/shop/product/**",
                        "/product_img/**").permitAll()
                .requestMatchers("/api/users/register", "/api/users/login", "/api/users/send-code",
                        "/api/users/request-password-reset", "/api/csrf-token", "/login").permitAll()

                // 商品讀取（匿名 GET）
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                // 後台需 ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 其餘需登入（若開發期要放行，可改 .anyRequest().permitAll()）
                .anyRequest().authenticated()
            )

            // 後台表單登入
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
            )

            // 未認證處理
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(delegatingAuthenticationEntryPoint)
            )

            // Remember-Me
            .rememberMe(remember -> remember
                .key(rememberMeKey)
                .rememberMeServices(delegatingRememberMeServices(
                        userRememberMeServices(), adminRememberMeServices()))
            )

            // 登出
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout", "POST"))
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "XSRF-TOKEN", "remember-me")
            )

            // CSRF：採用 CookieCsrfTokenRepository，前端可讀取（例如 JS 取 XSRF-TOKEN）
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            // 若有 H2 console / 內嵌頁面需求
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // reCAPTCHA 先於帳密驗證
            .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                // 配置 CSRF (跨站請求偽造) 防護
//                .csrf(csrf -> csrf
//                        // 使用基於 Cookie 的 CSRF Token 存儲庫，withHttpOnlyFalse() 允許前端 JS 讀取此 cookie
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // 在 UsernamePasswordAuthenticationFilter 之前添加自定義的 reCAPTCHA 驗證過濾器
                // 這確保了在嘗試用戶名密碼認證之前，先進行 reCAPTCHA 驗證
                .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 構建並返回 SecurityFilterChain
        return http.build();
    }
}
