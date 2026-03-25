package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("mtl_transactions")
@Data
public class MtlTransactions extends Model<MtlTransactions> {
    @TableField("org_id")
    private String orgId;

    @TableField("group_id")
    private String groupId;

    @TableId("transaction_id")
    private String transactionId;

    @TableField("bill_code")
    private String billCode;

    @TableField("transaction_code")
    private String transactionCode;

    @TableField("transaction_date")
    private Date transactionDate;

    @TableField("sheet_type_id")
    private String sheetTypeId;

    @TableField("type_id")
    private String typeId;

    @TableField("item_id")
    private String itemId;

    @TableField("inventory_id")
    private String inventoryId;

    @TableField("locator_id")
    private String locatorId;

    @TableField("period_id")
    private String periodId;

    @TableField("uom_code")
    private String uomCode;

    @TableField("transaction_qty")
    private Double transactionQty;

    @TableField("lot_number")
    private String lotNumber;

    @TableField("serial_number")
    private String serialNumber;

    @TableField("serial_id")
    private String serialId;

    @TableField("vmi_flag")
    private String vmiFlag;

    @TableField("vendor_id")
    private String vendorId;

    @TableField("erp_code")
    private String erpCode;

    @TableField("create_date")
    private Date createDate;

    @TableField("create_by")
    private String createBy;

    @TableField("update_date")
    private Date updateDate;

    @TableField("update_by")
    private String updateBy;

    @TableField("line_id")
    private String lineId;

    @TableField("pro_lot")
    private String proLot;

    @TableField("customer_id")
    private String customerId;

    @TableField("status")
    private Integer status;

    @TableField("header_id")
    private String headerId;

    @TableField("items_code")
    private String itemsCode;

    @TableField("items_name")
    private String itemsName;

    @TableField("vendor_code")
    private String vendorCode;

    @TableField("vendor_name")
    private String vendorName;

    @TableField("inventory_name")
    private String inventoryName;

    @TableField("locator_code")
    private String locatorCode;

    @TableField("sheet_title")
    private String sheetTitle;

    @TableField("inventory_code")
    private String inventoryCode;

    @TableField(value = "project_id")
    private String projectId;

    @TableField(value = "project_code")
    private String projectCode;

    @Override
    public Serializable pkVal() {
        return this.transactionId;
    }

}
