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
 * @date:2020-09-07
 * @description:
 *		实体类
 */
@Data
@TableName("mtl_calendar_period")
public class MtlCalendarPeriod extends Model<MtlCalendarPeriod> {

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
	@TableField(value = "calendar_id")
	private String calendar_id;
	/**
	 * 
	 */
	@TableId(value = "period_id")
	private String period_id;
	/**
	 * 年度
	 */
	@TableField(value = "period_year")
	private String period_year;
	/**
	 * 月度
	 */
	@TableField(value = "period_month")
	private String period_month;
	/**
	 * 开始日期
	 */
	@TableField(value = "start_date")
	private Date start_date;
	/**
	 * 结束日期
	 */
	@TableField(value = "end_date")
	private Date end_date;
	/**
	 * 状态
	 */
	@TableField(value = "status")
	private Integer status;

	@Override
	public Serializable pkVal() {
		return this.period_id;
	} 
}