package com.jcloud.system.task.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jcloud.system.entity.EmailModule;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.util.Date;

/**
 * 任务基本信息
 * **/
@Data
public class TaskInfo extends Model<EmailModule> {
    private String id;
    private String org_id;
    @TableField(value = "create_by",fill = FieldFill.INSERT)
    private String create_by;
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date create_time;

    /**
     * 任务标题
     * **/
    @Column(length = 50)
    private String  task_title;
    /**
     * 任务编号
     * **/
    @Column(length = 50,unique = true)
    private String  sheet_code;
    /**
     * 任务描述
     * **/
    @Column(length = 400)
    private String  task_desc;
    /**
     * 需求开始时间
     * **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date start_time;
    /**
     * 需求结束时间
     * **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date  end_time;
    /**
     * 处理人账户（username）
     * **/
    @Column(length = 50)
    private String  processer_id;
    /**
     * 处理人姓名(name)
     * **/
    @Column(length = 20)
    private String  processer_name;
    /**
     * 地点代码
     * **/
    @Column(length = 20)
    private String  area_code;
    /**
     * 地点名称
     * **/
    @Column(length = 50)
    private String  area_name;
    /**
     * 实际完成时间
     * **/
    private Date  finish_time;
    /**
     * 审批标志
     * **/
    @Column(length = 1)
    private Integer  audit_flag;
    /**
     * 紧急程度
     * **/
    @Column(length = 100)
    private String  urgent_level;
    /**
     * 任务级别
     * **/
    @Column(length = 20)
    private String  task_level;
    /**
     * 任务评价
     * **/
    @Column(length = 20)
    private String  task_evaluation;
    /**
     * 重要程度
     * **/
    @Column(length = 20)
    private String  task_importance_level;
    /**
     * 难易程度
     * **/
    @Column(length = 20)
    private String  task_difficulty_level;
    /**
     * 父级任务
     * **/
    @Column(length = 20)
    private String  parent_code;
    /**
     * 任务父子级别
     * **/

    private int  task_sort=1;
    /**
     * 任务状态
     * **/
    private String  task_status="CREATED";//默认为创建


    /**
     * 任务计划工时
     * **/
    @Column( columnDefinition = "Double(10,2)")
    private Double  task_plan_hour;
    /**
     * 任务实际工时
     * **/
    @Column( columnDefinition = "Double(10,2)")
    private Double  task_real_hour;
    /**
     * 任务分类
     * **/
    @Column(length = 20)
    private String  task_type;
    /**
     * 回调URL
     * **/
    @Column(length = 20)
    private String  task_callback_url;
    /**
     * 回调单据号
     * **/
    @Column(length = 20)
    private String  task_callback_code;
    /**
     * 是否过期
     * **/
    @Column(length = 1)
    private Integer  expire_status;
    /**
     * 文件JSON字符串
     * **/
    @Column(length = 500)
    private String file_info;
    /**
     * 提醒方式
     * **/
    @Column(length = 200)
    private String remind_type;
    /**
     * 参与人
     * **/
    @Column(name = "participant", columnDefinition = "TEXT")
    private String participant;

    /**
     * 编码规则id
     */
    private String sheet_type_id="50";
    /**
     * 关键字
     */
    private String keywords;

    private String sheet_id;

    /**
     * 待办或通知  task/notice
     * **/
    private String taskClass;

    /**
     * 业务类型
     * **/
    private String businessType;
}
