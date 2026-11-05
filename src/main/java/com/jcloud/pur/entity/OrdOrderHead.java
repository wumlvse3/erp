package com.jcloud.pur.entity;

import lombok.Data;

@Data
public class OrdOrderHead {
    public String uu_id;

    public String getUu_id() {
        return uu_id;
    }

    public void setUu_id(String uu_id) {
        this.uu_id = uu_id;
    }

    public  String uk_order_uuid;

    public String getUk_order_uuid() {
        return uk_order_uuid;
    }

    public void setUk_order_uuid(String uk_order_uuid) {
        this.uk_order_uuid = uk_order_uuid;
    }
    public  String pur_org;

    public String getPur_org() {
        return pur_org;
    }

    public void setPur_org(String pur_org) {
        this.pur_org = pur_org;
    }
}
