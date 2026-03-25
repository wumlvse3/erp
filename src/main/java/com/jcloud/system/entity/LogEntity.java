package com.jcloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.Date;
/**
 * 日志表
 * @Auther: yang
 * @Date: 2023/3/29 13:47
 * @Description:
 */
@Data
@TableName("sys_log")
public class LogEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@TableId(type =  IdType.AUTO)//数据库自增
	private Long id;

	private Long user_id;

	private String username;

	private String operation;

	private Integer time;

	private String method;

	private String params;

	private String ip;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date gmt_create;
}