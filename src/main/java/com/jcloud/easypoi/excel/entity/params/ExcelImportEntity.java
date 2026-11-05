package com.jcloud.easypoi.excel.entity.params;

import java.util.List;

public class ExcelImportEntity extends ExcelBaseEntity {

    public final static String IMG_SAVE_PATH = "/excel/upload/img";

    private String                  collectionName;

    private String                  saveUrl;

    private int                     saveType;

    private String                  classType;

    private String                  suffix;

    private boolean                 importField;

    private String                   enumImportMethod;

    private List<ExcelImportEntity> list;

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
    public String getSaveUrl() { return saveUrl; }
    public void setSaveUrl(String saveUrl) { this.saveUrl = saveUrl; }
    public int getSaveType() { return saveType; }
    public void setSaveType(int saveType) { this.saveType = saveType; }
    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }
    public boolean isImportField() { return importField; }
    public void setImportField(boolean importField) { this.importField = importField; }
    public String getEnumImportMethod() { return enumImportMethod; }
    public void setEnumImportMethod(String enumImportMethod) { this.enumImportMethod = enumImportMethod; }
    public List<ExcelImportEntity> getList() { return list; }
    public void setList(List<ExcelImportEntity> list) { this.list = list; }
}
