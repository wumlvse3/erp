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
@TableName("mtl_items_org")
@ToString
public class Mtl_items_org extends Model<Mtl_items_org> {

	/**
	 *
	 */
	@TableId(value = "item_id")
	private String item_id;
	/**
	 * 创建人
	 */
	@TableField(value = "create_by",fill = FieldFill.INSERT)
	private String create_by;
	/**
	 * 创建日期
	 */
	@TableField(value = "create_date",fill = FieldFill.INSERT)
	private Date create_date;
	/**
	 *
	 */
	@TableField(value = "update_by",fill = FieldFill.UPDATE)
	private String update_by;
	/**
	 *
	 */
	@TableField(value = "update_date",fill = FieldFill.UPDATE)
	private Date update_date;
	/**
	 *
	 */
	@TableField(value = "group_id")
	private String group_id;
	/**
	 *
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
	 * 物料描述
	 */
	@TableField(value = "items_desc")
	private String items_desc;
	/**
	 * 单位
	 */
	@TableField(value = "items_unit")
	private String items_unit;
	/**
	 * 包装量
	 */
	@TableField(value = "package_qty")
	private Double package_qty;
	/**
	 * 供应商控制
	 */
	@TableField(value = "vendor_flag")
	private String vendor_flag;
	/**
	 * 批次控制
	 */
	@TableField(value = "lot_flag")
	private Integer lot_flag;
	/**
	 * 货位控制
	 */
	@TableField(value = "locator_flag")
	private Integer locator_flag;
	/**
	 * 序列控制
	 */
	@TableField(value = "serial_flag")
	private Integer serial_flag;
	/**
	 * 检验标志
	 */
	@TableField(value = "check_flag")
	private String check_flag;
	/**
	 * 物料类型
	 */
	@TableField(value = "items_class")
	private String items_class;
	/**
	 * 包装类型
	 */
	@TableField(value = "package_type")
	private String package_type;
	/**
	 * 盛具代码
	 */
	@TableField(value = "full_appliance")
	private String full_appliance;
	/**
	 * 收货仓库
	 */
	@TableField(value = "inventory_id")
	private String inventory_id;
	/**
	 * 库管员
	 */
	@TableField(value = "wm_keeper")
	private String wm_keeper;

	/**
	 * 物料规格
	 * @return
	 */
	@TableField(value = "items_specification")
	private String items_specification;

	@TableField(value = "package_flag")
	private Integer package_flag;

	@TableField(value = "auto_flag")
	private Integer auto_flag;

	@TableField(value = "mtl_class")
	private String mtl_class;

	@TableField(value = "feeding_type")
	private String feeding_type;

	/**
	 * 第三方id
	 *
	 */
	@TableField(value = "remote_id")
	private String remote_id;

	/**
	 * 物料小类
	 */
	@TableField(value = "small_class")
	private String small_class;

	/**
	 *
	 * 质检类型
	 */
	@TableField(value = "quality_type")
	private String quality_type;

	@TableField(value = "purchase_unit")
	private String purchase_unit;


	@TableField(value = "nagetive_flag")
	private Integer nagetive_flag;

	@TableField(value = "create_by_name",fill = FieldFill.INSERT)
	private String create_by_name;

	@TableField(value = "update_by_name",fill = FieldFill.UPDATE)
	private String update_by_name;
	@TableField(value = "items_category")
	private String items_category;
	@TableField(value = "items_category_desc")
	private String items_category_desc;
	@TableField(value = "old_items_code")
	private String old_items_code;
	@TableField(value = "items_files")
	private String items_files;
	@TableField(value = "items_pictures")
	private String items_pictures;
	@TableField(value = "unit_group")
	private String unit_group;
	@TableField(value = "coefficient")
	private String coefficient;

	@TableField(value = "deleted")
	private Boolean deleted;
	@Override
	public Serializable pkVal() {
		return this.item_id;
	}

}