package com.jcloud.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.jcloud.system.enums.EmailSendTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * 模板参数
 */
@Data
public class EmailRecordParam extends Model<EmailRecordParam> {
    private Integer id;

    private Integer recordId;

    private String paramKey;

    private String paramValue;
}
