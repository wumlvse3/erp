package com.jcloud.system.task.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 任务基本信息
 * **/
@Data
public class TaskReqVo {
    /**
     * 任务标题
     * **/
    @NotNull(message = "任务标题不能为空！")
    private String  taskTitle;
    /**
     * 任务编号
     * **/
    private String  taskCode;
    /**
     * 任务描述
     * **/
    private String  taskDesc;
    /**
     * 需求开始时间
     * **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date startTime;
    /**
     * 需求结束时间
     * **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date  endTime;
    /**
     * 处理人ID
     * **/
    private Long  processerId;
    /**
     * 处理人姓名
     * **/
    private String  processerName;
    /**
     * 地点代码
     * **/
    private String  areaCode;
    /**
     * 地点名称
     * **/
    private String  areaName;
    /**
     * 实际完成时间
     * **/
    private Date  finishTime;
    /**
     * 审批标志
     * **/
    private Integer  auditFlag;
    /**
     * 紧急程度
     * **/
    private String  urgentLevel;
    /**
     * 任务级别
     * **/
    private String  taskLevel;
    /**
     * 重要程度
     * **/
    private String  taskImportanceLevel;
    /**
     * 难易程度
     * **/
    private String  taskDifficultyLevel;
    /**
     * 父级任务
     * **/
    private String  parentCode;
    /**
     * 任务状态
     * **/
    private String  taskStatus="CREATED";//默认为创建


    /**
     * 任务计划工时
     * **/
    private Double  taskPlanHour;
    /**
     * 任务实际工时
    private Double  taskRealHour;
    /**
     * 任务分类
     * **/

    private String  taskType;
    /**
     * 回调URL
     * **/
    @Column(length = 20)
    private String  taskCallbackUrl;
    /**
     * 回调单据号
     * **/
    @Column(length = 20)
    private String  taskCallbackCode;
    /**
     * 是否过期
     * **/
    @Column(length = 1)
    private Integer  expireStatus;
    /**
     * 文件JSON字符串
     * **/
    @Column(length = 500)
    private String fileInfo;
    /**
     * 提醒方式
     * **/
    @Column(length = 200)
    private String remindType;
    /**
     * 参与人
     * **/
    @Column(name = "participant", columnDefinition = "TEXT")
    private String participant;
    /**
     * 消息ID
     * **/
    @Column(length = 200)
    private Long messageId;
    /**
     * 临时字段 设置创建人姓名
     */
    @Transient
    private String createdBy;
}
