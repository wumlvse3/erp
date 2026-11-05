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
 *		项目实体类
 */
@Data
@TableName("epd_project")
@ToString
public class EpdProject extends Model<EpdProject> {

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
	 * 项目号
	 */
	@TableField(value = "project_no")
	private String project_no;
	/**
	 * 项目名称
	 */
	@TableField(value = "project_name")
	private String project_name;
	/**
	 * 项目类型
	 */
	@TableField(value = "project_type")
	private String project_type;
	/**
	 * 产品代码
	 */
	@TableField(value = "item_code")
	private String item_code;
	/**
	 * 产品名称
	 */
	@TableField(value = "item_name")
	private String item_name;
	/**
	 * 有效标记
	 */
	@TableField(value = "valid_flag")
	private String valid_flag;
	/**
	 * 备注说明
	 */
	@TableField(value = "remark")
	private String remark;
	/**
	 * 分配时间
	 */
	@TableField(value = "assign_time")
	private Date assign_time;
	/**
	 * 分配人ID
	 */
	@TableField(value = "assign_user_id")
	private Long assign_user_id;
	/**
	 * 分配人代码
	 */
	@TableField(value = "assign_user_code")
	private String assign_user_code;
	/**
	 * 分配人
	 */
	@TableField(value = "assign_user_man")
	private String assign_user_man;
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
	 * 库存组织
	 */
	@TableField(value = "inv_org")
	private String inv_org;

	@Override
	public Serializable pkVal() {
		return this.uu_id;
	}

}