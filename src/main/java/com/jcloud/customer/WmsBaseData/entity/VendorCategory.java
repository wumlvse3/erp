package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Transient;

/**
 供货品类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vendor_category")
@ApiModel(value="VendorCategory对象", description="")
public class VendorCategory extends Model<VendorCategory> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty(value = "创建人ID")
    @TableField(value = "create_by",fill = FieldFill.INSERT)
    private String create_by;

    @ApiModelProperty(value = "创建人名称")
    @TableField(value = "create_by_name",fill =  FieldFill.INSERT)
    private String create_by_name;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date create_time;

    @ApiModelProperty(value = "更新人ID")
    @TableField(value = "update_by",fill =  FieldFill.UPDATE)
    private String update_by;

    @ApiModelProperty(value = "更新人名称")
    @TableField(value = "update_by_name",fill =  FieldFill.UPDATE)
    private String update_by_name;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time",fill =  FieldFill.UPDATE)
    private Date update_time;

    @ApiModelProperty(value = "企业组织")
    private String org_id;

    @ApiModelProperty(value = "采购组织ID")
    private String purchase_org_id;

    @ApiModelProperty(value = "供应商ID")
    private String vendor_id;

    @ApiModelProperty(value = "物料品类")
    private String category_id;
    @ApiModelProperty(value = "物料品类显示")
    private String category_desc;
}
