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
 * (SysFile)表实体类
 *
 * @author Bianxs
 * @since 2024-02-19 10:36:36
 */
@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("$tableInfo.comment")
public class SysFile extends Model<SysFile> {

         @ApiModelProperty("主键")
             @TableId("id")
        
    
    
    
     
    private Integer id;

         @ApiModelProperty("文件（0=图片，1=文档，2=视频，3=音频，4=其他）")
             @TableField(value = "type")
        
    
    
    
     
    private Integer type;

         @ApiModelProperty("文件地址")
             @TableField(value = "url")
        
    
    
    
     
    private String url;

         @ApiModelProperty("创建时间")
             @TableField(value = "create_date")
        
    
    
    
     
    private Date createDate;

         @ApiModelProperty("创建人")
             @TableField(value = "create_by",fill = FieldFill.INSERT)
        
    
    
    
     
    private String createBy;

         @ApiModelProperty("对于功能的编号（表）")
             @TableField(value = "type_code")
        
    
    
    
     
    private String typeCode;

         @ApiModelProperty("对于功能的标识值（id）")
             @TableField(value = "type_id")
        
    
    
    
     
    private String typeId;

         @ApiModelProperty("文件名")
             @TableField(value = "filename")
        
    
    
    
     
    private String filename;



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

