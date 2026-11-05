package com.jcloud.epd.entity;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright erp
 *
 * @version V1.0.0
 * @author: wxc
 * @date: 2026-05-11
 * @description: EPD物料主数据实体类
 */
@Data
@TableName("epd_item_master_history")
@ToString
public class EpdItemMasterHistory extends Model<EpdItemMasterHistory> {
    public EpdItemMasterHistory() {}
    /**
     * 主键
     */
    @TableId(value = "uu_id", type = IdType.AUTO)
    private Integer uu_id;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id")
    private String create_user_id;

    /**
     *顺序号
     */
    @TableField(value = "seq_no")
    private String seq_no;
    /**
     * 操作类型
     */
    @TableField(value = "operate_type")
    private String operate_type;
    /**
     * 集团ID
     */
    @TableField(value = "group_id")
    private String group_id;

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
     * 库存组织
     */
    @TableField(value = "inv_org")
    private String inv_org;

    /**
     * 选择标记
     */
    @TableField(value = "select_flag")
    private String select_flag;

    /**
     * 物料代码
     */
    @TableField(value = "item_code")
    private String item_code;

    /**
     * 物料名称
     */
    @TableField(value = "item_name")
    private String item_name;

    /**
     * 物料简称
     */
    @TableField(value = "item_abb")
    private String item_abb;

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
     * 图号
     */
    @TableField(value = "draw_no")
    private String draw_no;

    /**
     * 国标号
     */
    @TableField(value = "nation_code")
    private String nation_code;

    /**
     * 助记码
     */
    @TableField(value = "mnemonic_code")
    private String mnemonic_code;

    /**
     * 参考号
     */
    @TableField(value = "ref_text")
    private String ref_text;

    /**
     * 单件体积
     */
    @TableField(value = "item_size")
    private Float item_size;

    /**
     * 体积单位
     */
    @TableField(value = "size_unit")
    private String size_unit;

    /**
     * 单件重量
     */
    @TableField(value = "item_weight")
    private Float item_weight;

    /**
     * 重量单位
     */
    @TableField(value = "weight_unit")
    private String weight_unit;

    /**
     * 提前期
     */
    @TableField(value = "lead_time")
    private Integer lead_time;

    /**
     * 提前期种类
     */
    @TableField(value = "lead_time_type")
    private String lead_time_type;

    /**
     * 动态系数
     */
    @TableField(value = "lead_dynamic")
    private Integer lead_dynamic;

    /**
     * 计划填补量
     */
    @TableField(value = "plan_makeup")
    private Integer plan_makeup;

    /**
     * 低层码
     */
    @TableField(value = "llc")
    private Integer llc;

    /**
     * 批量政策
     */
    @TableField(value = "lot_policy")
    private String lot_policy;

    /**
     * 批量数量
     */
    @TableField(value = "lot_qty")
    private Float lot_qty;

    /**
     * 提前期批量
     */
    @TableField(value = "lot_size")
    private Float lot_size;

    /**
     * 物料类型
     */
    @TableField(value = "item_type")
    private String item_type;

    /**
     * 外协带料标识
     */
    @TableField(value = "out_op_db")
    private String out_op_db;

    /**
     * 制造/采购标记
     */
    @TableField(value = "m_p_flag")
    private String m_p_flag;

    /**
     * MPS标记
     */
    @TableField(value = "mps_flag")
    private String mps_flag;

    /**
     * MRP标记
     */
    @TableField(value = "mrp_flag")
    private String mrp_flag;

    /**
     * ABC分类码
     */
    @TableField(value = "abc_code")
    private String abc_code;

    /**
     * 统计分类
     */
    @TableField(value = "item_class")
    private String item_class;

    /**
     * 采购分类
     */
    @TableField(value = "pur_class")
    private String pur_class;

    /**
     * 销售分类
     */
    @TableField(value = "sale_class")
    private String sale_class;

