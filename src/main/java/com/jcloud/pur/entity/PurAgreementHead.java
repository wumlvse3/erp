package com.jcloud.pur.entity;

import lombok.Data;

@Data
public class PurAgreementHead {
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

    private String create_qty;

    public String getCreate_qty() {
        return create_qty;
    }

    public void setCreate_qty(String create_qty) {
        this.create_qty = create_qty;
    }

    private String uk_order_no;

    public String getUk_order_no() {
        return uk_order_no;
    }

    public void setUk_order_no(String uk_order_no) {
        this.uk_order_no = uk_order_no;
    }

    private String uk_agreement_uuid;

    public String getUk_agreement_uuid() {
        return uk_agreement_uuid;
    }

    public void setUk_agreement_uuid(String uk_agreement_uuid) {
        this.uk_agreement_uuid = uk_agreement_uuid;
    }
}
