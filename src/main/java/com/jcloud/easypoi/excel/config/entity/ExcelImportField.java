package com.jcloud.easypoi.excel.config.entity;

import java.io.Serializable;

public class ExcelImportField implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long configId;
    private String columnName;
    private String columnComment;
    private String fieldName;
    private String javaType;
    private Integer columnLength;
    private Integer orderNum;
    private Boolean isRequired;
    private String dateFormat;
    private String replaceValue;
    private String defaultValue;
    private String validateRule;
    private Integer sortOrder;
    private String uniqueFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }

    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }

    public String getColumnComment() { return columnComment; }
    public void setColumnComment(String columnComment) { this.columnComment = columnComment; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getJavaType() { return javaType; }
    public void setJavaType(String javaType) { this.javaType = javaType; }

    public Integer getColumnLength() { return columnLength; }
    public void setColumnLength(Integer columnLength) { this.columnLength = columnLength; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

    public String getReplaceValue() { return replaceValue; }
    public void setReplaceValue(String replaceValue) { this.replaceValue = replaceValue; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getValidateRule() { return validateRule; }
    public void setValidateRule(String validateRule) { this.validateRule = validateRule; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getUniqueFlag() { return uniqueFlag; }
    public void setUniqueFlag(String uniqueFlag) { this.uniqueFlag = uniqueFlag; }
}
