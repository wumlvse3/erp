package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * Copyright 2016-2020 长沙湘北智造信息技术有限公司
 * 
 * @version xbzz V2.0.28
 * @author: 云之佳-开发部
 * @date:2020-08-24
 * @description:
 *		实体类
 */
@Data
@TableName("quality_claim_line")
@ToString
public class QualityClaimLine extends Model<QualityClaimLine> {

	/**
	 *
	 */
	@TableId(value = "id")
	private String id;
	/**
	 * head_id
	 */
	@TableField(value = "head_id")
	private String head_id;
	/**
	 * 创建人
	 */
	@TableField(value = "create_by",fill = FieldFill.INSERT)
	private String create_by;
	/**
	 * 创建人
	 */
	@TableField(value = "create_by_name",fill = FieldFill.INSERT)
	private String create_by_name;
	/**
	 * 创建日期
	 */
	@TableField(value = "create_time",fill = FieldFill.INSERT)
	private Date create_time;
	/**
	 *
	 */
	@TableField(value = "update_by")
	private String update_by;
	@TableField(value = "update_by_name")
	private String update_by_name;
	/**
	 *
	 */
	@TableField(value = "update_time",fill = FieldFill.UPDATE)
	private Date update_time;
	/**
	 *类型
	 */
	@TableField(value = "type")
	private String type;
	/**
	 *组织ID
	 */
	@TableField(value = "org_id")
	private String org_id;
	/**
	 * 物料编码
	 */
	@TableField(value = "items_code")
	private String items_code;
	/**
	 * 物料名称
	 */
	@TableField(value = "items_name")
	private String items_name;
	/**
	 * 损耗数量
	 */
	@TableField(value = "loss_qty")
	private Double loss_qty;
	/**
	 * 单价
	 */
	@TableField(value = "qty")
	private Double qty;
	/**
	 * 总金额
	 */
	@TableField(value = "total_amount")
	private Double total_amount;
	/**
	 * 岗位
	 */
	@TableField(value = "station")
	private String station;
	/**
	 * 人数
	 */
	@TableField(value = "number")
	private Double number;
	/**
	 * 工时
	 */
	@TableField(value = "work_house")
	private Double work_house;
	/**
	 * 索赔项目
	 */
	@TableField(value = "claim_prodct")
	private String claim_prodct;
	/**
	 * 数量
	 */
	@TableField(value = "line_qty")
	private Double line_qty;
	/**
	 * 币种
	 */
	@TableField(value = "currency")
	private Double currency;

}