package com.jcloud.pur.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PurApplyHead {
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
    private String uk_apply_uuid;
    public String getUk_apply_uuid() {
        return uk_apply_uuid;
    }
    public void setUk_apply_uuid(String uk_apply_uuid) {
        this.uk_apply_uuid = uk_apply_uuid;
    }
}
