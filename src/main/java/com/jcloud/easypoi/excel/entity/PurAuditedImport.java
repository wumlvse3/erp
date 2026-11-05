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

@ExcelTarget("purAuditedImport")
@TableName("pur_audited")
public class PurAuditedImport implements Serializable, IExcelModel {

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
    @Length(max = 49, message = "长度不能超过49")
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
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String deptName;

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

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
    @Length(max = 100, message = "长度不能超过100")
    private String userMan;

    public String getUserMan() { return userMan; }
    public void setUserMan(String userMan) { this.userMan = userMan; }

    /**
     * 申请组织
     */
    @TableField("apply_org")
    @Excel(name = "申请组织", orderNum = "80", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String applyOrg;

    public String getApplyOrg() { return applyOrg; }
    public void setApplyOrg(String applyOrg) { this.applyOrg = applyOrg; }

    /**
     * 申请部门编码
     */
    @TableField("apply_dept_code")
    @Excel(name = "申请部门编码", orderNum = "100", isImportField = "true")
    @Length(max = 20, message = "长度不能超过20")
    private String applyDeptCode;

    public String getApplyDeptCode() { return applyDeptCode; }
    public void setApplyDeptCode(String applyDeptCode) { this.applyDeptCode = applyDeptCode; }

    /**
     * 申请部门名称
     */
    @TableField("apply_dept_name")
    @Excel(name = "申请部门名称", orderNum = "110", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String applyDeptName;

    public String getApplyDeptName() { return applyDeptName; }
    public void setApplyDeptName(String applyDeptName) { this.applyDeptName = applyDeptName; }

    /**
     * 采购指导价是否审核
     */
    @TableField("bill_price")
    @Excel(name = "采购指导价是否审核", orderNum = "120", isImportField = "true")
    @Length(max = 5, message = "长度不能超过5")
    private String billPrice;

    public String getBillPrice() { return billPrice; }
    public void setBillPrice(String billPrice) { this.billPrice = billPrice; }

    /**
     * 请购申请是否审核
     */
    @TableField("bill_apply")
    @Excel(name = "请购申请是否审核", orderNum = "130", isImportField = "true")
    @Length(max = 5, message = "长度不能超过5")
    private String billApply;

    public String getBillApply() { return billApply; }
    public void setBillApply(String billApply) { this.billApply = billApply; }

    /**
     * 订单是否审核
     */
    @TableField("bill_order")
    @Excel(name = "订单是否审核", orderNum = "135", isImportField = "true")
    @Length(max = 5, message = "长度不能超过5")
    private String billOrder;

    public String getBillOrder() { return billOrder; }
    public void setBillOrder(String billOrder) { this.billOrder = billOrder; }

    /**
     * 计划是否审核
     */
    @TableField("bill_plan")
    @Excel(name = "计划是否审核", orderNum = "140", isImportField = "true")
    @Length(max = 5, message = "长度不能超过5")
    private String billPlan;

    public String getBillPlan() { return billPlan; }
    public void setBillPlan(String billPlan) { this.billPlan = billPlan; }

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
