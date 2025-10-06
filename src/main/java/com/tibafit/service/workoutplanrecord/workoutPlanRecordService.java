package com.tibafit.service.workoutplanrecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordRequestDTO;
import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordResponseDTO;
import com.tibafit.model.customsport.CustomSportVO;
import com.tibafit.model.sport.SportVO;
import com.tibafit.model.user.User;
import com.tibafit.model.workoutplan.WorkoutPlanSportFrom;
import com.tibafit.model.workoutplan.WorkoutPlanStatus;
import com.tibafit.model.workoutplan.WorkoutPlanVO;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordCalorieCountMethod;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordConverter;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordVO;
import com.tibafit.repository.customsport.CustomSportRepository;
import com.tibafit.repository.sport.SportRepository;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.repository.workoutplan.WorkoutPlanRepository;
import com.tibafit.repository.workoutplanrecord.WorkoutPlanRecordRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class workoutPlanRecordService implements workoutPlanRecordService_interface {

	@Autowired
	private WorkoutPlanRecordRepository planRecordRepo;

	@Autowired
	private WorkoutPlanRepository planRepo;

	@Autowired
	private SportRepository sportRepo;

	@Autowired
	private CustomSportRepository customSportRepo;
	
	@Autowired
	private UserRepository userRepo;
	
    @PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public Integer updateWorkoutPlanRecordDataStatusByRecordIds(Integer status, List<Integer> recordIds) {
		// 更新records狀態
		Integer affectNum = planRecordRepo.updateWorkoutPlanRecordDataStatusByRecordIds(status, recordIds);
		
		if(affectNum == 0) {
			return affectNum;
		}
		
		Set<Integer> planIds = new HashSet<>();
		List<Integer> planIdList = new ArrayList<>();
		
		// PO
		List<WorkoutPlanRecordVO> workoutPlanRecordVOs = planRecordRepo.findByWorkoutPlanRecordIdIn(recordIds);
		
		for (WorkoutPlanRecordVO vo : workoutPlanRecordVOs) {
			if(vo!=null && vo.getWorkoutPlanId() != null) {
				planIds.add(vo.getWorkoutPlanId());
			}
		}
		
		for (Integer planId : planIds) {
			planIdList.add(planId);
		}
		
		System.out.println("recordSvc planIds:" + planIds);
		
		// PO
		List<WorkoutPlanVO> workoutPlanVOs = planRepo.findByWorkoutPlanIdIn(planIdList);
		
		
		// 根據 計畫ID (planId)，將紀錄分組用於資料累加的Map，為了更新計畫(plan) 的 total相關資料
		Map<Integer, List<WorkoutPlanRecordVO>> planIdAndRecordsMap = new HashMap<>();

		// 遍歷 取得計畫個別的紀錄VOs，組成Map資料
		for (WorkoutPlanVO planVO : workoutPlanVOs) {
		    if (planVO == null || planVO.getWorkoutPlanRecordVOs() == null) {
		        continue;
		    }
		    
		    // 拿 計畫ID (planId)
		    Integer planId = planVO.getWorkoutPlanId();
		    
		    // PO
		    Set<WorkoutPlanRecordVO> recordVOs = planVO.getWorkoutPlanRecordVOs();
		    List<WorkoutPlanRecordVO> recordVoActiveList = new ArrayList();
		    
			for (WorkoutPlanRecordVO recordVO : recordVOs) {
				// 過濾出 存在狀態 的紀錄
				if(recordVO != null && recordVO.getWorkoutPlanRecordDataStatus() == 1) {
					recordVoActiveList.add(recordVO);	
				}
			}
			
		    // 沒有此key的話，新建一組key-value
		    if(!planIdAndRecordsMap.containsKey(planId)) {
		        planIdAndRecordsMap.put(planId, recordVoActiveList);
		    }
		}

		// 遍歷Map資料 (轉Set資料格式)
		for (Map.Entry<Integer, List<WorkoutPlanRecordVO>> entry : planIdAndRecordsMap.entrySet()) {
			Integer planId = entry.getKey();
			
			// PO
			WorkoutPlanVO planVO = planRepo.findById(planId).orElse(null);
			
			if(planVO == null || planVO.getActualTotalCount() == null) {
				continue;
			}
			if(planVO == null || planVO.getActualTotalCalories() == null) {
				continue;
			}
			if(planVO == null || planVO.getActualTotalDuration() == null) {
				continue;
			}
			
					
			List<WorkoutPlanRecordVO> activeRecords = entry.getValue();
			if(activeRecords == null) {
				continue;
			}
			
			Integer sumCalories = 0;
			Integer sumDuration = 0;
			Integer count = 0;
			for (WorkoutPlanRecordVO rvo: activeRecords) {
				if(rvo.getActualCalories() != null) {
					sumCalories += rvo.getActualCalories();
				}
				if(rvo.getActualDuration() != null) {
					sumDuration += rvo.getActualDuration();
				}
			}
			count = activeRecords.size();
			
			if(count > 0) {
				// 更新計畫狀態: 已執行
				planVO.setWorkoutPlanStatus(WorkoutPlanStatus.DONE.getCodeNum());
			} else {
				// 更新計畫狀態: 未執行
				planVO.setWorkoutPlanStatus(WorkoutPlanStatus.NOTYET.getCodeNum());
			}

			// 放入VO (PO已更新資料庫計畫total相關欄位，應不需save，保險起見)
			planVO.setActualTotalCount(count);
			planVO.setActualTotalCalories(sumCalories);
			planVO.setActualTotalDuration(sumDuration);
			
			System.out.println("updateWorkoutPlanRecordDataStatusByRecordIds Managed? " + entityManager.contains(planVO));
			planRepo.save(planVO);
			
		}
		
		
		return affectNum;
	}
	

	@Override
	public List<WorkoutPlanRecordResponseDTO> insertWorkoutPlanRecordMultiple(List<WorkoutPlanRecordRequestDTO> dtos) {
		Integer calorieCountMethod = WorkoutPlanRecordCalorieCountMethod.RAWCOUNT.getCodeNum();
		
		List<WorkoutPlanRecordResponseDTO> returnRecordDTOs = new ArrayList<>();
		if (dtos == null || dtos.isEmpty()) {
			return returnRecordDTOs;
		}

		List<WorkoutPlanRecordVO> planRecordVOs = new ArrayList<>();

		for (WorkoutPlanRecordRequestDTO dto : dtos) {
			if (dto == null) {
				continue;
			}

			WorkoutPlanRecordVO planRecordVO = WorkoutPlanRecordConverter.toInsertVO(dto);
			
			BigDecimal countCalories = BigDecimal.ZERO;
			// 預設分鐘數
			BigDecimal duration = BigDecimal.ONE;

			// 計算 duration (分鐘)
			if (planRecordVO.getActualStartTime() != null && planRecordVO.getActualEndTime() != null) {
				long rawDuration = Duration.between(planRecordVO.getActualStartTime(), planRecordVO.getActualEndTime()).toMinutes();
				duration = BigDecimal.valueOf(rawDuration);
				
				planRecordVO.setActualDuration(duration.intValue());
			}

			// 如果是 系統運動 (system)
			if ((WorkoutPlanSportFrom.SYSTEM.getCodeName()).equals(planRecordVO.getSportFrom()) && planRecordVO.getSportId() != null) {
				// 預設體重 60kg
				BigDecimal weightKg = BigDecimal.valueOf(60);

				// 取使用者填寫體重，沒填以預設體重估算
				Integer userId = planRecordVO.getUserId() != null ? planRecordVO.getUserId() : null;
				// PO
				User userVO = userRepo.findById(userId).orElse(null);
				if (userId != null && userVO != null && userVO.getWeightKg() != null
						&& userVO.getWeightKg().compareTo(BigDecimal.ZERO) > 0) {
					
					calorieCountMethod = WorkoutPlanRecordCalorieCountMethod.KGCOUNT.getCodeNum();
					weightKg = userVO.getWeightKg();
				}

				// PO，拿運動資料
				SportVO sport = sportRepo.findById(planRecordVO.getSportId()).orElse(null);
				
				if (sport != null && sport.getSportMets() != null) {
					BigDecimal mets = sport.getSportMets();

					// 計算 總消耗卡路里 = METs × 體重(kg) × (小時 (時長分鐘 ÷ 60))
					countCalories = mets.multiply(weightKg).multiply(
							duration.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
					// 放入VO
					planRecordVO.setActualCalories(countCalories.setScale(0, RoundingMode.HALF_UP).intValue());
				}
			}

			// 如果是 自訂義運動 (custom)
			if ((WorkoutPlanSportFrom.CUSTOM.getCodeName()).equals(planRecordVO.getSportFrom()) && planRecordVO.getCustomSportId() != null) {
				calorieCountMethod = WorkoutPlanRecordCalorieCountMethod.FILLIN.getCodeNum();
				
				// PO，拿運動資料
				CustomSportVO customSport = customSportRepo.findById(planRecordVO.getCustomSportId()).orElse(null);
				
				if (customSport != null && customSport.getSportEstimatedCalories() != null) {
					// 拿 運動估計消耗卡路里 (每小時(60分鐘)預估消耗)
					BigDecimal caloriePerSixtyMin = BigDecimal.valueOf(customSport.getSportEstimatedCalories());

					// 計算 每分鐘消耗卡路里 = 運動估計消耗卡路里 ÷ 60 (每小時(60分鐘))
					BigDecimal caloriePerMin = caloriePerSixtyMin.divide(BigDecimal.valueOf(60), 2,
							RoundingMode.HALF_UP);

					// 計算 總消耗卡路里 = 每分鐘消耗卡路里 × 總時長 (分鐘)
					countCalories = caloriePerMin.multiply(duration);
					// 放入VO
					planRecordVO.setActualCalories(countCalories.setScale(0, RoundingMode.HALF_UP).intValue());
				}
			}
			
			planRecordVO.setCalorieCountMethod(calorieCountMethod);

			planRecordVOs.add(planRecordVO);
		}

		// 批次更新資料庫 planRecordVOs
		List<WorkoutPlanRecordVO> saveRecordVOs = planRecordRepo.saveAll(planRecordVOs);
		
		// 清掉 Hibernate 快取，
		// 避免 findAllById 回傳舊物件 (DB 時間相關欄位沒更新)
		entityManager.flush();
		entityManager.clear();
		
		// 準備回傳資料 (供計算的消耗熱量顯示)
		List<Integer> planRecordIds = new ArrayList<>();
		for (WorkoutPlanRecordVO vo : saveRecordVOs) {
			planRecordIds.add(vo.getWorkoutPlanRecordId());
		}
		List<WorkoutPlanRecordVO> regetRecordVOs = planRecordRepo.findAllById(planRecordIds);
		returnRecordDTOs = WorkoutPlanRecordConverter.toDtoList(regetRecordVOs);
		

		// 根據 對應計畫ID (planId)，將紀錄分組用於資料累加的Map，為了更新計畫(plan) 的 total相關資料
		Map<Integer, List<WorkoutPlanRecordVO>> planIdAndRecordsMap = new HashMap<>();

		// 遍歷 要新增的紀錄VOs，組成Map資料
		for (WorkoutPlanRecordVO recordVO : planRecordVOs) {
		    if (recordVO == null || recordVO.getWorkoutPlanId() == null) {
		        continue;
		    }
		    
		    // 拿 對應計畫ID (planId)
		    Integer planId = recordVO.getWorkoutPlanId();
		    
		    // 沒有此key的話，新建一組key-value
		    if(!planIdAndRecordsMap.containsKey(planId)) {
		        planIdAndRecordsMap.put(planId, new ArrayList<>());
		    }
		    
		    // 先拿出已存在Map中的值(record列表)，將對應VO放入值陣列
		    List<WorkoutPlanRecordVO> records = planIdAndRecordsMap.get(planId);
	    	records.add(recordVO);
	        planIdAndRecordsMap.put(planId, records);
		}

		// 遍歷Map資料 (轉Set資料格式)
		for (Map.Entry<Integer, List<WorkoutPlanRecordVO>> entry : planIdAndRecordsMap.entrySet()) {
			Integer planId = entry.getKey();
			
			Integer oriTotalCount = 0;
			Integer oriTotalCalories = 0;
			Integer oriTotalDuration = 0;
			
			// PO
			WorkoutPlanVO planVO = planRepo.findById(planId).orElse(null);
			
			if(planVO != null && planVO.getActualTotalCount() != null) {
				oriTotalCount = planVO.getActualTotalCount();
			}
			if(planVO != null && planVO.getActualTotalCalories() != null) {
				oriTotalCalories = planVO.getActualTotalCalories();
			}
			if(planVO != null && planVO.getActualTotalDuration() != null) {
				oriTotalDuration = planVO.getActualTotalDuration();
			}
			
					
			List<WorkoutPlanRecordVO> records = entry.getValue();
			
			Integer sumCalories = 0;
			Integer sumDuration = 0;
			Integer count = 0;
			for (WorkoutPlanRecordVO vo: records) {
				if(vo.getActualCalories() != null) {
					sumCalories += vo.getActualCalories();
				}
				if(vo.getActualDuration() != null) {
					sumDuration += vo.getActualDuration();
				}
			}
			count = records.size();
			
			if((oriTotalDuration + sumDuration) > 0) {
				// 更新計畫狀態
				planVO.setWorkoutPlanStatus(WorkoutPlanStatus.DONE.getCodeNum());
			}

			// 放入VO (PO已更新資料庫計畫total相關欄位，應不需save，保險起見)
			planVO.setActualTotalCount(oriTotalCount + count);
			planVO.setActualTotalCalories(oriTotalCalories + sumCalories);
			planVO.setActualTotalDuration(oriTotalDuration + sumDuration);
			
			System.out.println("insertWorkoutPlanRecordMultiple Managed? " + entityManager.contains(planVO));
			planRepo.save(planVO);
			
		}
		
		return returnRecordDTOs;
	}

	@Override
	public List<WorkoutPlanRecordResponseDTO> getRecordsByWorkoutPlanId(Integer planId) {
		List<WorkoutPlanRecordVO> vos = planRecordRepo.findByWorkoutPlanId(planId);
		List<WorkoutPlanRecordResponseDTO> dtos = WorkoutPlanRecordConverter.toDtoList(vos);
		return dtos;
	}
	
	@Override
	public List<WorkoutPlanRecordResponseDTO> getRecordsByWorkoutPlanIdAndDataStatuses(Integer planId, List<Integer> Statuses) {
		List<WorkoutPlanRecordVO> vos = planRecordRepo.findByWorkoutPlanIdAndWorkoutPlanRecordDataStatusIn(planId, Statuses);
		List<WorkoutPlanRecordResponseDTO> dtos = WorkoutPlanRecordConverter.toDtoList(vos);
		return dtos;
	}
	
	@Override
	public Page<WorkoutPlanRecordResponseDTO> getRecordsByWorkoutPlanIdAndDataStatusesPage(Integer planId, List<Integer> Statuses, Pageable pageable) {
		Page<WorkoutPlanRecordVO> vosPage = planRecordRepo.findByWorkoutPlanIdAndWorkoutPlanRecordDataStatusIn(planId, Statuses, pageable);
		
		List<WorkoutPlanRecordResponseDTO> dtos = WorkoutPlanRecordConverter.toDtoList(vosPage.getContent());
		
		Page<WorkoutPlanRecordResponseDTO> dtosPage = new PageImpl<>(dtos, pageable, vosPage.getTotalElements());
		return dtosPage;
	}
	
	@Override
	public Map<Integer, List<WorkoutPlanRecordResponseDTO>> getRecordsByWorkoutPlanIds(List<Integer> planIds) {
		// PO
		List<WorkoutPlanRecordVO> planRecordVOs = planRecordRepo.findByWorkoutPlanIdIn(planIds);
		
		// 根據 對應計畫ID (planId)，將紀錄分組
		Map<Integer, List<WorkoutPlanRecordResponseDTO>> planIdAndRecordsMap = new HashMap<>();

		// 遍歷 要新增的紀錄VOs，組成Map資料
		for (WorkoutPlanRecordVO recordVO : planRecordVOs) {
		    if (recordVO == null || recordVO.getWorkoutPlanId() == null || recordVO.getWorkoutPlanVO() == null || recordVO.getWorkoutPlanVO().getWorkoutPlanId() == null) {
		        continue;
		    }
		    
		    // 拿 對應計畫ID (planId)
		    Integer planId = recordVO.getWorkoutPlanId();
		    
		    // 沒有此key的話，新建一組key-value
		    if(!planIdAndRecordsMap.containsKey(planId)) {
		        planIdAndRecordsMap.put(planId, new ArrayList<WorkoutPlanRecordResponseDTO>());
		    }
		    
		    // 先拿出已存在Map中的值(record列表)，將對應DTO(VO轉換好的)放入值陣列
		    List<WorkoutPlanRecordResponseDTO> recordDtos = planIdAndRecordsMap.get(planId);
		    WorkoutPlanRecordResponseDTO dto = WorkoutPlanRecordConverter.toDTO(recordVO);
		    recordDtos.add(dto);
	        planIdAndRecordsMap.put(planId, recordDtos);
		}
		return planIdAndRecordsMap;
	}
	
	@Override
	public Map<Integer, List<WorkoutPlanRecordResponseDTO>> getRecordsByWorkoutPlanIdsAndDataStatuses(List<Integer> planIds, List<Integer> Statuses) {
		// PO
		List<WorkoutPlanRecordVO> planRecordVOs = planRecordRepo.findByWorkoutPlanIdInAndWorkoutPlanRecordDataStatusIn(planIds, Statuses);
		
		// 根據 對應計畫ID (planId)，將紀錄分組
		Map<Integer, List<WorkoutPlanRecordResponseDTO>> planIdAndRecordsMap = new HashMap<>();

		// 遍歷 要新增的紀錄VOs，組成Map資料
		for (WorkoutPlanRecordVO recordVO : planRecordVOs) {
		    if (recordVO == null || recordVO.getWorkoutPlanId() == null || recordVO.getWorkoutPlanVO() == null || recordVO.getWorkoutPlanVO().getWorkoutPlanId() == null) {
		        continue;
		    }
		    
		    // 拿 對應計畫ID (planId)
		    Integer planId = recordVO.getWorkoutPlanId();
		    
		    // 沒有此key的話，新建一組key-value
		    if(!planIdAndRecordsMap.containsKey(planId)) {
		        planIdAndRecordsMap.put(planId, new ArrayList<WorkoutPlanRecordResponseDTO>());
		    }
		    
		    // 先拿出已存在Map中的值(record列表)，將對應DTO(VO轉換好的)放入值陣列
		    List<WorkoutPlanRecordResponseDTO> recordDtos = planIdAndRecordsMap.get(planId);
		    WorkoutPlanRecordResponseDTO dto = WorkoutPlanRecordConverter.toDTO(recordVO);
		    recordDtos.add(dto);
	        planIdAndRecordsMap.put(planId, recordDtos);
		}
		return planIdAndRecordsMap;
	}

	@Override
	public WorkoutPlanRecordResponseDTO getRecordByPrimaryKey(Integer id) {
		WorkoutPlanRecordVO vo = planRecordRepo.findByWorkoutPlanRecordId(id).orElse(null);
		WorkoutPlanRecordResponseDTO dto = WorkoutPlanRecordConverter.toDTO(vo);
		return dto;
	}
}
