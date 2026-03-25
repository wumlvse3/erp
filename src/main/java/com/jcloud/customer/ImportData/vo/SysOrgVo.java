package com.jcloud.customer.ImportData.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 组织对象
 *
 * @ClassName: SysOrgVo
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2023-11-21 16:26
 **/
@Data
public class SysOrgVo {
    @ApiModelProperty(value = "公司编码",required = true)
    @NotBlank(message = "公司编码不允许为空")
    private String	org_code;
    @ApiModelProperty(value = "公司名称",required = true)
    @NotBlank(message = "公司名称不允许为空")
    private String	org_name;
    @ApiModelProperty(value = "父级组织代码,空默认为根组织",required = false)
    private  String parentOrgCode;

    @ApiModelProperty(value = "组织银行账号",required = false)
    private String bankCode;

    @ApiModelProperty(value = "组织银行账号",required = false)
    private String taxCode;

    @ApiModelProperty(value = "邮政编码",required = false)
    private String postalCode;

    @ApiModelProperty(value = "法人",required = false)
    private String legalMan;

    @ApiModelProperty("采购组织")
    private String purchaseOrg;

    @ApiModelProperty("开户行")
    private String openingBank;

    @ApiModelProperty("电话")
    private String openingPhone;

    @ApiModelProperty("地址")
    private String location;

    @ApiModelProperty("全称")
    private String orgAllName;
    @ApiModelProperty(value = "公司第三方ID",required = true)
    @NotBlank(message = "公司第三方ID不允许为空")
    private String	thirdPartId;
    @ApiModelProperty(value = "是否删除(true删除，false修改或新增)默认false",required = true)
    @NotBlank(message = "删除标志不允许为空")
    private Boolean	is_deleted = false;

}
