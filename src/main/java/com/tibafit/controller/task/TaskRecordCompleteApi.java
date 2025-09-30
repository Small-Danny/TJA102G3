package com.tibafit.controller.task;

import org.springframework.http.HttpStatus;
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
import com.tibafit.service.task.TaskRecordService;

import jakarta.persistence.EntityManager;

@RestController
@RequestMapping("/api/taskrecords")
@CrossOrigin // 跨網域需要就保留
public class TaskRecordCompleteApi {

  private final TaskRecordService taskRecordService;
  private final TaskRecordStatusRepository statusRepo;
  private final EntityManager em;

  public TaskRecordCompleteApi(TaskRecordService svc,
                               TaskRecordStatusRepository stRepo,
                               EntityManager em) {
    this.taskRecordService = svc;
    this.statusRepo = stRepo;
    this.em = em;
  }

  @PatchMapping("/{id}/complete")
  @Transactional
  public TaskRecordDTO markComplete(@PathVariable Integer id) {
    TaskRecordVO vo = taskRecordService.getOneTaskRecord(id);
    if (vo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord " + id + " not found");

    // 1) 設定狀態 = 1
    TaskRecordStatusVO status = statusRepo.findById(1)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "statusId=1 not exists"));
    vo.setTaskRecordStatusVO(status);

    // 2) 設定完成時間 = 現在
    vo.setUserEndTime(java.time.LocalDateTime.now());

    // 3) （可選）暫時固定 userId=7（等你上會員再改用登入者）
    if (vo.getUser() == null || !Integer.valueOf(7).equals(vo.getUser().getUserId())) {
      vo.setUser(em.getReference(User.class, 7));
    }

    TaskRecordVO saved = taskRecordService.updateTaskRecord(vo);
    return TaskRecordDTO.from(saved);
  }
}