    /**
     * 库存分类
     */
    @TableField(value = "stock_class")
    private String stock_class;

    /**
     * 质量分类
     */
    @TableField(value = "quality_class")
    private String quality_class;

    /**
     * 财务分类
     */
    @TableField(value = "finance_class")
    private String finance_class;

    /**
     * 质检标记
     */
    @TableField(value = "quality_flag")
    private String quality_flag;

    /**
     * 专用分类
     */
    @TableField(value = "specialized_class_code")
    private String specialized_class_code;

    /**
     * 成本核算对象
     */
    @TableField(value = "gl_class")
    private String gl_class;

    /**
     * 材料差异分类码
     */
    @TableField(value = "variance_code")
    private String variance_code;

    /**
     * 标准成本
     */
    @TableField(value = "std_cost")
    private String std_cost;

    /**
     * 标准单价
     */
    @TableField(value = "unit_price")
    private Float unit_price;

    /**
     * 计划价格
     */
    @TableField(value = "plan_price")
    private Float plan_price;

    /**
     * 实际成本
     */
    @TableField(value = "actual_cost")
    private Float actual_cost;

    /**
     * 价格体系
     */
    @TableField(value = "price_system")
    private String price_system;

    /**
     * 计划员
     */
    @TableField(value = "planner_code")
    private String planner_code;

    /**
     * 采购员
     */
    @TableField(value = "buyer_code")
    private String buyer_code;

    /**
     * 订货点数量
     */
    @TableField(value = "order_point")
    private Float order_point;

    /**
     * 再订货数量
     */
    @TableField(value = "reorder_qty")
    private Float reorder_qty;

    /**
     * 采购单位
     */
    @TableField(value = "pur_unit")
    private String pur_unit;

    /**
     * 销售单位
     */
    @TableField(value = "sale_unit")
    private String sale_unit;

    /**
     * 计量单位
     */
    @TableField(value = "stock_unit")
    private String stock_unit;

    /**
     * 辅助计量单位
     */
    @TableField(value = "assist_unit")
    private String assist_unit;

    /**
     * 第二计量单位
     */
    @TableField(value = "second_unit")
    private String second_unit;

    /**
     * 合理储备天数
     */
    @TableField(value = "valid_stock_days")
    private Integer valid_stock_days;

    /**
     * 有效期天数
     */
    @TableField(value = "validity_day")
    private Integer validity_day;

    /**
     * 责任部门
     */
    @TableField(value = "dept_code")
    private String dept_code;

    /**
     * 工艺路线号
     */
    @TableField(value = "routing_no")
    private String routing_no;

    /**
     * 副产品标记
     */
    @TableField(value = "bulk_issue_id")
    private String bulk_issue_id;

    /**
     * 废品率
     */
    @TableField(value = "scrap_rate")
    private Float scrap_rate;

    /**
     * 默认仓库
     */
    @TableField(value = "warehouse")
    private String warehouse;

    /**
     * 默认货位
     */
    @TableField(value = "bin")
    private String bin;

    /**
     * 出库模式
     */
    @TableField(value = "outbound_mode")
    private String outbound_mode;

    /**
     * 批次标记
     */
    @TableField(value = "lot_flag")
    private String lot_flag;

    /**
     * 有效期管理标记
     */
    @TableField(value = "validity_flag")
    private String validity_flag;

    /**
     * 单件管理标记
     */
    @TableField(value = "single_flag")
    private String single_flag;

    /**
     * 安全库存
     */
    @TableField(value = "safety_stock")
    private Float safety_stock;

    /**
     * 最高储备
     */
    @TableField(value = "max_stock")
    private Float max_stock;

    /**
     * 最低储备
     */
    @TableField(value = "min_stock")
    private Float min_stock;

    /**
     * 盘点周期天数
     */
    @TableField(value = "cycle_count")
    private Integer cycle_count;

