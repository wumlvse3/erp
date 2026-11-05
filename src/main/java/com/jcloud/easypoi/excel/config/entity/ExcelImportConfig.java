package com.jcloud.easypoi.excel.config.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ExcelImportConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String tableName;
    private String tableComment;
    private String entityName;
    private String packageName;
    private String moduleName;
    private Integer titleRows = 0;
    private Integer headRows = 1;
    private String needVerify = "N";
    private String status;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String remark;

    private List<ExcelImportField> fieldList;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getTableComment() { return tableComment; }
    public void setTableComment(String tableComment) { this.tableComment = tableComment; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public Integer getTitleRows() { return titleRows != null ? titleRows : 0; }
    public void setTitleRows(Integer titleRows) { this.titleRows = titleRows; }

    public Integer getHeadRows() { return headRows != null ? headRows : 1; }
    public void setHeadRows(Integer headRows) { this.headRows = headRows; }

    public String getNeedVerify() { return needVerify != null ? needVerify :"N"; }
    public void setNeedVerify(String needVerify) { this.needVerify = needVerify; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<ExcelImportField> getFieldList() { return fieldList; }
    public void setFieldList(List<ExcelImportField> fieldList) { this.fieldList = fieldList; }
}
