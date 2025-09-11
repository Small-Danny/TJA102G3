package com.tibafit.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //回傳html,RestContorller是純資料
@RequestMapping("/admin")  //純資料會加上api,例如我的使用者/api/users
public class AdminController {
	 @GetMapping("/dashboard")
	public String showAdminDashboard() {
		
		   //告訴 Thymeleaf 去 templates/admin/ 資料夾裡找 layout.html
		 return "admin/dashboard";
	}
	
	
}
