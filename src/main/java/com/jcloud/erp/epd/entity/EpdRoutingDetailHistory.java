package com.jcloud.erp.epd.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * 工艺路线明细表历史记录实体类
 */
@Data
@TableName("epd_routing_detail_history")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpdRoutingDetailHistory extends Model<EpdRoutingDetailHistory> {

    /**
     * 主键
     */
    @TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    @JsonAlias("uu_id")
    private Integer uuId;

    /**
     * 所属集团
     */
    @TableField("group_id")
    @JsonAlias("group_id")
    private String groupId;

    /**
     * 工艺路线代码
     */
    @TableField("routing_no")
    @JsonAlias("routing_no")
    private String routingNo;

    /**
     * 变更序号
     */
    @TableField("seq_no")
    @JsonAlias("seq_no")
    private Integer seqNo;

    /**
     * 操作类型
     */
    @TableField("operate_type")
    @JsonAlias("operate_type")
    private String operateType;

    /**
     * 操作人ID
     */
    @TableField("operate_user_id")
    @JsonAlias("operate_user_id")
    private String operateUserId;

    /**
     * 操作人编码
     */
    @TableField("operate_user_code")
    @JsonAlias("operate_user_code")
    private String operateUserCode;

    /**
     * 操作人名称
     */
    @TableField("operate_user_name")
    @JsonAlias("operate_user_name")
    private String operateUserName;

    /**
     * 操作时间
     */
    @TableField("operate_time")
    @JsonAlias("operate_time")
    private Date operateTime;

    /**
     * 变更原因
     */
    @TableField("change_remark")
    @JsonAlias("change_remark")
    private String changeRemark;

    /**
     * 加工工序号
     */
    @TableField("op_no")
    @JsonAlias("op_no")
    private Integer opNo;

    /**
     * 加工工序名称
     */
    @TableField("op_name")
    @JsonAlias("op_name")
    private String opName;

    /**
     * 加工工序简称
     */
    @TableField("op_abb")
    @JsonAlias("op_abb")
    private String opAbb;

    /**
     * 工作中心代码
     */
    @TableField("wc_code")
    @JsonAlias("wc_code")
    private String wcCode;

    /**
     * 工序说明
     */
    @TableField("op_desc")
    @JsonAlias("op_desc")
    private String opDesc;

    /**
     * 是否可排计划
     */
    @TableField("shop_op")
    @JsonAlias("shop_op")
    private String shopOp;

    /**
     * 工种代码
     */
    @TableField("job_type")
    @JsonAlias("job_type")
    private String jobType;

    /**
     * 同时加工机床数
     */
    @TableField("machin_num")
    @JsonAlias("machin_num")
    private Integer machinNum;

    /**
     * 最小传送数量
     */
    @TableField("min_trans_qty")
    @JsonAlias("min_trans_qty")
    private Float minTransQty;

    /**
     * 交叉作业标识
     */
    @TableField("overlap_flag")
    @JsonAlias("overlap_flag")
    private String overlapFlag;

    /**
     * 加工时间
     */
    @TableField("run_time")
    @JsonAlias("run_time")
    private Float runTime;

    /**
     * 加工时间单位
     */
    @TableField("run_unit")
    @JsonAlias("run_unit")
    private String runUnit;

    /**
     * 准备时间
     */
    @TableField("setup_time")
    @JsonAlias("setup_time")
    private Float setupTime;

    /**
     * 准备时间单位
     */
    @TableField("setup_unit")
    @JsonAlias("setup_unit")
    private String setupUnit;

    /**
     * 工装描述
     */
    @TableField("tool_desc")
    @JsonAlias("tool_desc")
    private String toolDesc;

    /**
     * 有无工装标识
     */
    @TableField("tool_id")
    @JsonAlias("tool_id")
    private String toolId;

    /**
     * 转运时间
     */
    @TableField("transfer_time")
    @JsonAlias("transfer_time")
    private Float transferTime;

    /**
     * 转运时间单位
     */
    @TableField("transfer_unit")
    @JsonAlias("transfer_unit")
    private String transferUnit;

    /**
     * 其它时间
     */
    @TableField("other_time")
    @JsonAlias("other_time")
    private Float otherTime;

    /**
     * 其它时间单位
     */
    @TableField("other_unit")
    @JsonAlias("other_unit")
    private String otherUnit;

    /**
     * 外协标记
     */
    @TableField("out_op_id")
    @JsonAlias("out_op_id")
    private String outOpId;

    /**
     * 外协小时数
     */
    @TableField("out_op_time")
    @JsonAlias("out_op_time")
    private Float outOpTime;

    /**
     * 专检标记
     */
    @TableField("quality_flag")
    @JsonAlias("quality_flag")
    private String qualityFlag;

    /**
     * 自检标记
     */
    @TableField("self_check_flag")
    @JsonAlias("self_check_flag")
    private String selfCheckFlag;

    /**
     * 互检标记
     */
    @TableField("mutual_check_flag")
    @JsonAlias("mutual_check_flag")
    private String mutualCheckFlag;

    /**
     * 首件检标记
     */
    @TableField("first_check_flag")
    @JsonAlias("first_check_flag")
    private String firstCheckFlag;

    /**
     * 数控程序名称
     */
    @TableField("nc_code")
    @JsonAlias("nc_code")
    private String ncCode;

    /**
     * 合装标识
     */
    @TableField("cooperation_flag")
    @JsonAlias("cooperation_flag")
    private String cooperationFlag;

    /**
     * 参考图形
     */
    @TableField("refer")
    @JsonAlias("refer")
    private String refer;

    /**
     * 参考说明
     */
    @TableField("ref_text")
    @JsonAlias("ref_text")
    private String refText;

    /**
     * 偏置时间
     */
    @TableField("divert_time")
    @JsonAlias("divert_time")
    private Integer divertTime;

    /**
     * 偏置时间单位
     */
    @TableField("divert_unit")
    @JsonAlias("divert_unit")
    private String divertUnit;

    /**
     * 超额系数
     */
    @TableField("over_load")
    @JsonAlias("over_load")
    private String overLoad;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonAlias("create_time")
    private String createTime;

    /**
     * 创建人ID
     */
    @TableField("create_user_id")
    @JsonAlias("create_user_id")
    private String createUserId;

    /**
     * 创建人代码
     */
    @TableField("create_user_code")
    @JsonAlias("create_user_code")
    private String createUserCode;

    /**
     * 创建人
     */
    @TableField("create_user_name")
    @JsonAlias("create_user_name")
    private String createUserName;

    /**
     * 创建方式
     */
    @TableField("create_type")
    @JsonAlias("create_type")
    private String createType;

    /**
     * 最新修改时间
     */
    @TableField("modify_time")
    @JsonAlias("modify_time")
    private String modifyTime;

    /**
     * 最新修改人ID
     */
    @TableField("modify_user_id")
    @JsonAlias("modify_user_id")
    private String modifyUserId;

    /**
     * 最新修改人代码
     */
    @TableField("modify_user_code")
    @JsonAlias("modify_user_code")
    private String modifyUserCode;

    /**
     * 最新修改人
     */
    @TableField("modify_user_name")
    @JsonAlias("modify_user_name")
    private String modifyUserName;

    /**
     * 库存组织
     */
    @TableField("inv_org")
    @JsonAlias("inv_org")
    private String invOrg;

    /**
     * 工艺路线头uu_id
     */
    @TableField("routing_uu_id")
    @JsonAlias("routing_uu_id")
    private Integer routingUuId;

    @TableField("file_path")
    @JsonAlias("file_path")
    private String filePath;
}
