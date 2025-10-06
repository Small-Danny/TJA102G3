package com.tibafit.service.sporttype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.sport.SportResponseDTO;
import com.tibafit.dto.sporttype.SportTypeRequestDTO;
import com.tibafit.dto.sporttype.SportTypeResponseDTO;
import com.tibafit.dto.sporttype.SportTypeResponseExtraSportsDTO;
import com.tibafit.model.sport.SportConverter;
import com.tibafit.model.sport.SportVO;
import com.tibafit.model.sporttype.SportTypeConverter;
import com.tibafit.model.sporttype.SportTypeDataStatus;
import com.tibafit.model.sporttype.SportTypeVO;
import com.tibafit.model.sporttypeitem.SportTypeItemVO;
import com.tibafit.repository.sporttype.SportTypeRepository;
import com.tibafit.repository.sporttypeitem.SportTypeItemRepository;

@Service
@Transactional
public class SportTypeService implements SportTypeService_interface {

	@Autowired
	private SportTypeRepository sportTypeRepo;

	@Autowired
	private SportTypeItemRepository sportTypeItemRepo;


	@Override
	public void insertSportTypes(List<SportTypeRequestDTO> dtos) {
		List<SportTypeVO> vos = SportTypeConverter.toNewVoList(dtos);
		sportTypeRepo.saveAll(vos);
	}


