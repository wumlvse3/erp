package com.jcloud.timedTask.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 * 执行日志实体类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@TableName("interface_task_log")
public class TaskLog extends Model<TaskLog> {

    @TableId(value = "id")
    private String id;

    // 任务执行时间
    @TableField(value = "execute_time")
    @JsonFormat(timezone = "GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    private Date executeTime;
    // 结束时间
    @TableField(value = "execute_end_time")
    @JsonFormat(timezone = "GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    private Date execute_end_time;
    //接口耗时
    @TableField(value = "interface_time_span")
    private Long interfaceTimeSpan;
    //接口名称
    @TableField(value = "interface_name")
    private String interfaceName;
    // 接口组名称
    @TableField(value = "interface_group")
    private String interfaceGroup;

    @TableField(value = "param")
    private String param;
    // 接口 id
    @TableField(value = "interface_id")
    private String interfaceId;
    // 接口 id
    @TableField(value = "interface_code")
    private String interfaceCode;
    // 执行状态
    @TableField(value = "execute_status")
    private String executeStatus;
    // 回执请求执行状态
    @TableField(value = "reply_execute_status")
    private String replyExecuteStatus;
    // 额为信息 （预留）
    @TableField(value = "extra")
    private String extra;
    // 执行结果
    @TableField(value = "execute_result")
    private String execute_result;
    // 回执执行结果
    @TableField(value = "reply_execute_result")
    private String reply_execute_result;
    // 创建时间
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date create_time;
    // 更新时间
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    private Date update_time;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
