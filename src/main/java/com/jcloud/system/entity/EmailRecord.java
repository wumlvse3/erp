package com.jcloud.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jcloud.system.enums.EmailStatusEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 邮件内容
 */
@Data
public class EmailRecord extends Model<EmailRecord> {
    private Integer id;
    private String orgId;
    private String createBy;
    private Date createTime;

    private String subject;//主题

    private String content;//内容

    private EmailStatusEnum status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date sendTime;

    private String sendMethod;//发送方式 （不带附件--noEnclosure  带附件--enclosure）

    private String templateCode;//模板编码

    private String templateLanguage="zh";//模板语言

    private List<EmailRecordParam> param;//模板内容

    private List<EmailRecordReceiver> receivers;

    private List<EmailRecordEnclosure> emailRecordEnclosures;//邮件附件

}
