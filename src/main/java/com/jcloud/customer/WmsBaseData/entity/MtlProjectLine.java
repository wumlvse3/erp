package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@TableName("mtl_project_line")
@Data
public class MtlProjectLine extends Model<MtlProjectLine> {
    @TableField("org_id")
    private String orgId;

    @TableId("line_id")
    private String lineId;

    @TableField("project_id")
    private String projectId;

    @TableField("item_id")
    private String itemId;

    @TableField("items_code")
    private String itemsCode;

    @TableField("items_name")
    private String itemsName;

    @TableField("items_spec")
    private String itemsSpec;

    @TableField("uom_code")
    private String uomCode;

    @TableField("items_qty")
    private Double itemsQty;

    @TableField("do_way")
    private String doWay;

    @TableField("do_type")
    private String doType;

    @TableField("do_class")
    private String doClass;

    @TableField("vendor_id")
    private String vendorId;

    @TableField("vendor_code")
    private String vendorCode;

    @TableField("vendor_name")
    private String vendorName;

    @TableField("designated_brand")
    private String designatedBrand;

    @TableField("lot_number")
    private String lotNumber;

    @TableField("parent_code")
    private String parentCode;

    @TableField("parent_name")
    private String parentName;

    @TableField("parent_id")
    private String parentId;

    @TableField("parent_line_id")
    private String parentLineId;

    @TableField("machine_code")
    private String machineCode;

    @TableField("machine_name")
    private String machineName;

    @TableField("base_unit")
    private String baseUnit;

    @TableField("auxiliary_qty")
    private String auxiliaryQty;

    @TableField("create_date")
    private Date createDate;

    @TableField("created_by")
    private String createdBy;

    @TableField("update_date")
    private Date updateDate;

    @TableField("update_by")
    private String updateBy;

    @TableField("brand")
    private String brand;
}
