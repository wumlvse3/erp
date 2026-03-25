package com.jcloud.customer.ImportData.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("部门导入VO")
@Data
public class SysDeptVo {
    @ApiModelProperty(value = "公司编码",required = true)
    @NotBlank(message = "公司编码不允许为空")
    private String	org_code;

    @ApiModelProperty(value = "公司名称",required = false)
    private String	org_name="";

    @ApiModelProperty(value = "部门编码",required = true)
    @NotBlank(message = "部门编码不允许为空")
    private String dept_code;

    @ApiModelProperty(value = "部门名称",required = true)
    @NotBlank(message = "部门名称不允许为空")
    private String	dept_name;

    @ApiModelProperty(value = "上级部门编码")
    private String	dept_code_part;

    @ApiModelProperty(value = "上级部门名称")
    private String	dept_name_part;

    @ApiModelProperty(value = "部门负责人代码")
    private String	dept_head_code="";

    @ApiModelProperty(value = "部门负责人名称")
    private String	dept_head_name="";

    @ApiModelProperty(value = "排序")
    private String	order_num;

    @ApiModelProperty(value = "是否删除(true删除，false修改或新增)默认false",required = true)
    @NotBlank(message = "删除标志不允许为空")
    private Boolean	is_deleted = false;

    @ApiModelProperty(value = "第三方ID",required = true)
    @NotBlank(message = "第三方ID不允许为空")
    private String third_part_id; //是否必填

}
