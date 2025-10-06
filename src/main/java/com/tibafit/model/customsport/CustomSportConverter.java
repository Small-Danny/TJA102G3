package com.tibafit.model.customsport;

import java.util.ArrayList;
import java.util.List;

import com.tibafit.dto.customsport.CustomSportRequestDTO;
import com.tibafit.dto.customsport.CustomSportResponseDTO;

public class CustomSportConverter {

	public static CustomSportVO toNewVO(CustomSportRequestDTO dto) {
		if (dto == null) {
			return null;
		}

		CustomSportVO vo = new CustomSportVO();

		vo.setCustomSportId(dto.getCustomSportId());
		vo.setSportName(dto.getSportName());
		vo.setSportDescription(dto.getSportDescription());
		vo.setSportEstimatedCalories(dto.getSportEstimatedCalories());
		vo.setSportPic(dto.getSportPic());
		vo.setSportDataStatus(dto.getSportDataStatus());
		vo.setUserId(dto.getUserId());

		return vo;
	}
	
	public static CustomSportVO toUpdateVO(CustomSportVO oriVo, CustomSportRequestDTO dto) {
	    if (oriVo == null || dto == null) {
	        return null;
	    }

	    // copy oriVo
	    CustomSportVO vo = new CustomSportVO();
	    vo.setCustomSportId(oriVo.getCustomSportId());
	    vo.setSportName(oriVo.getSportName());
	    vo.setSportDescription(oriVo.getSportDescription());
	    vo.setSportEstimatedCalories(oriVo.getSportEstimatedCalories());
	    vo.setSportPic(oriVo.getSportPic());
	    vo.setSportDataStatus(oriVo.getSportDataStatus());
	    vo.setUserId(oriVo.getUserId());
	    vo.setCreateDatetime(oriVo.getCreateDatetime());
	    vo.setUpdateDatetime(oriVo.getUpdateDatetime());

	    // put dto 
	    if (dto.getSportName() != null) {
	        vo.setSportName(dto.getSportName());
	    }
	    if (dto.getSportDescription() != null) {
	        vo.setSportDescription(dto.getSportDescription());
	    }
	    if (dto.getSportEstimatedCalories() != null) {
	        vo.setSportEstimatedCalories(dto.getSportEstimatedCalories());
	    }
	    if (dto.getSportPic() != null) {
	        vo.setSportPic(dto.getSportPic());
	    }
	    
	    // customSportDataStatus do not update
	    
	    if (dto.getUserId() != null) {
	        vo.setUserId(dto.getUserId());
	    }

	    return vo;
	}
	

	public static List<CustomSportVO> toNewVoList(List<CustomSportRequestDTO> dtoList) {
		List<CustomSportVO> voList = new ArrayList<>();

		if (dtoList == null || dtoList.isEmpty()) {
			return voList;
		}

		for (CustomSportRequestDTO dto : dtoList) {
			voList.add(toNewVO(dto));
		}

		return voList;
	}
	
	
	public static List<CustomSportVO> toUpdateVoList(List<CustomSportVO> oriVoList, List<CustomSportRequestDTO> dtoList) {
	    List<CustomSportVO> voList = new ArrayList<>();

	    if (dtoList == null || dtoList.isEmpty() || oriVoList == null || oriVoList.isEmpty()) {
	        return voList;
	    }

	    for (CustomSportRequestDTO dto : dtoList) {
	        if (dto == null || dto.getCustomSportId() == null) {
	            continue;
	        }

	        // find oriVo
	        CustomSportVO oriVo = null;
	        for (CustomSportVO temp : oriVoList) {
	            if (dto.getCustomSportId().equals(temp.getCustomSportId())) {
	                oriVo = temp;
	                break;
	            }
	        }

	        if (oriVo != null) {
	            voList.add(toUpdateVO(oriVo, dto));
	        }
	    }

	    return voList;
	}
	

	public static CustomSportResponseDTO toDTO(CustomSportVO vo) {
		if (vo == null) {
			return null;
		}

		CustomSportResponseDTO dto = new CustomSportResponseDTO();

		dto.setCustomSportId(vo.getCustomSportId());
		dto.setSportName(vo.getSportName());
		dto.setSportDescription(vo.getSportDescription());
		dto.setSportEstimatedCalories(vo.getSportEstimatedCalories());
		dto.setSportPic(vo.getSportPic());
		dto.setSportDataStatus(vo.getSportDataStatus());
		dto.setSportDataStatusText(CustomSportDataStatus.getDisplayNameByCodeNum(vo.getSportDataStatus()));
		dto.setCreateDatetime(vo.getCreateDatetime());
		dto.setUpdateDatetime(vo.getUpdateDatetime());
		dto.setUserId(vo.getUserId());
		// TODO: 根據實際需求補值
		dto.setUserIdText("待補");

		return dto;
	}

	public static List<CustomSportResponseDTO> toDtoList(List<CustomSportVO> voList) {
		List<CustomSportResponseDTO> dtoList = new ArrayList<>();

		if (voList == null || voList.isEmpty()) {
			return dtoList;
		}

		for (CustomSportVO vo : voList) {
			dtoList.add(toDTO(vo));
		}

		return dtoList;
	}
}
