package com.tibafit.service.customsport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.customsport.CustomSportRequestDTO;
import com.tibafit.model.customsport.CustomSportConverter;
import com.tibafit.model.customsport.CustomSportVO;
import com.tibafit.repository.customsport.CustomSportRepository;

@Service
public class CustomSportService implements CustomSportService_interface {

    @Autowired
    private CustomSportRepository repo;

	@Override
	@Transactional(readOnly = true)
	public List<CustomSportVO> getSportAll() {
		return repo.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public CustomSportVO getSportByPrimaryKey(Integer customSportId) {
		Optional<CustomSportVO> opt = repo.findById(customSportId); 
		return opt.orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CustomSportVO> getSportByDataStatuses(List<Integer> sportDataStatuses) {
		return repo.findBySportDataStatuses(sportDataStatuses);
	}
	
	@Override
	@Transactional
	public void insertSportMultiple(List<CustomSportRequestDTO> dtos) {	
	    if (dtos == null || dtos.isEmpty()) {
	        return;
	    }
	    
		List<CustomSportVO> vos = CustomSportConverter.toNewVoList(dtos);	
		

		repo.saveAll(vos);
	}
	

	@Override
	@Transactional
	public void updateSportMultiple(List<CustomSportRequestDTO> dtos) {		
	    if (dtos == null || dtos.isEmpty()) {
	        return;
	    }

	    List<Integer> ids = new ArrayList<>();
	    for (CustomSportRequestDTO dto : dtos) {
	        if (dto != null && dto.getCustomSportId() != null) {
	            ids.add(dto.getCustomSportId());
	        }
	    }

	    if (ids.isEmpty()) {
	        return;
	    }

	    // PO
	    List<CustomSportVO> oriVos = repo.findAllById(ids);

	    if (oriVos.isEmpty()) {
	        return;
	    }

	    List<CustomSportVO> vos = CustomSportConverter.toUpdateVoList(oriVos, dtos);

	    
	    if (!vos.isEmpty()) {
	        repo.saveAll(vos);
	    }
	}


	@Override
	@Transactional
	public Integer updateSportDataStatusByIds(Integer targetStatus, List<Integer> sportIds) {
		if (sportIds == null || sportIds.isEmpty()) {
			return 0;
		};
		Integer affectNum = repo.updateSportDataStatusByIds(targetStatus, sportIds);
		return affectNum;
	}
}
