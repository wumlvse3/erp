package com.jcloud.epd.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 工艺路线头表历史记录实体类
 */
@Data
@TableName("epd_routing_head_history")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpdRoutingHeadHistory extends Model<EpdRoutingHeadHistory> {

    /**
     * 主键
     */
    @TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    @JsonAlias( "uu_id")
    private Integer uuId;

    /**
     * 集团ID
     */
    @TableField("group_id")
    @JsonAlias( "group_id")
    private String groupId;

    /**
     * 变更序号
     */
    @TableField("seq_no")
    @JsonAlias( "seq_no")
    private Integer seqNo;

    /**
     * 工艺路线代码
     */
    @TableField("routing_no")
    @JsonAlias( "routing_no")
    private String routingNo;

    /**
     * 操作类型
     */
    @TableField("operate_type")
    @JsonAlias( "operate_type")
    private String operateType;

    /**
     * 操作人ID
     */
    @TableField("operate_user_id")
    @JsonAlias( "operate_user_id")
    private Long operateUserId;

    /**
     * 操作人编码
     */
    @TableField("operate_user_code")
    @JsonAlias( "operate_user_code")
    private String operateUserCode;

    /**
     * 操作人名称
     */
    @TableField("operate_user_name")
    @JsonAlias( "operate_user_name")
    private String operateUserName;

    /**
     * 操作时间
     */
    @TableField("operate_time")
    @JsonAlias( "operate_time")
    private Date operateTime;

    /**
     * 变更原因
     */
    @TableField("change_remark")
    @JsonAlias( "change_remark")
    private String changeRemark;

    /**
     * 工艺路线名称
     */
    @TableField("routing_name")
    @JsonAlias( "routing_name")
    private String routingName;

    /**
     * 工艺路线简称
     */
    @TableField("routing_abb")
    @JsonAlias( "routing_abb")
    private String routingAbb;

    /**
     * 库存组织
     */
    @TableField("inv_org")
    @JsonAlias( "inv_org")
    private String invOrg;

    /**
     * 关联物料代码
     */
    @TableField("item_code")
    @JsonAlias( "item_code")
    private String itemCode;

    /**
     * 工艺类型
     */
    @TableField("routing_type")
    @JsonAlias( "routing_type")
    private String routingType;

    /**
     * 工艺版本
     */
    @TableField("routing_version")
    @JsonAlias( "routing_version")
    private String routingVersion;

    /**
     * 工艺说明
     */
    @TableField("routing_desc")
    @JsonAlias( "routing_desc")
    private String routingDesc;

    /**
     * 创建人ID
     */
    @TableField("create_user_id")
    @JsonAlias( "create_user_id")
    private String createUserId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonAlias( "create_time")
    private Date createTime;

    /**
     * 更新人ID
     */
    @TableField("modify_user_id")
    @JsonAlias( "modify_user_id")
    private String modifyUserId;

    /**
     * 更新时间
     */
    @TableField("modify_time")
    @JsonAlias( "modify_time")
    private Date modifyTime;

    /**
     * 工作流ID
     */
    @TableField("flow_id")
    @JsonAlias( "flow_id")
    private String flowId;
}
