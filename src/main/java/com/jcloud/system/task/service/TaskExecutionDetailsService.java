package com.jcloud.system.task.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.task.domain.TaskExecutionDetails;
import com.jcloud.system.task.mapper.TaskExecutionDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskExecutionDetailsService {

    @Autowired
    private TaskExecutionDetailsMapper mapper;

    public TaskExecutionDetails getObj(String taskExecutionDetailsId) {
        TaskExecutionDetails taskExecutionDetails=mapper.selectById(taskExecutionDetailsId);
        if (taskExecutionDetails==null) {
            throw new RuntimeException("未找到任务");
        }
        return taskExecutionDetails;
    }

    public List<TaskExecutionDetails> getObjs(Wrapper<TaskExecutionDetails> param) {
        return mapper.selectList(param);
    }

    public TaskExecutionDetails modifyObj(String id, TaskExecutionDetails factory) {
        TaskExecutionDetails taskExecutionDetails=mapper.selectById(id);
        if (taskExecutionDetails==null){
            throw new RuntimeException("未找到任务");
        }
        factory.setId(id);
        factory.setCreate_by(taskExecutionDetails.getCreate_by());
        mapper.updateById(factory);
        return factory;
    }

    public TaskExecutionDetails addObj(TaskExecutionDetails taskExecutionDetails) {
        mapper.insert(taskExecutionDetails);
        return taskExecutionDetails ;
    }

    public TaskExecutionDetails deleteObj(String id) {
        TaskExecutionDetails taskExecutionDetails=mapper.selectById(id);
        if (taskExecutionDetails==null){
            throw new RuntimeException("未找到任务");
        }
        mapper.deleteById(id);
        return taskExecutionDetails;
    }

    public List<TaskExecutionDetails> getObjsByTaskCode(String sheetCode){
        return mapper.findByOrgIdAndTaskCodeAndDeletedIsFalseOrderByCreateTimeDesc(ShiroUtils.getOrgId(),sheetCode);
    }
}
