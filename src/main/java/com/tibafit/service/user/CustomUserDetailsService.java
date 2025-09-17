package com.tibafit.service.user;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	 System.out.println("加载用户信息用于生成remember-me token：" + username);
        com.tibafit.model.user.User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + username));

        // 新增：檢查賬號是否被停權
        if (user.getAccountStatus() != 1) {
            throw new UsernameNotFoundException("賬號已被停權，請聯系管理員");
        }

        // 轉換為Spring Security的UserDetails
        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}