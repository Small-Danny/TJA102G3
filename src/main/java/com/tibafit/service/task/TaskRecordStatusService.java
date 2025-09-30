package com.tibafit.service.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.task.TaskRecordStatusVO;
import com.tibafit.repository.task.TaskRecordStatusRepository;

@Service
@Transactional
public class TaskRecordStatusService {

    @Autowired
    private TaskRecordStatusRepository repository;

    /** 新增 */
    public TaskRecordStatusVO add(TaskRecordStatusVO vo) {
        if (vo == null) throw new IllegalArgumentException("TaskRecordStatusVO 不可為 null");
        // 若有唯一性需求（例如 statusName 唯一），可在此做重複檢查
        return repository.save(vo);
    }

    /** 更新（全量覆蓋） */
    public TaskRecordStatusVO update(TaskRecordStatusVO vo) {
        if (vo == null || vo.getTaskRecordStatus() == null) {
            throw new IllegalArgumentException("更新失敗：statusId 不可為空");
        }
        // 可選：先確認存在，避免誤更新
        repository.findById(vo.getTaskRecordStatus())
                  .orElseThrow(() -> new IllegalArgumentException("找不到狀態 id=" + vo.getTaskRecordStatus()));
        return repository.save(vo);
    }

    /** 刪除（不存在不拋錯） */
    public void delete(Integer statusId) {
        repository.deleteById(statusId);
    }

    /** 取單筆（找不到回傳 null） */
    @Transactional(readOnly = true)
    public TaskRecordStatusVO getOne(Integer statusId) {
        return repository.findById(statusId).orElse(null);
    }

    /** 取全部（不排序） */
    @Transactional(readOnly = true)
    public List<TaskRecordStatusVO> getAll() {
        return repository.findAll();
    }

    /** 取全部並依名稱升冪排序（你的頁面有用到 statusName） */
    @Transactional(readOnly = true)
    public List<TaskRecordStatusVO> getAllOrderByNameAsc() {
        return repository.findAll(Sort.by("statusName").ascending());
    }

    /** 工具：是否存在 */
    @Transactional(readOnly = true)
    public boolean exists(Integer statusId) {
        return repository.existsById(statusId);
    }

    /** 工具：計數 */
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }
}
