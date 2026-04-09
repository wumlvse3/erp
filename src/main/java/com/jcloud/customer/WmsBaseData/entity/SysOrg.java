package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("sys_org")
public class SysOrg extends Model<SysOrg> {

	/**
	 * 
	 */
	@TableId("org_id")
	private String org_id;
	/**
	 * 类型代码
	 */
	@TableField(value = "org_code")
	private String org_code;
	/**
	 * 类型名称
	 */
	@TableField(value = "org_name")
	private String org_name;
	/**
	 * 默认仓库
	 */
	@TableField(value = "create_date",fill = FieldFill.INSERT)
	private Date create_date;
	/**
	 * 
	 */
	@TableField(value = "create_by",fill=FieldFill.INSERT)
	private String create_by;
	/**
	 * 
	 */
	@TableField(value = "create_by_name",fill =  FieldFill.INSERT)
	private String create_by_name;
	/**
	 * 
	 */
	@TableField(value = "org_type")
	private String org_type;

	@TableField(value = "org_class")
	private String org_class;

	@TableField(value = "purchase_org")
	private String purchase_org;
	/**
	 * 
	 */
	@TableField(value = "parent_org_id")
	private String parent_org_id;

	@TableField(value = "parent_org_code")
	private String parent_org_code;

	@TableField(value = "parent_org_name")
	private String parent_org_name;


	@TableField(exist = false)
	List<SysOrg> child_org;


	@TableField(value = "opening_bank")
	private String opening_bank;

	@TableField(value = "opening_phone")
	private String opening_phone;

	@TableField(value = "location")
	private String location;

	@TableField(value = "bank_code")
	private String bank_code;

	@TableField(value = "tax_code")
	private String tax_code;

	@TableField(value = "legal_man")
	private String legal_man;

	@TableField(value = "postal_code")
	private String postal_code;

	@TableField(value = "third_part_id")
	private String thirdPartId;
	@TableField(value = "update_by_name")
	private String updateByName;
	@TableField(value = "update_time")
	private Date updateTime;
	@TableField(value = "status")
	private String status;
	@TableField(value = "org_all_name")
	private String org_all_name;
	@Override
	public Serializable pkVal() {
		return this.org_id;
	} 
}