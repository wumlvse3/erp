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
@TableName("mtl_inventories")
public class MtlInventories extends Model<MtlInventories> {

	/**
	 * 
	 */
	@TableField(value = "org_id")
	private String orgId;
	/**
	 * 
	 */
	@TableField(value = "group_id")
	private String groupId;
	/**
	 * 
	 */
	@TableId(value = "inventory_id")
	private String inventoryId;
	/**
	 *
	 */
	@TableField(value = "inventory_code")
	private String inventoryCode;
	/**
	 *
	 */
	@TableField(value = "inventory_name")
	private String inventoryName;
	/**
	 *
	 */
	@TableField(value = "lot_flag")
	private Integer lotFlag;
	/**
	 *
	 */
	@TableField(value = "vendor_flag")
	private Integer vendorFlag;
	/**
	 *
	 */
	@TableField(value = "vmi_flag")
	private Integer vmiFlag;
	/**
	 *
	 */
	@TableField(value = "serial_flag")
	private Integer serialFlag;
	/**
	 *
	 */
	@TableField(value = "inventory_class")
	private String inventoryClass;
	/**
	 * 
	 */
	@TableField(value = "location_id")
	private String locationId;
	/**
	 *
	 */
	@TableField(value = "locator_flag")
	private Integer locatorFlag;
	/**
	 *
	 */
	@TableField(value = "trans_flag")
	private Integer transFlag;
	/**
	 *
	 */
	@TableField(value = "enable_flag")
	private Integer enableFlag;
	/**
	 * 
	 */
	@TableField(value = "create_date",fill = FieldFill.INSERT)
	private Date createDate;
	/**
	 * 
	 */
	@TableField(value = "create_by",fill = FieldFill.INSERT)
	private String createBy;
	/**
	 * 
	 */
	@TableField(value = "update_date",fill = FieldFill.UPDATE)
	private Date updateDate;
	/**
	 * 
	 */
	@TableField(value = "update_by",fill = FieldFill.UPDATE)
	private String updateBy;
	/**
	 *
	 */
	@TableField(value = "stocker")
	private String stocker;
	/**
	 *
	 */
	@TableField(value = "stocker_id")
	private String stockerId;
	/**
	 *
	 */
	@TableField(value = "remote_id")
	private String remoteId;
	/**
	 *
	 */
	@TableField(value = "nagetive_flag")
	private Integer nagetiveFlag;
	/**
	 *
	 */
	@TableField(value = "qitao_flag")
	private Integer qitaoFlag;
	/**
	 *
	 */
	@TableField(value = "k3_group_id")
	private String k3GroupId;
	/**
	 *
	 */
	@TableField(value = "k3_lot_flag")
	private Integer k3LotFlag;
	/**
	 *
	 */
	@TableField(value = "factory_id")
	private String factoryId;

	@Override
	public Serializable pkVal() {
		return this.inventoryId;
	} 
}