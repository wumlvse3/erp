package com.jcloud.erp.ord.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Copyright erp
 * 
 * @version V1.0.0
 * @author: wxc
 * @date:2026-04-24
 * @description:
 *		发票头实体类
 */
@Data
@TableName("ord_invoice_head")
@ToString
public class OrdInvoiceHead extends Model<OrdInvoiceHead> {

	/**
	 * 主键
	 */
	@TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
	private Integer uu_id;
	/**
	 * 上游单据主键
	 */
	@TableField(value = "s_uu_id")
	private String s_uu_id;
	/**
	 * 集团ID
	 */
	@TableField(value = "group_id")
	private String group_id;
	/**
	 * 发货单号
	 */
	@TableField(value = "uk_ship_no")
	private String uk_ship_no;
	/**
	 * 上游单号
	 */
	@TableField(value = "up_bill")
	private String up_bill;
	/**
	 * 销售组�?	 */
	@TableField(value = "sales_org")
	private String sales_org;
	/**
	 * 客户ID
	 */
	@TableField(value = "customer_id")
	private String customer_id;
	/**
	 * 客户编码
	 */
	@TableField(value = "customer_code")
	private String customer_code;
	/**
	 * 客户名称
	 */
	@TableField(value = "customer_name")
	private String customer_name;
	/**
	 * 销售组ID
	 */
	@TableField(value = "role_id")
	private String role_id;
	/**
	 * 销售组编码
	 */
	@TableField(value = "role_code")
	private String role_code;
	/**
	 * 销售组名称
	 */
	@TableField(value = "role_name")
	private String role_name;
	/**
	 * 销售部门ID
	 */
	@TableField(value = "dept_id")
	private String dept_id;
	/**
	 * 销售部门编�?	 */
	@TableField(value = "dept_code")
	private String dept_code;
	/**
	 * 销售部门名�?	 */
	@TableField(value = "dept_name")
	private String dept_name;
	/**
	 * 销售员ID
	 */
	@TableField(value = "sales_person_id")
	private String sales_person_id;
	/**
	 * 销售员编码
	 */
	@TableField(value = "sales_person_code")
	private String sales_person_code;
	/**
	 * 销售员名称
	 */
	@TableField(value = "sales_person_man")
	private String sales_person_man;
	/**
	 * 本位币ID
	 */
	@TableField(value = "base_currency_id")
	private String base_currency_id;
	/**
	 * 本位币代�?	 */
	@TableField(value = "base_currency_code")
	private String base_currency_code;
	/**
	 * 本位币名�?	 */
	@TableField(value = "base_currency_desc")
	private String base_currency_desc;
	/**
	 * 汇总本位币含税金额
	 */
	@TableField(value = "base_tax_money")
	private BigDecimal base_tax_money;
	/**
	 * 订单类型
	 */
	@TableField(value = "type_code")
	private String type_code;
	/**
	 * 运输方式
	 */
	@TableField(value = "transport_type")
	private String transport_type;
	/**
	 * 收货地址
	 */
	@TableField(value = "address")
	private String address;
	/**
	 * 付款方式
	 */
	@TableField(value = "pay_way")
	private String pay_way;
	/**
	 * 结算方式
	 */
	@TableField(value = "settlement")
	private String settlement;
	/**
	 * 审批人ID
	 */
	@TableField(value = "approver_user_id")
	private String approver_user_id;
	/**
	 * 审批人编�?	 */
	@TableField(value = "approver_user_code")
	private String approver_user_code;
	/**
	 * 审批人名�?	 */
	@TableField(value = "approver_user_man")
	private String approver_user_man;
	/**
	 * 审批时间
	 */
	@TableField(value = "approver_time")
	private Date approver_time;
	/**
	 * 审批意见
	 */
	@TableField(value = "approver_opinion")
	private String approver_opinion;
	/**
	 * 单据日期
	 */
	@TableField(value = "bill_date")
	private String bill_date;
	/**
	 * 单据状�?	 */
	@TableField(value = "bill_status")
	private String bill_status;
	/**
	 * 是否退库单
	 */
	@TableField(value = "is_ship_ret")
	private String is_ship_ret;
	/**
	 * 退货原�?	 */
	@TableField(value = "back_reason")
	private String back_reason;
	/**
	 * 备注
	 */
	@TableField(value = "notes")
	private String notes;
	/**
	 * 创建人ID
	 */
	@TableField(value = "create_user_id")
	private String create_user_id;
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private Date create_time;
	/**
	 * 更新人ID
	 */
	@TableField(value = "modify_user_id")
	private String modify_user_id;
	/**
	 * 更新时间
	 */
	@TableField(value = "modify_time", fill = FieldFill.UPDATE)
	private Date modify_time;
	/**
	 * 工作流ID
	 */
	@TableField(value = "flow_id")
	private String flow_id;
	/**
	 * 逻辑删除标记
	 */
	@TableField(value = "is_deleted")
	private Boolean is_deleted;

	@Override
	public Serializable pkVal() {
		return this.uu_id;
	}

}