package com.tibafit.model.sporttypeitem;

import java.util.ArrayList;
import java.util.List;

import com.tibafit.dto.sport.SportResponseDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemRequestDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemResponseDTO;
import com.tibafit.model.sport.SportConverter;

public class SportTypeItemConverter {

    public static SportTypeItemVO toNewVO(SportTypeItemRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        SportTypeItemVO vo = new SportTypeItemVO();
        
        vo.setSportTypeId(dto.getSportTypeId());
        vo.setSportId(dto.getSportId());
        
        return vo;
    }

    public static SportTypeItemResponseDTO toDTO(SportTypeItemVO vo) {
        if (vo == null) {
            return null;
        }
        
        SportTypeItemResponseDTO dto = new SportTypeItemResponseDTO();
        
        dto.setSportTypeItemId(vo.getSportTypeItemId());
        dto.setSportTypeId(vo.getSportTypeId());
        dto.setSportId(vo.getSportId());
        
        
        dto.setCreateDatetime(vo.getCreateDatetime());
        dto.setUpdateDatetime(vo.getUpdateDatetime());
        
    	SportResponseDTO sportResponseDTO = SportConverter.toDTO(vo.getSportVO());
    	dto.setSportResponseDTO(sportResponseDTO);
    	
        return dto;
    }
    
    
    public static List<SportTypeItemVO> toNewVoList(List<SportTypeItemRequestDTO> dtos) {
        List<SportTypeItemVO> list = new ArrayList<>();
        
        if (dtos == null || dtos.isEmpty()) {
            return list;
        }
        
        for (SportTypeItemRequestDTO dto : dtos) {
            SportTypeItemVO vo = toNewVO(dto);
            if (vo != null) {
                list.add(vo);
            }
        }
        
        return list;
    }
    

    public static List<SportTypeItemResponseDTO> toDtoList(List<SportTypeItemVO> vos) {
        List<SportTypeItemResponseDTO> list = new ArrayList<>();
        if (vos == null || vos.isEmpty()) {
            return list;
        }
        for (SportTypeItemVO vo : vos) {
            list.add(toDTO(vo));
        }
        return list;
    }
}
