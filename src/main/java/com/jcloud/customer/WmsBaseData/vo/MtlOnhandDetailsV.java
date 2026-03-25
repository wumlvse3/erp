package com.jcloud.customer.WmsBaseData.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mtl_onhand_details_v")
public class MtlOnhandDetailsV extends Model<MtlOnhandDetailsV> {
    @TableField(value = "items_code")
    private String items_code;
    @TableField(value = "items_name")
    private String items_name;
    @TableField(value = "items_unit")
    private String items_unit;
    @TableField(value = "vendor_code")
    private String vendor_code;
    @TableField(value = "vendor_name")
    private String vendor_name;
    @TableField(value = "inventory_code")
    private String inventory_code;
    @TableField(value = "inventory_name")
    private String inventory_name;
    @TableField(value = "locator_code")
    private String locator_code;
    @TableField(value = "item_qty")
    private Double item_qty;
    @TableField(value = "lot_number")
    private String lot_number;
    @TableField(value = "serial_number")
    private String serial_number;
    @TableField(value = "instorage_date")
    private Date instorage_date;
    @TableField(value = "item_id")
    private String item_id;
    @TableField(value = "vendor_id")
    private String vendor_id;
    @TableField(value = "inventory_id")
    private String inventory_id;
    @TableField(value = "locator_id")
    private String locator_id;
    @TableField(value = "status")
    private String status;
    @TableId(value = "detail_id")
    private String detail_id;
    @TableField(value = "customer_id")
    private String customer_id;
    @TableField(value = "customer_code")
    private String customer_code;
    @TableField(value = "customer_name")
    private String customer_name;
    @TableField(value = "base_unit")
    private String base_unit;
    @TableField(value = "base_qty")
    private String base_qty;
    @TableField(value = "valid_date")
    private String Date;
    @TableField(value = "vendor_lot")
    private String vendor_lot;
}
