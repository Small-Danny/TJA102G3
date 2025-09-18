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

// @Configuration 標識該類為 Spring 的配置類
@Configuration
// @EnableWebSecurity 啟用 Spring Security 的 Web 安全性功能
@EnableWebSecurity
public class SecurityConfig {

    // 自動注入自定義的登出成功處理器
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

    // 解決循環依賴：未認證請求的重定向規則
    // @Bean 標識這是一個 Spring Bean，Spring 容器會管理它的生命週期
    // 'static' 關鍵字用於解決循環依賴問題，確保此 Bean 在 SecurityConfig 實例化之前被創建
    @Bean
    public static AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
        // 使用 LinkedHashMap 保持插入順序，確保請求匹配規則的優先級
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

        // 規則 1: 當未認證的請求路徑匹配 "/api/**" 時
        entryPoints.put(new AntPathRequestMatcher("/api/**"), (request, response, authException) -> {
            // 設置 HTTP 狀態碼為 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 設置響應內容類型為 JSON
            response.setContentType("application/json;charset=UTF-8");
            // 創建錯誤訊息 Map
            Map<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("error", "未登入或憑證無效");
            errorDetails.put("message", "請先登入以存取此資源");
            errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
            // 將錯誤訊息 Map 轉換為 JSON 字符串並寫入響應
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
        });

        // 規則 2: 當未認證的請求路徑匹配 "/admin/**" 時，重定向到後台登錄頁
        entryPoints.put(new AntPathRequestMatcher("/admin/**"),
                new LoginUrlAuthenticationEntryPoint("/admin/login"));

     // 【⭐修改點 1】規則 3: 將你自己的前端路徑也指向正確的登入頁
        entryPoints.put(AntPathRequestMatcher.antMatcher("/frontend-template/**"),
                new LoginUrlAuthenticationEntryPoint("/frontend-template/login.html"));

        // 【⭐修改點 2】規則 4: 預設規則，捕獲所有其他請求(包含夥伴的路徑)，並指向「正確的」登入頁
        final LoginUrlAuthenticationEntryPoint defaultEntryPoint = new LoginUrlAuthenticationEntryPoint("/frontend-template/login.html");

