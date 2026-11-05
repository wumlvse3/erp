package com.jcloud.erp.epd.entity;

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
 * Copyright erp
 * 
 * @version V1.0.0
 * @author: wxc
 * @date:2026-04-24
 * @description:
 *		供应商实体类
 */
@Data
@TableName("epd_vendor_def")
@ToString
public class EpdVendorDef extends Model<EpdVendorDef> {

	/**
	 * 主键
	 */
	@TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
	private Integer uu_id;
	/**
	 * 集团ID
	 */
	@TableField(value = "group_id")
	private String group_id;
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
	/**
	 * 供应商代�?	 */
	@TableField(value = "vendor_code")
	private String vendor_code;
	/**
	 * 供应商名�?	 */
	@TableField(value = "vendor_name")
	private String vendor_name;
	/**
	 * 供应商简�?	 */
	@TableField(value = "vendor_abb")
	private String vendor_abb;
	/**
	 * 客商分类
	 */
	@TableField(value = "c_v_flag")
	private String c_v_flag;
	/**
	 * 内部标记
	 */
	@TableField(value = "internal_flag")
	private String internal_flag;
	/**
	 * 所在国家代�?	 */
	@TableField(value = "country_code")
	private String country_code;
	/**
	 * 所在省份代�?	 */
	@TableField(value = "province_code")
	private String province_code;
	/**
	 * 所在城市代�?	 */
	@TableField(value = "city_code")
	private String city_code;
	/**
	 * 所在区县代�?	 */
	@TableField(value = "county_code")
	private String county_code;
	/**
	 * 所在行�?	 */
	@TableField(value = "industry_code")
	private String industry_code;
	/**
	 * 企业性质
	 */
	@TableField(value = "nature_code")
	private String nature_code;
	/**
	 * 企业法人
	 */
	@TableField(value = "legal_man")
	private String legal_man;
	/**
	 * 开户银�?	 */
	@TableField(value = "bank")
	private String bank;
	/**
	 * 银行账号
	 */
	@TableField(value = "bank_account")
	private String bank_account;
	/**
	 * 邮政编码
	 */
	@TableField(value = "postal_code")
	private String postal_code;
	/**
	 * 联系电话
	 */
	@TableField(value = "phone")
	private String phone;
	/**
	 * 传真
	 */
	@TableField(value = "fax")
	private String fax;
	/**
	 * 通讯地址
	 */
	@TableField(value = "address")
	private String address;
	/**
	 * 电子邮件地址
	 */
	@TableField(value = "e_mail")
	private String e_mail;
	/**
	 * 网站地址
	 */
	@TableField(value = "websit")
	private String websit;
	/**
	 * 统一社会信用代码
	 */
	@TableField(value = "tax_registration_no")
	private String tax_registration_no;
	/**
	 * 货币类别
	 */
	@TableField(value = "currency_code")
	private String currency_code;
	/**
	 * 税码代码
	 */
	@TableField(value = "tax_code")
	private String tax_code;
	/**
	 * 备注说明
	 */
	@TableField(value = "remark")
	private String remark;
	/**
	 * 有效标记
	 */
	@TableField(value = "valid_flag")
	private String valid_flag;
	/**
	 * 创建方式
	 */
	@TableField(value = "create_type")
	private String create_type;
	/**
	 * 分配人ID
	 */
	@TableField(value = "assign_user_id")
	private Long assign_user_id;
	/**
	 * 分配人代�?	 */
	@TableField(value = "assign_user_code")
	private String assign_user_code;
	/**
	 * 分配�?	 */
	@TableField(value = "assign_user_man")
	private String assign_user_man;
	/**
	 * 分配时间
	 */
	@TableField(value = "assign_time")
	private Date assign_time;
	/**
	 * 分配来源组织
	 */
	@TableField(value = "assign_from_org")
	private String assign_from_org;
	/**
	 * 选择标记
	 */
	@TableField(value = "select_flag")
	private String select_flag;
	/**
	 * 固定电话
	 */
	@TableField(value = "telephone")
	private String telephone;
	/**
	 * 上级部门
	 */
	@TableField(value = "master_dept")
	private String master_dept;
	/**
	 * 信誉�?	 */
	@TableField(value = "trust")
	private Integer trust;
	/**
	 * 供应商分�?	 */
	@TableField(value = "class_code")
	private String class_code;
	/**
	 * 助记�?	 */
	@TableField(value = "mne_code")
	private String mne_code;
	/**
	 * 发货地址
	 */
	@TableField(value = "ship_address")
	private String ship_address;
	/**
	 * 内部会计主体
	 */
	@TableField(value = "internal_acct_org")
	private String internal_acct_org;
	/**
	 * 采购组织
	 */
	@TableField(value = "pm_org")
	private String pm_org;

	@Override
	public Serializable pkVal() {
		return this.uu_id;
	}

}