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

@ExcelTarget("ordgroupItemDetailImport")
@TableName("ordgroup_item_detail")
public class OrdgroupItemDetailImport implements Serializable, IExcelModel {

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
     * 客户ID
     */
    @TableField("customer_id")
    @Excel(name = "客户ID", orderNum = "20", isImportField = "true", type = 10)
    private Long customerId;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    /**
     * 客户编码
     */
    @TableField("customer_code")
    @Excel(name = "客户编码", orderNum = "30", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String customerCode;

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    /**
     * 客户名称
     */
    @TableField("customer_name")
    @Excel(name = "客户名称", orderNum = "40", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String customerName;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    /**
     * 库存组织
     */
    @TableField("inv_org")
    @Excel(name = "库存组织", orderNum = "50", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 50, message = "长度不能超过50")
    private String invOrg;

    public String getInvOrg() { return invOrg; }
    public void setInvOrg(String invOrg) { this.invOrg = invOrg; }

    /**
     * 物料ID
     */
    @TableField("item_id")
    @Excel(name = "物料ID", orderNum = "60", isImportField = "true", type = 10)
    private Long itemId;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    /**
     * 物料编码
     */
    @TableField("item_code")
    @Excel(name = "物料编码", orderNum = "70", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String itemCode;

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    /**
     * 物料名称
     */
    @TableField("item_name")
    @Excel(name = "物料名称", orderNum = "80", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String itemName;

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    /**
     * 物料规格
     */
    @TableField("item_spec")
    @Excel(name = "物料规格", orderNum = "90", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String itemSpec;

    public String getItemSpec() { return itemSpec; }
    public void setItemSpec(String itemSpec) { this.itemSpec = itemSpec; }

    /**
     * 物料型号
     */
    @TableField("item_model")
    @Excel(name = "物料型号", orderNum = "100", isImportField = "true")
    @NotBlank(message = "不能为空")
    @Length(max = 100, message = "长度不能超过100")
    private String itemModel;

    public String getItemModel() { return itemModel; }
    public void setItemModel(String itemModel) { this.itemModel = itemModel; }

    /**
     * 销售组ID
     */
    @TableField("role_id")
    @Excel(name = "销售组ID", orderNum = "110", isImportField = "true", type = 10)
    private Long roleId;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    /**
     * 销售组编码
     */
    @TableField("role_code")
    @Excel(name = "销售组编码", orderNum = "120", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String roleCode;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    /**
     * 销售组名称
     */
    @TableField("role_name")
    @Excel(name = "销售组名称", orderNum = "130", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String roleName;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    /**
     * 客户分类ID
     */
    @TableField(exist = false)
    @Excel(name = "客户分类ID", orderNum = "140", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String class_id;

    public String getClass_id() { return class_id; }
    public void setClass_id(String class_id) { this.class_id = class_id; }

    /**
     * 客户分类编码
     */
    @TableField(exist = false)
    @Excel(name = "客户分类编码", orderNum = "150", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String class_code;

    public String getClass_code() { return class_code; }
    public void setClass_code(String class_code) { this.class_code = class_code; }

    /**
     * 客户分类名称
     */
    @TableField(exist = false)
    @Excel(name = "客户分类名称", orderNum = "160", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String class_name;

    public String getClass_name() { return class_name; }
    public void setClass_name(String class_name) { this.class_name = class_name; }

    /**
     * 分配方式
     */
    @TableField(exist = false)
    @Excel(name = "分配方式", orderNum = "170", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
    private String distribute;

    public String getDistribute() { return distribute; }
    public void setDistribute(String distribute) { this.distribute = distribute; }

    /**
     * 备注
     */
    @TableField(exist = false)
    @Excel(name = "备注", orderNum = "180", isImportField = "true")
    @Length(max = 50, message = "长度不能超过50")
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
