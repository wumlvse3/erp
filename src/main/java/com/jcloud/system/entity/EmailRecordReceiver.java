package com.jcloud.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.jcloud.system.enums.EmailSendTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * 邮件接收人
 */
@Data
public class EmailRecordReceiver extends Model<EmailRecordReceiver> {
    private Integer id;
    private String orgId;
    private String createBy;
    private Date createTime;

    private Integer recordId;

    private String email;

    private String name;

    private EmailSendTypeEnum sendType;
}
