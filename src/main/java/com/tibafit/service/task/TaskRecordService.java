package com.tibafit.service.task;

import java.util.List;

import com.tibafit.model.task.TaskRecordVO;

public interface TaskRecordService {

    /** 新增 */
    TaskRecordVO addTaskRecord(TaskRecordVO vo);

    /** 修改（全量覆蓋） */
    TaskRecordVO updateTaskRecord(TaskRecordVO vo);

    /** 刪除（by PK） */
    void deleteTaskRecord(Integer taskRecordId);

    /** 取單筆（by PK），若不存在回傳 null */
    TaskRecordVO getOneTaskRecord(Integer taskRecordId);

    /** 取全部 */
    List<TaskRecordVO> getAll();
}
