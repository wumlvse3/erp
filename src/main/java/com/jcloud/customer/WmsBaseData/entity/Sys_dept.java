package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_dept")
public class Sys_dept {
//    @TableId("dept_id")
    @TableId(type = IdType.AUTO)//数据库自增
    private Integer dept_id;

    /**
     * 父级id
     */
    @TableField(value = "parent_id")
    private Integer parent_id;
    /**
     * 名字
     */
    @TableField(value = "name")
    private String name;
    /**
     * 排序
     */
    @TableField(value = "order_num")
    private Integer order_num;
    /**
     * 组织id
     */
    @TableField(value = "org_id")
    private String org_id;
    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String create_by;
    /**
     * 创建时间
     */
    @TableField(value = "create_date")
    private Date create_date;
    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String update_by;
    /**
     * 修改时间
     */
    @TableField(value = "update_date")
    private Date update_date;

    /**
     * 部门编号
     */
    @TableField(value = "dept_code")
    private String dept_code;

    /**
     * 部门负责人名称
     */
    @TableField(value = "dept_head_name")
    private String dept_head_name;

    /**
     * 部门负责人编码
     */
    @TableField(value = "dept_head_code")
    private String dept_head_code;

    @TableField(value = "deleted")
    private Boolean deleted;
    @TableField(value = "third_part_id")
    private String third_part_id; //第三方id
}
