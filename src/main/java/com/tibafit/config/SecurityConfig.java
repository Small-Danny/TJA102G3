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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter;

    @Autowired
    private AdminDetailsService adminDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.security.remember-me-key}")
    private String rememberMeKey;

    @Autowired
    private AuthenticationEntryPoint delegatingAuthenticationEntryPoint;

    @Autowired
    @Qualifier("customUserAuthenticationProvider")
    private AuthenticationProvider customUserAuthenticationProvider;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    /* =====================  未認證導向規則（API 回 401；後台/前台導向對應登入頁）  ===================== */
    @Bean
    public static AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

        // API：未登入回 401 + JSON
        entryPoints.put(new AntPathRequestMatcher("/api/**"), (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowSemicolonHttpFirewall());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /* =====================  Security Filter Chain  ===================== */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 兩種 Provider
                .authenticationProvider(adminAuthenticationProvider())
                .authenticationProvider(customUserAuthenticationProvider)

                // 路由授權
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

                        // 匿名 API
                        .requestMatchers(HttpMethod.GET,
                                "/api/analytics/**", "/analytics", "/analytics/**",
                                "/products", "/shop/products", "/shop/product/**",
                                "/product_img/**").permitAll()
                        .requestMatchers("/api/users/register", "/api/users/login", "/api/users/send-code",
                                "/api/users/request-password-reset", "/api/csrf-token", "/login", "/api/cart/**",
                                "/api/checkout", "/api/line-pay/request", "/api/line-pay/mock-confirm",
                                "/api/ecpay/**","/payment/ecpay","/api/ecpay/callback").permitAll()

                        // 商品讀取（匿名 GET）
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()

                        // 後台需 ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 其餘需登入
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
                .cors(withDefaults())
                // CSRF 配置
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**", "/ecpay/callback", "/payment/ecpay", "/payment/ecpay/return")
                )

                // 安全標頭配置（包含綠界CSP）
                .headers(headers -> headers
                        .defaultsDisabled()
                        .frameOptions(frame -> frame.sameOrigin())
                        // 補充CSP配置，添加缺失的sha256
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("script-src 'self' " +
                                        "https://payment-stage.ecpay.com.tw " +
                                        "https://gpayment-stage.ecpay.com.tw " +
                                        "https://*.googletagmanager.com " +
                                        "https://www.googleadservices.com " +
                                        "https://googleads.g.doubleclick.net " +
                                        "https://www.google.com.tw " +
                                        "https://*.google-analytics.com " +
                                        "https://analytics.google.com " +
                                        "https://payments.developers.google.com " +
                                        "https://connect.facebook.net " +
                                        "https://*.clarity.ms " +
                                        "https://*.bing.com " +
                                        "'unsafe-eval' " +
                                        "'sha256-F1ogW9PGWrDvjjgNJcQcD4s7PYlq6ercHhJSTSCH84E=' " +
                                        "'unsafe-inline';")
                        )
                )

                // reCAPTCHA 過濾器
                .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}