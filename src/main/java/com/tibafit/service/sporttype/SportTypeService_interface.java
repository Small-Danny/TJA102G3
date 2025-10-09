package com.tibafit.service.sporttype;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.tibafit.dto.sporttype.SportTypeRequestDTO;
import com.tibafit.dto.sporttype.SportTypeResponseDTO;
import com.tibafit.dto.sporttype.SportTypeResponseExtraSportsDTO;
import com.tibafit.model.sporttype.SportTypeVO;
import com.tibafit.model.sporttypeitem.SportTypeItemVO;

public interface SportTypeService_interface {

    // 新增
	public void insertSportTypes(List<SportTypeRequestDTO> dtos);

    // 更新
    public void updateSportTypes(List<SportTypeRequestDTO> dtos);
    
    // 檢查名稱是否存在
    public Boolean isExistBySportTypeName(String sportTypeName);

    // 查單筆
    public SportTypeResponseDTO getBySportTypeId(Integer sportTypeId);
    
    // 查多筆
    public List<SportTypeResponseDTO> getBySportTypeIds(List<Integer> sportTypeIds);
    
    // 查單筆分類 + 分類下運動 (狀態)
    public SportTypeResponseExtraSportsDTO getBySportTypeId_SportDataStatuses(Integer sportTypeId, List<Integer> sportDataStatuses);
    
    // 查多筆分類 (分類) + 分類下運動 (狀態)
    public List<SportTypeResponseExtraSportsDTO> getBySportTypeDataStatuses_SportDataStatuses(List<Integer> sportTypeDataStatuses, List<Integer> sportDataStatuses);


    // 查詢狀態範圍內的單筆
    public SportTypeResponseDTO getBySportTypeIdAndSportTypeDataStatuses(Integer sportTypeId, List<Integer> statuses);

    // 查詢狀態範圍內的多筆
    public List<SportTypeResponseDTO> getBySportTypeIdsAndSportTypeDataStatuses(List<Integer> sportTypeIds, List<Integer> statuses);

    // 查詢所有符合狀態的
    public List<SportTypeResponseDTO> getBySportTypeDataStatuses(List<Integer> statuses);
    
    // 複合查詢 + 狀態
    public List<SportTypeResponseDTO> getByComplexCondition(
	        String sportTypeNameFuzzy,
	        String createStartDate, 
	        String createEndDate, 
	        String updateStartDate, 
	        String updateEndDate, 
	        List<Integer> statuses);

    // 批次更新狀態
    public Integer updateSportTypeDataStatusBySportTypeIds(Integer dataStatus, List<Integer> sportTypeIds);
}
