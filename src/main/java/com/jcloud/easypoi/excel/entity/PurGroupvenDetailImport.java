package com.jcloud.easypoi.excel.entity;

import com.jcloud.easypoi.excel.annotation.Excel;
import com.jcloud.easypoi.excel.annotation.ExcelTarget;
import com.jcloud.easypoi.handler.inter.IExcelModel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.util.Date;

@ExcelTarget("purGroupvenDetailImport")
@TableName("pur_groupven_detail")
public class PurGroupvenDetailImport implements Serializable, IExcelModel {

    private static final long serialVersionUID = 1L;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * Excel行号（不持久化）
     */
    @TableField(exist = false)
    private Integer rowNum;

    public Integer getRowNum() { return rowNum; }
    public void setRowNum(Integer rowNum) { this.rowNum = rowNum; }

    /**
     * 采购组织
     */
    @TableField("pur_org")
    @Excel(name = "采购组织", orderNum = "10", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String purOrg;

    public String getPurOrg() { return purOrg; }
    public void setPurOrg(String purOrg) { this.purOrg = purOrg; }

    /**
     * 采购组编码
     */
    @TableField("role_code")
    @Excel(name = "采购组编码", orderNum = "30", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String roleCode;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    /**
     * 采购组名称
     */
    @TableField("role_name")
    @Excel(name = "采购组名称", orderNum = "40", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String roleName;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    /**
     * 供应商编码
     */
    @TableField("vendor_code")
    @Excel(name = "供应商编码", orderNum = "60", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String vendorCode;

    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }

    /**
     * 供应商名称
     */
    @TableField("vendor_name")
    @Excel(name = "供应商名称", orderNum = "70", isImportField = "true")
    @Length(max = 100, message = "长度不能超过100")
    private String vendorName;

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    /**
     * 是否默认
     */
    @TableField("is_default")
    @Excel(name = "是否默认", orderNum = "80", isImportField = "true", replace = {"是_Y", "否_N"})
    @Length(max = 5, message = "长度不能超过5")
    private String isDefault;

    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }

    /**
     * 单据行是否关闭
     */
    @TableField("is_close")
    @Excel(name = "单据行是否关闭", orderNum = "90", isImportField = "true", replace = {"是_Y", "否_N"})
    @Length(max = 5, message = "长度不能超过5")
    private String isClose;

    public String getIsClose() { return isClose; }
    public void setIsClose(String isClose) { this.isClose = isClose; }

    /**
     * 创建人ID
     */
    @TableField("create_user_id")
    private String createUserId;

    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }

    /**
     * 创建时间
     */
    @TableField("create_time")
    private java.util.Date createTime;

    public java.util.Date getCreateTime() { return createTime; }
    public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }

    @Override
    public String getErrorMsg() { return errorMsg; }
    @Override
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

}
