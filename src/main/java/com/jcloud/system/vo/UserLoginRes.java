package com.jcloud.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 接口登录返回对象
 *
 * @ClassName: VendorAreaVo
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2023-09-12 15:45
 **/
@Data
@ApiModel("接口登录返回对象")
public class UserLoginRes {
    @ApiModelProperty("token")
    private String token;
}