    /**
     * 库存允差
     */
    @TableField(value = "tolenance")
    private Float tolenance;

    /**
     * 税码代码
     */
    @TableField(value = "tax_code")
    private String tax_code;

    /**
     * 取整倍数
     */
    @TableField(value = "round_factor")
    private Integer round_factor;

    /**
     * 采购/库存换算比
     */
    @TableField(value = "unit_factor_p")
    private Float unit_factor_p;

    /**
     * 销售/库存换算比
     */
    @TableField(value = "unit_factor_s")
    private Float unit_factor_s;

    /**
     * 第二计量/库存换算比
     */
    @TableField(value = "unit_factor_2")
    private Float unit_factor_2;

    /**
     * 分配人ID
     */
    @TableField(value = "assign_user_id")
    private String assign_user_id;

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
     * 分配时间
     */
    @TableField(value = "assign_time")
    private Date assign_time;

    /**
     * 分配来源组织
     */
    @TableField(value = "assign_from_org")
    private String assign_from_org;

    /**
     * 关主件标记
     */
    @TableField(value = "master_part_flag")
    private String master_part_flag;

    /**
     * 工序领料
     */
    @TableField(value = "op_pick_flag")
    private String op_pick_flag;

    /**
     * 限额领料标记
     */
    @TableField(value = "limite_flag")
    private String limite_flag;

    /**
     * 超额领料系数
     */
    @TableField(value = "excess_material")
    private Integer excess_material;

    /**
     * 倒冲标记
     */
    @TableField(value = "backflush_flag")
    private String backflush_flag;

    /**
     * 倒冲仓库
     */
    @TableField(value = "backflush_warehouse")
    private String backflush_warehouse;

    /**
     * 倒冲货位
     */
    @TableField(value = "backflush_bin")
    private String backflush_bin;

    /**
     * 工序计划标记
     */
    @TableField(value = "routing_flag")
    private String routing_flag;

    /**
     * 下达车间标记
     */
    @TableField(value = "shop_flag")
    private String shop_flag;

    /**
     * 外协标记
     */
    @TableField(value = "out_op_id")
    private String out_op_id;

    /**
     * 关键件大类
     */
    @TableField(value = "master_part_class")
    private String master_part_class;

    /**
     * 质量跟踪方式
     */
    @TableField(value = "qc_track_type")
    private String qc_track_type;

    /**
     * 产品定额工时
     */
    @TableField(value = "norm_hour")
    private Integer norm_hour;

    /**
     * 特征码
     */
    @TableField(value = "special_code")
    private String special_code;

    /**
     * 偏置天数
     */
    @TableField(value = "deviation_days")
    private Integer deviation_days;

    /**
     * 合批标记
     */
    @TableField(value = "if_nosum_lot")
    private String if_nosum_lot;

    /**
     * 可采购
     */
    @TableField(value = "pur_flag")
    private String pur_flag;

    /**
     * 可销售
     */
    @TableField(value = "sale_flag")
    private String sale_flag;

    /**
     * 资源合批标记
     */
    @TableField(value = "if_merge_mp")
    private String if_merge_mp;

    /**
     * 工作令合批标记
     */
    @TableField(value = "if_merge_work_order")
    private String if_merge_work_order;

    /**
     * 完整性确认标记
     */
    @TableField(value = "integrity_flag")
    private String integrity_flag;

    /**
     * 有效标记
     */
    @TableField(value = "valid_flag")
    private String valid_flag;

    /**
     * 创建方式
     */
    @TableField(value = "create_type")
    private String create_type;

    /**
     * 物料加成率
     */
    @TableField(value = "pur_group_ratio")
    private Float pur_group_ratio;

    /**
     * 集采标记
     */
    @TableField(value = "pur_group_flag")
    private String pur_group_flag;

    /**
     * 集采采购组织
     */
    @TableField(value = "pur_org")
    private String pur_org;


}
