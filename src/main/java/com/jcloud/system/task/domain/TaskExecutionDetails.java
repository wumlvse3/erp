package com.jcloud.system.task.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.jcloud.system.entity.EmailModule;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * 任务执行明细
 * **/
@Data
public class TaskExecutionDetails extends Model<TaskExecutionDetails> {
    private String id;
    private String org_id;
    private String create_by;
    private Date create_time;

    /**
     * 任务编号
     * **/
    @Column(length = 50)
    private String  sheet_code;
    /**
     * 处理时间
     * **/
    private Date  process_time;
    /**
     * 处理人ID
     * **/
    @Column(length = 50)
    private String  process_person_id;
    /**
     * 处理人姓名
     * **/
    @Column(length = 100)
    private String process_person_name;
    /**
     * 处理意见
     * **/
    @Column(length = 1000)
    private String  process_remark;
    /**
     * 处理类型
     * **/
    @Column(length = 50)
    private String  process_type;
}
