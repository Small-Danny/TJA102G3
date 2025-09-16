package com.tibafit.service.user;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tibafit.repository.user.UserRepository;

@Service("userDetailsService") // 我們給它一個明確的名字，方便引用
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        com.tibafit.model.user.User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + username));
        
        // 檢查帳號狀態
        if (user.getAccountStatus() != 1) {
            throw new DisabledException("帳號已停用");
        }
        // 將我們自己的 User 物件，轉換成 Spring Security 的 UserDetails 物件
        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 賦予一般使用者權限
        );
    }
}