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

@ExcelTarget("ordUserDetailImport")
@TableName("ord_user_detail")
public class OrdUserDetailImport implements Serializable, IExcelModel {

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
     * 销售组织
     */
    @TableField("sales_org")
    @Excel(name = "销售组织", orderNum = "10", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String salesOrg;

    public String getSalesOrg() { return salesOrg; }
    public void setSalesOrg(String salesOrg) { this.salesOrg = salesOrg; }

    /**
     * 销售组ID
     */
    @TableField("role_id")
    @Excel(name = "销售组ID", orderNum = "20", isImportField = "true", type = 10)
    private Long roleId;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    /**
     * 销售组编码
     */
    @TableField("role_code")
    @Excel(name = "销售组编码", orderNum = "30", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String roleCode;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    /**
     * 销售组名称
     */
    @TableField("role_name")
    @Excel(name = "销售组名称", orderNum = "40", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String roleName;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Excel(name = "用户ID", orderNum = "50", isImportField = "true", type = 10)
    private Long userId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    /**
     * 用户编码
     */
    @TableField("user_code")
    @Excel(name = "用户编码", orderNum = "60", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String userCode;

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    /**
     * 用户名称
     */
    @TableField("user_man")
    @Excel(name = "用户名称", orderNum = "70", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String userMan;

    public String getUserMan() { return userMan; }
    public void setUserMan(String userMan) { this.userMan = userMan; }

    /**
     * 单据额度控制
     */
    @TableField("bill_control")
    @Excel(name = "单据额度控制", orderNum = "80", isImportField = "true", type = 10)
    private Double billControl;

    public Double getBillControl() { return billControl; }
    public void setBillControl(Double billControl) { this.billControl = billControl; }

    /**
     * 是否默认
     */
    @TableField("is_default")
    @Excel(name = "是否默认", orderNum = "90", isImportField = "true")
    @Length(max = 5, message = "长度不能超过5")
    private String isDefault;

    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }

    /**
     * 部门ID（仅用于导入到ord_user表）
     */
    @TableField(exist = false)
    @Excel(name = "部门ID", orderNum = "110", isImportField = "true", type = 10)
    private Long dept_id;

    public Long getDept_id() { return dept_id; }
    public void setDept_id(Long dept_id) { this.dept_id = dept_id; }

    /**
     * 部门编码（仅用于导入到ord_user表）
     */
    @TableField(exist = false)
    @Excel(name = "部门编码", orderNum = "120", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 20, message = "长度不能超过20")
    private String dept_code;

    public String getDept_code() { return dept_code; }
    public void setDept_code(String dept_code) { this.dept_code = dept_code; }

    /**
     * 部门名称（仅用于导入到ord_user表）
     */
    @TableField(exist = false)
    @Excel(name = "部门名称", orderNum = "130", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String dept_name;

    public String getDept_name() { return dept_name; }
    public void setDept_name(String dept_name) { this.dept_name = dept_name; }

    /**
     * 备注（仅用于导入到ord_user表）
     */
    @TableField(exist = false)
    @Excel(name = "备注", orderNum = "140", isImportField = "true")
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
