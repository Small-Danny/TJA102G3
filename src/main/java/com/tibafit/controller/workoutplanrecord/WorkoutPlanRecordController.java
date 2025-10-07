package com.tibafit.controller.workoutplanrecord;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordRequestDTO;
import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordResponseDTO;
import com.tibafit.service.workoutplanrecord.workoutPlanRecordService_interface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/workoutPlanRecord/api")
public class WorkoutPlanRecordController {

    @Autowired
    private workoutPlanRecordService_interface workoutPlanRecordSvc;

    // 批次新增
    @PostMapping("/insertMultiple")
    public ApiResponseDTO<List<WorkoutPlanRecordResponseDTO>> insertMultiple(
            @Valid @RequestBody InsertMultipleRequest req) {
        List<WorkoutPlanRecordResponseDTO> result =
                workoutPlanRecordSvc.insertWorkoutPlanRecordMultiple(req.getRecords());
        return ApiResponseDTO.success(result);
    }

    // 批次更新紀錄狀態
    @PostMapping("/updateWorkoutPlanRecordDataStatusByRecordIds")
    public ApiResponseDTO<Integer> updateWorkoutPlanRecordDataStatusByRecordIds(
            @Valid @RequestBody UpdateStatusRequest req) {
        Integer result = workoutPlanRecordSvc.updateWorkoutPlanRecordDataStatusByRecordIds(
                req.getDataStatus(), req.getWorkoutPlanRecordIds());
        return ApiResponseDTO.success(result);
    }

    
    
    // ------ DTOs ------

    public static class InsertMultipleRequest {
        private List<WorkoutPlanRecordRequestDTO> records;

        @NotEmpty(message = "新增紀錄列表: 不可為空")
        @Valid
        public List<WorkoutPlanRecordRequestDTO> getRecords() {
            return records;
        }
        public void setRecords(List<WorkoutPlanRecordRequestDTO> records) {
            this.records = records;
        }
    }

    public static class UpdateStatusRequest {
        private Integer dataStatus;
        private List<Integer> workoutPlanRecordIds;

        @NotNull(message = "紀錄資料狀態: 不可為空")
        @Min(value = 0, message = "紀錄資料狀態: 只能是 0,1")
        @Max(value = 1, message = "紀錄資料狀態: 只能是 0,1")
        public Integer getDataStatus() {
            return dataStatus;
        }
        public void setDataStatus(Integer dataStatus) {
            this.dataStatus = dataStatus;
        }

        
        @NotEmpty(message = "workoutPlanRecordIds 不可為空")
        public List<Integer> getWorkoutPlanRecordIds() {
            return workoutPlanRecordIds;
        }
        public void setWorkoutPlanRecordIds(List<Integer> workoutPlanRecordIds) {
            this.workoutPlanRecordIds = workoutPlanRecordIds;
        }
    }
}
