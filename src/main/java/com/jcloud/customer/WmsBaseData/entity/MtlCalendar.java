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
 * @date:2020-09-07
 * @description:
 *		实体类
 */
@Data
@TableName("mtl_calendar")
public class MtlCalendar extends Model<MtlCalendar> {

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
	@TableId(value = "calendar_id")
	private String calendar_id;
	/**
	 * 代码
	 */
	@TableField(value = "calendar_code")
	private String calendar_code;
	/**
	 * 一次性生成年数
	 */
	@TableField(value = "num_of_year")
	private Integer num_of_year;
	/**
	 * 会计起始日
	 */
	@TableField(value = "month_start_day")
	private String month_start_day;
	/**
	 * 
	 */
	@TableField(value = "month_end_day")
	private String month_end_day;
	/**
	 * 
	 */
	@TableField(value = "create_date",fill = FieldFill.INSERT)
	private Date create_date;
	/**
	 * 
	 */
	@TableField(value = "create_by",fill = FieldFill.INSERT)
	private String create_by;
	/**
	 * 
	 */
	@TableField(value = "update_date",fill = FieldFill.UPDATE)
	private Date update_date;
	/**
	 * 
	 */
	@TableField(value = "update_by",fill = FieldFill.UPDATE)
	private String update_by;
	/**
	 * 描述
	 */
	@TableField(value = "calendar_desc")
	private String calendar_desc;

	@Override
	public Serializable pkVal() {
		return this.calendar_id;
	} 
}