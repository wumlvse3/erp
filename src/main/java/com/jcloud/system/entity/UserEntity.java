package com.jcloud.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: yang
 * @Date: 2023/03/27/9:19
 * @Description:系统用户表
 */
@Data
@TableName("sys_user")
public class UserEntity implements Serializable {
    public static final long serialVersionUID = 1L;

    //    @JsonProperty("user_id") //jackson的注解,用于进行属性格式化!
    @TableId(type = IdType.AUTO, value = "user_id")//数据库自增
    private Long userId;

    private String username;

    private String name;

    //忽略属性 不生成json 不接受json数据  @JsonIgnore
    // @JsonInclude(JsonInclude.Include.NON_NULL)  + null 当这个值不为null的时候生成json,为null不生成
    // 不影响接收json
    @JsonInclude(JsonInclude.Include.NON_NULL)//为空不返回
//    @TableField(exist = false)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "dept_id")
    private String deptId;

    //    电子邮箱
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    //    手机号码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mobile;
    //    是否可用账号
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    //    创建用户id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "user_id_create")
    private Long userIdCreate;
    //    创建时间
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "gmt_create")
    private Date gmtCreate;
    // 修改时间
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "gmt_modified")
    private Date gmtModified;
    //    图片id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "gmt_modified")
    private String pic_id;
    //    所有地区
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String district;
    //    登录ip
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "login_ip")
    private String loginIp;
    //      物理地址
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mac;
    //  启动定点ip登录
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "start_ip")
    private Integer startIp;
    //类型
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;
    //主题显示
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "show_type")
    private Integer showType;
    //  密码规则
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "rule_id")
    private Long ruleId;

    //  权限等级
    @TableField(value = "region_grade")
    private Integer regionGrade;
    //组织id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "org_id")
    private String orgId;

    @TableField(value = "org_power")
    private String orgPower;

    @TableField(value = "leader_id")
    private String leader_id;

    private String third_part_id;

    @TableField(value = "update_by_name")
    private String updateByName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passworddate;
}

