package com.jcloud.customer.WmsBaseData.entity;

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
 * (StorageLocation)表实体类
 *
 * @author Bianxs
 * @since 2023-07-27 17:24:01
 */
@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("$tableInfo.comment")
public class StorageLocation extends Model<StorageLocation> {

             @TableId("id")
        
    
    
    
     
    private String id;

             @TableField(value = "org_id")
        
    
    
    
     
    private String orgId;

         @ApiModelProperty("新增用户")
             @TableField(value = "create_by",fill = FieldFill.INSERT)
        
    
    
    
     
    private String createBy;

         @ApiModelProperty("新增时间")
             @TableField(value = "create_time",fill = FieldFill.INSERT)
        
    
    
    
     
    private Date createTime;

         @ApiModelProperty("修改用户")
             @TableField(value = "update_by",fill = FieldFill.UPDATE)
        
    
    
    
     
    private String updateBy;

         @ApiModelProperty("修改时间")
             @TableField(value = "update_time",fill = FieldFill.UPDATE)
        
    
    
    
     
    private Date updateTime;

         @ApiModelProperty("库存地点")
             @TableField(value = "location")
        
    
    
    
     
    private String location;

         @ApiModelProperty("收货地点")
             @TableField(value = "receive_address")
        
    
    
    
     
    private String receiveAddress;

         @ApiModelProperty("收货联系人")
             @TableField(value = "receive_contact")
        
    
    
    
     
    private String receiveContact;

         @ApiModelProperty("收货联系人电话")
             @TableField(value = "receive_phone")
        
    
    
    
     
    private String receivePhone;

         @ApiModelProperty("收货联系人邮件")
             @TableField(value = "receive_mail")
        
    
    
    
     
    private String receiveMail;



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

