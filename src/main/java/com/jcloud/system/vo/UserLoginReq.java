package com.jcloud.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录请求对昂
 *
 * @ClassName: VendorAreaVo
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2023-09-12 15:45
 **/
@Data
@ApiModel("用户登录请求对象")
public class UserLoginReq {
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("密码")
    private String password;
}
