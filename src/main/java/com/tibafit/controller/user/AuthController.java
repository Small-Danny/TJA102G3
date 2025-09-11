package com.tibafit.controller.user; // 請確認這是你的 controller 套件路徑

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // 這個方法專門用來顯示我們為後台製作的登入頁面
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login"; // 這會去找 templates/admin/login.html
    }
}