package com.tibafit.controller.workoutplan;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.dto.workoutplan.WorkoutPlanRequestDTO;
import com.tibafit.dto.workoutplan.WorkoutPlanResponseDTO;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.workoutplan.workoutPlanService_interface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/workoutPlan/api")
public class WorkoutPlanController {

    @Autowired
    private workoutPlanService_interface workoutPlanSvc;
    
    @Autowired
    private UserRepository userRepo;

    // 批次新增
    @PostMapping("/insertMultiple")
    public ApiResponseDTO<Void> insertMultiple(@Valid @RequestBody InsertMultipleRequest req) {
        workoutPlanSvc.insertWorkoutPlanMultiple(req.getList());
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    // 批次更新
    @PostMapping("/updateMultiple")
    public ApiResponseDTO<Void> updateMultiple(@Valid @RequestBody UpdateMultipleRequest req) {
        workoutPlanSvc.updateWorkoutPlanMultiple(req.getList());
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    // 批次更新資料狀態
    @PostMapping("/updateMultipleWorkoutPlanDataStatus")
    public ApiResponseDTO<Integer> updateStatus(@Valid @RequestBody UpdateStatusRequest req) {
        Integer result = workoutPlanSvc.updateWorkoutPlanDataStatusByIds(
                req.getDataStatus(), req.getWorkoutPlanIds());
        return ApiResponseDTO.success(result);
    }

    // 依日期區間查詢
    @PostMapping("/getMultipleByUserIdAndDateRange")
    public ApiResponseDTO<List<WorkoutPlanResponseDTO>> getMultipleByUserIdAndDateRange(
            @Valid @RequestBody UserIdAndDateRangeRequest req) {
        List<WorkoutPlanResponseDTO> result = workoutPlanSvc.getWorkoutPlanByDateRange(
                req.getUserId(),
                LocalDate.parse(req.getStartDate()),
                LocalDate.parse(req.getEndDate()),
                req.getDataStatuses()
        );
        return ApiResponseDTO.success(result);
    }

    // 依日期查詢
    @PostMapping("/getMultipleByUserIdAndWorkoutPlanDate")
    public ApiResponseDTO<List<WorkoutPlanResponseDTO>> getMultipleByUserIdAndWorkoutPlanDate(
            @Valid @RequestBody UserIdAndWorkoutPlanDateRequest req) {
    	
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                currentUser = userRepo.findByEmail(email).orElse(null);
            } else if (principal instanceof String str && !"anonymousUser".equals(str)) {
                currentUser = userRepo.findByEmail(str).orElse(null);
            }
        }


        boolean isLoggedIn = currentUser != null;
        System.out.println("currentUser: " + currentUser);
    	
        List<WorkoutPlanResponseDTO> result = workoutPlanSvc.getWorkoutPlanByDate(
                req.getUserId(),
                LocalDate.parse(req.getWorkoutPlanDate()),
                req.getDataStatuses()
        );
        return ApiResponseDTO.success(result);
    }

    
    
    // ------ DTOs ------

    public static class InsertMultipleRequest {
        private List<WorkoutPlanRequestDTO> list;

        @NotEmpty(message = "新增計畫列表: 不可為空")
        @Valid
        public List<WorkoutPlanRequestDTO> getList() {
            return list;
        }
        public void setList(List<WorkoutPlanRequestDTO> list) {
            this.list = list;
        }
    }

    public static class UpdateMultipleRequest {
        private List<WorkoutPlanRequestDTO> list;

        @NotEmpty(message = "list 不可為空")
        @Valid
        public List<WorkoutPlanRequestDTO> getList() {
            return list;
        }
        public void setList(List<WorkoutPlanRequestDTO> list) {
            this.list = list;
        }
    }

    public static class UpdateStatusRequest {
        private Integer dataStatus;
        private List<Integer> workoutPlanIds;

        @NotNull(message = "計畫資料狀態: 不可為空")
        @Min(value = 0, message = "計畫資料狀態: 只能是 0,1")
        @Max(value = 1, message = "計畫資料狀態: 只能是 0,1")
        public Integer getDataStatus() {
            return dataStatus;
        }
        public void setDataStatus(Integer dataStatus) {
            this.dataStatus = dataStatus;
        }

        
        @NotEmpty(message = "workoutPlanIds 不可為空")
        public List<Integer> getWorkoutPlanIds() {
            return workoutPlanIds;
        }
        public void setWorkoutPlanIds(List<Integer> workoutPlanIds) {
            this.workoutPlanIds = workoutPlanIds;
        }
    }

    public static class UserIdAndDateRangeRequest {
        private Integer userId;
        private String startDate;
        private String endDate;
        private List<Integer> dataStatuses;

        @NotNull(message = "建立人員ID: 不可為空")
        public Integer getUserId() {
            return userId;
        }
        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        
        @NotNull(message = "開始日期: 不可為空")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "startDate 格式必須為 yyyy-MM-dd")
        public String getStartDate() {
            return startDate;
        }
        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        
        @NotNull(message = "結束日期: 不可為空")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "endDate 格式必須為 yyyy-MM-dd")
        public String getEndDate() {
            return endDate;
        }
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        
        @NotEmpty(message = "計畫資料狀態列表: 不可為空")
        public List<
	        @NotNull(message = "計畫資料狀態: 不可為空")
	        @Min(value = 0, message = "計畫資料狀態: 只能是 0,1")
	        @Max(value = 1, message = "計畫資料狀態: 只能是 0,1")
	        Integer> getDataStatuses() {
            return dataStatuses;
        }
        public void setDataStatuses(List<Integer> dataStatuses) {
            this.dataStatuses = dataStatuses;
        }
    }

    public static class UserIdAndWorkoutPlanDateRequest {
        private Integer userId;
        private String workoutPlanDate;
        private List<Integer> dataStatuses;

        @NotNull(message = "建立人員ID: 不可為空")
        public Integer getUserId() {
            return userId;
        }
        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        
        @NotNull(message = "計畫安排日期: 不可為空")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "workoutPlanDate 格式必須為 yyyy-MM-dd")
        public String getWorkoutPlanDate() {
            return workoutPlanDate;
        }
        public void setWorkoutPlanDate(String workoutPlanDate) {
            this.workoutPlanDate = workoutPlanDate;
        }

        
        @NotEmpty(message = "計畫資料狀態列表: 不可為空")
        public List<
	        @NotNull(message = "計畫資料狀態: 不可為空")
	        @Min(value = 0, message = "計畫資料狀態: 只能是 0,1")
	        @Max(value = 1, message = "計畫資料狀態: 只能是 0,1")
	        Integer> getDataStatuses() {
            return dataStatuses;
        }
        public void setDataStatuses(List<Integer> dataStatuses) {
            this.dataStatuses = dataStatuses;
        }
    }
}
