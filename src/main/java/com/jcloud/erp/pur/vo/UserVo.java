package com.jcloud.erp.pur.vo;

import lombok.Data;
@Data
public class UserVo {
    /**
     * 用户id
     * **/
    public String user_id;
    public String pur_org;
    public String org_power;

    public String getPur_org() {
        return pur_org;
    }

    public String getOrg_power() {
        return org_power;
    }

    public void setOrg_power(String org_power) {
        this.org_power = org_power;
    }

    public void setPur_org(String pur_org) {
        this.pur_org = pur_org;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
