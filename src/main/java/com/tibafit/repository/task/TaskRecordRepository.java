package com.tibafit.repository.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.task.TaskRecordStatusVO;
import com.tibafit.model.task.TaskRecordVO;

public interface TaskRecordRepository extends JpaRepository<TaskRecordVO, Integer> {
     
	 @Query("SELECT tr FROM TaskRecordVO tr WHERE tr.taskVO.taskId = :taskId")
	 List<TaskRecordVO> findByTaskId(@Param("taskId")Integer taskId);
     
     @Query("SELECT tr FROM TaskRecordVO tr WHERE tr.user.userId = :userId")
     List<TaskRecordVO> findAllByUserId(@Param("userId") Integer userId);
     
     @org.springframework.data.jpa.repository.Modifying
     @org.springframework.transaction.annotation.Transactional
     @org.springframework.data.jpa.repository.Query(
       "update TaskRecordVO tr set tr.taskRecordStatusVO = :status where tr.taskRecordId = :id"
     )
     int updateStatusById(@org.springframework.data.repository.query.Param("id") Integer id,
                          @org.springframework.data.repository.query.Param("status") com.tibafit.model.task.TaskRecordStatusVO status);
     
     @Modifying
     @Transactional
     @Query(value =
         "UPDATE task_record " +
         "SET task_record_status_id = 1, " +              // 設為完成
         "    user_end_time = CURRENT_TIMESTAMP " +       // 完成時間=現在
         "WHERE user_id = :userId " +
         "  AND task_id = :taskId " +
         "  AND (task_record_status_id <> 1 OR task_record_status_id IS NULL) " + // 只更新未完成
         "  AND (is_deleted = 0 OR is_deleted IS NULL)",   // 若無軟刪欄位就移除這行
         nativeQuery = true)
     int markCompleteByUserAndTask(
         @Param("userId") Integer userId,
         @Param("taskId") Integer taskId
     );
     
  // 讀：我的 TaskRecord（可帶 statusId 篩選）
     @Query("""
            SELECT tr
              FROM TaskRecordVO tr
             WHERE tr.user.userId = :userId
               AND (:statusId IS NULL OR tr.taskRecordStatusVO.taskRecordStatus = :statusId)
             ORDER BY tr.userStartTime DESC
            """)
     List<TaskRecordVO> findByUserAndOptionalStatus(@Param("userId") Integer userId,
                                                    @Param("statusId") Integer statusId);

     // 寫：只有“擁有者”才能把這筆設為完成（原子性更新）
     @Modifying
     @Transactional
     @Query("""
            UPDATE TaskRecordVO tr
               SET tr.taskRecordStatusVO = :done,
                   tr.userEndTime = :now
             WHERE tr.taskRecordId = :id
               AND tr.user.userId = :userId
               AND (tr.taskRecordStatusVO.taskRecordStatus <> 1)
            """)
     int markCompleteIfOwner(@Param("id") Integer recordId,
                             @Param("userId") Integer userId,
                             @Param("done") TaskRecordStatusVO done,
                             @Param("now") java.time.LocalDateTime now);
}
