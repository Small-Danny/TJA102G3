package com.tibafit.controller.sporttypeitem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemRequestDTO;
import com.tibafit.service.sporttypeitem.SportTypeItemService_interface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/sportTypeItem/api")
public class SportTypeItemController {

    @Autowired
    private SportTypeItemService_interface sportTypeItemSvc;

    // 批次新增
    @PostMapping("/insertMultiple")
    public ApiResponseDTO<Void> insertSportTypeItems(@Valid @RequestBody InsertMultipleRequest req) {
        sportTypeItemSvc.insertSportTypeItems(req.getDtos());
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    // 檢查是否存在
    @PostMapping("/isExitBySportTypeIdAndSportId")
    public ApiResponseDTO<Boolean> isExitBySportTypeIdAndSportId(@Valid @RequestBody SportTypeIdAndSportIdRequest req) {
        Boolean result = sportTypeItemSvc.isExitBySportTypeIdAndSportId(
        		req.getSportTypeId(), 
        		req.getSportId()
        );
        return ApiResponseDTO.success(result);
    }

    // 批次刪除
    @PostMapping("/deleteBySportTypeItemIds")
    public ApiResponseDTO<Void> deleteBySportTypeItemIds(@Valid @RequestBody DeleteBySportTypeItemIdsRequest req) {
        sportTypeItemSvc.deleteBySportTypeItemIds(req.getSportTypeItemIds());
        Void result = null;
        return ApiResponseDTO.success(result);
    }

    
    
    // ------ DTOs ------

    public static class InsertMultipleRequest {
        private List<SportTypeItemRequestDTO> dtos;

        @NotEmpty(message = "新增運動分類明細列表: 不可為空")
        @Valid
        public List<SportTypeItemRequestDTO> getDtos() {
            return dtos;
        }
        public void setDtos(List<SportTypeItemRequestDTO> dtos) {
            this.dtos = dtos;
        }
    }

    public static class SportTypeIdAndSportIdRequest {
        private Integer sportTypeId;
        private Integer sportId;

        @NotNull(message = "運動分類ID: 不可為空")
        public Integer getSportTypeId() {
            return sportTypeId;
        }
        public void setSportTypeId(Integer sportTypeId) {
            this.sportTypeId = sportTypeId;
        }

        
        @NotNull(message = "運動ID: 不可為空")
        public Integer getSportId() {
            return sportId;
        }
        public void setSportId(Integer sportId) {
            this.sportId = sportId;
        }
    }

    public static class DeleteBySportTypeItemIdsRequest {
        private List<Integer> sportTypeItemIds;

        @NotEmpty(message = "運動分類明細IDs: 不可為空")
        public List<Integer> getSportTypeItemIds() {
            return sportTypeItemIds;
        }
        public void setSportTypeItemIds(List<Integer> sportTypeItemIds) {
            this.sportTypeItemIds = sportTypeItemIds;
        }
    }
}
