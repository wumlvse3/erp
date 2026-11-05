package com.jcloud.easypoi.excel.entity.params;

import java.util.Map;

public class ExcelCollectionParams {

    private String                         name;

    private String                         excelName;

    private Class<?>                       type;

    private Map<String, ExcelImportEntity> excelParams;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExcelName() { return excelName; }
    public void setExcelName(String excelName) { this.excelName = excelName; }
    public Class<?> getType() { return type; }
    public void setType(Class<?> type) { this.type = type; }
    public Map<String, ExcelImportEntity> getExcelParams() { return excelParams; }
    public void setExcelParams(Map<String, ExcelImportEntity> excelParams) { this.excelParams = excelParams; }
}
