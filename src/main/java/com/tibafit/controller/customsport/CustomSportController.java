package com.tibafit.controller.customsport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.customsport.CustomSportRequestDTO;
import com.tibafit.dto.customsport.CustomSportResponseDTO;
import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.model.customsport.CustomSportConverter;
import com.tibafit.model.customsport.CustomSportVO;
import com.tibafit.service.customsport.CustomSportService_interface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/customSport/api")
public class CustomSportController {

    @Autowired
    private CustomSportService_interface svc;

    // 查多筆
    @PostMapping("/getMultipleBySportDataStatuses")
    public ApiResponseDTO<List<CustomSportResponseDTO>> getMultipleBySportDataStatuses(
            @Valid @RequestBody GetMultipleByStatusesRequest req) {
        List<Integer> statuses = req.getStatuses();

        List<CustomSportVO> rawList = svc.getSportByDataStatuses(statuses);
        List<CustomSportResponseDTO> result = CustomSportConverter.toDtoList(rawList);

        return ApiResponseDTO.success(result);
    }

    
    // 批次新增
    @PostMapping("/insertMultiple")
    public ApiResponseDTO<Void> sportInsertMultiple(@Valid @RequestBody InsertMultipleRequest req) {
        svc.insertSportMultiple(req.getSports());
        Void result = null;
        return ApiResponseDTO.success(result);
    }


    // 批次更新
    @PostMapping("/updateMultiple")
    public ApiResponseDTO<Void> updateMultiple(@Valid @RequestBody UpdateMultipleRequest req) {
        svc.updateSportMultiple(req.getSports());
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    
    // 批次更新狀態
    @PostMapping("/updateMultipleSportDataStatus")
    public ApiResponseDTO<Void> updateMultipleSportDataStatus(@Valid @RequestBody UpdateMultipleStatusRequest req) {
        svc.updateSportDataStatusByIds(
        		req.getSportDataStatus(), 
        		req.getSportIds()
        );
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    
    
    // ------ DTOs ------

    public static class GetMultipleByStatusesRequest {
        private List<Integer> statuses;

        @NotEmpty(message = "自訂義運動資料狀態列表: 不可為空")
        public List<	        
	        @NotNull(message = "自訂義運動資料狀態: 不可為空")
	        @Min(value = 0, message = "自訂義運動資料狀態: 只能是 0,1")
	        @Max(value = 1, message = "自訂義運動資料狀態: 只能是 0,1") 
	        Integer> getStatuses() {
            return statuses;
        }
        public void setStatuses(List<Integer> statuses) {
            this.statuses = statuses;
        }
    }

    public static class InsertMultipleRequest {
        private List<CustomSportRequestDTO> sports;

        @NotEmpty(message = "新增自訂義運動列表: 不可為空")
        @Valid
        public List<CustomSportRequestDTO> getSports() {
            return sports;
        }
        public void setSports(List<CustomSportRequestDTO> sports) {
            this.sports = sports;
        }
    }

    public static class UpdateMultipleRequest {
        private List<CustomSportRequestDTO> sports;

        @NotEmpty(message = "更新自訂義運動列表: 不可為空")
        @Valid
        public List<CustomSportRequestDTO> getSports() {
            return sports;
        }
        public void setSports(List<CustomSportRequestDTO> sports) {
            this.sports = sports;
        }
    }

    public static class UpdateMultipleStatusRequest {
        private Integer sportDataStatus;
        private List<Integer> sportIds;

        @NotNull(message = "自訂義運動資料狀態: 不可為空")
        @Min(value = 0, message = "自訂義運動資料狀態: 只能是 0,1")
        @Max(value = 1, message = "自訂義運動資料狀態: 只能是 0,1") 
        public Integer getSportDataStatus() {
            return sportDataStatus;
        }
        public void setSportDataStatus(Integer sportDataStatus) {
            this.sportDataStatus = sportDataStatus;
        }

        
        @NotEmpty(message = "自訂義運動IDs: 不可為空")
        @Valid
        public List<@NotNull(message = "自訂義運動ID: 不可為空") Integer> getSportIds() {
            return sportIds;
        }
        public void setSportIds(List<Integer> sportIds) {
            this.sportIds = sportIds;
        }
    }
}
