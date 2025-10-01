// src/main/java/com/task/api/TaskRecordApiController.java
package com.tibafit.controller.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tibafit.dto.task.TaskRecordDTO;
import com.tibafit.dto.task.TaskRecordPatchReq;
import com.tibafit.dto.task.TaskRecordStatusUpdateReq;
import com.tibafit.dto.task.TaskRecordUpsertReq;
import com.tibafit.model.task.TaskRecordStatusVO;
import com.tibafit.model.task.TaskRecordVO;
import com.tibafit.model.task.TaskVO;
import com.tibafit.model.user.User;
import com.tibafit.repository.task.TaskRecordRepository;
import com.tibafit.repository.task.TaskRecordStatusRepository;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.task.TaskRecordService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/taskrecords")
@CrossOrigin // 如需跨網域（file:// 或不同埠）可先開著；之後可移到全域 CORS
public class TaskRecordApiController {

	private final TaskRecordService taskRecordService;
	private final TaskRecordRepository taskRecordRepository;
	private final TaskRecordStatusRepository statusRepository;
	private final UserRepository userRepository;

	@PersistenceContext
	private EntityManager em;

	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public TaskRecordApiController(TaskRecordService svc, TaskRecordRepository repo,
			TaskRecordStatusRepository statusRepo, UserRepository userRepository) {
		this.taskRecordService = svc;
		this.taskRecordRepository = repo;
		this.statusRepository = statusRepo;
		this.userRepository = userRepository;
	}

	// ===== Read =====
//  @GetMapping
//  public List<TaskRecordDTO> list(@RequestParam(required=false) Integer userId,
//                                  @RequestParam(required=false) Integer taskId, Authentication authentication) {
//    
//	List<TaskRecordVO> rows;
//    if (taskId != null) {
//      // 你原本 repo 若有 findByTaskId 可用；沒有就用 service.getAll() 後過濾
//      try {
//        rows = taskRecordRepository.findByTaskId(taskId);
//      } catch (Exception e) {
//        rows = taskRecordService.getAll().stream()
//               .filter(x -> x.getTaskVO()!=null && taskId.equals(x.getTaskVO().getTaskId()))
//               .toList();
//      }
//    } else if (userId != null) {
//      try {
//        rows = taskRecordRepository.findAllByUserId(userId);
//      } catch (Exception e) {
//        rows = taskRecordService.getAll().stream()
//               .filter(x -> x.getUser()!=null && userId.equals(x.getUser().getUserId()))
//               .toList();
//      }
//    } else {
//      rows = taskRecordService.getAll();
//    }
//    return rows.stream().map(TaskRecordDTO::from).toList();
//  }
//
//  @GetMapping("/{id}")
//  public TaskRecordDTO get(@PathVariable Integer id, Authentication authentication) {
//    TaskRecordVO vo = taskRecordService.getOneTaskRecord(id);
//    if (vo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord "+id+" not found");
//    return TaskRecordDTO.from(vo);
//  }

//GET /api/taskrecords?statusId=0
	@GetMapping
	@Transactional(readOnly = true)
	public List<TaskRecordDTO> list(@RequestParam(required = false) Integer statusId, Authentication authentication) {
		// ★ 從 Session 拿目前登入者（不要接收前端的 userId）
//		Integer me = ((User) authentication.getPrincipal()).getUserId();
		// 1. 【安全】從 Authentication 物件取得使用者帳號 (email)
		String userEmail = authentication.getName();

		// 2. 【可靠】使用帳號去資料庫撈取完整的 User 物件，確保資料一致性
		User currentUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("找不到已驗證的使用者: " + userEmail));

		// 3. 從撈出的 User 物件取得 userId
		Integer userId = currentUser.getUserId();
		// ----------------------------------------------

