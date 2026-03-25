package com.jcloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单显示
 * @Auther: yang
 * @Date: 2023/04/12/15:26
 * @Description:
 */
@Data
@TableName("sys_user_role")
public class UserRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(type =  IdType.AUTO)
    private Long id;
    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "role_id")
    private String roleId;
    @TableField(value = "org_id")
    private String orgId;


}
