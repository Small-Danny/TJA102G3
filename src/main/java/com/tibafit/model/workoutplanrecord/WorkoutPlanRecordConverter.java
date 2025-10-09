package com.tibafit.model.workoutplanrecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordRequestDTO;
import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordResponseDTO;
import com.tibafit.model.workoutplan.WorkoutPlanSportFrom;

public class WorkoutPlanRecordConverter {

    // RequestDTO 轉 VO
    public static WorkoutPlanRecordVO toInsertVO(WorkoutPlanRecordRequestDTO dto) {
        if (dto == null) {
        	return null;
        }

        WorkoutPlanRecordVO vo = new WorkoutPlanRecordVO();
        
		String tempSportFrom = dto.getSportFrom();
		Integer tempSportId = dto.getSportId();
		Integer tempCustomSportId = dto.getCustomSportId();
		Integer tempCalorieCountMethod = WorkoutPlanRecordCalorieCountMethod.RAWCOUNT.getCodeNum();
        
		boolean checkResult = false;
		if((WorkoutPlanSportFrom.SYSTEM.getCodeName()).equals(tempSportFrom) && tempSportId != null) {
			tempCustomSportId = null;
			// 系統運動紀錄先預設 "估算"
			tempCalorieCountMethod = WorkoutPlanRecordCalorieCountMethod.RAWCOUNT.getCodeNum();
			checkResult = true;
		}
		if ((WorkoutPlanSportFrom.CUSTOM.getCodeName()).equals(tempSportFrom) && tempCustomSportId != null) {
			tempSportId = null;
			// 自訂義運動紀錄先預設 "自填"
			tempCalorieCountMethod = WorkoutPlanRecordCalorieCountMethod.FILLIN.getCodeNum();
			checkResult = true;
		}
		
		// 拋例外
		if(!checkResult) {
	        throw new IllegalArgumentException("WorkoutPlanRecordConverter 運動來源參數不合法: sportFrom=" + tempSportFrom 
	                + ", sportId=" + tempSportId 
	                + ", customSportId=" + tempCustomSportId);
		}
		
		LocalDateTime tempActualStartTime = LocalDateTime.parse(dto.getActualStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		LocalDateTime tempActualEndTime = LocalDateTime.parse(dto.getActualEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		
        vo.setWorkoutPlanRecordId(dto.getWorkoutPlanRecordId());
        
        vo.setWorkoutPlanId(dto.getWorkoutPlanId());
        
        vo.setSportFrom(tempSportFrom);
        vo.setSportId(tempSportId);
        vo.setCustomSportId(tempCustomSportId);
        
        // 先放預設，到svc裡會再依計算方式變動
        vo.setCalorieCountMethod(tempCalorieCountMethod);
        
        vo.setActualStartTime(tempActualStartTime);
        vo.setActualEndTime(tempActualEndTime);

        vo.setWorkoutPlanRecordDataStatus(WorkoutPlanRecordDataStatus.EXIST.getCodeNum());
        
        vo.setUserId(dto.getUserId());
        
        return vo;
    }

    // VO 轉 ResponseDTO
    public static WorkoutPlanRecordResponseDTO toDTO(WorkoutPlanRecordVO vo) {
        if (vo == null) {
        	return null;
        }

        WorkoutPlanRecordResponseDTO dto = new WorkoutPlanRecordResponseDTO();
        
		Integer tempExpectedDuration = vo.getActualDuration();
		StringBuilder durationStr = new StringBuilder("0 hr 0 min");
		if (tempExpectedDuration != null && tempExpectedDuration > 0) {
			// 小時
		    int hours = tempExpectedDuration / 60;
		    // 分鐘
		    int minutes = tempExpectedDuration % 60;
		    
		    durationStr.setLength(0);
		    durationStr.append( hours + " hr " + minutes + " min");
		}
		
		Integer tempCalorieCountMethod = vo.getCalorieCountMethod();
        
        dto.setWorkoutPlanRecordId(vo.getWorkoutPlanRecordId());
        dto.setWorkoutPlanId(vo.getWorkoutPlanId());
        
        dto.setSportFrom(vo.getSportFrom());
        dto.setSportId(vo.getSportId());
        dto.setCustomSportId(vo.getCustomSportId());
        
        dto.setActualCalories(vo.getActualCalories());
        dto.setCalorieCountMethod(tempCalorieCountMethod);
        dto.setCalorieCountMethodText(WorkoutPlanRecordCalorieCountMethod.getDisplayNameByCodeNum(tempCalorieCountMethod));
        
        dto.setActualStartTime(vo.getActualStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        dto.setActualEndTime(vo.getActualEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        dto.setActualDuration(tempExpectedDuration);
        dto.setActualDurationText(durationStr.toString());
        
        dto.setActualRecordDatetime(vo.getActualRecordDatetime() == null? "" : vo.getActualRecordDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setWorkoutPlanRecordDataStatus(vo.getWorkoutPlanRecordDataStatus());
        dto.setCreateDatetime(vo.getCreateDatetime() == null? "" : vo.getCreateDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setUpdateDatetime(vo.getUpdateDatetime() == null? "" : vo.getUpdateDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return dto;
    }

    // List DTO 轉 VO (新增)
    public static List<WorkoutPlanRecordVO> toInsertVoList(List<WorkoutPlanRecordRequestDTO> dtoList) {
    	List<WorkoutPlanRecordVO> voList = new ArrayList<>();
        
    	if (dtoList == null || dtoList.isEmpty()) {
        	return voList;
        }

        for (WorkoutPlanRecordRequestDTO dto : dtoList) {
            if (dto != null) {
                WorkoutPlanRecordVO vo = WorkoutPlanRecordConverter.toInsertVO(dto);
                voList.add(vo);
            }
        }

        return voList;
    }

    // List VO 轉 DTO
    public static List<WorkoutPlanRecordResponseDTO> toDtoList(List<WorkoutPlanRecordVO> voList) {
    	List<WorkoutPlanRecordResponseDTO> dtoList = new ArrayList<>();
    	
    	if (voList == null || voList.isEmpty()) {
        	return dtoList;
    	}
        for (WorkoutPlanRecordVO vo : voList) {
            if (vo!= null) {
            	WorkoutPlanRecordResponseDTO dto = WorkoutPlanRecordConverter.toDTO(vo);
                dtoList.add(dto);
            }
        }

        return dtoList;
    }
}
