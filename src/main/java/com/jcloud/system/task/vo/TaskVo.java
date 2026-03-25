package com.jcloud.system.task.vo;

import lombok.Data;

/**
 * 任务处理序列化对象
 *
 * @ClassName: TaskVo
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2022-07-27 16:47
 **/
@Data
public class TaskVo {
    /**
     * 任务编码
     * **/
    public String sheet_code;
    /**
     * 备注信息
     * **/
    public String remark;
    /**
     * 执行人账户
     * **/
    public String username;
    /**
     * 执行人姓名
     * **/
    public String name;

}
