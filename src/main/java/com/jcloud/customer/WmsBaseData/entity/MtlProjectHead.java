package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@TableName("mtl_project_head")
@Data
public class MtlProjectHead extends Model<MtlProjectHead> {
    @TableField("org_id")
    private String orgId;

    @TableId("project_id")
    private String projectId;

    @TableField("project_code")
    private String projectCode;

    @TableField("project_name")
    private String projectName;

    @TableField("project_desc")
    private String projectDesc;

    @TableField("project_manager")
    private String projectManager;

    @TableField("project_curement")
    private String projectCurement;

    @TableField("sheet_code")
    private String sheetCode;

    @TableField("project_address")
    private String projectAddress;

    @TableField("project_date")
    private Date projectDate;

    @TableField("curement_date")
    private Date curementDate;

    @TableField("concat_phone")
    private String concatPhone;

    @TableField("create_date")
    private Date createDate;

    @TableField("created_by")
    private String createdBy;

    @TableField("update_date")
    private Date updateDate;

    @TableField("update_by")
    private String updateBy;

    @TableField("items_id")
    private String itemsId;

    @TableField("items_code")
    private String itemsCode;

    @TableField("items_name")
    private String itemsName;
}
