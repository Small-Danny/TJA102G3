package com.tibafit.controller.task;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.task.TaskDTO;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.task.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskAvailableApi {
  private final TaskService taskService;
  private final UserRepository userRepository;

  public TaskAvailableApi(TaskService taskService, UserRepository userRepository) { this.taskService = taskService; this.userRepository = userRepository;}

  @GetMapping("/available")
  public List<TaskDTO> listAvailable(/*@RequestParam(defaultValue = "not-active") String mode,*/
                                     Authentication authentication) {
//    Integer me = ((User) authentication.getPrincipal()).getUserId(); // ★從 Session 取 userId
//    AvailableMode m = "never".equalsIgnoreCase(mode) ? AvailableMode.NEVER : AvailableMode.NOT_ACTIVE;
    
	// 1. 【安全】從 Authentication 物件取得使用者帳號 (email)
	String userEmail = authentication.getName();

	// 2. 【可靠】使用帳號去資料庫撈取完整的 User 物件，確保資料一致性
	User currentUser = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new UsernameNotFoundException("找不到已驗證的使用者: " + userEmail));

	// 3. 從撈出的 User 物件取得 userId
	Integer userId = currentUser.getUserId();
	
    return taskService.findAvailable(userId).stream()
        .map(TaskDTO::from) // 這裡用你現有的 DTO 轉換
        .toList();
  }
}
