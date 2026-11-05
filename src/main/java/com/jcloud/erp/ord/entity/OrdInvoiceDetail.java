package com.jcloud.erp.ord.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Copyright erp
 * 
 * @version V1.0.0
 * @author: wxc
 * @date:2026-04-24
 * @description:
 *		发票明细实体�? */
@Data
@TableName("ord_invoice_detail")
@ToString
public class OrdInvoiceDetail extends Model<OrdInvoiceDetail> {

	/**
	 * 主键
	 */
	@TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
	private Integer uu_id;
	/**
	 * 上游单据主键
	 */
	@TableField(value = "s_uu_id")
	private String s_uu_id;
	/**
	 * 集团ID
	 */
	@TableField(value = "group_id")
	private String group_id;
	/**
	 * 发货单号uuid
	 */
	@TableField(value = "uk_ship_uuid")
	private String uk_ship_uuid;
	/**
	 * 发货单号
	 */
	@TableField(value = "uk_ship_no")
	private String uk_ship_no;
	/**
	 * 行号
	 */
	@TableField(value = "uk_line_no")
	private String uk_line_no;
	/**
	 * 上游单号
	 */
	@TableField(value = "up_bill")
	private String up_bill;
	/**
	 * 上游单号行号
	 */
	@TableField(value = "up_bill_line")
	private String up_bill_line;
	/**
	 * 物料ID
	 */
	@TableField(value = "item_id")
	private String item_id;
	/**
	 * 物料编码
	 */
	@TableField(value = "item_code")
	private String item_code;
	/**
	 * 物料名称
	 */
	@TableField(value = "item_name")
	private String item_name;
	/**
	 * 物料规格
	 */
	@TableField(value = "item_spec")
	private String item_spec;
	/**
	 * 物料型号
	 */
	@TableField(value = "item_model")
	private String item_model;
	/**
	 * 发货数量
	 */
	@TableField(value = "ship_qty")
	private BigDecimal ship_qty;
	/**
	 * 发货日期
	 */
	@TableField(value = "ship_date")
	private String ship_date;
	/**
	 * 单位
	 */
	@TableField(value = "ord_unit_id")
	private String ord_unit_id;
	/**
	 * 单位名称
	 */
	@TableField(value = "ord_unit_name")
	private String ord_unit_name;
	/**
	 * 价税合计
	 */
	@TableField(value = "amt")
	private BigDecimal amt;
	/**
	 * 发货组织
	 */
	@TableField(value = "send_org")
	private String send_org;
	/**
	 * 退库是否质检
	 */
	@TableField(value = "is_quality")
	private String is_quality;
	/**
	 * 可发数量
	 */
	@TableField(value = "arrive_qty")
	private BigDecimal arrive_qty;
	/**
	 * 换算�?	 */
	@TableField(value = "change_rate")
	private BigDecimal change_rate;
	/**
	 * 主数�?	 */
	@TableField(value = "main_qty")
	private BigDecimal main_qty;
	/**
	 * 主单�?	 */
	@TableField(value = "main_unit_id")
	private String main_unit_id;
	/**
	 * 主单位名�?	 */
	@TableField(value = "main_unit_name")
	private String main_unit_name;
	/**
	 * 税码代码
	 */
	@TableField(value = "tax_code")
	private String tax_code;
	/**
	 * 税码描述
	 */
	@TableField(value = "tax_name")
	private String tax_name;
	/**
	 * 汇率
	 */
	@TableField(value = "exchange_rate")
	private BigDecimal exchange_rate;
	/**
	 * 币种ID
	 */
	@TableField(value = "currency_id")
	private String currency_id;
	/**
	 * 币种代码
	 */
	@TableField(value = "currency_code")
	private String currency_code;
	/**
	 * 币种名称
	 */
	@TableField(value = "currency_desc")
	private String currency_desc;
	/**
	 * 含税单价
	 */
	@TableField(value = "unit_price")
	private BigDecimal unit_price;
	/**
	 * 税额
	 */
	@TableField(value = "tax_amount")
	private BigDecimal tax_amount;
	/**
	 * 不含税单�?	 */
	@TableField(value = "no_tax_price")
	private BigDecimal no_tax_price;
	/**
	 * 不含税金�?	 */
	@TableField(value = "no_tax_amount")
	private BigDecimal no_tax_amount;
	/**
	 * 本位币ID
	 */
	@TableField(value = "base_currency_id")
	private String base_currency_id;
	/**
	 * 本位币代�?	 */
	@TableField(value = "base_currency_code")
	private String base_currency_code;
	/**
	 * 本位币名�?	 */
	@TableField(value = "base_currency_desc")
	private String base_currency_desc;
	/**
	 * 本位币含税单�?	 */
	@TableField(value = "base_tax_price")
	private BigDecimal base_tax_price;
	/**
	 * 本位币含税金�?	 */
	@TableField(value = "base_tax_money")
	private BigDecimal base_tax_money;
	/**
	 * 是否赠品
	 */
	@TableField(value = "gift")
	private String gift;
	/**
	 * 仓库ID
	 */
	@TableField(value = "warehouse_id")
	private String warehouse_id;
	/**
	 * 仓库代码
	 */
	@TableField(value = "warehouse_code")
	private String warehouse_code;
	/**
	 * 仓库描述
	 */
	@TableField(value = "warehouse_name")
	private String warehouse_name;
	/**
	 * 货区
	 */
	@TableField(value = "rack")
	private String rack;
	/**
	 * 项目号ID
	 */
	@TableField(value = "project_id")
	private String project_id;
	/**
	 * 项目�?	 */
	@TableField(value = "project_no")
	private String project_no;
	/**
	 * 项目名称
	 */
	@TableField(value = "project_name")
	private String project_name;
	/**
	 * 工作令号
	 */
	@TableField(value = "work_order")
	private String work_order;
	/**
	 * 批号
	 */
	@TableField(value = "batch_no")
	private String batch_no;
	/**
	 * 是否生成出（退）库�?	 */
	@TableField(value = "is_stock")
	private String is_stock;
	/**
	 * 累计报检数量
	 */
	@TableField(value = "inspection_qty")
	private BigDecimal inspection_qty;
	/**
	 * 累计出（退）库数量
	 */
	@TableField(value = "store_qty")
	private BigDecimal store_qty;
	/**
	 * 累计检验合格数�?	 */
	@TableField(value = "quality_qty")
	private BigDecimal quality_qty;
	/**
	 * 单据行是否关�?	 */
	@TableField(value = "is_close")
	private String is_close;
	/**
	 * 是否检验完�?	 */
	@TableField(value = "is_check")
	private String is_check;
	/**
	 * 退货原�?	 */
	@TableField(value = "back_reason")
	private String back_reason;
	/**
	 * 备注
	 */
	@TableField(value = "notes")
	private String notes;
	/**
	 * 生产厂家
	 */
	@TableField(value = "manufacturer")
	private String manufacturer;
	/**
	 * 生产日期
	 */
	@TableField(value = "prod_date")
	private String prod_date;
	/**
	 * 单据行状�?	 */
	@TableField(value = "line_status")
	private String line_status;
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

	@Override
	public Serializable pkVal() {
		return this.uu_id;
	}

}