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

@ExcelTarget("purGroupImport")
@TableName("pur_group")
public class PurGroupImport implements Serializable, IExcelModel {

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
     * 部门编码
     */
    @TableField("dept_code")
    @Excel(name = "部门编码", orderNum = "30", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 20, message = "长度不能超过20")
    private String deptCode;

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    /**
     * 部门名称
     */
    @TableField("dept_name")
    @Excel(name = "部门名称", orderNum = "40", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String deptName;

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    /**
     * 采购组编码
     */
    @TableField("role_code")
    @Excel(name = "采购组编码", orderNum = "50", isImportField = "true", unique = true)
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String roleCode;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    /**
     * 采购组名称
     */
    @TableField("role_name")
    @Excel(name = "采购组名称", orderNum = "60", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String roleName;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    /**
     * 备注
     */
    @TableField("notes")
    @Excel(name = "备注", orderNum = "70", isImportField = "true")
    @Length(max = 500, message = "长度不能超过500")
    private String notes;

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

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
