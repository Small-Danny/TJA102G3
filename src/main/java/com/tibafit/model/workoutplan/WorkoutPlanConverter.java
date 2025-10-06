package com.tibafit.model.workoutplan;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tibafit.dto.workoutplan.WorkoutPlanRequestDTO;
import com.tibafit.dto.workoutplan.WorkoutPlanResponseDTO;
import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordResponseDTO;
import com.tibafit.model.sport.SportLevel;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordConverter;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordVO;

public class WorkoutPlanConverter {

	// RequestDTO 轉 VO (新增)
	public static WorkoutPlanVO toNewVO(WorkoutPlanRequestDTO dto) {
		if (dto == null) {
			return null;
		}

		WorkoutPlanVO vo = new WorkoutPlanVO();
		
		String tempSportFrom = dto.getSportFrom();
		Integer tempSportId = dto.getSportId();
		Integer tempCustomSportId = dto.getCustomSportId();
		
		boolean checkResult = false;
		if((WorkoutPlanSportFrom.SYSTEM.getCodeName()).equals(tempSportFrom) && tempSportId != null) {
			tempCustomSportId = null;
			checkResult = true;
		} else if ((WorkoutPlanSportFrom.CUSTOM.getCodeName()).equals(tempSportFrom) && tempCustomSportId != null) {
			tempSportId = null;
			checkResult = true;
		}
		
		// 拋例外
		if(!checkResult) {
	        throw new IllegalArgumentException("WorkoutPlanConverter 運動來源參數不合法: sportFrom=" + tempSportFrom 
	                + ", sportId=" + tempSportId 
	                + ", customSportId=" + tempCustomSportId);
		}

		vo.setWorkoutPlanId(dto.getWorkoutPlanId());
		vo.setWorkoutPlanName(dto.getWorkoutPlanName());
		
		vo.setUserId(dto.getUserId());
//		User userVO = new User();
//		userVO.setUserId(dto.getUserId());
//		vo.setUserVO(userVO);
		
		vo.setSportFrom(tempSportFrom);
		
		vo.setSportId(tempSportId);
//		SportVO sportVO = new SportVO();
//		sportVO.setSportId(tempSportId);
//		vo.setSportVO(sportVO);
		
		vo.setCustomSportId(tempCustomSportId);
//		CustomSportVO customsportVO = new CustomSportVO();
//		customsportVO.setCustomSportId(tempCustomSportId);
//		vo.setCustomSportVO(customsportVO);
		
		vo.setWorkoutPlanDate(LocalDate.parse(dto.getWorkoutPlanDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		vo.setWorkoutPlanTime(dto.getWorkoutPlanTime() == null? null : LocalTime.parse(dto.getWorkoutPlanTime(), DateTimeFormatter.ofPattern("HH:mm")));
		vo.setWorkoutPlanIsNotify(dto.getWorkoutPlanIsNotify());
		vo.setWorkoutPlanExpectedDuration(dto.getWorkoutPlanExpectedDuration());

		vo.setWorkoutPlanDataStatus(WorkoutPlanDataStatus.EXIST.getCodeNum());
		vo.setWorkoutPlanStatus(WorkoutPlanStatus.NOTYET.getCodeNum());
		vo.setActualTotalCount(0);
		vo.setActualTotalDuration(0);
		vo.setActualTotalCalories(0);	
		
		vo.setWorkoutPlanNotifyStatus(WorkoutPlanNotifyStatus.NOTYET.getCodeNum());

		return vo;
	}
	
	// RequestDTO 轉 VO (更新) (注意PO議題)
	public static WorkoutPlanVO toUpdateVO(WorkoutPlanVO oriVo, WorkoutPlanRequestDTO dto) {
		if (dto == null) {
			return null;
		}
		
	    WorkoutPlanVO vo = new WorkoutPlanVO();
	    
	    
	    vo.setWorkoutPlanId(oriVo.getWorkoutPlanId());
	    vo.setWorkoutPlanName(oriVo.getWorkoutPlanName());
	    vo.setUserId(oriVo.getUserId());
	    
	    vo.setSportFrom(oriVo.getSportFrom());
	    vo.setSportId(oriVo.getSportId());
	    vo.setCustomSportId(oriVo.getCustomSportId());
	    
	    vo.setWorkoutPlanDate(oriVo.getWorkoutPlanDate());
	    vo.setWorkoutPlanTime(oriVo.getWorkoutPlanTime());
	    vo.setWorkoutPlanIsNotify(oriVo.getWorkoutPlanIsNotify());
	    vo.setWorkoutPlanExpectedDuration(oriVo.getWorkoutPlanExpectedDuration());

	    vo.setWorkoutPlanStatus(oriVo.getWorkoutPlanStatus());
	    
	    vo.setActualTotalCount(oriVo.getActualTotalCount());
	    vo.setActualTotalDuration(oriVo.getActualTotalDuration());
	    vo.setActualTotalCalories(oriVo.getActualTotalCalories());
	    
	    vo.setWorkoutPlanDataStatus(oriVo.getWorkoutPlanDataStatus());
	    
	    vo.setWorkoutPlanNotifyStatus(oriVo.getWorkoutPlanNotifyStatus());
	    

	    if (dto.getWorkoutPlanId() != null) {
	        vo.setWorkoutPlanId(dto.getWorkoutPlanId());
	    }
	    if (dto.getWorkoutPlanName() != null) {
	        vo.setWorkoutPlanName(dto.getWorkoutPlanName());
	    }
	    if (dto.getUserId() != null) {
	        vo.setUserId(dto.getUserId());
	    }
	    
	    String tempSportFrom = dto.getSportFrom();
	    Integer tempSportId = dto.getSportId();
	    Integer tempCustomSportId = dto.getCustomSportId();

	    boolean checkResult = false;
	    if ((WorkoutPlanSportFrom.SYSTEM.getCodeName()).equals(tempSportFrom) && tempSportId != null) {
	        tempCustomSportId = null;
	        checkResult = true;
	    } else if ((WorkoutPlanSportFrom.CUSTOM.getCodeName()).equals(tempSportFrom) && tempCustomSportId != null) {
	        tempSportId = null;
	        checkResult = true;
	    }

	    // 拋例外
	    if (tempSportFrom != null && !checkResult) {
	        throw new IllegalArgumentException("WorkoutPlanConverter 運動來源參數不合法: sportFrom=" + tempSportFrom 
	                + ", sportId=" + tempSportId 
	                + ", customSportId=" + tempCustomSportId);
	    }

	    if (tempSportFrom != null) {
	        vo.setSportFrom(tempSportFrom);
	    }
	    if ((WorkoutPlanSportFrom.SYSTEM.getCodeName()).equals(tempSportFrom) && tempSportId != null) {
	        vo.setSportId(tempSportId);
	        vo.setCustomSportId(null);
	    } else if ((WorkoutPlanSportFrom.CUSTOM.getCodeName()).equals(tempSportFrom) && tempCustomSportId != null) {
	        vo.setSportId(null);
	        vo.setCustomSportId(tempCustomSportId);
	    }
	    

	    if (dto.getWorkoutPlanDate() != null) {
	        vo.setWorkoutPlanDate(
	            LocalDate.parse(dto.getWorkoutPlanDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	        );
	    }
	    
	    // 可能為null
	    if (dto.getWorkoutPlanTime() == null) {
	        vo.setWorkoutPlanTime(null);
	    } else {
	        vo.setWorkoutPlanTime(
		            LocalTime.parse(dto.getWorkoutPlanTime(), DateTimeFormatter.ofPattern("HH:mm"))
		        );
	    }
	    
	    if (dto.getWorkoutPlanIsNotify() != null) {
	        vo.setWorkoutPlanIsNotify(dto.getWorkoutPlanIsNotify());
	    }
	    
	    if (dto.getWorkoutPlanExpectedDuration() != null) {
	        vo.setWorkoutPlanExpectedDuration(dto.getWorkoutPlanExpectedDuration());
	    }
	    
	   // workoutPlanDataStatus do not update
	 	
		return vo;
	}


	// VO 轉 ResponseDTO
	public static WorkoutPlanResponseDTO toDTO(WorkoutPlanVO vo) {
		if (vo == null) {
			return null;
		}

		WorkoutPlanResponseDTO dto = new WorkoutPlanResponseDTO();
		
		String tempSportFrom = vo.getSportFrom();
		String tempWorkoutPlanDate = vo.getWorkoutPlanDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String tempWorkoutPlanTime = vo.getWorkoutPlanTime() == null? null : vo.getWorkoutPlanTime().format(DateTimeFormatter.ofPattern("HH:mm"));
		
		Integer tempExpectedDuration = vo.getWorkoutPlanExpectedDuration();
		StringBuilder durationStr = new StringBuilder("0 hr 0 min");
		if (tempExpectedDuration != null && tempExpectedDuration > 0) {
			// 小時
		    int hours = tempExpectedDuration / 60;
		    // 分鐘
		    int minutes = tempExpectedDuration % 60;
		    
		    durationStr.setLength(0);
		    durationStr.append( hours + " hr " + minutes + " min");
		}
		
		Integer tempActualTotalDuration = vo.getActualTotalDuration();
		StringBuilder totalDurationStr = new StringBuilder("0 hr 0 min");
		if (tempActualTotalDuration != null && tempActualTotalDuration > 0) {
			// 小時
		    int hours = tempActualTotalDuration / 60;
		    // 分鐘
		    int minutes = tempActualTotalDuration % 60;

		    totalDurationStr.setLength(0);
		    totalDurationStr.append( hours + " hr " + minutes + " min");
		}
		
		dto.setWorkoutPlanId(vo.getWorkoutPlanId());
		dto.setWorkoutPlanName(vo.getWorkoutPlanName());
		dto.setUserId(vo.getUserId());
		
		dto.setSportFrom(tempSportFrom);
		dto.setSportFromText(WorkoutPlanSportFrom.getDisplayNameByCodeName(tempSportFrom));
		
		dto.setSportId(vo.getSportId());
		dto.setCustomSportId(vo.getCustomSportId());
		if(WorkoutPlanSportFrom.SYSTEM.getCodeName().equals(tempSportFrom)) {
			if(vo.getSportVO() != null) {
				if(vo.getSportVO().getSportName() != null) {
					dto.setSportName(vo.getSportVO().getSportName());
				} else {
					dto.setSportName("");
				}
				if(vo.getSportVO().getSportLevel() != null) {
					dto.setSportLevel(vo.getSportVO().getSportLevel());
					dto.setSportLevelText(SportLevel.getDisplayNameByCodeName(vo.getSportVO().getSportLevel()));				
				} else {
					dto.setSportLevel("");
					dto.setSportLevelText("");	
				}
				if(vo.getSportVO().getSportEstimatedCalories() != null) {
					dto.setSportCalories(vo.getSportVO().getSportEstimatedCalories());
				} else {
					dto.setSportCalories(0);
				}
			}
		}
		if(WorkoutPlanSportFrom.CUSTOM.getCodeName().equals(tempSportFrom)) {
			if(vo.getCustomSportVO() != null) {
				if(vo.getCustomSportVO().getSportName() != null) {
					dto.setSportName(vo.getCustomSportVO().getSportName());
				} else {
					dto.setSportName("");
				}
				
				dto.setSportLevel("");
				dto.setSportLevelText("");	
				
				if(vo.getCustomSportVO().getSportEstimatedCalories() != null) {
					dto.setSportCalories(vo.getCustomSportVO().getSportEstimatedCalories());
				} else {
					dto.setSportCalories(0);
				}
			}
		}
		
		Set<WorkoutPlanRecordVO> tempRecordVoSets = vo.getWorkoutPlanRecordVOs();
		List<WorkoutPlanRecordVO> tempRecordVoList = new ArrayList<>();
		for (WorkoutPlanRecordVO rsvo : tempRecordVoSets) {
			if(rsvo != null) {
				tempRecordVoList.add(rsvo);
			}
		}
		List<WorkoutPlanRecordResponseDTO> tempRecordDtoList = WorkoutPlanRecordConverter.toDtoList(tempRecordVoList);
		dto.setWorkoutPlanRecordResponseDTOs(tempRecordDtoList);
		;
		dto.setWorkoutPlanStatus(vo.getWorkoutPlanStatus());
		dto.setWorkoutPlanStatusText(WorkoutPlanStatus.getDisplayNameByCodeNum(vo.getWorkoutPlanStatus()));
		
		dto.setWorkoutPlanDate(tempWorkoutPlanDate);
		dto.setWorkoutPlanTime(tempWorkoutPlanTime);
		
		dto.setWorkoutPlanIsNotify(vo.getWorkoutPlanIsNotify());
		dto.setWorkoutPlanIsNotifyText(WorkoutPlanIsNotify.getDisplayNameByCodeNum(vo.getWorkoutPlanIsNotify()));
		
		dto.setWorkoutPlanExpectedDuration(tempExpectedDuration);
		dto.setWorkoutPlanExpectedDurationText(durationStr.toString());
		
		dto.setActualTotalCount(vo.getActualTotalCount());
		
		dto.setActualTotalDuration(tempActualTotalDuration);
		dto.setActualTotalDurationText(totalDurationStr.toString());
		
		dto.setActualTotalCalories(vo.getActualTotalCalories());
		
		dto.setWorkoutPlanDataStatus(vo.getWorkoutPlanDataStatus());	
		dto.setTaskRecordId(vo.getTaskRecordId());
		dto.setCreateDatetime(vo.getCreateDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		dto.setUpdateDatetime(vo.getUpdateDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		return dto;
	}

	// List DTO 轉 VO (新增)
	public static List<WorkoutPlanVO> toNewVoList(List<WorkoutPlanRequestDTO> dtoList) {
		List<WorkoutPlanVO> voList = new ArrayList<>();

		if (dtoList == null || dtoList.isEmpty()) {
			return voList;
		}

		for (WorkoutPlanRequestDTO dto : dtoList) {
			if (dto != null) {
				WorkoutPlanVO vo = WorkoutPlanConverter.toNewVO(dto);
				voList.add(vo);
			}
		}

		return voList;
	}
	
	// List DTO 轉 VO (更新) (注意PO議題)
	public static List<WorkoutPlanVO> toUpdateVoList(List<WorkoutPlanVO> oriVoList, List<WorkoutPlanRequestDTO> dtoList) {
		List<WorkoutPlanVO> voList = new ArrayList<>();
		
		if (oriVoList == null || oriVoList.isEmpty() || dtoList == null || dtoList.isEmpty()) {
	        return voList;
	    }

	    Map<Integer, WorkoutPlanVO> oriVoMap = new HashMap<>();
	    for (WorkoutPlanVO oriVo : oriVoList) {
	        if (oriVo  != null && oriVo.getWorkoutPlanId() != null) {
	        	oriVoMap.put(oriVo.getWorkoutPlanId(), oriVo);
	        }
	    }

	    for (WorkoutPlanRequestDTO dto : dtoList) {
	        if (dto == null || dto.getWorkoutPlanId() == null) {
	            continue;
	        }

	        WorkoutPlanVO oriVo = oriVoMap.get(dto.getWorkoutPlanId());
	        
	        if (oriVo != null) {
	        	WorkoutPlanVO vo = WorkoutPlanConverter.toUpdateVO(oriVo, dto);
				voList.add(vo);
	        }
	    }

	    return voList;
	}

	// List VO 轉 ResponseDTO
	public static List<WorkoutPlanResponseDTO> toDtoList(List<WorkoutPlanVO> voList) {
		List<WorkoutPlanResponseDTO> dtoList = new ArrayList<>();

		if (voList == null || voList.isEmpty()) {
			return dtoList;
		}
		for (WorkoutPlanVO vo : voList) {
			if (vo != null) {
				WorkoutPlanResponseDTO dto = WorkoutPlanConverter.toDTO(vo);
				dtoList.add(dto);
			}
		}

		return dtoList;
	}
}
