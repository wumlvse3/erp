package com.jcloud.easypoi.handler.inter;

import java.util.Map;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

public interface IExcelDataHandler<T> {

    public String[] getNeedHandlerFields();

    public Object exportHandler(T obj, String name, Object value);

    public Object importHandler(T obj, String name, Object value);

    public void setNeedHandlerFields(String[] fields);

    public void setMapValue(Map<String, Object> map, String originKey, Object value);

    public Hyperlink getHyperlink(CreationHelper creationHelper, T obj, String name, Object value);

}
