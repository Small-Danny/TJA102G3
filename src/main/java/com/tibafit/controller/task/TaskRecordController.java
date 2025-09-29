package com.tibafit.controller.task;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tibafit.model.task.TaskRecordStatusVO;
import com.tibafit.model.task.TaskRecordVO;
import com.tibafit.model.task.TaskVO;
import com.tibafit.model.user.User;
import com.tibafit.service.task.TaskRecordServiceImpl;
import com.tibafit.service.task.TaskRecordStatusService;
import com.tibafit.service.task.TaskService;
import com.tibafit.service.user.UserServiceImpl;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/taskrecord")
public class TaskRecordController {

	@Autowired
	private TaskRecordServiceImpl taskRecordService;

	// 如果你有 Task 與 User 的下拉清單，請解除註解並注入
	@Autowired
	private TaskService taskService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private TaskRecordStatusService taskRecordStatusService;

	/** 把前後空白自動去除，避免 " 文字 " 影響驗證或入庫 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : text.trim());
			}
		});
	}

	/** 供 select_page 下拉（可選，如果不需要可移除） */
	@ModelAttribute("taskRecordListData")
	public List<TaskRecordVO> populateTaskRecordList() {
		return taskRecordService.getAll();
	}

	// 若你要在新增/修改頁提供 Task / User 下拉，請放開以下兩個 @ModelAttribute 並實作對應 service 的 getAll()
//     @ModelAttribute("taskListData")
//     public List<TaskVO> populateTaskList() { return taskService.getAll(); }

	@ModelAttribute("taskListData")
	public List<TaskVO> populateTaskList() {
		return taskService.getAll();
	}

	@ModelAttribute("userListData")
	public List<User> populateUserList() {
		return userService.findAll();
	}

	@ModelAttribute("taskRecordStatusListData")
	public List<TaskRecordStatusVO> populateTaskRecordStatusList() {
		return taskRecordStatusService.getAll();
	}

	/** 入口頁 */
	@GetMapping("/select_page")
	public String selectPage() {
		return "back-end/taskrecord/select_page";
	}

	/** 列出全部 */
	@GetMapping("/listAll")
	public String listAll(Model model) {
		model.addAttribute("list", taskRecordService.getAll());
		return "back-end/taskrecord/listAllTaskRecord";
	}

	/** 進入新增頁 */
	@GetMapping("/add")
	public String addPage(Model model) {
		model.addAttribute("taskRecordVO", new TaskRecordVO());
		return "back-end/taskrecord/add_taskrecord";
	}

	/** 新增 */
	@PostMapping("/insert")
	public String insert(@Valid @ModelAttribute("taskRecordVO") TaskRecordVO taskRecordVO, BindingResult result,
			Model model) {

		if (result.hasErrors()) {
			return "back-end/taskrecord/add_taskrecord";
		}

		taskRecordService.addTaskRecord(taskRecordVO);
		model.addAttribute("list", taskRecordService.getAll());
		return "back-end/taskrecord/listAllTaskRecord";
	}

	/** 查單筆 */
	@GetMapping("/getOne")
	public String getOne(@RequestParam("taskRecordId") Integer taskRecordId, Model model) {
		TaskRecordVO vo = taskRecordService.getOneTaskRecord(taskRecordId);
		if (vo == null) {
			model.addAttribute("errorMessage", "查無資料：id=" + taskRecordId);
			return "back-end/taskrecord/select_page";
		}
		model.addAttribute("taskRecordVO", vo);
		return "back-end/taskrecord/listOneTaskRecord";
	}

	/** 進入修改頁 */
	@GetMapping("/getOne_For_Update")
	public String getOneForUpdate(@RequestParam("taskRecordId") Integer taskRecordId, Model model) {
		TaskRecordVO vo = taskRecordService.getOneTaskRecord(taskRecordId);
		if (vo == null) {
			model.addAttribute("errorMessage", "資料不存在或已被刪除：id=" + taskRecordId);
			return "back-end/taskrecord/select_page";
		}
		model.addAttribute("taskRecordVO", vo);
		return "back-end/taskrecord/update_taskrecord_input";
	}

	/** 修改 */
	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("taskRecordVO") TaskRecordVO formVO, BindingResult result,
			Model model) {

		if (result.hasErrors()) {
			return "back-end/taskrecord/update_taskrecord_input";
		}

		taskRecordService.updateTaskRecord(formVO);
		model.addAttribute("taskRecordVO", taskRecordService.getOneTaskRecord(formVO.getTaskRecordId()));
		return "back-end/taskrecord/listOneTaskRecord";
	}

	/** 刪除 */
	@PostMapping("/delete")
	public String delete(@RequestParam("taskRecordId") Integer taskRecordId, Model model) {
		// 如有外鍵限制（任務/會員）可在此 try-catch 並回報友善訊息
		taskRecordService.deleteTaskRecord(taskRecordId);
		model.addAttribute("list", taskRecordService.getAll());
		return "back-end/taskrecord/listAllTaskRecord";
	}
}
