package com.jcloud.system.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.system.task.domain.TaskExecutionDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskExecutionDetailsMapper extends BaseMapper<TaskExecutionDetails> {
    List<TaskExecutionDetails> findByOrgIdAndTaskCodeAndDeletedIsFalseOrderByCreateTimeDesc(String orgId, String sheetCode);
}
