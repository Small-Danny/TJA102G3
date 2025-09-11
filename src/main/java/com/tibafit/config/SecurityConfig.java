package com.tibafit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("password123"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 規則一 (最優先)：先把所有人都需要用到的靜態資源和公開頁面放行
                .requestMatchers(
                    // ★★★ 關鍵：後台樣板的靜態資源也必須放行 ★★★
                    "/adminlte/**", 
                    
                    "/frontend-template/**", 
                    "/images/**",
                    "/", "/index.html", "/login.html", "/register.html",
                    "/forgot-password.html", "/reset-set-password.html",
                    "/api/**" // 為了簡單起見，我們先放行所有 API
                ).permitAll()
                // 規則二：接著設定需要 ADMIN 角色的後台路徑
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 規則三 (最後)：剩下的所有其他請求，都需要登入
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // 指向我們自己建立的 Controller 和登入頁
                .loginPage("/admin/login") 
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .permitAll() // 確保登入頁本身是公開的
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}