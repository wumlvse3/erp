package com.jcloud.easypoi.excel.entity.params;

import java.lang.reflect.Method;
import java.util.List;

import com.jcloud.easypoi.entity.BaseTypeConstants;

public class ExcelBaseEntity {

    protected String     name;

    protected String     groupName;

    private int          type = BaseTypeConstants.STRING_TYPE;

    private String       databaseFormat;

    private String       format;

    private String[]     replace;

    private String       dict;

    private Method       method;

    private List<Object> methodParams;

    private boolean     hyperlink;

    private Integer      fixedIndex;

    private String       timezone;

    private boolean      addressList;

    private boolean      unique;

    private List<Method> methods;

    private List<List<Object>> methodsParams;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public String getDatabaseFormat() { return databaseFormat; }
    public void setDatabaseFormat(String databaseFormat) { this.databaseFormat = databaseFormat; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String[] getReplace() { return replace; }
    public void setReplace(String[] replace) { this.replace = replace; }
    public String getDict() { return dict; }
    public void setDict(String dict) { this.dict = dict; }
    public Method getMethod() { return method; }
    public void setMethod(Method method) { this.method = method; }
    public List<Object> getMethodParams() { return methodParams; }
    public void setMethodParams(List<Object> methodParams) { this.methodParams = methodParams; }
    public boolean isHyperlink() { return hyperlink; }
    public void setHyperlink(boolean hyperlink) { this.hyperlink = hyperlink; }
    public Integer getFixedIndex() { return fixedIndex; }
    public void setFixedIndex(Integer fixedIndex) { this.fixedIndex = fixedIndex; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public boolean isAddressList() { return addressList; }
    public void setAddressList(boolean addressList) { this.addressList = addressList; }
    public boolean isUnique() { return unique; }
    public void setUnique(boolean unique) { this.unique = unique; }
    public List<Method> getMethods() { return methods; }
    public void setMethods(List<Method> methods) { this.methods = methods; }
    public List<List<Object>> getMethodsParams() { return methodsParams; }
    public void setMethodsParams(List<List<Object>> methodsParams) { this.methodsParams = methodsParams; }
}
