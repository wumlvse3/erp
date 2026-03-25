package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author: 云之佳-开发部
 * @date:2021-01-07
 * @description:
 *		实体类
 */
@Data
@TableName("scoring_group_user")
public class ScoringGroupUser extends Model<ScoringGroupUser> {

	/**
	 * 主键id
	 */
	@TableId("id")
	private String id;
	/**
	 * 评分项代码
	 */
	@TableField(value = "user_id")
	private String user_id;
	/**
	 * 评分项
	 */
	@TableField(value = "username")
	private String username;
	/**
	 * 描述
	 */
	@TableField(value = "name")
	private String name;
	/**
	 * 头ID
	 */
	@TableField(value = "scoring_group_id")
	private String scoring_group_id;

	/**
	 * 组织ID
	 */
	@TableField(value = "org_id")
	private String org_id;

	/**
	 * 创建人
	 */
	@TableField(value = "create_by")
	private String create_by;
	/**
	 *	创建人名称
	 */
	@TableField(value = "create_by_name")
	private String create_by_name;
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time")
	private Date create_time;
	/**
	 *	更新人
	 */
	@TableField(value = "update_by")
	private String update_by;
	/**
	 *	更新人名称
	 */
	@TableField(value = "update_by_name")
	private String update_by_name;
	/**
	 *	更新时间
	 */
	@TableField(value = "update_time")
	private Date update_time;

	@Override
	public Serializable pkVal() {
		return this.id;
	}
}