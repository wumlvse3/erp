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
@TableName("scoring_group")
public class ScoringGroup extends Model<ScoringGroup> {

	/**
	 * 主键id
	 */
	@TableId("scoring_group_id")
	private String scoring_group_id;
	/**
	 * 评分项代码
	 */
	@TableField(value = "scoring_code")
	private String scoring_code;
	/**
	 * 评分项
	 */
	@TableField(value = "scoring_name")
	private String scoring_name;
	/**
	 * 描述
	 */
	@TableField(value = "scoring_desc")
	private String scoring_desc;

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
		return this.scoring_group_id;
	}
}