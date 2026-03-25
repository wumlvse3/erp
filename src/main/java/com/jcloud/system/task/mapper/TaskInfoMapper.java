package com.jcloud.system.task.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.system.task.domain.TaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo>{

    TaskInfo findOneById(String taskInfoId);

    List<TaskInfo> list(TaskInfo taskInfo);

    TaskInfo findByOrgIdAndTaskCodeAndDeletedIsFalse(String org_id, String sheet_code);

    List<TaskInfo> needToHandle(@Param("org_id") String org_id, @Param("username") String username, @Param("task_status") List<String> task_status);

    Integer findTotal(@Param("org_id") String org_id, @Param("username") String username, @Param("task_status") List<String> task_status);

    @Update("update task_info set task_status='FINISHED' where sheet_id=#{sheetId}")
    Integer finishBySheetId(String sheetId);
}