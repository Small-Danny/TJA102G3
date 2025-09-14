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

    @Autowired
    private ReCaptchaAuthenticationFilter recaptchaAuthenticationFilter;

    @Autowired
    private AdminDetailsService adminDetailsService; 
    
    @Value("${app.security.remember-me-key}")
    private String rememberMeKey;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService); //手動注入，避免自動有問題
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 這個 Bean 維持不變，它「專門」給前台的 UserServiceImpl 注入使用
    @Bean
    public RememberMeServices rememberMeServices(@Qualifier("userDetailsService") UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMe.setTokenValiditySeconds(86400 * 14);
        return rememberMe;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(adminAuthenticationProvider())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/", "/index.html", "/login.html", "/register.html", "/forgot-password.html",
                        "/reset-set-password.html", "/css/**", "/js/**", "/images/**", "/fonts/**", "/assets/**",
                        "/adminlte/**", "/frontend-template/**", "/avatars/**", "/api/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            // 【修正】為後台的 rememberMe 建立一個「獨立」的設定，不再共用 Bean
            .rememberMe(remember -> remember
                .key(rememberMeKey) // 使用我們注入的密鑰
                .userDetailsService(adminDetailsService) // ★★★ 關鍵：明確指定後台要用 AdminDetailsService
                .tokenValiditySeconds(86400 * 14) // 設定有效期
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
            )
            .addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}