package com.jcloud.erp.pur.entity;

import lombok.Data;

@Data
public class PurShipHead {
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

    private  String approver_opinion;

    public String getApprover_opinion() {
        return approver_opinion;
    }

    public void setApprover_opinion(String approver_opinion) {
        this.approver_opinion = approver_opinion;
    }

    private String uk_bill_uuid;

    public String getUk_bill_uuid() {
        return uk_bill_uuid;
    }

    public void setUk_bill_uuid(String uk_bill_uuid) {
        this.uk_bill_uuid = uk_bill_uuid;
    }
}
