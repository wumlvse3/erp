package com.jcloud.erp.epd.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
/**
 * Copyright erp
 *
 * @version V1.0.0
 * @author: wxc
 * @date: 2026-05-11
 * @description: EPDBOM主数据实体类
 */
@Data
@TableName("epd_bom_history")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpdBomHistory extends Model<EpdBomHistory> {
    public EpdBomHistory() {}
    @JsonAlias({"uu_id"})
    @TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
     private Integer uu_id;
        /**
         * 所属集团
         */
        @TableField("group_id")
        @JsonAlias({"group_id"})
        private String groupId;

        /**
         * 变更序号
         */
        @TableField("seq_no")
        @JsonAlias({"seq_no"})
        private Integer seqNo;

        /**
         * 父项代码
         */
        @TableField(value = "parent_code")
        @JsonAlias({"parent_code"})
        private String parentCode;

        /**
         * 子项代码
         */
        @JsonAlias({"component_code"})
        @TableField(value = "component_code")
        private String componentCode;

        /**
         * 起始有效日期
         */
        @JsonAlias({"effective_date"})
        @TableField("effective_date")
        private String effectiveDate;

        /**
         * 终止有效日期

         */
        @TableField("discon_date")
        @JsonAlias({"discon_date"})
        private String disconDate;

        /**
         * 偏置天数
         */
        @TableField("offset_time")
        @JsonAlias({"offset_time"})
        private String offsetTime;

        /**
         * 废品率
         */
        @TableField("scrap_rate")
        @JsonAlias({"scrap_rate"})
        private String scrapRate;

        /**
         * 消耗定额
         */
        @JsonAlias({"usage_factor"})
        @TableField("usage_factor")
        private String usageFactor;

        /**
         * 倒冲标记
         */
        @JsonAlias({"backflush_flag"})
        @TableField("backflush_flag")
        private String backflushFlag;

        /**
         * 倒冲仓库
         */
        @TableField("backflush_warehouse")
        @JsonAlias({"backflush_warehouse"})
        private String backflushWarehouse;

        /**
         * 倒冲货位
         */
        @TableField("backflush_bin")
        @JsonAlias({"backflush_bin"})
        private String backflushBin;

        /**
         * 配送地点
         */
        @JsonAlias({"delivery_location"})
        @TableField("delivery_location")
        private String deliveryLocation;

        /**
         * 操作用户ID
         */
        @JsonAlias({"operate_user_id"})
        @TableField("operate_user_id")
        private String operateUserId;

        /**
         * 操作用户代码
         */
        @JsonAlias({"operate_user_code"})
        @TableField("operate_user_code")
        private String operateUserCode;

        /**
         * 操作用户
         */
        @JsonAlias({"operate_user_name"})
        @TableField("operate_user_name")
        private String operateUserName;

        /**
         * 操作时间
         */
        @JsonAlias({"operate_time"})
        @TableField("operate_time")
        private String operateTime;

        /**
         * 操作类型，I-新增；D-删除；U-修改
         */
        @JsonAlias({"operate_type"})
        @TableField("operate_type")
        private String operateType;

        /**
         * 领料标记
         */
        @JsonAlias({"pick_flag"})
        @TableField("pick_flag")
        private String pickFlag;

        /**
         * 项目号
         */
        @JsonAlias({"project_no"})
        @TableField("project_no")
        private String projectNo;

        /**
         * 变更原因
         */
        @JsonAlias({"change_reason"})
        @TableField("change_reason")
        private String changeReason;

        /**
         * 备注
         */
        @JsonAlias({"remark"})
        @TableField("remark")
        private String remark;

        /**
         * 库存组织
         */
        @JsonAlias({"inv_org"})
        @TableField(value = "inv_org")
        private String invOrg;
    }
