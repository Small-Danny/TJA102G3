package com.tibafit.model.task;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("taskService") // ← 原本是 empService，請一併改掉
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    // C
    public TaskVO addTask(TaskVO taskVO) {
    	taskVO.setTaskId(null);           // 保險起見，交給 DB 自動編號
        return repository.save(taskVO);
    }

    // U
    public TaskVO updateTask(TaskVO taskVO) {
        // 你也可以加上存在性檢查：if (!repository.existsById(vo.getTaskId())) throw ...
        return repository.save(taskVO);
    }

    // D
    public void deleteTask(Integer taskId) {
        if (repository.existsById(taskId)) {
            // 若你有自訂 deleteByTaskId，可改這行；否則直接用 deleteById
            repository.deleteById(taskId);
        }
    }

    // R-單筆
    @Transactional(readOnly = true)
    public TaskVO getOneTask(Integer taskId) {
        Optional<TaskVO> optional = repository.findById(taskId);
        return optional.orElse(null);
    }

    // R-全部
    @Transactional(readOnly = true)
    public List<TaskVO> getAll() {
        return repository.findAll();
    }
}
