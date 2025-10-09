package com.tibafit.service.sport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.sport.SportRequestDTO;
import com.tibafit.model.sport.SportConverter;
import com.tibafit.model.sport.SportDataStatus;
import com.tibafit.model.sport.SportVO;
import com.tibafit.repository.sport.SportRepository;

@Service("sportService")
public class SportService implements SportService_Interface {

	@Autowired
	private SportRepository sportRepo;
	
    @Override
    @Transactional(readOnly = true)
    public Boolean isExistBySportName(String sportName) {
        Boolean isExist = sportRepo.existsBySportNameAndSportDataStatusNot(sportName, SportDataStatus.DELETE.getCodeNum());
        return isExist;
    }

	@Override
	@Transactional(readOnly = true)
	public List<SportVO> getSportAll() {
		return sportRepo.findAll(Sort.by(Sort.Direction.DESC, "sportId"));
	}

	@Override
	@Transactional(readOnly = true)
	public SportVO getSportByPrimaryKey(Integer sportId) {
		Optional<SportVO> opt = sportRepo.findById(sportId);
		return opt.orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SportVO> getSportByDataStatuses(List<Integer> sportDataStatuses) {
		return sportRepo.findBySportDataStatuses(sportDataStatuses);
	}
	

	@Override
	public List<SportDataStatus> getSportDataStatusOptions() {
		List<SportDataStatus> tempList = new ArrayList<>();

		for (SportDataStatus level : SportDataStatus.values()) {

			if (level.getCodeNum() == 0 || level.getCodeNum() == 1) {
				tempList.add(level);
			}
		}
		return tempList;
	}

	@Override
	public List<SportDataStatus> getSportDataStatusNeed(List<Integer> needStatusCodeNums) {
		List<SportDataStatus> tempList = new ArrayList<>();

		for (SportDataStatus level : SportDataStatus.values()) {
			int index = needStatusCodeNums.indexOf(level.getCodeNum());
			if (index != -1) {
				tempList.add(level);
			}
		}
		return tempList;
	}

	@Override
	@Transactional
	public void insertSport(SportRequestDTO dto) {
		SportVO vo = SportConverter.toNewVO(dto);
		sportRepo.save(vo);
	}

	@Override
	@Transactional
	public void insertSportMultiple(List<SportRequestDTO> dtos) {
		List<SportVO> vos = SportConverter.toNewVoList(dtos);
		sportRepo.saveAll(vos);
	}

	@Override
	@Transactional
	public void updateSport(SportRequestDTO dto) {
	    if (dto == null || dto.getSportId() == null) {
	        throw new IllegalArgumentException("SportSvc sportId 不可為空");
	    }
		
	   SportVO oriVo = sportRepo.findById(dto.getSportId())
	            .orElseThrow(() -> new IllegalArgumentException("SportSvc 查無此 sportId VO: " + dto.getSportId()));
	   
		SportVO vo = SportConverter.toUpdateVO(oriVo, dto);
		sportRepo.save(vo);
	}

	@Override
	@Transactional
	public void updateSportMultiple(List<SportRequestDTO> dtos) {
	    if (dtos == null || dtos.isEmpty()) {
	        return;
	    }
	    
	    List<Integer> ids = new ArrayList<>();
	    for (SportRequestDTO dto : dtos) {
	        if (dto != null && dto.getSportId() != null) {
	            ids.add(dto.getSportId());
	        }
	    }
	    
	    // PO
	    List<SportVO> oriVoList = sportRepo.findAllById(ids);
	    
		List<SportVO> vos = SportConverter.toUpdateVoList(oriVoList, dtos);
		
		sportRepo.saveAll(vos);
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<SportVO> getByComplexCondition(
			String sportNameDescFuzzy, String sportLevel, 
			String createStartDate, String createEndDate, 
			String updateStartDate, String updateEndDate, 
			List<Integer> statuses) {
		List<SportVO> sportVos = sportRepo.findByComplexCondition(sportNameDescFuzzy, sportLevel,
				createStartDate, createEndDate, updateStartDate, updateEndDate, statuses);
		return sportVos;
	}
	

	@Override
	@Transactional
	public Integer updateSportDataStatusByIds(Integer targetStatus, List<Integer> sportIds) {
		if (sportIds == null || sportIds.isEmpty()) {
			return 0;
		}

		Integer affectNumOfSport = sportRepo.updateSportDataStatusByIds(targetStatus, sportIds);

		return affectNumOfSport;
	}

}
