package com.tibafit.controller.task;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tibafit.model.task.TaskService;
import com.tibafit.model.task.TaskTypeService;
import com.tibafit.model.task.TaskTypeVO;
import com.tibafit.model.task.TaskVO;

import jakarta.validation.Valid;

// 若你有任務類型下拉，請提供對應的型別與 Service（自行調整 import）
/*
import com.tasktype.model.TaskTypeService;
import com.tasktype.model.TaskTypeVO; // 或 DTO
*/

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskSvc;
    private final TaskTypeService taskTypeSvc; // 若需要類型下拉

    public TaskController(TaskService taskSvc , TaskTypeService taskTypeSvc ) {
    					//TaskService 建構子注入   TaskTypeService 建構子注入
        this.taskSvc = taskSvc;
        this.taskTypeSvc = taskTypeSvc;
    }

    /* ======== 新增頁 ======== */
    @GetMapping("addTask")
    public String addTask(ModelMap model) {
        TaskVO taskVO = new TaskVO();
        taskVO.setPoints(0);
        model.addAttribute("taskVO", taskVO);
        return "back-end/task/addTask";
    }

    /* ======== 新增處理 ======== */
    @PostMapping("insert")
    public String insert(@Valid TaskVO taskVO, BindingResult result, ModelMap model) {

        // 你原本有移除檔案欄位錯誤的邏輯；這裡若有欄位要跳過驗證，可用 removeFieldError
        // 範例：result = removeFieldError(taskVO, result, "someFieldName");

        // taskIcon 必填且為 URL（若在 entity 用 @NotBlank 已驗證，這段可省略或換為更精細的檢核）
        if (result.hasErrors()) {
            return "back-end/task/addTask";
        }

        taskSvc.addTask(taskVO);

        // 列表資料 & 成功訊息
        model.addAttribute("success", "- (新增成功)");
        return "redirect:/tasks/listAllTask";
    }

    /* ======== 前往修改頁 ======== */
    @PostMapping("getOne_For_Update")
    public String getOne_For_Update(@RequestParam("taskId") Integer taskId, ModelMap model) {
        TaskVO taskVO = taskSvc.getOneTask(taskId);
        model.addAttribute("taskVO", taskVO);
        return "back-end/task/update_task_input";
    }

    /* ======== 修改處理 ======== */
    @PostMapping("update")
    public String update(@Valid TaskVO taskVO, BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/task/update_task_input";
        }

        taskSvc.updateTask(taskVO);

        model.addAttribute("success", "- (修改成功)");
        taskVO = taskSvc.getOneTask(taskVO.getTaskId());
        model.addAttribute("taskVO", taskVO);
        return "back-end/task/listOneTask";
    }

    /* ======== 刪除 ======== */
    @PostMapping("delete")
    public String delete(@RequestParam("taskId") Integer taskId, ModelMap model) {
        taskSvc.deleteTask(taskId);
        model.addAttribute("success", "- (刪除成功)");
        return "back-end/task/listAllTask";
    }

    /* ======== 下拉/列表資料供 View 使用 ======== */

    /** listAllTask.html / select_page.html 會用到的清單 */
    @ModelAttribute("taskListData")
    protected List<TaskVO> taskListData() {
        return taskSvc.getAll();
    }

    /** 任務類型下拉（如果你在表單用了 taskTypeListData） */
    
    @ModelAttribute("taskTypeListData")
    protected List<TaskTypeVO> taskTypeListData() {
        return taskTypeSvc.getAll();
    }
    

    /* ======== 共用：移除某欄位的驗證錯誤（保留你原本用法） ======== */
    public BindingResult removeFieldError(TaskVO taskVO, BindingResult result, String removedFieldname) {
        List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
                .filter(fieldname -> !fieldname.getField().equals(removedFieldname))
                .collect(Collectors.toList());
        result = new BeanPropertyBindingResult(taskVO, "taskVO");
        for (FieldError fieldError : errorsListToKeep) {
            result.addError(fieldError);
        }
        return result;
    }
}
