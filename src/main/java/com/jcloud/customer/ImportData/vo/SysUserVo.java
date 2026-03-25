package com.jcloud.customer.ImportData.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("员工导入VO")
@Data
public class SysUserVo {
    @ApiModelProperty(value = "公司代码",required = true)
    @NotBlank(message = "公司代码不允许为空")
    private String	org_code;

    @ApiModelProperty(value = "公司名称",hidden = true)
    private String	org_name;

    @ApiModelProperty(value = "部门代码",required = true)
    @NotBlank(message = "部门代码不允许为空")
    private String dept_code;

    @ApiModelProperty(value = "部门名称",hidden = true)
    private String	dept_name;

    @ApiModelProperty(value = "员工代码",required = true)
    @NotBlank(message = "员工代码不允许为空")
    private String	username;

    @ApiModelProperty(value = "员工名称",required = true)
    @NotBlank(message = "员工名称不允许为空")
    private String	name;

    @ApiModelProperty(value = "性别")
    private String	gender;

    @ApiModelProperty(value = "职位")
    private String	position;

    @ApiModelProperty(value = "上级领导代码",hidden = true)
    private String	leader_code;

    @ApiModelProperty(value = "上级领导名称",hidden = true)
    private String	leader_name;

    @ApiModelProperty(value = "邮箱",required = false)
    private String	email;

    @ApiModelProperty(value = "手机号",required = false)
    private String	mobile;
    @ApiModelProperty(value = "是否删除(true删除，false修改或新增)默认false",required = true)
    @NotBlank(message = "删除标志不允许为空")
    private Boolean	is_deleted = false;
    @ApiModelProperty(value = "第三方ID",required = true)
    @NotBlank(message = "第三方ID不允许为空")
    private String	thirdPartId;
}
