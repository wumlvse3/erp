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
 * Copyright 2016-2020 长沙湘北智造信息技术有限公司
 * 
 * @version xbzz V2.0.28
 * @author: 云之佳-开发部
 * @date:2020-12-25
 * @description:
 *		实体类
 */
@Data
@TableName("mtl_unit_conversion")
public class Mtl_unit_conversion extends Model<Mtl_unit_conversion> {

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 单位编码
	 */
	@TableField(value = "unit_code")
	private String unit_code;
	/**
	 * 单位名称
	 */
	@TableField(value = "unit_name")
	private String unit_name;
	/**
	 * 单位组
	 */
	@TableField(value = "unit_group")
	private String unit_group;
	/**
	 * 是否为基础单位  0 基础单位  1 非基础单位
	 */
	@TableField(value = "base_unit_flag")
	private Integer base_unit_flag;
	/**
	 * 单位换算量
	 */
	@TableField(value = "business_qty")
	private Double business_qty;
	/**
	 * 
	 */
	@TableField(value = "create_by")
	private String create_by;
	/**
	 *
	 */
	@TableField(value = "create_by_name")
	private String create_by_name;
	/**
	 * 
	 */
	@TableField(value = "create_time")
	private Date create_time;
	/**
	 *
	 */
	@TableField(value = "update_by")
	private String update_by;
	/**
	 *
	 */
	@TableField(value = "update_by_name")
	private String update_by_name;
	/**
	 *
	 */
	@TableField(value = "update_time")
	private Date update_time;
	/**
	 * 
	 */
	@TableField(value = "org_id")
	private String org_id;

	/**
	 * 删除标识
	 */
	@TableField(value = "deleted")
	private Boolean deleted;

	/**
	 * 英文名称
	 */
	@TableField(value = "e_name")
	private String e_name;

	@TableField(value = "third_part_id")
	private String third_part_id; //第三方id
	@Override
	public Serializable pkVal() {
		return this.id;
	} 
}