	@Override
	public void updateSportTypes(List<SportTypeRequestDTO> dtos) {
		List<Integer> typeIds = new ArrayList<>();
		for (SportTypeRequestDTO dto : dtos) {
			if (dto.getSportTypeId() != null) {
				typeIds.add(dto.getSportTypeId());
			}
		}

		// PO
		List<SportTypeVO> oriVos = sportTypeRepo.findBySportTypeIdIn(typeIds);

		List<SportTypeVO> vos = SportTypeConverter.toUpdateVoList(oriVos, dtos);

		sportTypeRepo.saveAll(vos);
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isExistBySportTypeName(String sportTypeName) {
		Boolean isExist = sportTypeRepo.existsBySportTypeNameAndSportTypeDataStatusNot(sportTypeName, SportTypeDataStatus.DELETE.getCodeNum());
		return isExist;
	}

	@Override
	@Transactional(readOnly = true)
	public SportTypeResponseDTO getBySportTypeId(Integer sportTypeId) {
		Optional<SportTypeVO> vo = sportTypeRepo.findBySportTypeId(sportTypeId);
		SportTypeResponseDTO dto = SportTypeConverter.toDTO(vo.orElse(null));
		return dto;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<SportTypeResponseDTO> getBySportTypeIds(List<Integer> sportTypeIds) {
		List<SportTypeVO> vos = sportTypeRepo.findBySportTypeIdIn(sportTypeIds);
		List<SportTypeResponseDTO> dtos = SportTypeConverter.toDtoList(vos);
		return dtos;
	}
	
	
	// try lambda、stream，暫時不用方法簡寫
	// return extra DTO
	@Override
	@Transactional(readOnly = true)
	public SportTypeResponseExtraSportsDTO getBySportTypeId_SportDataStatuses(Integer sportTypeId, List<Integer> sportDataStatuses) {
		// PO
		// Optional 簡化
		SportTypeVO typeVo = sportTypeRepo.findBySportTypeId(sportTypeId).orElse(null);
		
		if (typeVo == null) {
			return null;
		}
		
		SportTypeResponseDTO typeDto = SportTypeConverter.toDTO(typeVo);
		

		// 過濾 child 運動 List<SportResponseDTO> 
		Set<SportTypeItemVO> typeItemVos = typeVo.getSportTypeItemVOs();
		// 僅保險起見
		if(typeItemVos == null || typeItemVos.isEmpty()) {
			typeDto.setSportTypeItemResponseDTOs(Collections.emptyList());
			return SportTypeConverter.toExtraSportsDTO(typeDto, Collections.emptyList());
		}
				
		// many to one，保險資料問題去重		
		Set<SportVO> sportVos = typeItemVos.stream().map(sti -> sti.getSportVO()).collect(Collectors.toCollection(()-> new LinkedHashSet<>()));	
		// 過濾出運動狀態（sportDataStatuses為null/空，全留）
		List<SportVO> activeSportVos = sportVos.stream().filter(sportVO -> (sportDataStatuses == null || sportDataStatuses.isEmpty())
                || sportDataStatuses.contains(sportVO.getSportDataStatus())).collect(Collectors.toList());
			
		List<SportResponseDTO> activeSportDtos = SportConverter.toDtoList(activeSportVos);
		
		SportTypeResponseExtraSportsDTO extraSportDto = SportTypeConverter.toExtraSportsDTO(typeDto, activeSportDtos);

		return extraSportDto;
	}

	
	// try lambda、stream，暫時不用方法簡寫
	// return extra DTO
	@Override
	@Transactional(readOnly = true)
	public List<SportTypeResponseExtraSportsDTO> getBySportTypeDataStatuses_SportDataStatuses(List<Integer> sportTypeDataStatuses, List<Integer> sportDataStatuses) {
		
		List<SportTypeVO> typeVos = null;
	    // 無 sportTypeDataStatuses 查全部
	    if (sportTypeDataStatuses == null || sportTypeDataStatuses.isEmpty()) {
	    	// PO
	    	typeVos = sportTypeRepo.findAll(Sort.by(Sort.Direction.ASC, "sportTypeId"));
	    } else {
		    // PO
		    typeVos = sportTypeRepo.findBySportTypeDataStatusIn(sportTypeDataStatuses, Sort.by(Sort.Direction.ASC, "sportTypeId"));
	    }

	    if (typeVos == null || typeVos.isEmpty()) {
	        return Collections.emptyList();
	    }

	    // 遍歷 typeVos，各別過濾出運動狀態 & 轉 DTO 
	    List<SportTypeResponseExtraSportsDTO> typeDtos = typeVos.stream().map(typeVo -> {
	        // 單筆轉 DTO
	        SportTypeResponseDTO typeDto = SportTypeConverter.toDTO(typeVo);

	        // 取關聯items
	        Set<SportTypeItemVO> typeItemVos = typeVo == null ? null : typeVo.getSportTypeItemVOs();
	        // 僅保險起見
	        if (typeItemVos == null || typeItemVos.isEmpty()) {
	            // 空陣列
	        	typeDto.setSportTypeItemResponseDTOs(Collections.emptyList());
	            return SportTypeConverter.toExtraSportsDTO(typeDto, Collections.emptyList());
	        }

	        // many to one，保險去重
	        Set<SportVO> sportVos = typeItemVos.stream()
	                .map(sti -> sti.getSportVO())
	                .filter(sportVO -> sportVO != null)
	                .collect(Collectors.toCollection(()-> new LinkedHashSet<>()));

	        // 過濾出運動狀態（sportDataStatuses為null/空，全留)
	        List<SportVO> activeSportVos = sportVos.stream()
	                .filter(sportVO -> (sportDataStatuses == null || sportDataStatuses.isEmpty())
	                        || sportDataStatuses.contains(sportVO.getSportDataStatus()))
	                .collect(Collectors.toList());

	        // 轉DTO
	        List<SportResponseDTO> activeSportDtos = SportConverter.toDtoList(activeSportVos);
	        
	        SportTypeResponseExtraSportsDTO extraSportDto = SportTypeConverter.toExtraSportsDTO(typeDto, activeSportDtos);
	        
	        return extraSportDto;
	    }).collect(Collectors.toList());

	    return typeDtos;
	}


	@Override
	@Transactional(readOnly = true)
	public SportTypeResponseDTO getBySportTypeIdAndSportTypeDataStatuses(Integer sportTypeId, List<Integer> statuses) {
		Optional<SportTypeVO> vo = sportTypeRepo.findBySportTypeIdAndSportTypeDataStatusIn(sportTypeId, statuses);
		SportTypeResponseDTO dto = SportTypeConverter.toDTO(vo.orElse(null));
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SportTypeResponseDTO> getBySportTypeIdsAndSportTypeDataStatuses(List<Integer> sportTypeIds,
			List<Integer> statuses) {
		List<SportTypeVO> vos = sportTypeRepo.findBySportTypeIdInAndSportTypeDataStatusIn(sportTypeIds, statuses);
		List<SportTypeResponseDTO> dtos = SportTypeConverter.toDtoList(vos);
		return dtos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SportTypeResponseDTO> getBySportTypeDataStatuses(List<Integer> statuses) {
		List<SportTypeVO> vos = sportTypeRepo.findBySportTypeDataStatusIn(statuses, Sort.by(Sort.Direction.ASC, "sportTypeId"));
		List<SportTypeResponseDTO> dtos = SportTypeConverter.toDtoList(vos);
		return dtos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SportTypeResponseDTO> getByComplexCondition(String sportTypeNameFuzzy, String createStartDate,
			String createEndDate, String updateStartDate, String updateEndDate, List<Integer> statuses) {
		List<SportTypeVO> vos = sportTypeRepo.findByComplexCondition(sportTypeNameFuzzy, createStartDate, createEndDate,
				updateStartDate, updateEndDate, statuses);
		List<SportTypeResponseDTO> dtos = SportTypeConverter.toDtoList(vos);
		return dtos;
	}

	@Override
	public Integer updateSportTypeDataStatusBySportTypeIds(Integer dataStatus, List<Integer> sportTypeIds) {
		Integer affectNumOfType = sportTypeRepo.updateSportTypeDataStatusBySportTypeIds(dataStatus, sportTypeIds);

		// 如為 刪除分類
		if (affectNumOfType > 0 && (SportTypeDataStatus.DELETE.getCodeNum()).equals(dataStatus)) {
			// 一併刪除該分類明細
			// PO
			List<SportTypeItemVO> itemList = sportTypeItemRepo.findBySportTypeIdIn(sportTypeIds);
			List<Integer> itemIdList = new ArrayList<>();
			for (SportTypeItemVO item : itemList) {
				if (item != null && item.getSportTypeItemId() != null) {
					itemIdList.add(item.getSportTypeItemId());
				}
			}
			sportTypeItemRepo.deleteBySportTypeItemIdIn(itemIdList);
		}

		return affectNumOfType;
	}
}
