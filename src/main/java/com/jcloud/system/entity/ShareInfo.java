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
 * (ShareInfo)公共下载
 *
 * @author Bianxs
 * @since 2023-09-12 14:03:00
 */
@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("$tableInfo.comment")
public class ShareInfo extends Model<ShareInfo> {

         @ApiModelProperty("ID")
             @TableId("id")
        
    
    
    
     
    private String id;

         @ApiModelProperty("组织")
             @TableField(value = "org_id")
        
    
    
    
     
    private String orgId;

         @ApiModelProperty("创建人")
             @TableField(value = "create_by",fill = FieldFill.INSERT)
        
    
    
    
     
    private String createBy;

         @ApiModelProperty("创建时间")
             @TableField(value = "create_time",fill = FieldFill.INSERT)
        
    
    
    
     
    private Date createTime;

         @ApiModelProperty("修改人")
             @TableField(value = "update_by",fill = FieldFill.UPDATE)
        
    
    
    
     
    private String updateBy;

         @ApiModelProperty("修改时间")
             @TableField(value = "update_time",fill = FieldFill.UPDATE)
        
    
    
    
     
    private Date updateTime;

         @ApiModelProperty("阅读次数")
             @TableField(value = "download_count")
        
    
    
    
     
    private String downloadCount;

         @ApiModelProperty("通知等级")
             @TableField(value = "level")
        
    
    
    
     
    private String shareLevel;

         @ApiModelProperty("接收范围")
             @TableField(value = "receiver_range")
        
    
    
    
     
    private String receiverRange;

         @ApiModelProperty("邮件发送标示")
             @TableField(value = "send_mail")
        
    
    
    
     
    private String sendMail;

         @ApiModelProperty("发送时间")
             @TableField(value = "send_time")
        
    
    
    
     
    private Date sendTime;

         @ApiModelProperty("排序编号")
             @TableField(value = "sort_no")
        
    
    
    
     
    private Long sortNo;

         @ApiModelProperty("通知状态")
             @TableField(value = "status")
        
    
    
    
     
    private String status;

         @ApiModelProperty("通知标题")
             @TableField(value = "title")
        
    
    
    
     
    private String shareTitle;

         @ApiModelProperty("通知类型")
             @TableField(value = "type")
        
    
    
    
     
    private String shareType;

         @ApiModelProperty("发布人")
             @TableField(value = "user_id")
        
    
    
    
     
    private Long userId;

         @ApiModelProperty("文件")
             @TableField(value = "share_file")
        
    
    
    
     
    private String shareFile;



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

