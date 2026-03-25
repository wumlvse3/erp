package com.jcloud.system.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (JdOrgRel)表实体类
 *
 * @author Bianxs
 * @since 2023-11-23 18:53:14
 */
@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("$tableInfo.comment")
public class JdOrgRel extends Model<JdOrgRel> {

    @ApiModelProperty("主键")
    @TableId("id")


    private String id;

    @ApiModelProperty("创建人ID")
    @TableField(value = "create_by", fill = FieldFill.INSERT)


    private String createBy;

    @ApiModelProperty("创建人")
    @TableField(value = "create_by_name", fill = FieldFill.INSERT)


    private String createByName;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)


    private Date createTime;

    @ApiModelProperty("更新人ID")
    @TableField(value = "update_by", fill = FieldFill.UPDATE)


    private String updateBy;

    @ApiModelProperty("更新人")
    @TableField(value = "update_by_name", fill = FieldFill.UPDATE)


    private String updateByName;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)


    private Date updateTime;

    @ApiModelProperty("企业组织")
    @TableField(value = "org_id")


    private String orgId;

    @ApiModelProperty("商城类型")
    @TableField(value = "market_type")


    private String marketType;

    @ApiModelProperty("采购账号")
    @TableField(value = "token_pin")


    private String tokenPin;

    @ApiModelProperty("采购账号密码")
    @TableField(value = "token_pin_pwd")


    private String tokenPinPwd;


    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

