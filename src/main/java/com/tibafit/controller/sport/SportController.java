package com.tibafit.controller.sport;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.dto.sport.SportRequestDTO;
import com.tibafit.dto.sport.SportResponseDTO;
import com.tibafit.model.sport.SportConverter;
import com.tibafit.model.sport.SportDataStatus;
import com.tibafit.model.sport.SportVO;
import com.tibafit.service.sport.SportService_Interface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/sport/api")
public class SportController {

    @Autowired
    SportService_Interface sportSvc;

    // 檢查名稱是否存在
    @PostMapping("/isExistSportName")
    public ApiResponseDTO<Boolean> isExistSportName(@Valid @RequestBody SportNameRequest req) {
        Boolean result = sportSvc.isExistBySportName(req.getSportName());
        return ApiResponseDTO.success(result);
    }

    // 查多筆 (狀態)
    @PostMapping("/getMultipleByDataStatuses")
    public ApiResponseDTO<List<SportResponseDTO>> getSportByDataStatuses() {
    	List<Integer> statuses = new ArrayList<>();
    	statuses.add(SportDataStatus.ONLINE.getCodeNum());
    	statuses.add(SportDataStatus.OFFLINE.getCodeNum());
    	
        List<SportVO> rawList = sportSvc.getSportByDataStatuses(statuses);
        List<SportResponseDTO> result = SportConverter.toDtoList(rawList);
        return ApiResponseDTO.success(result);
    }
    
    // 查全部
    @PostMapping("/getMultipleAll")
    public ApiResponseDTO<List<SportResponseDTO>> getMultipleAll() {
        List<SportVO> rawList = sportSvc.getSportAll();
        List<SportResponseDTO> result = SportConverter.toDtoList(rawList);
        return ApiResponseDTO.success(result);
    }

    // 批次新增
    @PostMapping("/insertMultiple")
    public ApiResponseDTO<Void> sportInsertMultiple(@Valid @RequestBody InsertMultipleRequest req) {
        sportSvc.insertSportMultiple(req.getSports());
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    // 單筆更新
    @PostMapping("/updateSingle")
    public ApiResponseDTO<Void> sportUpdateSingle(@Valid @RequestBody SportRequestDTO dto) {
        sportSvc.updateSport(dto);
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    // 批次更新狀態
    @PostMapping("/updateMultipleSportDataStatus")
    public ApiResponseDTO<Void> updateMultipleSportDataStatus(@Valid @RequestBody UpdateMultipleStatusRequest req) {
        sportSvc.updateSportDataStatusByIds(
            req.getSportDataStatus(),
            req.getSportIds()
        );
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    // 複合條件查詢
    @PostMapping("/getByComplexCondition")
    public ApiResponseDTO<List<SportResponseDTO>> getByComplexCondition(
            @Valid @RequestBody ComplexConditionRequest req) {
        List<SportVO> voList = sportSvc.getByComplexCondition(
            req.getSportNameDescFuzzy(),
            req.getSportLevel(),
            req.getCreateStartDate(),
            req.getCreateEndDate(),
            req.getUpdateStartDate(),
            req.getUpdateEndDate(),
            req.getStatuses()
        );

        List<SportResponseDTO> result = SportConverter.toDtoList(voList);
        return ApiResponseDTO.success(result);
    }

    
    // ------ DTOs -------

    public static class SportNameRequest {
        private String sportName;

        @NotBlank(message = "運動名稱: 不可為空")
        @Size(max = 50, message = "運動名稱: 長度不可超過 50")
        public String getSportName() {
            return sportName;
        }
        public void setSportName(String sportName) {
            this.sportName = sportName;
        }
    }

    public static class InsertMultipleRequest {
        private List<SportRequestDTO> sports;

        @NotEmpty(message = "新增運動列表: 不可為空")
        @Valid
        public List<SportRequestDTO> getSports() {
            return sports;
        }
        public void setSports(List<SportRequestDTO> sports) {
            this.sports = sports;
        }
    }

    public static class UpdateMultipleStatusRequest {
        private Integer sportDataStatus;
        private List<Integer> sportIds;

        @NotNull(message = "運動資料狀態: 不可為空")
        @Min(value = 0, message = "運動資料狀態: 只能是 0,1,2")
        @Max(value = 2, message = "運動資料狀態: 只能是 0,1,2")
        public Integer getSportDataStatus() {
            return sportDataStatus;
        }
        public void setSportDataStatus(Integer sportDataStatus) {
            this.sportDataStatus = sportDataStatus;
        }

        
        @NotEmpty(message = "運動IDs: 不可為空")
        @Valid
        public List<@NotNull(message = "運動ID: 不可為空") Integer> getSportIds() {
            return sportIds;
        }
        public void setSportIds(List<Integer> sportIds) {
            this.sportIds = sportIds;
        }
    }

    public static class ComplexConditionRequest {
        private String sportNameDescFuzzy;
        private String sportLevel;
        private String createStartDate;
        private String createEndDate;
        private String updateStartDate;
        private String updateEndDate;
        private List<Integer> statuses;

        @Size(max = 50, message = "運動名稱/描述: 長度不可超過 50")
        public String getSportNameDescFuzzy() {
            return sportNameDescFuzzy;
        }
        public void setSportNameDescFuzzy(String sportNameDescFuzzy) {
            this.sportNameDescFuzzy = sportNameDescFuzzy;
        }

        
        public String getSportLevel() {
            return sportLevel;
        }
        public void setSportLevel(String sportLevel) {
            this.sportLevel = sportLevel;
        }

        
        public String getCreateStartDate() {
            return createStartDate;
        }
        public void setCreateStartDate(String createStartDate) {
            this.createStartDate = createStartDate;
        }

        
        public String getCreateEndDate() {
            return createEndDate;
        }
        public void setCreateEndDate(String createEndDate) {
            this.createEndDate = createEndDate;
        }

        
        public String getUpdateStartDate() {
            return updateStartDate;
        }
        public void setUpdateStartDate(String updateStartDate) {
            this.updateStartDate = updateStartDate;
        }

        
        public String getUpdateEndDate() {
            return updateEndDate;
        }
        public void setUpdateEndDate(String updateEndDate) {
            this.updateEndDate = updateEndDate;
        }

        
        @NotEmpty(message = "運動資料狀態列表: 不可為空")
        @Valid
        public List<
	        @NotNull(message = "運動資料狀態: 不可為空")
	        @Min(value = 0, message = "運動資料狀態: 只能是 0,1,2")
	        @Max(value = 2, message = "運動資料狀態: 只能是 0,1,2") 
	        Integer> getStatuses() {
            return statuses;
        }
        public void setStatuses(List<Integer> statuses) {
            this.statuses = statuses;
        }
    }
}
