package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("mtl_onhand_details")
public class MtlOnhandDetails extends Model<MtlOnhandDetails> {

    @TableId(value = "detail_id")
    private String detail_id;
    @TableField(value = "org_id")
    private String org_id;
    @TableField(value = "group_id")
    private String group_id;
    @TableField(value = "status")
    private Integer status;


    @TableField(value = "item_qty")
    private Double item_qty;
    @TableField(value = "uom_code")
    private String uom_code;
    @TableField(value = "lot_number")
    private String lot_number;
    @TableField(value = "serial_number")
    private String serial_number;


    @TableField(value = "vendor_id")
    private String vendor_id;
    @TableField(value = "vendor_code")
    private String vendor_code;
    @TableField(value = "vendor_name")
    private String vendor_name;
    @TableField(value = "locator_id")
    private String locator_id;
    @TableField(value = "inventory_id")
    private String inventory_id;


    @TableField(value = "item_id")
    private String item_id;
    @TableField(value = "instorage_date")
    private Date instorage_date;
    @TableField(value = "valid_date")
    private Date valid_date;
    @TableField(value = "vmi_flag")
    private Integer vmi_flag;



    @TableField(value = "create_date",fill = FieldFill.INSERT)
    private Date create_date;
    @TableField(value = "create_by",fill = FieldFill.INSERT)
    private String create_by;
    @TableField(value = "update_date",fill = FieldFill.UPDATE)
    private Date update_date;
    @TableField(value = "update_by",fill = FieldFill.UPDATE)
    private String update_by;
    @TableField(value = "customer_id")
    private String customer_id;
    @TableField(value = "vendor_lot")
    private String vendor_lot;

    private String items_code;

    private String items_name;

    private String inventory_code;

    private String inventory_name;

    private String locator_code;

    @TableField(value = "project_id")
    private String project_id;

    @TableField(value = "project_code")
    private String project_code;

    @Override
    public Serializable pkVal() {
        return this.detail_id;
    }
}