        // 創建委派認證入口點
        final DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        // 設置正確的默認入口點
        delegatingEntryPoint.setDefaultEntryPoint(defaultEntryPoint);
        return delegatingEntryPoint;
    }
    

    // 定義前台用戶的認證管理器 Bean
    @Bean
    public AuthenticationManager userAuthenticationManager(
            // 指定注入名為 "customUserAuthenticationProvider" 的 Bean
            @Qualifier("customUserAuthenticationProvider") AuthenticationProvider customUserAuthenticationProvider) {
        // ProviderManager 是 AuthenticationManager 的一個常見實現，它將認證委託給一個 AuthenticationProvider 列表
        return new ProviderManager(customUserAuthenticationProvider);
    }

    // 定義後台管理員的認證提供者 Bean
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        // DaoAuthenticationProvider 是 Spring Security 提供的一個基於 DAO 的認證提供者
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // 設置用於獲取用戶詳細資訊的 Service
        authProvider.setUserDetailsService(adminDetailsService);
        // 設置密碼編碼器
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // 定義前台用戶的 "記住我" 服務 Bean
    @Bean("userRememberMeServices")
    public RememberMeServices userRememberMeServices() {
        // 使用基於 Token 的 "記住我" 服務實現
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        // 設置 token 的有效期為 14 天（單位：秒）
        rememberMe.setTokenValiditySeconds(86400 * 14);
        // 設置為 true，表示即使客戶端沒有勾選 "記住我"，只要配置了此服務就會生效
        rememberMe.setAlwaysRemember(true);
        return rememberMe;
    }

    // 定義後台管理員的 "記住我" 服務 Bean
    @Bean("adminRememberMeServices")
    public RememberMeServices adminRememberMeServices() {
        // 使用基於 Token 的 "記住我" 服務實現
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, adminDetailsService);
        // 設置 token 的有效期為 14 天（單位：秒）
        rememberMe.setTokenValiditySeconds(86400 * 14);
        return rememberMe;
    }

    // 定義一個委派的 "記住我" 服務，根據請求路徑區分前後台
    // @Primary 標註當有多個同類型的 Bean 時，此 Bean 為優先注入的對象
    @Bean
    @Primary
    public RememberMeServices delegatingRememberMeServices(
            @Qualifier("userRememberMeServices") RememberMeServices userRememberMeServices,
            @Qualifier("adminRememberMeServices") RememberMeServices adminRememberMeServices) {

        // 返回一個 RememberMeServices 的匿名內部類實現
        return new RememberMeServices() {
            @Override
            // 當請求中包含 "記住我" cookie 時，此方法會被調用以嘗試自動登錄
            public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
                String uri = request.getRequestURI();
                // 如果是後台路徑，使用後台的 "記住我" 服務
                if (uri.startsWith("/admin/")) {
                    return adminRememberMeServices.autoLogin(request, response);
                }
                // 如果是前台 API 路徑或特定前台頁面，使用前台的 "記住我" 服務
                else if (uri.startsWith("/api/") || uri.startsWith("/frontend-template/")
                        || uri.equals("/login.html") || uri.equals("/register.html")) {
                    return userRememberMeServices.autoLogin(request, response);
                }
                // 默認情況下也使用前台的 "記住我" 服務
                return userRememberMeServices.autoLogin(request, response);
            }

            @Override
            // 登錄成功時調用此方法，用於生成和設置 "記住我" cookie
            public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
                // 根據請求路徑，調用對應的 "記住我" 服務的 loginSuccess 方法
                if (request.getRequestURI().startsWith("/admin/")) {
                    adminRememberMeServices.loginSuccess(request, response, auth);
                } else {
                    userRememberMeServices.loginSuccess(request, response, auth);
                }
            }

            @Override
            // 登錄失敗時調用此方法
            public void loginFail(HttpServletRequest request, HttpServletResponse response) {
                // 根據請求路徑，調用對應的 "記住我" 服務的 loginFail 方法
                if (request.getRequestURI().startsWith("/admin/")) {
                    adminRememberMeServices.loginFail(request, response);
                } else {
                    userRememberMeServices.loginFail(request, response);
                }
            }
        };
    }

    // 配置一個 HttpFirewall Bean，以允許 URL 中包含分號等特殊字符
    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        // StrictHttpFirewall 是 Spring Security 默認的防火牆實現
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // 設置為 true 以允許 URL 中包含分號 (;)
        firewall.setAllowSemicolon(true);
        // 設置為 true 以允許 URL 中包含編碼後的斜槓 (%2F)
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    // 將自定義的 HttpFirewall 應用到 Web 安全性配置中
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowSemicolonHttpFirewall());
    }

    // 核心的安全過濾器鏈配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 註冊認證提供者，這裡同時註冊了後台和前台的提供者
                .authenticationProvider(adminAuthenticationProvider())
                .authenticationProvider(customUserAuthenticationProvider)

                // 配置 HTTP 請求的授權規則
                .authorizeHttpRequests(authorize -> authorize
                	    // ✅ 後台登入頁
                	    .requestMatchers("/admin/login").permitAll()

                	    // ✅ 前台公開頁與靜態資源
                	    .requestMatchers("/", "/index.html", "/login.html", "/register.html",
                	        "/css/**", "/js/**", "/images/**", "/adminlte/**", "/frontend-template/**"
                	    ).permitAll()

                	    // ⭐ 前台頁面路由（含 /shop/products 頁）
                	    .requestMatchers("/shop/**").permitAll()

                	    // ⭐ 錯誤與 well-known 必放行，不然會一直被重導
                	    .requestMatchers("/error", "/error/**", "/.well-known/**").permitAll()

                	    // ✅ 你已開放的匿名 API
                	    .requestMatchers(HttpMethod.GET, "/api/analytics/**", "/analytics", "/analytics/**","/products","/shop/products","/shop/product/**","/product_img/**").permitAll()
                	    .requestMatchers("/api/users/register", "/api/users/login", "/api/users/send-code",
                	                     "/api/users/request-password-reset", "/api/csrf-token", "/login").permitAll()

                	    // ⭐ 如果前台「商品列表」需要匿名讀取，加上這條（依你實際 API 路徑調整）
                	    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                	    // 後台保護
                	    .requestMatchers("/admin/**").hasRole("ADMIN")

                	    // 其他需要登入（或開發期你想先放行就換成 .anyRequest().permitAll()）
                	    .anyRequest().authenticated()
                	)

                // 配置後台的表單登錄
                .formLogin(form -> form
                        // 指定後台登錄頁面的 URL
                        .loginPage("/admin/login")
                        // 指定處理登錄請求的 URL
                        .loginProcessingUrl("/admin/login")
                        // 登錄成功後默認跳轉的 URL
                        .defaultSuccessUrl("/admin/dashboard", true)
                        // 登錄失敗後跳轉的 URL
                        .failureUrl("/admin/login?error=true"))

                // 配置異常處理
                .exceptionHandling(ex -> ex
                        // 指定處理認證入口點，即未認證用戶訪問受保護資源時的處理方式
                        .authenticationEntryPoint(delegatingAuthenticationEntryPoint))

                // 配置 "記住我" 功能
                .rememberMe(remember -> remember
                        // 設置用於生成 token 的密鑰
                        .key(rememberMeKey)
                        // 使用自定義的委派 "記住我" 服務
                        .rememberMeServices(delegatingRememberMeServices(
                                userRememberMeServices(),
                                adminRememberMeServices())))

                // 配置登出功能
                .logout(logout -> logout
                        // 匹配 POST 方法的 "/**/logout" URL 作為登出請求
                        .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout", "POST"))
                        // 使用自定義的登出成功處理器
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        // 登出時使 HttpSession 失效
                        .invalidateHttpSession(true)
                        // 清除認證信息
                        .clearAuthentication(true)
                        // 登出時刪除指定的 cookie
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN", "remember-me"))

                // 配置 CSRF (跨站請求偽造) 防護
                .csrf(csrf -> csrf
                        // 使用基於 Cookie 的 CSRF Token 存儲庫，withHttpOnlyFalse() 允許前端 JS 讀取此 cookie
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // 在 UsernamePasswordAuthenticationFilter 之前添加自定義的 reCAPTCHA 驗證過濾器
                // 這確保了在嘗試用戶名密碼認證之前，先進行 reCAPTCHA 驗證
                .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 構建並返回 SecurityFilterChain
        return http.build();
    }
}