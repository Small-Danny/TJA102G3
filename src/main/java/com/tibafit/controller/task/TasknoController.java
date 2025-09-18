package com.tibafit.controller.task;

import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tibafit.model.task.TaskService;
import com.tibafit.model.task.TaskVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/tasks")
public class TasknoController {

    @Autowired
    TaskService taskSvc;

    /*
     * select_page.html 送出查單筆時的處理
     */
    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            // 1) 參數與驗證：empno → taskId
            @NotEmpty(message = "任務ID: 請勿空白")
            @Digits(integer = 10, fraction = 0, message = "任務ID: 請填數字")
            @Min(value = 1, message = "任務ID: 需 ≥ {value}")
            @RequestParam("taskId") String taskId,
            ModelMap model) {

        // 2) 查詢資料：Emp → Task
        TaskVO taskVO = taskSvc.getOneTask(Integer.valueOf(taskId));

        // 下拉清單資料：empListData → taskListData
        List<TaskVO> list = taskSvc.getAll();
        model.addAttribute("taskListData", list);

        if (taskVO == null) {
            model.addAttribute("errorMessage", "查無資料");
            // 視圖路徑：/back-end/emp/... → /back-end/task/...
            return "back-end/task/select_page";
        }

        // 3) 成功：放入單筆 taskVO，回 select_page 由 fragment 顯示
        model.addAttribute("taskVO", taskVO); // 給 listOneTask.html 片段/或 select_page 右側區塊使用
        return "back-end/task/select_page";
    }

    // 驗證失敗的例外處理（沿用你原本的寫法，物件/路徑改為 Task 版）
    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> v : violations) {
            sb.append(v.getMessage()).append("<br>");
        }

        // 供下拉使用
        List<TaskVO> list = taskSvc.getAll();
        model.addAttribute("taskListData", list);

        return new ModelAndView("back-end/task/select_page",
                "errorMessage", "請修正以下錯誤:<br>" + sb);
    }
}
