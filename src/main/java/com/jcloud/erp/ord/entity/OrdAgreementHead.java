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
 *		协议单头实体�? */
@Data
@TableName("ord_agreement_head")
@ToString
public class OrdAgreementHead extends Model<OrdAgreementHead> {

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
	 * 协议单号
	 */
	@TableField(value = "uk_agreement_id")
	private String uk_agreement_id;
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
	 * 订单类型
	 */
	@TableField(value = "type_code")
	private String type_code;
	/**
	 * 协议类型
	 */
	@TableField(value = "agreement_type")
	private String agreement_type;
	/**
	 * 是否集销
	 */
	@TableField(value = "is_collect")
	private String is_collect;
	/**
	 * 合同�?	 */
	@TableField(value = "contract_no")
	private String contract_no;
	/**
	 * 合同名称
	 */
	@TableField(value = "contract_name")
	private String contract_name;
	/**
	 * 单据日期
	 */
	@TableField(value = "bill_date")
	private String bill_date;
	/**
	 * 运输方式
	 */
	@TableField(value = "transport_type")
	private String transport_type;
	/**
	 * 客户ID
	 */
	@TableField(value = "customer_id")
	private Long customer_id;
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
	 * 开票客户ID
	 */
	@TableField(value = "invoice_customer_id")
	private Long invoice_customer_id;
	/**
	 * 开票客户编�?	 */
	@TableField(value = "invoice_customer_code")
	private String invoice_customer_code;
	/**
	 * 开票客户名�?	 */
	@TableField(value = "invoice_customer_name")
	private String invoice_customer_name;
	/**
	 * 签订日期
	 */
	@TableField(value = "sign_date")
	private String sign_date;
	/**
	 * 签订人ID
	 */
	@TableField(value = "sign_person_id")
	private Long sign_person_id;
	/**
	 * 签订人编�?	 */
	@TableField(value = "sign_person_code")
	private String sign_person_code;
	/**
	 * 签订人名�?	 */
	@TableField(value = "sign_person_man")
	private String sign_person_man;
	/**
	 * 销售组ID
	 */
	@TableField(value = "role_id")
	private Long role_id;
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
	private Long dept_id;
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
	private Long sales_person_id;
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
	 * 整单折扣
	 */
	@TableField(value = "bill_discount")
	private Float bill_discount;
	/**
	 * 汇总本位币含税金额
	 */
	@TableField(value = "base_tax_money")
	private BigDecimal base_tax_money;
	/**
	 * 本位币ID
	 */
	@TableField(value = "currency_id")
	private Long currency_id;
	/**
	 * 本位币代�?	 */
	@TableField(value = "currency_code")
	private String currency_code;
	/**
	 * 本位币名�?	 */
	@TableField(value = "currency_desc")
	private String currency_desc;
	/**
	 * 收货地址
	 */
	@TableField(value = "address")
	private String address;
	/**
	 * 结算方式
	 */
	@TableField(value = "settlement")
	private String settlement;
	/**
	 * 付款方式
	 */
	@TableField(value = "pay_way")
	private String pay_way;
	/**
	 * 协议生效日期
	 */
	@TableField(value = "effective_date")
	private String effective_date;
	/**
	 * 协议失效日期
	 */
	@TableField(value = "expiring_date")
	private String expiring_date;
	/**
	 * 审批人ID
	 */
	@TableField(value = "approver_user_id")
	private Long approver_user_id;
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
	 * 单据状�?	 */
	@TableField(value = "bill_status")
	private String bill_status;
	/**
	 * 业务说明
	 */
	@TableField(value = "remarks")
	private String remarks;
	/**
	 * 付款条件代码
	 */
	@TableField(value = "pay_term")
	private String pay_term;
	/**
	 * 项目号ID
	 */
	@TableField(value = "project_id")
	private Long project_id;
	/**
	 * 项目�?	 */
	@TableField(value = "project_no")
	private String project_no;
	/**
	 * 项目名称
	 */
	@TableField(value = "project_name")
	private String project_name;
	/**
	 * 工作令号
	 */
	@TableField(value = "work_order")
	private String work_order;
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