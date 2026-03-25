package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("Mtl_period_transactions")
public class MtlPeriodTransactions extends Model<MtlPeriodTransactions> {

    @TableId("transaction_id")
    private String transaction_id;

    private String org_id;

    private String period_id;

    private String item_id;

    private String items_code;

    private String items_name;

    private String vendor_id;

    private String vendor_code;

    private String vendor_name;

    private String inventory_id;

    private String inventory_code;

    private String inventory_name;

    private Double period_init_qty;

    private Double period_in_qty;

    private Double period_out_qty;

    private Double period_end_qty;

    private String period_month;

    private String create_by;

    private Date create_date;

    private String update_by;

    private Date update_date;

    @Override
    public Serializable pkVal() {
        return this.transaction_id;
    }


}
