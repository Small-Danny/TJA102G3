package com.tibafit.model.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TaskRecordService {

    private final TaskRecordRepository recordRepo;
    private final TaskRepository taskRepo;
    private final TaskRecordStatusRepository statusRepo;
//    private final UserRepository userRepo; // 你的 User repository

    public TaskRecordService(TaskRecordRepository recordRepo,
                             TaskRepository taskRepo,
                             TaskRecordStatusRepository statusRepo/*,
                             UserRepository userRepo*/) {
        this.recordRepo = recordRepo;
        this.taskRepo = taskRepo;
        this.statusRepo = statusRepo;
//        this.userRepo = userRepo;
    }

    @Transactional
    public TaskRecordVO create(Integer userId, Integer taskId, Integer taskRecordStatus,
                               LocalDateTime start, LocalDateTime end) {

        TaskRecordVO r = new TaskRecordVO();
//        r.setUser(userRepo.findById(userId).orElseThrow());
        r.setTaskVO(taskRepo.findById(taskId).orElseThrow());
        r.setTaskRecordStatusVO(statusRepo.findById(taskRecordStatus).orElseThrow());
        r.setUserStartTime(start);
        r.setUserEndTime(end);
        return recordRepo.save(r);
    }

    @Transactional
    public TaskRecordVO update(Integer recordId, Integer statusCode,
                               LocalDateTime start, LocalDateTime end) {
        TaskRecordVO r = recordRepo.findById(recordId).orElseThrow();
        if (statusCode != null) {
            r.setTaskRecordStatusVO(statusRepo.findById(statusCode).orElseThrow());
        }
        if (start != null) r.setUserStartTime(start);
        if (end   != null) r.setUserEndTime(end);
        return recordRepo.save(r);
    }

    public TaskRecordVO getOne(Integer id) { return recordRepo.findById(id).orElse(null); }
    public void delete(Integer id) { if (recordRepo.existsById(id)) recordRepo.deleteById(id); }
}
