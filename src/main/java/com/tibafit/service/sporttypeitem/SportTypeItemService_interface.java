package com.tibafit.service.sporttypeitem;

import java.util.List;
import java.util.Optional;

import com.tibafit.dto.sporttypeitem.SportTypeItemRequestDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemResponseDTO;
import com.tibafit.model.sporttypeitem.SportTypeItemVO;

public interface SportTypeItemService_interface {

    // 新增
	public void insertSportTypeItems(List<SportTypeItemRequestDTO> dtos);
    
    // 檢查是否存在 (sportTypeId + sportId)
    public Boolean isExitBySportTypeIdAndSportId(Integer sportTypeId, Integer sportId);

    // 查單筆
    public SportTypeItemResponseDTO getBySportTypeItemId(Integer sportTypeItemId);

    // 查多筆 (sportTypeId)
    public List<SportTypeItemResponseDTO> getBySportTypeId(Integer sportTypeId);

    // 查單筆 (sportTypeId + sportId)
    public SportTypeItemResponseDTO getBySportTypeIdAndSportId(Integer sportTypeId, Integer sportId);
    
    
    public void deleteBySportTypeItemIds(List<Integer> sportTypeRecordIds);
}