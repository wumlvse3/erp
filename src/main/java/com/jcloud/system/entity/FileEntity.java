package com.jcloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件表
 * @Auther: yang
 * @Date: 2023/4/4 14:03
 * @Description:
 */
@Data
@TableName("sys_file")
public class FileEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @TableId(type =  IdType.AUTO)
	private Long id;

	private Integer type;

	private String url;

	@TableField(value = "create_date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	@TableField(value = "create_by")
	private String createBy;

	@TableField(value = "type_code")
	private String typeCode;

	@TableField(value = "type_id")
	private String typeId;


	private String filename;
}