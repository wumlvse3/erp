package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * @date:2020-08-26
 * @description:
 *		实体类
 */
@Data
@TableName("mtl_sheet_types")
public class Mtl_sheet_types extends Model<Mtl_sheet_types> {

	/**
	 * 
	 */
	@TableId(value = "sheet_type_id")
	private String sheet_type_id;
	/**
	 * 交易类型
	 */
	@TableField(value = "type_id")
	private Integer type_id;
	/**
	 * 单据名称
	 */
	@TableField(value = "sheet_title")
	private String sheet_title;
	/**
	 * 前缀
	 */
	@TableField(value = "pre_code")
	private String pre_code;
	/**
	 * 后缀
	 */
	@TableField(value = "suffix_code")
	private String suffix_code;
	/**
	 * 
	 */
	@TableField(value = "date_flag")
	private Integer date_flag;
	/**
	 * 开始序列
	 */
	@TableField(value = "start_no")
	private String start_no;
	/**
	 * 日重置
	 */
	@TableField(value = "date_break")
	private Integer date_break;
	/**
	 * 当前序列
	 */
	@TableField(value = "current_serial")
	private Integer current_serial;
	/**
	 * 创建日期
	 */
	@TableField(value = "create_date",fill = FieldFill.INSERT)
	private Date create_date;
	/**
	 * 创建人
	 */
	@TableField(value = "created_by")
	private String created_by;
	/**
	 * 更新日期
	 */
	@TableField(value = "update_date",fill = FieldFill.UPDATE)
	private Date update_date;
	/**
	 * 更新人
	 */
	@TableField(value = "update_by",fill = FieldFill.UPDATE)
	private String update_by;
	/**
	 * ERP上传标志
	 */
	@TableField(value = "erp_flag")
	private Integer erp_flag;
	/**
	 * 日期格式
	 */
	@TableField(value = "date_type")
	private String date_type;
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
	@TableField(value = "third_part_code")
	private String third_part_code;
	/**
	 *
	 */
	@TableField(value = "third_part_id")
	private String third_part_id;


	@TableField(value = "interface_id")
	private String interface_id;

	@TableField(value = "create_by_name")
	private String create_by_name;

	@TableField(value = "update_by_name")
	private String update_by_name;

	@TableField(exist = false)
	private String date_str;


	@Override
	public Serializable pkVal() {
		return this.sheet_type_id;
	} 
}