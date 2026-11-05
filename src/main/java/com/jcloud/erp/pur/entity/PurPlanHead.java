package com.jcloud.erp.pur.entity;

import lombok.Data;

@Data
public class PurPlanHead {
    /**
     * 用户id
     * **/
    public String uu_id;

    public String getUu_id() {
        return uu_id;
    }

    public void setUu_id(String uu_id) {
        this.uu_id = uu_id;
    }

    private String bill_status;

    public String getBill_status() {
        return bill_status;
    }

    public void setBill_status(String bill_status) {
        this.bill_status = bill_status;
    }

    private String approver_opinion;

    public String getApprover_opinion() {
        return approver_opinion;
    }

    public void setApprover_opinion(String approver_opinion) {
        this.approver_opinion = approver_opinion;
    }
    private String line_opinion;

    public String getLine_opinion() {
        return line_opinion;
    }
    public void setLine_opinion(String line_opinion) {
        this.line_opinion = line_opinion;
    }

    private String uk_plan_uuid;
    public String getUk_plan_uuid() {
        return uk_plan_uuid;
    }
    public void setUk_plan_uuid(String uk_plan_uuid) {
        this.uk_plan_uuid = uk_plan_uuid;
    }
    private String create_qty;

    public String getCreate_qty() {
        return create_qty;
    }

    public void setCreate_qty(String create_qty) {
        this.create_qty = create_qty;
    }

    public String getUk_order_no() {
        return uk_order_no;
    }

    public void setUk_order_no(String uk_order_no) {
        this.uk_order_no = uk_order_no;
    }

    private String  uk_order_no;
}
