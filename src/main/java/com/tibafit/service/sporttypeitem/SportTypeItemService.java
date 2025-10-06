package com.tibafit.service.sporttypeitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.sporttypeitem.SportTypeItemRequestDTO;
import com.tibafit.dto.sporttypeitem.SportTypeItemResponseDTO;
import com.tibafit.model.sporttypeitem.SportTypeItemConverter;
import com.tibafit.model.sporttypeitem.SportTypeItemVO;
import com.tibafit.repository.sporttypeitem.SportTypeItemRepository;

@Service
@Transactional
public class SportTypeItemService implements SportTypeItemService_interface {

    @Autowired
    private SportTypeItemRepository sportTypeItemRepo;

    @Override
    public void insertSportTypeItems(List<SportTypeItemRequestDTO> dtos) {
        List<SportTypeItemVO> vos = SportTypeItemConverter.toNewVoList(dtos);
        sportTypeItemRepo.saveAll(vos);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isExitBySportTypeIdAndSportId(Integer sportTypeId, Integer sportId) {
        Boolean isExist = sportTypeItemRepo.existsBySportTypeIdAndSportId(sportTypeId, sportId);
        return isExist;
    }

    @Override
    @Transactional(readOnly = true)
    public SportTypeItemResponseDTO getBySportTypeItemId(Integer sportTypeItemId) {
        Optional<SportTypeItemVO> vo = sportTypeItemRepo.findBySportTypeItemId(sportTypeItemId);
        SportTypeItemResponseDTO dto = SportTypeItemConverter.toDTO(vo.orElse(null));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportTypeItemResponseDTO> getBySportTypeId(Integer sportTypeId) {
        List<SportTypeItemVO> vos = sportTypeItemRepo.findBySportTypeId(sportTypeId);
        List<SportTypeItemResponseDTO> dtos = SportTypeItemConverter.toDtoList(vos);
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public SportTypeItemResponseDTO getBySportTypeIdAndSportId(Integer sportTypeId, Integer sportId) {
        Optional<SportTypeItemVO> vo = sportTypeItemRepo.findBySportTypeIdAndSportId(sportTypeId, sportId);
        SportTypeItemResponseDTO dto = SportTypeItemConverter.toDTO(vo.orElse(null));
        return dto;
    }
    
    @Override
    public void deleteBySportTypeItemIds(List<Integer> sportTypeRecordIds) {
        sportTypeItemRepo.deleteBySportTypeItemIdIn(sportTypeRecordIds);
    }
}
