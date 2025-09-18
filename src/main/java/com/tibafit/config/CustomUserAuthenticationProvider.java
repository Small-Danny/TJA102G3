package com.tibafit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("customUserAuthenticationProvider")
public class CustomUserAuthenticationProvider implements AuthenticationProvider {

    // 注入專門給「前台會員」用的 UserDetailsService
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        // 這裡可以加入額外的檢查，例如帳號是否被停權
        // if (!user.isEnabled()) {
        //     throw new BadCredentialsException("帳號已被停權");
        // }

        if (passwordEncoder.matches(password, user.getPassword())) {
            // 認證成功，回傳一個包含完整權限資訊的 Authentication 物件
            return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        } else {
            // 密碼錯誤
            throw new BadCredentialsException("帳號或密碼錯誤");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 表示這個 Provider 只處理 UsernamePasswordAuthenticationToken 類型的認證請求
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}