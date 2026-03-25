package com.jcloud.customer.WmsBaseData.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MtlProjectLineView {
    private String org_id;

    private String id;

    private String items_code;

    private String items_name;

    private String parent_code;

    private String parent_id;

    private String project_id;

}
