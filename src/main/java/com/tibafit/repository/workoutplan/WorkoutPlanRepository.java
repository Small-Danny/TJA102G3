package com.tibafit.repository.workoutplan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tibafit.model.workoutplan.WorkoutPlanVO;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlanVO, Integer> {
	
	@EntityGraph(attributePaths = {
	    "userVO",
	    "sportVO",
	    "customSportVO",
	    "workoutPlanRecordVOs"
	})
	public Optional<WorkoutPlanVO> findByWorkoutPlanId(Integer workoutPlanId);
	
	
	@EntityGraph(attributePaths = {
	    "userVO",
	    "sportVO",
	    "customSportVO",
	    "workoutPlanRecordVOs"
	})
	public List<WorkoutPlanVO> findByWorkoutPlanIdIn(List<Integer> workoutPlanIds);

	
//	// 日期區間查詢 (區間最長一個月)
//	@EntityGraph(attributePaths = {
//	    "userVO",
//	    "sportVO",
//	    "customSportVO",
//	    "workoutPlanRecordVOs"
//	})
//	public List<WorkoutPlanVO> findByUserIdAndWorkoutPlanDateBetweenAndWorkoutPlanDataStatusIn(Integer userId, LocalDate startDate, LocalDate endDate, List<Integer> statuses);
	@Query("SELECT DISTINCT w FROM WorkoutPlanVO w " +
		       "LEFT JOIN FETCH w.sportVO s " +
		       "LEFT JOIN FETCH w.customSportVO cs " +
		       "LEFT JOIN FETCH w.workoutPlanRecordVOs wr " +
		       "WHERE w.userId = :userId " +
		       "  AND w.workoutPlanDate BETWEEN :startDate AND :endDate " +
		       "  AND w.workoutPlanDataStatus IN :statuses " +
		       "ORDER BY " +
		       "  CASE WHEN w.workoutPlanTime IS NULL THEN 1 ELSE 0 END ASC, " + 
		       "  w.workoutPlanTime ASC, " +                                    
		       "  w.workoutPlanId ASC")                                         
		List<WorkoutPlanVO> findByUserIdAndWorkoutPlanDateBetweenAndWorkoutPlanDataStatusIn(
		        @Param("userId") Integer userId,
		        @Param("startDate") LocalDate startDate,
		        @Param("endDate") LocalDate endDate,
		        @Param("statuses") List<Integer> statuses);


	// 單日期查詢
//	@EntityGraph(attributePaths = {
//	    "userVO",
//	    "sportVO",
//	    "customSportVO",
//	    "workoutPlanRecordVOs"
//	})
//	public List<WorkoutPlanVO> findByUserIdAndWorkoutPlanDateAndWorkoutPlanDataStatusIn(Integer userId,
//			LocalDate workoutPlanDate, List<Integer> statuses);
	@Query("SELECT DISTINCT w FROM WorkoutPlanVO w " +
		       "LEFT JOIN FETCH w.sportVO s " +
		       "LEFT JOIN FETCH w.customSportVO cs " +
		       "LEFT JOIN FETCH w.workoutPlanRecordVOs wr " +
		       "WHERE w.userId = :userId " +
		       "  AND w.workoutPlanDate = :workoutPlanDate " +
		       "  AND w.workoutPlanDataStatus IN :statuses " +
		       "ORDER BY " +
		       "  CASE WHEN w.workoutPlanTime IS NULL THEN 1 ELSE 0 END ASC, " +
		       "  w.workoutPlanTime ASC, " +                                    
		       "  w.workoutPlanId ASC")                                         
		List<WorkoutPlanVO> findByUserIdAndWorkoutPlanDateAndWorkoutPlanDataStatusIn(
		        @Param("userId") Integer userId,
		        @Param("workoutPlanDate") LocalDate workoutPlanDate,
		        @Param("statuses") List<Integer> statuses);
	
	
	// 單日期查詢
	@EntityGraph(attributePaths = {
	    "userVO",
	    "sportVO",
	    "customSportVO",
	    "workoutPlanRecordVOs"
	})
	public Page<WorkoutPlanVO> findByUserIdAndWorkoutPlanDateAndWorkoutPlanDataStatusIn(Integer userId,
			LocalDate workoutPlanDate, List<Integer> statuses, Pageable pageable);

	
	@Modifying
	@Query(value = "UPDATE workout_plan SET workout_plan_data_status = :status WHERE workout_plan_id IN :ids", nativeQuery = true)
	public Integer updateWorkoutPlanDataStatusByIds(@Param("status") Integer status, @Param("ids") List<Integer> ids);
	
	
	// 正常/補寄需提醒資料：開提醒/有日期/有時間/未刪除/未寄
	@Query("SELECT DISTINCT w FROM WorkoutPlanVO w " +
	       "LEFT JOIN FETCH w.sportVO s " +
	       "LEFT JOIN FETCH w.customSportVO cs " +
	       "LEFT JOIN FETCH w.userVO u " +
	       "WHERE w.workoutPlanIsNotify = 1 " +
	       "AND w.workoutPlanTime IS NOT NULL " +
	       "AND w.workoutPlanDate IS NOT NULL " +
	       "AND w.workoutPlanNotifyStatus = 0 " +
	       "AND w.workoutPlanDataStatus <> 0 " +
	       "ORDER BY w.workoutPlanDate ASC, w.workoutPlanTime ASC")
	List<WorkoutPlanVO> findAllPendingNotifyPlans();
    
    
    // 修改計畫的寄信狀態
    @Modifying
    @Query(value = "UPDATE workout_plan " +
                   "SET workout_plan_notify_status = :status " +
                   "WHERE workout_plan_id = :id", nativeQuery = true)
    Integer updateWorkoutPlanNotifyStatusById(@Param("id") Integer workoutPlanId,
                                          @Param("status") Integer notifyStatus);
}
