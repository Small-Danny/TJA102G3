package com.tibafit.service.task;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tibafit.model.task.TaskTypeVO;
import com.tibafit.repository.task.TaskTypeRepository;

@Service("taskTypeService")
public class TaskTypeService {

    @Autowired
    private TaskTypeRepository repository; // 請確認 Repository 使用的是 TaskTypeVO

    /** 新增 */
    public TaskTypeVO addTaskType(TaskTypeVO vo) {
        return repository.save(vo);
    }

    /** 修改 */
    public TaskTypeVO updateTaskType(TaskTypeVO vo) {
        return repository.save(vo);
    }

    /** 刪除（by PK） */
    public void deleteTaskType(Integer taskTypeId) {
        if (repository.existsById(taskTypeId)) {
            repository.deleteById(taskTypeId);
        }
    }

    /** 取單筆 */
    public TaskTypeVO getOneTaskType(Integer taskTypeId) {
        Optional<TaskTypeVO> opt = repository.findById(taskTypeId);
        return opt.orElse(null);
    }

    /** 取全部 */
    public List<TaskTypeVO> getAll() {
        return repository.findAll();
    }

    /** 依名稱查單筆（若無回傳 null） */
    public TaskTypeVO getByTaskTypeName(String taskTypeName) {
        return repository.findByTaskTypeName(taskTypeName).orElse(null);
    }

    /** 名稱是否已存在（給新增/修改驗證用） */
    public boolean existsByTaskTypeName(String taskTypeName) {
        return repository.existsByTaskTypeName(taskTypeName);
    }
    
    
}
