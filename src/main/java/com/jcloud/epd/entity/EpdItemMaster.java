package com.jcloud.epd.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Copyright erp
 *
 * @version V1.0.0
 * @author: wxc
 * @date: 2026-05-11
 * @description: EPD物料主数据实体类
 */
@Data
@TableName("epd_item_master")
@ToString
public class EpdItemMaster extends Model<EpdItemMaster> {
    public EpdItemMaster() {}
    /**
     * 主键
     */
    @TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer uuId;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id")
    private String createUserId;

    /**
     * 集团ID
     */
    @TableField(value = "group_id")
    private String groupId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新人ID
     */
    @TableField(value = "modify_user_id")
    private String modifyUserId;

    /**
     * 更新时间
     */
    @TableField(value = "modify_time", fill = FieldFill.UPDATE)
    private Date modifyTime;

    /**
     * 工作流ID
     */
    @TableField(value = "flow_id")
    private String flowId;

    /**
     * 逻辑删除标记
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 库存组织
     */
    @TableField(value = "inv_org")
    private String invOrg;

    /**
     * 选择标记
     */
    @TableField(value = "select_flag")
    private String selectFlag;

    /**
     * 物料代码
     */
    @TableField(value = "item_code")
    private String itemCode;

    /**
     * 物料名称
     */
    @TableField(value = "item_name")
    private String itemName;

    /**
     * 物料简称
     */
    @TableField(value = "item_abb")
    private String itemAbb;

    /**
     * 物料规格
     */
    @TableField(value = "item_spec")
    private String itemSpec;

    /**
     * 物料型号
     */
    @TableField(value = "item_model")
    private String itemModel;

    /**
     * 图号
     */
    @TableField(value = "draw_no")
    private String drawNo;

    /**
     * 国标号
     */
    @TableField(value = "nation_code")
    private String nationCode;

    /**
     * 助记码
     */
    @TableField(value = "mnemonic_code")
    private String mnemonicCode;

    /**
     * 参考号
     */
    @TableField(value = "ref_text")
    private String refText;

    /**
     * 单件体积
     */
    @TableField(value = "item_size")
    private Float itemSize;

    /**
     * 体积单位
     */
    @TableField(value = "size_unit")
    private String sizeUnit;

    /**
     * 单件重量
     */
    @TableField(value = "item_weight")
    private Float itemWeight;

    /**
     * 重量单位
     */
    @TableField(value = "weight_unit")
    private String weightUnit;

    /**
     * 提前期
     */
    @TableField(value = "lead_time")
    private Integer leadTime;

    /**
     * 提前期种类
     */
    @TableField(value = "lead_time_type")
    private String leadTimeType;

    /**
     * 动态系数
     */
    @TableField(value = "lead_dynamic")
    private Integer leadDynamic;

    /**
     * 计划填补量
     */
    @TableField(value = "plan_makeup")
    private Integer planMakeup;

    /**
     * 低层码
     */
    @TableField(value = "llc")
    private Integer llc;

    /**
     * 批量政策
     */
    @TableField(value = "lot_policy")
    private String lotPolicy;

    /**
     * 批量数量
     */
    @TableField(value = "lot_qty")
    private Float lotQty;

    /**
     * 提前期批量
     */
    @TableField(value = "lot_size")
    private Float lotSize;

    /**
     * 物料类型
     */
    @TableField(value = "item_type")
    private String itemType;

    /**
     * 外协带料标识
     */
    @TableField(value = "out_op_db")
    private String outOpDb;

    /**
     * 制造/采购标记
     */
    @TableField(value = "m_p_flag")
    private String mpFlag;

    /**
     * MPS标记
     */
    @TableField(value = "mps_flag")
    private String mpsFlag;

    /**
     * MRP标记
     */
    @TableField(value = "mrp_flag")
    private String mrpFlag;

    /**
     * ABC分类码
     */
    @TableField(value = "abc_code")
    private String abcCode;

    /**
     * 统计分类
     */
    @TableField(value = "item_class")
    private String itemClass;

