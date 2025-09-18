package com.tibafit.config;

import org.springframework.beans.factory.ObjectProvider; // ★ 1. 引入 ObjectProvider
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tibafit.service.user.AdminDetailsService;
import com.tibafit.service.user.ReCaptchaService;

import jakarta.servlet.http.HttpServletRequest;

@Component("customAdminAuthenticationProvider")
public class CustomAdminAuthenticationProvider implements AuthenticationProvider {

	private final AdminDetailsService adminDetailsService;
	private final PasswordEncoder passwordEncoder;
	// ↓↓↓↓↓↓ ★ 2. 將 ReCaptchaService 改為 ObjectProvider<ReCaptchaService> ↓↓↓↓↓↓
	private final ObjectProvider<ReCaptchaService> reCaptchaServiceProvider;

	@Autowired
	// ↓↓↓↓↓↓ ★ 3. 修改建構子，注入 ObjectProvider<ReCaptchaService> ↓↓↓↓↓↓
	public CustomAdminAuthenticationProvider(AdminDetailsService adminDetailsService, PasswordEncoder passwordEncoder,
			ObjectProvider<ReCaptchaService> reCaptchaServiceProvider) {
		this.adminDetailsService = adminDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.reCaptchaServiceProvider = reCaptchaServiceProvider;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		// ↓↓↓↓↓↓ ★ 4. 在方法內部，透過 Provider 取得實例來使用 ↓↓↓↓↓↓
		ReCaptchaService reCaptchaService = reCaptchaServiceProvider.getObject();

		String recaptchaToken = request.getParameter("g-recaptcha-response");
		if (recaptchaToken == null || !reCaptchaService.validateToken(recaptchaToken)) {
			throw new BadCredentialsException("reCAPTCHA 驗證失敗");
		}

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		UserDetails userDetails = adminDetailsService.loadUserByUsername(username);

		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("帳號或密碼錯誤");
		}

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}