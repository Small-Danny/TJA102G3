package com.tibafit.config;

import java.util.LinkedHashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.authentication.ProviderManager;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

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

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tibafit.config.filter.ReCaptchaAuthenticationFilter;

import com.tibafit.service.user.AdminDetailsService;

import jakarta.servlet.http.HttpServletResponse;

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

// ★★★ 加上 static，打破循環依賴 ★★★

	@Bean

	public static AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {

		final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

		entryPoints.put(new AntPathRequestMatcher("/api/**"), (request, response, authException) -> {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			response.setContentType("application/json;charset=UTF-8");

			Map<String, Object> errorDetails = new LinkedHashMap<>();

			errorDetails.put("error", "未登入或憑證無效");

			errorDetails.put("message", "請先登入以存取此資源");

			errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());

			response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));

		});

		final LoginUrlAuthenticationEntryPoint defaultEntryPoint = new LoginUrlAuthenticationEntryPoint("/admin/login");

		final DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(

				entryPoints);

		delegatingEntryPoint.setDefaultEntryPoint(defaultEntryPoint);

		return delegatingEntryPoint;

	}

	@Bean

	public AuthenticationManager userAuthenticationManager(

			@Qualifier("customUserAuthenticationProvider") AuthenticationProvider customUserAuthenticationProvider) {

		return new ProviderManager(customUserAuthenticationProvider);

	}

	@Bean

	public DaoAuthenticationProvider adminAuthenticationProvider() {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(adminDetailsService);

		authProvider.setPasswordEncoder(passwordEncoder);

		return authProvider;

	}

	@Bean

	public RememberMeServices rememberMeServices(

			@Qualifier("userDetailsService") UserDetailsService userDetailsService) {

		TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);

		rememberMe.setTokenValiditySeconds(86400 * 14); // 14 天

		return rememberMe;

	}

	@Bean

	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http

				.authenticationProvider(adminAuthenticationProvider())

				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/", "/index.html", "/login.html",

						"/register.html", "/css/**", "/js/**", "/images/**", "/adminlte/**", "/frontend-template/**",

						"/api/users/register", "/api/users/login", "/api/users/send-code", // 註冊的發送驗證碼 API

						"/api/users/request-password-reset", // 忘記密碼的 API

						"/api/csrf-token" //

				).permitAll().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/api/**").authenticated()

						.anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/admin/login").loginProcessingUrl("/admin/login")

						.defaultSuccessUrl("/admin/dashboard", true).failureUrl("/admin/login?error=true").permitAll())

				.exceptionHandling(ex -> ex.authenticationEntryPoint(delegatingAuthenticationEntryPoint))

				.rememberMe(remember -> remember.key(rememberMeKey).userDetailsService(adminDetailsService)

						.tokenValiditySeconds(86400 * 14))

				.logout(logout -> logout
					    // 同时支持前台/logout和后台/admin/logout的POST请求
					    .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout", "POST"))
					    .logoutSuccessHandler(customLogoutSuccessHandler) // 复用现有的登出成功处理器（已适配前后台跳转）
					    .invalidateHttpSession(true)
					    .clearAuthentication(true)
					    .deleteCookies("JSESSIONID", "XSRF-TOKEN")
					)

				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

				.addFilterBefore(recaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

}