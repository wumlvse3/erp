package com.jcloud.customer.WmsBaseData.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class Sys_user implements Serializable {
//    public static final long serialVersionUID = 1L;

//    @JsonProperty("user_id") //jackson的注解,用于进行属性格式化!
    @TableId(type = IdType.AUTO)//数据库自增
    private Long user_id;

    private String username;

    private String name;

    //忽略属性 不生成json 不接受json数据  @JsonIgnore
    // @JsonInclude(JsonInclude.Include.NON_NULL)  + null 当这个值不为null的时候生成json,为null不生成
    // 不影响接收json
    @JsonInclude(JsonInclude.Include.NON_NULL)//为空不返回
//    @TableField(exist = false)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer dept_id;

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
    private Integer user_id_create;
    //    创建时间
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_create;
    // 修改时间
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_modified;
    //    图片id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String pic_id;
    //    所有地区
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String district;
    //    登录ip
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String login_ip;
    //      物理地址
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mac;
    //  启动定点ip登录
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer start_ip;
    //类型
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;
    //主题显示
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer show_type;
    //  密码规则
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long rule_id;

    //  权限等级
    private Integer region_grade;
    //组织id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String org_id;

    //授权组织
    @TableField(value = "org_power")
    private String org_power;

    @TableField(value = "gender")
    private String  gender;

    @TableField(value = "position")
    private String  position;

    @TableField(value = "leader_id")
    private Long  leader_id;
    @TableField(value = "update_time")
    private Date  update_time;
    @TableField(value = "create_by_name")
    private String  create_by_name;
    @TableField(value = "update_by_name")
    private String  update_by_name;
    @TableField(value = "third_part_id")
    private String  third_part_id;
}

