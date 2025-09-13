package com.tibafit.controller.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tibafit.model.user.User;
import com.tibafit.service.user.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller // 回傳html,RestContorller是純資料
@RequestMapping("/admin") // 純資料會加上api,例如我的使用者/api/users
public class AdminController {
	// 注入 UserService，這樣我們才能查詢使用者
	private final UserService userService;

	@Autowired
	public AdminController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/dashboard") // 後台首頁
	public String showAdminDashboard() {

		// 告訴 Thymeleaf 去 templates/admin/ 資料夾裡找 layout.html
		return "admin/dashboard";
	}

	/**
	 * 
	 * @param 這是用來查詢所有會員的方法
	 * @return
	 */
	@GetMapping("/members")
	public String showMemberList(@RequestParam(required = false) String keyword, Model model) {
		List<User> userList;

		// 先修剪 keyword 的頭尾空白
		if (keyword != null) {
			keyword = keyword.trim();
		}
		// 使用 isBlank() 來判斷，可以順便過濾掉使用者只輸入好幾個空格的情況
		if (keyword != null && !keyword.isBlank()) {
			userList = userService.searchUser(keyword);
		} else {
			userList = userService.findAll();
		}
		model.addAttribute("users", userList);
		model.addAttribute("keyword", keyword); // 讓搜尋框能記住上次的查詢
		return "admin/members";
	}

	/**
	 * 
	 * @param userId 這是用來單一查詢的方法
	 * @param model
	 * @return 回傳member-details.html檔案
	 */
	@GetMapping("/members/{userId}")
	public String showMemberDetails(@PathVariable Integer userId, Model model) {
		// PathVariable {userId} 裡的數字取出來並存入 userId
		User user = userService.findById(userId);
		model.addAttribute("user", user);
		return "admin/member-details";
	}
	/**
	 * 
	 * @param resp,專門用來獲得CSV檔
	 * @throws IOException
	 */
    @GetMapping("/members/export/csv")
    public void exportMembersToCsv(@RequestParam(required = false) String keyword, HttpServletResponse response) throws IOException {
        // 1. 設定 Response Headers
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"members.csv\"");
        response.setCharacterEncoding("UTF-8");

        // 2. 根據有無 keyword，決定要撈取的會員資料
        List<User> userList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            userList = userService.searchUser(keyword.trim());
        } else {
            userList = userService.findAll();
        }
        
        // 3. 準備日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 4. 寫入 CSV 內容
        try (PrintWriter writer = response.getWriter()) {
//        	自動切換到正確的 UTF-8 模式來打開檔案
        	writer.print("\uFEFF");
            writer.println("ID,Email,姓名,註冊日期");

            for (User user : userList) {
                String createTimeString = user.getCreateTime() != null ? dateFormat.format(user.getCreateTime()) : "";
                writer.println(
                    user.getUserId() + "," +
                    user.getEmail() + "," +
                    user.getName() + "," +
                    createTimeString
                );
            }
        }
    }
}