package com.jcloud.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * 邮件附件
 */
@Data
public class EmailRecordEnclosure extends Model<EmailRecordEnclosure> {
    private String id;
    private String orgId;
    private String createBy;
    private Date createTime;

    private Integer recordId;

    private Long enclosureId;//附件id

    private String enclosureName;//附件名称

    private String enclosurePath;//附件地址
}
