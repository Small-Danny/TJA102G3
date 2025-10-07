package com.tibafit.service.customsport;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tibafit.dto.customsport.CustomSportRequestDTO;
import com.tibafit.dto.customsport.CustomSportResponseDTO;
import com.tibafit.model.customsport.CustomSportVO;
import com.tibafit.model.sport.SportVO;

public interface CustomSportService_interface {
	public List<CustomSportVO> getSportAll();
	
	public List<CustomSportVO> getSportByDataStatuses(List<Integer> sportDataStatuses);
	
	public CustomSportVO getSportByPrimaryKey(Integer sportIds);
	
	
	public void insertSportMultiple(List<CustomSportRequestDTO> dtos);
	

	public void updateSportMultiple(List<CustomSportRequestDTO> dtos);
	
	public Integer updateSportDataStatusByIds(Integer targetStatus, List<Integer> sportIds);
}
