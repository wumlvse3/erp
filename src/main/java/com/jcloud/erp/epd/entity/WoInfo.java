package com.jcloud.erp.epd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 工作令优先级表
 */
@Data
@TableName("WO_INFO")
public class WoInfo extends Model<WoInfo> {

    /** 公司号 */
    @TableField(value = "COMPANY_CODE")
    private String company_code;

    /** 工作令号 */
    @TableField(value = "WORK_NO")
    private String work_no;

    /** 工作令描述 */
    @TableField(value = "WORK_DESC")
    private String work_desc;

    /** 优先级日期 */
    @TableField(value = "PRIORITY_DATE")
    private String priority_date;

    /** 优先等级，相同优先级日期排序 */
    @TableField(value = "PRIORITY_LEVEL")
    private Integer priority_level;

    /** 优先级序号 */
    @TableField(value = "PRIORITY_NO")
    private Integer priority_no;

    /** 领料状态 */
    @TableField(value = "PICK_STATUS")
    private String pick_status;

    /** 是否参与排序，默认：Y */
    @TableField(value = "VALID_FLAG")
    private String valid_flag;

    /** 维护人编码 */
    @TableField(value = "RECORD_CODE")
    private String record_code;

    /** 维护人姓名 */
    @TableField(value = "RECORD_NAME")
    private String record_name;

    /** 维护日期 */
    @TableField(value = "RECORD_DATE")
    private String record_date;

    @Override
    public Serializable pkVal() {
        return this.work_no;
    }
}
