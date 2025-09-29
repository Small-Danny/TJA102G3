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

import com.tibafit.model.task.TaskTypeVO;
import com.tibafit.service.task.TaskTypeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasktype")
public class TaskTypeController {

    @Autowired
    private TaskTypeService taskTypeService; // bean name: "taskTypeService"
    
    @GetMapping("/select_page")
    public String selectPage(Model model) {
        model.addAttribute("taskTypeListData", taskTypeService.getAll()); // 供下拉
        return "back-end/tasktype/select_page";
    }
    
    @ModelAttribute("taskTypeListData")
    public List<TaskTypeVO> populateTaskTypeList() {
        return taskTypeService.getAll();
    }

    /** 讓前後空白自動去除，避免 "  文字  " 進DB或驗證錯誤 */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override public void setAsText(String text) {
                setValue(text == null ? null : text.trim());
            }
        });
    }

    /** 進入選擇頁（可放查詢單筆/新增入口） */
//    @GetMapping("/select_page")
//    public String selectPage() {
//        return "back-end/tasktype/select_page";
//    }

    /** 列出所有 TaskType */
    @GetMapping("/listAll")
    public String listAll(Model model) {
        model.addAttribute("list", taskTypeService.getAll());
        return "back-end/tasktype/listAllTaskType";
    }

    /** 新增頁面 */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("taskTypeVO", new TaskTypeVO());
        return "back-end/tasktype/add_tasktype";
    }

    /** 新增動作 */
    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute("taskTypeVO") TaskTypeVO taskTypeVO,
                         BindingResult result,
                         Model model) {
        // 名稱重複驗證（假設 TaskTypeVO 有 taskTypeName 欄位）
        if (taskTypeVO.getTaskTypeName() != null &&
            taskTypeService.existsByTaskTypeName(taskTypeVO.getTaskTypeName())) {
            result.rejectValue("taskTypeName", "duplicate", "類別名稱已存在");
        }

        if (result.hasErrors()) {
            return "back-end/tasktype/add_tasktype";
        }

        taskTypeService.addTaskType(taskTypeVO);
        // 新增完導回列表
        model.addAttribute("list", taskTypeService.getAll());
        return "back-end/tasktype/listAllTaskType";
    }

    /** 顯示單筆（依 id） */
    @GetMapping("/getOne")
    public String getOne(@RequestParam("taskTypeId") Integer taskTypeId, Model model) {
        TaskTypeVO vo = taskTypeService.getOneTaskType(taskTypeId);
        if (vo == null) {
            model.addAttribute("errorMessage", "查無資料：id=" + taskTypeId);
            return "back-end/tasktype/select_page";
        }
        model.addAttribute("taskTypeVO", vo);
        return "back-end/tasktype/listOneTaskType";
    }

    /** 進入修改頁 */
    @GetMapping("/getOne_For_Update")
    public String getOneForUpdate(@RequestParam("taskTypeId") Integer taskTypeId, Model model) {
        TaskTypeVO vo = taskTypeService.getOneTaskType(taskTypeId);
        if (vo == null) {
            model.addAttribute("errorMessage", "查無資料：id=" + taskTypeId);
            return "back-end/tasktype/select_page";
        }
        model.addAttribute("taskTypeVO", vo);
        return "back-end/tasktype/update_tasktype_input";
    }

    /** 修改動作 */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("taskTypeVO") TaskTypeVO formVO,
                         BindingResult result,
                         Model model) {
        // 取出原資料以確認是否更名，並做「同名不同 id」的重複驗證
        TaskTypeVO dbVO = taskTypeService.getOneTaskType(formVO.getTaskTypeId());
        if (dbVO == null) {
            model.addAttribute("errorMessage", "資料不存在或已被刪除：id=" + formVO.getTaskTypeId());
            return "back-end/tasktype/select_page";
        }

        if (formVO.getTaskTypeName() != null) {
            boolean nameExists = taskTypeService.existsByTaskTypeName(formVO.getTaskTypeName());
            boolean nameChanged = !formVO.getTaskTypeName().equalsIgnoreCase(dbVO.getTaskTypeName() == null ? "" : dbVO.getTaskTypeName());
            if (nameChanged && nameExists) {
                result.rejectValue("taskTypeName", "duplicate", "類別名稱已存在");
            }
        }

        if (result.hasErrors()) {
            return "back-end/tasktype/update_tasktype_input";
        }

        taskTypeService.updateTaskType(formVO);
        model.addAttribute("taskTypeVO", taskTypeService.getOneTaskType(formVO.getTaskTypeId()));
        return "back-end/tasktype/listOneTaskType";
    }

    /** 刪除 */
    @PostMapping("/delete")
    public String delete(@RequestParam("taskTypeId") Integer taskTypeId, Model model) {
        // 若有外鍵關聯，這裡可補 try-catch 給出友善訊息
        taskTypeService.deleteTaskType(taskTypeId);
        model.addAttribute("list", taskTypeService.getAll());
        return "back-end/tasktype/listAllTaskType";
    }
}
