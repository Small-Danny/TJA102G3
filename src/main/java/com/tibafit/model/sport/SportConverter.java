package com.tibafit.model.sport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.tibafit.dto.sport.SportRequestDTO;
import com.tibafit.dto.sport.SportResponseDTO;

import com.tibafit.model.sport.SportDataStatus;
import com.tibafit.model.sport.SportDataStatus;

public class SportConverter {

    public static SportResponseDTO toDTO(SportVO vo) {
        if (vo == null) {
        	return null;
        }

        SportResponseDTO dto = new SportResponseDTO();
        
        dto.setSportId(vo.getSportId());
        dto.setSportName(vo.getSportName());
        dto.setSportDescription(vo.getSportDescription());
        dto.setSportMets(vo.getSportMets());
        dto.setSportEstimatedCalories(vo.getSportEstimatedCalories());
        dto.setSportLevel(vo.getSportLevel());
        dto.setSportLevelText(SportLevel.getDisplayNameByCodeName(vo.getSportLevel()));
        dto.setSportPic(vo.getSportPic());
        dto.setSportDataStatus(vo.getSportDataStatus());
        dto.setSportDataStatusText(SportDataStatus.getDisplayNameByCodeNum(vo.getSportDataStatus()));
        dto.setCreateDatetime(vo.getCreateDatetime());
        dto.setUpdateDatetime(vo.getUpdateDatetime());
        dto.setAdminId(vo.getAdminId());
        // TODO: 根據實際需求補值
        dto.setAdminIdText("待補");

        return dto;
    }

   
    public static List<SportResponseDTO> toDtoList(List<SportVO> voList) {
    	List<SportResponseDTO> dtoList = new ArrayList<>();
    	
        if (voList == null || voList.isEmpty()) {
        	return dtoList;
        }

        for (SportVO vo : voList) {
            dtoList.add(toDTO(vo));
        }
        
        return dtoList;
    }
    
    
    public static SportVO toNewVO(SportRequestDTO dto) {
        if (dto == null) {
        	return null;
        }

        SportVO vo = new SportVO();
        String tempDes = dto.getSportDescription() == null? "" : dto.getSportDescription();
        BigDecimal tempMets = BigDecimal.valueOf(dto.getSportMets()).setScale(2, RoundingMode.HALF_UP);
        
        vo.setSportId(dto.getSportId());
        vo.setSportName(dto.getSportName());
        vo.setSportDescription(tempDes);
        vo.setSportMets(tempMets);
        vo.setSportEstimatedCalories(dto.getSportEstimatedCalories());
        vo.setSportLevel(SportLevel.judgeSportLevel(tempMets));
        vo.setSportPic(dto.getSportPic());
        vo.setSportDataStatus(dto.getSportDataStatus());
        vo.setAdminId(dto.getAdminId());

        return vo;
    }
    
    public static SportVO toUpdateVO(SportVO oriVo, SportRequestDTO dto) {
        if (oriVo == null || dto == null) {
            return null;
        }

        // copy oriVo
        SportVO vo = new SportVO();
        
        vo.setSportId(oriVo.getSportId());
        vo.setSportName(oriVo.getSportName());
        vo.setSportDescription(oriVo.getSportDescription());
        vo.setSportMets(oriVo.getSportMets());
        vo.setSportEstimatedCalories(oriVo.getSportEstimatedCalories());
        vo.setSportLevel(oriVo.getSportLevel());
        vo.setSportPic(oriVo.getSportPic());
        vo.setSportDataStatus(oriVo.getSportDataStatus());
        vo.setAdminId(oriVo.getAdminId());
        vo.setCreateDatetime(oriVo.getCreateDatetime());
        vo.setUpdateDatetime(oriVo.getUpdateDatetime());
        

        // put dto
        if (dto.getSportName() != null) {
            vo.setSportName(dto.getSportName());
        }
        if (dto.getSportDescription() != null) {
            vo.setSportDescription(dto.getSportDescription());
        }
        if (dto.getSportMets() != null) {
            BigDecimal tempMets = BigDecimal.valueOf(dto.getSportMets()).setScale(2, RoundingMode.HALF_UP);
            vo.setSportMets(tempMets);
            vo.setSportLevel(SportLevel.judgeSportLevel(tempMets));
        }
        if (dto.getSportEstimatedCalories() != null) {
            vo.setSportEstimatedCalories(dto.getSportEstimatedCalories());
        }
        if (dto.getSportPic() != null) {
            vo.setSportPic(dto.getSportPic());
        }
        
        // sportDataStatus do not update
        
        
        if (dto.getAdminId() != null) {
            vo.setAdminId(dto.getAdminId());
        }

        return vo;
    }
   
    public static List<SportVO> toNewVoList(List<SportRequestDTO> dtoList) {
    	List<SportVO> voList = new ArrayList<>();
    	
        if (dtoList == null || dtoList.isEmpty()) {
        	return voList;
        }

        for (SportRequestDTO dto : dtoList) {
        	voList.add(toNewVO(dto));
        }
        
        return voList;
    }
    
    
    public static List<SportVO> toUpdateVoList(List<SportVO> oriVoList, List<SportRequestDTO> dtoList) {
        List<SportVO> voList = new ArrayList<>();

        if (dtoList == null || dtoList.isEmpty() || oriVoList == null || oriVoList.isEmpty()) {
            return voList;
        }

        for (SportRequestDTO dto : dtoList) {
            if (dto == null || dto.getSportId() == null) {
                continue;
            }

            // find oriVo
            SportVO oriVo = oriVoList.stream()
                    .filter(v -> v.getSportId() != null && v.getSportId().equals(dto.getSportId()))
                    .findFirst()
                    .orElse(null);

            if (oriVo != null) {
                voList.add(toUpdateVO(oriVo, dto));
            }
        }

        return voList;
    }
}
