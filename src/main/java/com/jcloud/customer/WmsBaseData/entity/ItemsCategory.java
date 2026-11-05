package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("items_category")
@ToString
public class ItemsCategory extends Model<ItemsCategory> {

	@TableId("id")
	private 	String	id	;
	@TableField(value = "create_time",fill = FieldFill.INSERT)
	private Date create_time	;
	@TableField(value = "create_by",fill = FieldFill.INSERT)
	private 	String	create_by	;
	@TableField(value = "create_by_name",fill =  FieldFill.INSERT)
	private 	String	create_by_name	;
	@TableField(value = "update_by",fill =  FieldFill.UPDATE)
	private 	String	update_by	;
	@TableField(value = "update_time",fill =  FieldFill.UPDATE)
	private 	Date	update_time	;
	@TableField(value = "update_by_name",fill =  FieldFill.UPDATE)
	private 	String	update_by_name	;

	@TableField(value = "deleted")
	private Boolean deleted;

	@TableField(value = "category_code",insertStrategy = FieldStrategy.NOT_EMPTY,updateStrategy = FieldStrategy.NOT_EMPTY)
	private String category_code;

	@TableField(value = "category_name",insertStrategy = FieldStrategy.NOT_EMPTY,updateStrategy = FieldStrategy.NOT_EMPTY)
	private String category_name;

	private String parent_id;
	private String parent_code;
	private String parent_name;

	@TableField(value = "category_level",insertStrategy = FieldStrategy.NOT_EMPTY,updateStrategy = FieldStrategy.NOT_EMPTY)
	private String category_level;//品类层级

	private String sourcing_control;//寻源控制

	private String category_rank;//品类等级

	private String qualifications;//表格数据

	private String status;

	private String category_type;

	private String third_part_id;//第三方ID

	private String person_id;

	private String person_name;

	@Override
	public Serializable pkVal() {
		return this.id;
	}

}