		// ★ 只看進行中：前端會傳 statusId=0；若沒帶則看全部
		List<TaskRecordVO> vos = taskRecordService.findByUserAndStatus(userId, statusId);
		return vos.stream().map(TaskRecordDTO::from).toList(); // 用你的 DTO 轉換
	}

	// ===== Create =====
	@PostMapping
	@Transactional
	public TaskRecordDTO create(@RequestBody TaskRecordUpsertReq req, Authentication authentication) {
		TaskRecordVO vo = new TaskRecordVO();

		// ----------------------------------------------
		// 1. 【安全】從 Authentication 物件取得使用者帳號 (email)
		String userEmail = authentication.getName();

		// 2. 【可靠】使用帳號去資料庫撈取完整的 User 物件，確保資料一致性
		User currentUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("找不到已驗證的使用者: " + userEmail));

		// 3. 從撈出的 User 物件取得 userId
		Integer userId = currentUser.getUserId();
		// ----------------------------------------------

		User userRef = em.getReference(User.class, userId);
		vo.setUser(userRef);
		if (req.taskId != null) {
			TaskVO taskRef = em.getReference(TaskVO.class, req.taskId);
			vo.setTaskVO(taskRef);
		}
		req.setUserStartTime(LocalDateTime.now());
		if (req.userStartTime != null)
			vo.setUserStartTime(LocalDateTime.parse(req.userStartTime, ISO));
		if (req.userEndTime != null)
			vo.setUserEndTime(LocalDateTime.parse(req.userEndTime, ISO));
		req.statusId = 0;
		if (req.statusId != null) {
			TaskRecordStatusVO st = statusRepository.findById(req.statusId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid statusId"));
			vo.setTaskRecordStatusVO(st);
		}

		TaskRecordVO saved = taskRecordService.addTaskRecord(vo); // 你的 service 命名依專案調整
		return TaskRecordDTO.from(saved);
	}

	// ===== Replace (full) =====
	@PutMapping("/{id}")
	@Transactional
	public TaskRecordDTO replace(@PathVariable Integer id, @RequestBody TaskRecordUpsertReq req,
			Authentication authentication) {
		TaskRecordVO vo = taskRecordService.getOneTaskRecord(id);

		if (vo == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord " + id + " not found");

		if (req.userId != null)
			vo.setUser(em.getReference(User.class, req.userId));
		else
			vo.setUser(null);
		if (req.taskId != null)
			vo.setTaskVO(em.getReference(TaskVO.class, req.taskId));
		else
			vo.setTaskVO(null);
		vo.setUserStartTime(req.userStartTime == null ? null : LocalDateTime.parse(req.userStartTime, ISO));
		vo.setUserEndTime(req.userEndTime == null ? null : LocalDateTime.parse(req.userEndTime, ISO));
		if (req.statusId != null) {
			TaskRecordStatusVO st = statusRepository.findById(req.statusId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid statusId"));
			vo.setTaskRecordStatusVO(st);
		} else {
			vo.setTaskRecordStatusVO(null);
		}

		TaskRecordVO saved = taskRecordService.updateTaskRecord(vo);
		return TaskRecordDTO.from(saved);
	}

	// ===== Patch (partial) =====
	@PatchMapping("/{id}")
	@Transactional
	public TaskRecordDTO patch(@PathVariable Integer id, @RequestBody TaskRecordPatchReq req,
			Authentication authentication) {
		TaskRecordVO vo = taskRecordService.getOneTaskRecord(id);

		if (vo == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord " + id + " not found");

		if (req.userId != null)
			vo.setUser(em.getReference(User.class, req.userId));
		if (req.taskId != null)
			vo.setTaskVO(em.getReference(TaskVO.class, req.taskId));
		if (req.userStartTime != null)
			vo.setUserStartTime(LocalDateTime.parse(req.userStartTime, ISO));
		if (req.userEndTime != null)
			vo.setUserEndTime(LocalDateTime.parse(req.userEndTime, ISO));
		if (req.statusId != null) {
			TaskRecordStatusVO st = statusRepository.findById(req.statusId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid statusId"));
			vo.setTaskRecordStatusVO(st);
		}

		TaskRecordVO saved = taskRecordService.updateTaskRecord(vo);
		return TaskRecordDTO.from(saved);
	}

	// ===== Patch only status (bulk update，避開整體驗證) =====
	@PatchMapping("/{id}/status")
	@Transactional
	public TaskRecordDTO updateStatus(@PathVariable Integer id, @RequestBody TaskRecordStatusUpdateReq req,
			Authentication authentication) {
		TaskRecordVO current = taskRecordService.getOneTaskRecord(id);

		if (current == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord " + id + " not found");

		TaskRecordStatusVO status = statusRepository.findById(req.statusId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid statusId"));

		int updated = taskRecordRepository.updateStatusById(id, status);
		if (updated == 0)
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed");

		return TaskRecordDTO.from(taskRecordService.getOneTaskRecord(id));
	}

	// ===== Delete =====
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void delete(@PathVariable Integer id, Authentication authentication) {

		TaskRecordVO vo = taskRecordService.getOneTaskRecord(id);
		if (vo == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskRecord " + id + " not found");
		taskRecordService.deleteTaskRecord(id); // 命名依你的 service 調整
	}
}
