package com.tibafit.controller.task;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tibafit.dto.task.TaskRecordDTO;
import com.tibafit.model.task.TaskRecordStatusVO;
import com.tibafit.model.task.TaskRecordVO;
import com.tibafit.model.user.User;
import com.tibafit.repository.task.TaskRecordStatusRepository;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.task.TaskRecordService;

import jakarta.persistence.EntityManager;

@RestController
@RequestMapping("/api/taskrecords")
@CrossOrigin // 跨網域需要就保留
public class TaskRecordCompleteApi {

  private final TaskRecordService taskRecordService;
  private final TaskRecordStatusRepository statusRepo;
  private final EntityManager em;
  private final UserRepository userRepository;

  public TaskRecordCompleteApi(TaskRecordService svc,
                               TaskRecordStatusRepository stRepo,
                               EntityManager em, UserRepository userRepository) {
    this.taskRecordService = svc;
    this.statusRepo = stRepo;
    this.em = em;
    this.userRepository = userRepository;
  }

  @PatchMapping("/{id}/complete")
  @Transactional
  public TaskRecordDTO markComplete(@PathVariable Integer id, Authentication authentication) {
    TaskRecordVO vo = taskRecordService.getOneTaskRecord(id);
    
	// ----------------------------------------------
	// 1. 【安全】從 Authentication 物件取得使用者帳號 (email)
	String userEmail = authentication.getName();

	// 2. 【可靠】使用帳號去資料庫撈取完整的 User 物件，確保資料一致性
	User currentUser = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new UsernameNotFoundException("找不到已驗證的使用者: " + userEmail));

	// 3. 從撈出的 User 物件取得 userId
	Integer userId = currentUser.getUserId();
	// ----------------------------------------------
    
    if (vo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord " + id + " not found");

    // 1) 設定狀態 = 1
    TaskRecordStatusVO status = statusRepo.findById(1)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "statusId=1 not exists"));
    vo.setTaskRecordStatusVO(status);

    // 2) 設定完成時間 = 現在
    vo.setUserEndTime(java.time.LocalDateTime.now());

    // 3) （可選) userId（等你上會員再改用登入者）
    if (vo.getUser() == null || !Integer.valueOf(userId).equals(vo.getUser().getUserId())) {
      vo.setUser(em.getReference(User.class, userId));
    }

    TaskRecordVO saved = taskRecordService.updateTaskRecord(vo);
    return TaskRecordDTO.from(saved);
  }
}

