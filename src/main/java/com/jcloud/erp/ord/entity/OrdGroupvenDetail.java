package com.jcloud.erp.ord.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 集团客户分配明细实体类
 * 对应表：ord_groupven_detail
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ord_groupven_detail")
public class OrdGroupvenDetail implements Serializable {

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
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 销售组织
     */
    @TableField("sales_org")
    private String salesOrg;

    /**
     * 销售组ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 销售组编码
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 销售组名称
     */
    @TableField("role_name")
    private String roleName;

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
     * 是否默认
     */
    @TableField("is_default")
    private String isDefault;

    /**
     * 单据行是否关闭
     */
    @TableField("is_close")
    private String isClose;

    /**
     * 是否选择
     */
    @TableField("is_select")
    private String isSelect;

}