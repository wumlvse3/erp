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
@TableName("scoring_model_line")
public class ScoringModelLine extends Model<ScoringModelLine> {

	/**
	 * 主键id
	 */
	@TableId("line_id")
	private String line_id;
	/**
	 * 评分维度代码
	 */
	@TableField(value = "scoring_dimension_code")
	private String scoring_dimension_code;
	/**
	 * 评分维度名称
	 */
	@TableField(value = "scoring_dimension_name")
	private String scoring_dimension_name;
	/**
	 * 评分类型：1.商务类;2.技术类;3资信类;
	 */
	@TableField(value = "scoring_type")
	private String scoring_type;
	/**
	 * 分值
	 */
	@TableField(value = "scoring_dimension_value")
	private Float scoring_dimension_value;
	/**
	 * 头id
	 */
	@TableField(value = "header_id")
	private String header_id;

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
		return this.line_id;
	}
}