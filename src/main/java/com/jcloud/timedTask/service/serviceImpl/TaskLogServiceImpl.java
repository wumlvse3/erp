package com.jcloud.timedTask.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcloud.timedTask.entity.TaskLog;
import com.jcloud.timedTask.mapper.TaskLogMapper;
import com.jcloud.timedTask.service.TaskLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 */
@Service
public class TaskLogServiceImpl implements TaskLogService {

    private final TaskLogMapper taskLogMapper;

    @Autowired
    public TaskLogServiceImpl(TaskLogMapper taskLogMapper) {
        this.taskLogMapper = taskLogMapper;
    }

    @Override
    public TaskLog add(TaskLog taskLog) {
        taskLogMapper.insert(taskLog);
        return taskLog;
    }

    @Override
    public void delete(String taskLogId) {
        taskLogMapper.deleteById(taskLogId);
    }

    @Override
    public void clearBeforeDate(Date date) {

        taskLogMapper.delete(new QueryWrapper<TaskLog>().lt("createTime",date));
    }

}
