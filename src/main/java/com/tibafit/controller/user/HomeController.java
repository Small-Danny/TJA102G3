package com.tibafit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 注意這裡用的是 @Controller，而不是 @RestController
public class HomeController {

    /**
     * 處理對網站根路徑 ("/") 的請求。
     * 自動將使用者重新導向到真正的前端首頁。
     * @return 一個重新導向的視圖字串
     */
    @GetMapping("/")
    public String redirectToIndex() {
        // "redirect:" 前綴會告訴 Spring MVC 執行一個 HTTP 302 重新導向
        return "redirect:/frontend-template/index.html";
    }
}