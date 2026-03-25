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
@TableName("quality_claim")
@ToString
public class QualityClaim extends Model<QualityClaim> {

	/**
	 *
	 */
	@TableId(value = "id")
	private String id;
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
	 *索赔单号
	 */
	@TableField(value = "claim_code")
	private String claim_code;
	/**
	 *组织ID
	 */
	@TableField(value = "org_id")
	private String org_id;
	/**
	 * 供应商ID
	 */
	@TableField(value = "vendor_id")
	private String vendor_id;
	/**
	 * 供应商编码
	 */
	@TableField(value = "vendor_code")
	private String vendor_code;
	/**
	 * 供应商名称
	 */
	@TableField(value = "vendor_name")
	private String vendor_name;
	/**
	 * 索赔描述
	 */
	@TableField(value = "claim_description")
	private String claim_description;
	/**
	 * 索赔金额
	 */
	@TableField(value = "claim_amount")
	private Double claim_amount;
	/**
	 * 联系人
	 */
	@TableField(value = "contact_name")
	private String contact_name;
	/**
	 * 联系电话
	 */
	@TableField(value = "contact_phone")
	private Integer contact_phone;
	/**
	 * 附件
	 */
	@TableField(value = "file")
	private Integer file;
	/**
	 * 供应商反馈结果
	 */
	@TableField(value = "retroaction")
	private Integer retroaction;
	/**
	 * 供应商申述意见
	 */
	@TableField(value = "representations")
	private String representations;
	/**
	 * 确认金额
	 */
	@TableField(value = "sure_qty")
	private Double sure_qty;
	/**
	 * 确认意见
	 */
	@TableField(value = "sure_rep")
	private String sure_rep;
	/**
	 * 备注
	 */
	@TableField(value = "remark")
	private String remark;
	/**
	 * 供应商确认附件
	 */
	@TableField(value = "vendor_file")
	private String vendor_file;
	/**
	 * 索赔结果附件
	 */
	@TableField(value = "claim_file")
	private String claim_file;

	/**
	 * 索赔处理方式
	 * @return
	 */
	@TableField(value = "claim_type")
	private String claim_type;

	/**
	 * 状态
	 * @return
	 */
	@TableField(value = "type")
	private String type;

	/**
	 * 采购反馈结果
	 * @return
	 */
	@TableField(value = "procure_result")
	private String procure_result;

	/**
	 * 索赔标题
	 * @return
	 */
	@TableField(value = "claim_title")
	private String claim_title;

	/**
	 * 采购组织
	 * @return
	 */
	@TableField(value = "pur_group")
	private String pur_group;


}