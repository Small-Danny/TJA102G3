package com.tibafit.controller.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tibafit.dto.user.ChangePasswordRequest;
import com.tibafit.exception.ValidationException;
import com.tibafit.model.user.User;
import com.tibafit.service.user.AdminService;
import com.tibafit.service.user.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller // 回傳html,RestContorller是純資料
@RequestMapping("/admin") // 純資料會加上api,例如我的使用者/api/users
public class AdminController {
	// 注入 UserService，這樣我們才能查詢使用者
	private final UserService userService;
	private final AdminService adminService;
	
	@Autowired
	public AdminController(UserService userService,AdminService adminService) {
		this.userService = userService;
		this.adminService = adminService;
	}

	@GetMapping("/dashboard") // 後台首頁
	public String showAdminDashboard() {

		// 告訴 Thymeleaf 去 templates/admin/ 資料夾裡找 layout.html
		return "admin/dashboard";
	}

	@GetMapping("/login")
	public String showLoginPage() {
		// 去templates/admin/資料夾裡，處理登入登出邏輯
		return "admin/login";
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
	 * @param @PathVariable，要去 URL 路徑中Id,新增停權啟用功能
	 * @return
	 */
	@PostMapping("/members/{id}/toggle-status")
	public String toggleMemberStatus(@PathVariable("id") Integer userId) {
		userService.toggleAccountStatus(userId);
		return "redirect:/admin/members";

	}

	/**
	 * 
	 * @param resp,專門用來獲得CSV檔
	 * @throws IOException
	 */
	@GetMapping("/members/export/csv")
	public void exportMembersToCsv(@RequestParam(required = false) String keyword, HttpServletResponse response)
			throws IOException {
		// 1. 設定 Response Headers
		response.setContentType("text/csv; charset=UTF-8");
		String formattedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		String fileName = "members-" + formattedDate + ".csv";
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
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
			writer.println("會員ID,姓名,Email,電話,性別,身高(cm),體重(kg),BMI,帳戶點數,帳號狀態,註冊日期");

			for (User user : userList) {
				String gender = switch (user.getGender()) {
				case 1 -> "男";
				case 2 -> "女";
				default -> "不透露";
				};
				String status = (user.getAccountStatus() == 1) ? "啟用" : "停權";
				String phone = user.getPhone() != null ? user.getPhone() : "";
				String createTimeString = user.getCreateTime() != null ? dateFormat.format(user.getCreateTime()) : "";
				String line = String.join(",", user.getUserId().toString(), user.getName(), user.getEmail(), phone,
						gender, user.getHeightCm() != null ? user.getHeightCm().toString() : "",
						user.getWeightKg() != null ? user.getWeightKg().toString() : "",
						user.getBmi() != null ? user.getBmi().toString() : "",
						user.getPointsBalance() != null ? user.getPointsBalance().toString() : "0", status,
						createTimeString);

				writer.println(line);
			}
		}
	}
	
	@GetMapping("/profile")
	public String showProfilePage() {
		return "admin/profile";
	}
	
	//修改密碼表單提交
	@PostMapping("/profile/change-password")
	public String changeAdminPassword (ChangePasswordRequest changePasswordRequest, // 直接用 DTO 接收
            RedirectAttributes redirectAttributes) {
		try {
			  // 1. 從 Spring Security 中獲取當前登入的管理員帳號
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentAdminAccount = authentication.getName();
            
            adminService.changeAdminPassword(currentAdminAccount, changePasswordRequest);
            redirectAttributes.addFlashAttribute("successMessage", "密碼已成功修改！");
   
		}catch(ValidationException e) {
	        //驗證錯誤
			 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}catch(Exception e) {   
			//處理其他錯誤
			redirectAttributes.addFlashAttribute("errorMessage", "發生未知錯誤，請稍後再試。");
            e.printStackTrace(); // 在後台印出詳細錯誤
		}
	        
	        // 操作完成後，重新導向回個人設定頁面
	        return "redirect:/admin/profile";
	}
}