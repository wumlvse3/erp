package com.jcloud.erp.ord.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 集团客户分配主表/项实体类
 * 对应表：ordgroup_item
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ordgroup_item")
public class OrdgroupItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "uu_id", type = IdType.AUTO)
    private Integer uuId;

    /**
     * 集团ID
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 创建人ID
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    @TableField("modify_user_id")
    private Long modifyUserId;

    /**
     * 更新时间
     */
    @TableField("modify_time")
    private LocalDateTime modifyTime;

    /**
     * 工作流ID
     */
    @TableField("flow_id")
    private String flowId;

    /**
     * 逻辑删除标记 (0-未删除, 1-已删除)
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 销售组织
     */
    @TableField("sales_org")
    private String salesOrg;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 客户编码
     */
    @TableField("customer_code")
    private String customerCode;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 分配方式
     */
    @TableField("distribute")
    private String distribute;

    /**
     * 备注
     */
    @TableField("notes")
    private String notes;

    /**
     * 供应商分类ID
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 客户分类编码
     */
    @TableField("class_code")
    private String classCode;

    /**
     * 客户分类名称
     */
    @TableField("class_name")
    private String className;

}