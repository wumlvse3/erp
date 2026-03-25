package com.jcloud.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.util.Date;

/**
 * 邮件模板
 */
@Data
public class EmailModule extends Model<EmailModule> {
    private String id;
    private String orgId;
    private Integer createBy;
    private Date createTime;
    private Integer updateBy;
    private Date updateTime;
    /**
     * 模板内容
     */
    private String moduleContent;
    /**
     * 模板内容英文
     */
    private String moduleContentEng;
    /**
     * 模块编码
     */
    private String moduleCode;
    /**
     * 邮件主题前缀
     */
    private String subjectPrefix;

    private Integer enclosuresStatus;

    private Integer enclosuresFile;
}
