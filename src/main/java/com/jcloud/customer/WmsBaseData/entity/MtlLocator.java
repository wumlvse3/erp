package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("mtl_locator")
@Data
public class MtlLocator extends Model<MtlLocator> {
    @TableField("org_id")
    private String orgId;
    
    @TableField("group_id")
    private String groupId;

    @TableId("locator_id")
    private String locatorId;

    @TableField("inventory_id")
    private String inventoryId;

    @TableField("locator_code")
    private String locatorCode;

    @TableField("locator_name")
    private String locatorName;

    @TableField("temp_flag")
    private Integer tempFlag;

    @TableField("max_num")
    private Double maxNum;

    @TableField("create_date")
    private Date createDate;

    @TableField("create_by")
    private String createBy;

    @TableField("update_date")
    private Date updateDate;

    @TableField("update_by")
    private String updateBy;

    @TableField("area_id")
    private String areaId;

    @TableField("k3_group_id")
    private String k3GroupId;

    @TableField("remote_id")
    private String remoteId;

    @TableField("electronic_locator_code")
    private String electronicLocatorCode;

    @Override
    public Serializable pkVal() {
        return this.locatorId;
    }

}
