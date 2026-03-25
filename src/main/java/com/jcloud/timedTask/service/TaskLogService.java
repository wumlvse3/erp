package com.jcloud.timedTask.service;
import com.jcloud.timedTask.entity.TaskLog;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 *
 */
public interface TaskLogService {
    // 添加一条日志
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    TaskLog add(TaskLog taskLog);
    // 根据id删除一条日志
    void delete(String taskLogId);
    // 清空某个日期之前的日志
    void clearBeforeDate(Date date);
}
