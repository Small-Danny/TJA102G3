package com.tibafit.model.sporttype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tibafit.dto.sport.SportResponseDTO;
import com.tibafit.dto.sporttype.SportTypeRequestDTO;
import com.tibafit.dto.sporttype.SportTypeResponseDTO;
import com.tibafit.dto.sporttype.SportTypeResponseExtraSportsDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemResponseDTO;
import com.tibafit.model.sporttypeitem.SportTypeItemConverter;
import com.tibafit.model.sporttypeitem.SportTypeItemVO;

public class SportTypeConverter {

    public static SportTypeVO toNewVO(SportTypeRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        SportTypeVO vo = new SportTypeVO();
        
        vo.setSportTypeId(null);
        vo.setSportTypeName(dto.getSportTypeName());
        vo.setSportTypePic(dto.getSportTypePic());
        vo.setSportTypeDataStatus(dto.getSportTypeDataStatus());
        
        return vo;
    }

    public static SportTypeVO toUpdateVO(SportTypeVO ori, SportTypeRequestDTO dto) {
        if (ori == null || dto == null) {
            return null;
        }
        
        SportTypeVO vo = new SportTypeVO();
        
        vo.setSportTypeId(ori.getSportTypeId());
        vo.setSportTypeName(ori.getSportTypeName());
        vo.setSportTypePic(ori.getSportTypePic());
        vo.setSportTypeDataStatus(ori.getSportTypeDataStatus());
        
        
        if (dto.getSportTypeId() != null) {
           vo.setSportTypeId(dto.getSportTypeId());
    	}
        if (dto.getSportTypeName() != null) {
            vo.setSportTypeName(dto.getSportTypeName());
        }
        if (dto.getSportTypePic() != null) {
            vo.setSportTypePic(dto.getSportTypePic());
        }

	    // sportTypeDataStatus do not update
        
        return vo;
    }

    public static SportTypeResponseDTO toDTO(SportTypeVO vo) {
        if (vo == null) {
            return null;
        }
        
        SportTypeResponseDTO dto = new SportTypeResponseDTO();
        
        dto.setSportTypeId(vo.getSportTypeId());
        dto.setSportTypeName(vo.getSportTypeName());
        dto.setSportTypePic(vo.getSportTypePic());
        
        dto.setSportTypeDataStatus(vo.getSportTypeDataStatus());
        dto.setSportTypeDataStatusText(SportTypeDataStatus.getDisplayNameByCodeNum(vo.getSportTypeDataStatus()));
        
        dto.setCreateDatetime(vo.getCreateDatetime());
        dto.setUpdateDatetime(vo.getUpdateDatetime());
        
        // Set轉List
        List<SportTypeItemVO> voList = new ArrayList<>();
        for (SportTypeItemVO setVo : vo.getSportTypeItemVOs()) {
        	voList.add(setVo);
        }
        List<SportTypeItemResponseDTO> sportTypeItemResponseDTOs = SportTypeItemConverter.toDtoList(voList);
    	dto.setSportTypeItemResponseDTOs(sportTypeItemResponseDTOs);
        
        return dto;
    }
    
    public static List<SportTypeVO> toNewVoList(List<SportTypeRequestDTO> dtos) {
        List<SportTypeVO> list = new ArrayList<>();
        
        if (dtos == null || dtos.isEmpty()) {
            return list;
        }
        
        for (SportTypeRequestDTO dto : dtos) {
            SportTypeVO vo = toNewVO(dto);
            if (vo != null) {
                list.add(vo);
            }
        }
        
        return list;
    }
    
    public static List<SportTypeVO> toUpdateVoList(List<SportTypeVO> oriVos, List<SportTypeRequestDTO> dtos) {
        List<SportTypeVO> list = new ArrayList<>();
        
        if (oriVos == null || oriVos.isEmpty() || dtos == null || dtos.isEmpty()) {
            return list;
        }
        
        // build Map
        Map<Integer, SportTypeVO> oriVoMap = new HashMap<>();
        for (SportTypeVO oriVo : oriVos) {
            if (oriVo != null && oriVo.getSportTypeId() != null) {
                oriVoMap.put(oriVo.getSportTypeId(), oriVo);
            }
        }

        // find oriVo
        for (SportTypeRequestDTO dto : dtos) {
            if (dto == null || dto.getSportTypeId() == null) {
                continue;
            }

            SportTypeVO oriVo = oriVoMap.get(dto.getSportTypeId());
            if (oriVo != null) {
                SportTypeVO vo = toUpdateVO(oriVo, dto);
                if (vo != null) {
                    list.add(vo);
                }
            }
        }

        return list;
    }

    public static List<SportTypeResponseDTO> toDtoList(List<SportTypeVO> vos) {
        List<SportTypeResponseDTO> list = new ArrayList<>();
        
        if (vos == null || vos.isEmpty()) {
            return list;
        }
        
        for (SportTypeVO vo : vos) {
            list.add(toDTO(vo));
        }
        
        return list;
    }
    
    
    /**
     * 將 SportTypeResponseDTO 轉成 SportTypeResponseExtraSportsDTO
     * 額外加 sportResponseDTOs
     */
    public static SportTypeResponseExtraSportsDTO toExtraSportsDTO(
            SportTypeResponseDTO baseDto,
            List<SportResponseDTO> sportResponseDTOs) {

        if (baseDto == null) {
            return null;
        }

        SportTypeResponseExtraSportsDTO dto = new SportTypeResponseExtraSportsDTO();

        // 複製共用欄位
        dto.setSportTypeId(baseDto.getSportTypeId());
        dto.setSportTypeName(baseDto.getSportTypeName());
        dto.setSportTypePic(baseDto.getSportTypePic());
        dto.setSportTypeDataStatus(baseDto.getSportTypeDataStatus());
        dto.setSportTypeDataStatusText(baseDto.getSportTypeDataStatusText());
        dto.setCreateDatetime(baseDto.getCreateDatetime());
        dto.setUpdateDatetime(baseDto.getUpdateDatetime());
        dto.setSportTypeItemResponseDTOs(baseDto.getSportTypeItemResponseDTOs());

        // 加額外欄位
        dto.setSportResponseDTOs(sportResponseDTOs);

        return dto;
    }
}
