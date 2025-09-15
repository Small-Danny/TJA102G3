package com.tibafit.service.user;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tibafit.model.user.Admin;
import com.tibafit.repository.user.AdminRepository;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查詢資料庫
        Admin admin = adminRepository.findByAccount(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到管理員: " + username));

        // 轉換成 Spring Security 需要的 UserDetails 格式
        //    這裡我們給予所有管理員 "ROLE_ADMIN" 的權限名稱
        //SimpleGrantedAuthority 純粹標籤
        //Collections.singletonList()是建立一個只包含單一元素的、不可修改的列表。
        return new User(
            admin.getAccount(),
            admin.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }
}