package com.tibafit.repository.workoutplanrecord;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordVO;

@Repository
public interface WorkoutPlanRecordRepository extends JpaRepository<WorkoutPlanRecordVO, Integer> {
 
	 // 計畫ID查詢
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	 })
	 public List<WorkoutPlanRecordVO> findByWorkoutPlanId(Integer workoutPlanId);
	 
	 
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	 })
	 public List<WorkoutPlanRecordVO> findByWorkoutPlanIdAndWorkoutPlanRecordDataStatusIn(Integer workoutPlanId, List<Integer> statuses);
			
	 
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	 })
	 public Page<WorkoutPlanRecordVO> findByWorkoutPlanIdAndWorkoutPlanRecordDataStatusIn(Integer workoutPlanId, List<Integer> statuses, Pageable pageable);
	 
	
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	})
	public List<WorkoutPlanRecordVO> findByWorkoutPlanIdIn(List<Integer> workoutPlanIds);
	 
	 
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	})
	public List<WorkoutPlanRecordVO> findByWorkoutPlanIdInAndWorkoutPlanRecordDataStatusIn(List<Integer> workoutPlanIds, List<Integer> statuses);
	 
	 
	 // 計畫紀錄ID查詢
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	 })
	 public Optional<WorkoutPlanRecordVO> findByWorkoutPlanRecordId(Integer workoutPlanRecordId);
	 
	 
	 // 計畫紀錄ID查詢
	 @EntityGraph(attributePaths = {
	    "sportVO",
		"customSportVO",
	    "workoutPlanVO",
	    "workoutPlanVO.userVO"
	 })
	 public List<WorkoutPlanRecordVO> findByWorkoutPlanRecordIdIn(List<Integer> workoutPlanRecordIds);
	
	
	 @Modifying
	 @Query(value = "UPDATE workout_plan_record SET workout_plan_record_data_status = :status WHERE workout_plan_record_id IN :ids", nativeQuery = true)
	 public Integer updateWorkoutPlanRecordDataStatusByRecordIds(@Param("status") Integer status, @Param("ids") List<Integer> recordIds);
}
