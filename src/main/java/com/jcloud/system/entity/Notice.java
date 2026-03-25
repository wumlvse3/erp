package com.jcloud.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * 通知公告
 */

@Data
public class Notice extends Model<Notice> {
    private String id;
    private String org_id;
    @TableField(value = "create_by",fill = FieldFill.INSERT)
    private String create_by;
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date create_time;
    @TableField(value = "update_by",fill =  FieldFill.UPDATE)
    private String update_by;
    @TableField(value = "update_time",fill =  FieldFill.UPDATE)
    private Date update_time;
    private String title;//标题
    private String content;//内容
    private String type;//类型
    private String receiver_range;//接收范围
    private String allow_reply;
    private Integer down_count;
    private String level;
    private Integer read_count;
    private Integer reply_count;
    private Integer send_mail;
    private Date send_time;
    private Integer sort_no;
    private String status;
    private Integer up_count;
    private String user_id;
    private String notice_file;//通知附件
    private String notice_receiver;//接收人员
}
