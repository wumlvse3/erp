package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * Copyright 2016-2021 长沙湘北智造信息技术有限公司
 * 
 * @version xbzz V2.0.28
 * @author: 云之佳-开发部
 * @date:2021-01-07
 * @description:
 *		实体类
 */
@Data
@TableName("mtl_class")
public class Mtl_class extends Model<Mtl_class> {

	/**
	 * 
	 */
	@TableId("class_id")
	private String class_id;
	/**
	 * 类型代码
	 */
	@TableField(value = "class_code")
	private String class_code;
	/**
	 * 类型名称
	 */
	@TableField(value = "class_name")
	private String class_name;
	/**
	 * 默认仓库
	 */
	@TableField(value = "inventory_id")
	private String inventory_id;
	/**
	 * 
	 */
	@TableField(value = "org_id")
	private String org_id;
	/**
	 * 
	 */
	@TableField(value = "group_id")
	private String group_id;
	/**
	 * 
	 */
	@TableField(value = "create_by")
	private String create_by;
	/**
	 * 
	 */
	@TableField(value = "create_date")
	private Date create_date;


	@Override
	public Serializable pkVal() {
		return this.class_id;
	} 
}