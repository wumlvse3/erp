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

@ExcelTarget("purCodeImport")
@TableName("pur_code")
public class PurCodeImport implements Serializable, IExcelModel {

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
    @TableField("inv_org")
    @Excel(name = "采购组织", orderNum = "10", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String invOrg;

    public String getInvOrg() { return invOrg; }
    public void setInvOrg(String invOrg) { this.invOrg = invOrg; }

    /**
     * 档案类型编码
     */
    @TableField("in_code_class")
    @Excel(name = "档案类型编码", orderNum = "25", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String inCodeClass;

    public String getInCodeClass() { return inCodeClass; }
    public void setInCodeClass(String inCodeClass) { this.inCodeClass = inCodeClass; }

    /**
     * 档案类型名称
     */
    @TableField("in_code_class_name")
    @Excel(name = "档案类型名称", orderNum = "35", isImportField = "true")
    @Length(max = 100, message = "长度不能超过100")
    private String inCodeClassName;

    public String getInCodeClassName() { return inCodeClassName; }
    public void setInCodeClassName(String inCodeClassName) { this.inCodeClassName = inCodeClassName; }

    /**
     * 档案编码
     */
    @TableField("in_code")
    @Excel(name = "档案编码", orderNum = "40", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String inCode;

    public String getInCode() { return inCode; }
    public void setInCode(String inCode) { this.inCode = inCode; }

    /**
     * 档案名称
     */
    @TableField("in_code_desc")
    @Excel(name = "档案名称", orderNum = "45", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String inCodeDesc;

    public String getInCodeDesc() { return inCodeDesc; }
    public void setInCodeDesc(String inCodeDesc) { this.inCodeDesc = inCodeDesc; }

    /**
     * 是否分级
     */
    @TableField("is_grade")
    @Excel(name = "是否分级", orderNum = "70", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 5, message = "长度不能超过5")
    private String isGrade;

    public String getIsGrade() { return isGrade; }
    public void setIsGrade(String isGrade) { this.isGrade = isGrade; }

    /**
     * 上级档案
     */
    @TableField("pid_code")
    @Excel(name = "上级档案", orderNum = "80", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 5, message = "长度不能超过5")
    private String pidCode;

    public String getPidCode() { return pidCode; }
    public void setPidCode(String pidCode) { this.pidCode = pidCode; }

    /**
     * 单据行是否关闭
     */
    @TableField("is_close")
    @Excel(name = "单据行是否关闭", orderNum = "100", isImportField = "true", replace = {"是_Y", "否_N"})
    @NotBlank(message = "不能为空")
    @Length(max = 5, message = "长度不能超过5")
    private String isClose;

    public String getIsClose() { return isClose; }
    public void setIsClose(String isClose) { this.isClose = isClose; }

    /**
     * 备注
     */
    @TableField("notes")
    @Excel(name = "备注", orderNum = "110", isImportField = "true")
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