    /**
     * 采购分类
     */
    @TableField(value = "pur_class")
    private String purClass;

    /**
     * 销售分类
     */
    @TableField(value = "sale_class")
    private String saleClass;

    /**
     * 库存分类
     */
    @TableField(value = "stock_class")
    private String stockClass;

    /**
     * 质量分类
     */
    @TableField(value = "quality_class")
    private String qualityClass;

    /**
     * 财务分类
     */
    @TableField(value = "finance_class")
    private String financeClass;

    /**
     * 质检标记
     */
    @TableField(value = "quality_flag")
    private String qualityFlag;

    /**
     * 专用分类
     */
    @TableField(value = "specialized_class_code")
    private String specializedClassCode;

    /**
     * 成本核算对象
     */
    @TableField(value = "gl_class")
    private String glClass;

    /**
     * 材料差异分类码
     */
    @TableField(value = "variance_code")
    private String varianceCode;

    /**
     * 标准成本
     */
    @TableField(value = "std_cost")
    private String stdCost;

    /**
     * 标准单价
     */
    @TableField(value = "unit_price")
    private Float unitPrice;

    /**
     * 计划价格
     */
    @TableField(value = "plan_price")
    private Float planPrice;

    /**
     * 实际成本
     */
    @TableField(value = "actual_cost")
    private Float actualCost;

    /**
     * 价格体系
     */
    @TableField(value = "price_system")
    private String priceSystem;

    /**
     * 计划员
     */
    @TableField(value = "planner_code")
    private String plannerCode;

    /**
     * 采购员
     */
    @TableField(value = "buyer_code")
    private String buyerCode;

    /**
     * 订货点数量
     */
    @TableField(value = "order_point")
    private Float orderPoint;

    /**
     * 再订货数量
     */
    @TableField(value = "reorder_qty")
    private Float reorderQty;

    /**
     * 采购单位
     */
    @TableField(value = "pur_unit")
    private String purUnit;

    /**
     * 销售单位
     */
    @TableField(value = "sale_unit")
    private String saleUnit;

    /**
     * 计量单位
     */
    @TableField(value = "stock_unit")
    private String stockUnit;

    /**
     * 辅助计量单位
     */
    @TableField(value = "assist_unit")
    private String assistUnit;

    /**
     * 第二计量单位
     */
    @TableField(value = "second_unit")
    private String secondUnit;

    /**
     * 合理储备天数
     */
    @TableField(value = "valid_stock_days")
    private Integer validStockDays;

    /**
     * 有效期天数
     */
    @TableField(value = "validity_day")
    private Integer validityDay;

    /**
     * 责任部门
     */
    @TableField(value = "dept_code")
    private String deptCode;

    /**
     * 工艺路线号
     */
    @TableField(value = "routing_no")
    private String routingNo;

    /**
     * 副产品标记
     */
    @TableField(value = "bulk_issue_id")
    private String bulkIssueId;

    /**
     * 废品率
     */
    @TableField(value = "scrap_rate")
    private Float scrapRate;

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
    private String outboundMode;

    /**
     * 批次标记
     */
    @TableField(value = "lot_flag")
    private String lotFlag;

    /**
     * 有效期管理标记
     */
    @TableField(value = "validity_flag")
    private String validityFlag;

    /**
     * 单件管理标记
     */
    @TableField(value = "single_flag")
    private String singleFlag;

    /**
     * 安全库存
     */
    @TableField(value = "safety_stock")
    private Float safetyStock;

    /**
     * 最高储备
     */
    @TableField(value = "max_stock")
    private Float maxStock;

    /**
     * 最低储备
     */
    @TableField(value = "min_stock")
    private Float minStock;

    /**
     * 盘点周期天数
     */
    @TableField(value = "cycle_count")
    private Integer cycleCount;

    /**
     * 库存允差
     */
    @TableField(value = "tolenance")
    private Float tolenence;

    /**
     * 税码代码
     */
    @TableField(value = "tax_code")
    private String taxCode;

