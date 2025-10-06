package com.tibafit.service.workoutplan;

import java.time.LocalDate;
import java.util.List;
//import java.util.Map;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

import com.tibafit.dto.workoutplan.WorkoutPlanRequestDTO;
import com.tibafit.dto.workoutplan.WorkoutPlanResponseDTO;

public interface workoutPlanService_interface {
	public void insertWorkoutPlanMultiple(List<WorkoutPlanRequestDTO> dtos);
	
	public void updateWorkoutPlanMultiple(List<WorkoutPlanRequestDTO> dtos);

	WorkoutPlanResponseDTO getWorkoutPlanByPrimaryKey(Integer id);

	public List<WorkoutPlanResponseDTO> getWorkoutPlanByDateRange(Integer userId, LocalDate startDate, LocalDate endDate, List<Integer> statuses);
	
	public List<WorkoutPlanResponseDTO> getWorkoutPlanByDate(Integer userId, LocalDate workoutPlanDate, List<Integer> statuses);

	public Integer updateWorkoutPlanDataStatusByIds(Integer status, List<Integer> ids);
}
