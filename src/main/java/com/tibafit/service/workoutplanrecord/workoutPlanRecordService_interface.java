package com.tibafit.service.workoutplanrecord;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordRequestDTO;
import com.tibafit.dto.workoutplanrecord.WorkoutPlanRecordResponseDTO;

public interface workoutPlanRecordService_interface {
	
	public Integer updateWorkoutPlanRecordDataStatusByRecordIds(Integer status, List<Integer> recordIds);
	
	public List<WorkoutPlanRecordResponseDTO> insertWorkoutPlanRecordMultiple(List<WorkoutPlanRecordRequestDTO> dtos);
	
	public List<WorkoutPlanRecordResponseDTO> getRecordsByWorkoutPlanId(Integer planId);
	
	public List<WorkoutPlanRecordResponseDTO> getRecordsByWorkoutPlanIdAndDataStatuses(Integer planId, List<Integer> Statuses);
	
	public Page<WorkoutPlanRecordResponseDTO> getRecordsByWorkoutPlanIdAndDataStatusesPage(Integer planId, List<Integer> Statuses, Pageable pageable);
	
	public Map<Integer, List<WorkoutPlanRecordResponseDTO>> getRecordsByWorkoutPlanIds(List<Integer> planIds);
	
	public Map<Integer, List<WorkoutPlanRecordResponseDTO>> getRecordsByWorkoutPlanIdsAndDataStatuses(List<Integer> planIds, List<Integer> Statuses);
	
	public WorkoutPlanRecordResponseDTO getRecordByPrimaryKey(Integer id);
}