    /**
     * 取整倍数
     */
    @TableField(value = "round_factor")
    private Integer roundFactor;

    /**
     * 采购/库存换算比
     */
    @TableField(value = "unit_factor_p")
    private Float unitFactorP;

    /**
     * 销售/库存换算比
     */
    @TableField(value = "unit_factor_s")
    private Float unitFactorS;

    /**
     * 第二计量/库存换算比
     */
    @TableField(value = "unit_factor_2")
    private Float unitFactor2;

    /**
     * 分配人ID
     */
    @TableField(value = "assign_user_id")
    private Long assignUserId;

    /**
     * 分配人代码
     */
    @TableField(value = "assign_user_code")
    private String assignUserCode;

    /**
     * 分配人
     */
    @TableField(value = "assign_user_man")
    private String assignUserMan;

    /**
     * 分配时间
     */
    @TableField(value = "assign_time")
    private Date assignTime;

    /**
     * 分配来源组织
     */
    @TableField(value = "assign_from_org")
    private String assignFromOrg;

    /**
     * 关主件标记
     */
    @TableField(value = "master_part_flag")
    private String masterPartFlag;

    /**
     * 工序领料
     */
    @TableField(value = "op_pick_flag")
    private String opPickFlag;

    /**
     * 限额领料标记
     */
    @TableField(value = "limite_flag")
    private String limiteFlag;

    /**
     * 超额领料系数
     */
    @TableField(value = "excess_material")
    private Integer excessMaterial;

    /**
     * 倒冲标记
     */
    @TableField(value = "backflush_flag")
    private String backflushFlag;

    /**
     * 倒冲仓库
     */
    @TableField(value = "backflush_warehouse")
    private String backflushWarehouse;

    /**
     * 倒冲货位
     */
    @TableField(value = "backflush_bin")
    private String backflushBin;

    /**
     * 工序计划标记
     */
    @TableField(value = "routing_flag")
    private String routingFlag;

    /**
     * 下达车间标记
     */
    @TableField(value = "shop_flag")
    private String shopFlag;

    /**
     * 外协标记
     */
    @TableField(value = "out_op_id")
    private String outOpId;

    /**
     * 关键件大类
     */
    @TableField(value = "master_part_class")
    private String masterPartClass;

    /**
     * 质量跟踪方式
     */
    @TableField(value = "qc_track_type")
    private String qcTrackType;

    /**
     * 产品定额工时
     */
    @TableField(value = "norm_hour")
    private Integer normHour;

    /**
     * 特征码
     */
    @TableField(value = "special_code")
    private String specialCode;

    /**
     * 偏置天数
     */
    @TableField(value = "deviation_days")
    private Integer deviationDays;

    /**
     * 合批标记
     */
    @TableField(value = "if_nosum_lot")
    private String ifNosumLot;

    /**
     * 可采购
     */
    @TableField(value = "pur_flag")
    private String purFlag;

    /**
     * 可销售
     */
    @TableField(value = "sale_flag")
    private String saleFlag;

    /**
     * 资源合批标记
     */
    @TableField(value = "if_merge_mp")
    private String ifMergeMp;

    /**
     * 工作令合批标记
     */
    @TableField(value = "if_merge_work_order")
    private String ifMergeWorkOrder;

    /**
     * 完整性确认标记
     */
    @TableField(value = "integrity_flag")
    private String integrityFlag;

    /**
     * 有效标记
     */
    @TableField(value = "valid_flag")
    private String validFlag;

    /**
     * 创建方式
     */
    @TableField(value = "create_type")
    private String createType;

    /**
     * 物料加成率
     */
    @TableField(value = "pur_group_ratio")
    private Float purGroupRatio;

    /**
     * 集采标记
     */
    @TableField(value = "pur_group_flag")
    private String purGroupFlag;

    /**
     * 集采采购组织
     */
    @TableField(value = "pur_org")
    private String purOrg;

    /**
     * 文件路径
     */
    @TableField(value = "file_path")
    private String filePath;
}
