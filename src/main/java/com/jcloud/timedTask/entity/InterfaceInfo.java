package com.jcloud.timedTask.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 * 接口信息实体类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@TableName("interface_info")
public class InterfaceInfo extends Model<InterfaceInfo> {

    //数据库id
    @TableId(value = "id")
    private String id;
    //请求的url
    @TableField(value = "pre_url")
    private String preUrl;

    @TableField(value = "pre_params")
    private String preParams;
    // 请求方式 "get" "post"
    @TableField(value = "method")
    private String method;
    // 回执url
    @TableField(value = "reply_url")
    private String replyUrl;
    // 回执请求方式 "get" "post"
    @TableField(value = "reply_method")
    private String replyMethod;
    // 接口名称
    @TableField(value = "name")
    private String name;
    // 接口代码
    @TableField(value = "code")
    private String code;

    // 接口分组名称
    @TableField(value = "group_name")
    private String groupName;
    // corn 表达式
    @TableField(value = "corn")
    private String corn;
    // 执行类的全限定名
    @TableField(value = "class_name")
    private String className;
    // 是否启用定时执行 0 启用  1 不启用
    @TableField(value = "active")
    private Integer active;

    // 最后调用时间(调用成功才记录)
    @TableField(value = "last_execute_time")
    private Date last_execute_time;

    @TableField(value = "end_time")
    private Date end_time;

    // 预留保存信息
    @TableField(value = "extra")
    private String extra;

    // 组织信息
    @TableField(value = "org_id")
    private String org_id;

    // 创建时间
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date create_time;
    // 修改时间
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private Date update_time;
    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
