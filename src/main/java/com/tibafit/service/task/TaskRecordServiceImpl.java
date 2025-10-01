package com.tibafit.service.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.task.TaskRecordVO;
import com.tibafit.repository.task.TaskRecordRepository;

@Service
@Transactional // 預設所有 public 方法皆開啟交易
public class TaskRecordServiceImpl implements TaskRecordService {

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Override
    public TaskRecordVO addTaskRecord(TaskRecordVO vo) {
        // 這裡可加商業規則驗證（外鍵存在性、狀態合法性…）
        return taskRecordRepository.save(vo);
    }

    @Override
    public TaskRecordVO updateTaskRecord(TaskRecordVO vo) {
        if (vo.getTaskRecordId() == null) {
            throw new IllegalArgumentException("更新失敗：taskRecordId 不可為空");
        }
        // 確認目標存在（避免 save() 走到 insert）
        TaskRecordVO db = taskRecordRepository.findById(vo.getTaskRecordId()).orElse(null);
        if (db == null) {
            throw new IllegalArgumentException("更新失敗：找不到 id=" + vo.getTaskRecordId());
        }
        // 若你想保留部分欄位，可在此進行欄位覆寫，而不是直接存 vo
        // e.g. db.setStatus(vo.getStatus()); db.setNote(vo.getNote()); ...
        // 這裡示範全量覆寫（以 vo 為主）：
        return taskRecordRepository.save(vo);
    }

    @Override
    public void deleteTaskRecord(Integer taskRecordId) {
        try {
            taskRecordRepository.deleteById(taskRecordId);
        } catch (EmptyResultDataAccessException ignore) {
            // 刪除不存在的 id -> 視為無事發生（也可選擇丟出錯誤）
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaskRecordVO getOneTaskRecord(Integer taskRecordId) {
        return taskRecordRepository.findById(taskRecordId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskRecordVO> getAll() {
        return taskRecordRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskRecordVO> findByUserAndStatus(Integer userId, Integer statusId) {
      return taskRecordRepository.findByUserAndOptionalStatus(userId, statusId);
    }
}
