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

@Data
@TableName("source_list")
@ToString
public class Source_list extends Model<Source_list> {

	@TableId("id")
	private 	String	id	;
	@TableField(value = "create_time",fill = FieldFill.INSERT)
	private Date create_time	;
	@TableField(value = "create_by",fill = FieldFill.INSERT)
	private 	String	create_by	;
	@TableField(value = "update_by",fill =  FieldFill.UPDATE)
	private 	String	update_by	;
	@TableField(value = "update_time",fill =  FieldFill.UPDATE)
	private 	Date	update_time	;

	private String org_id;

	private String item_id;

	private String item_code;
	private String item_name;
	private String vendor_id;

	private String vendor_code;//

	private String vendor_name;//

	private String package_qty;//

	private String enable_date;//表格数据

	private String main_flag;

	private String status;

	private String items_attribute;

	private String items_unit;

	private String source_code;

	@Override
	public Serializable pkVal() {
		return this.id;
	}

}