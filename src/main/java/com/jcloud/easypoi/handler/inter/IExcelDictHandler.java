package com.jcloud.easypoi.handler.inter;

import java.util.List;
import java.util.Map;

public interface IExcelDictHandler {

    default public List<Map> getList(String dict) {
        return null;
    }

    public String toName(String dict, Object obj, String name, Object value);

    public String toValue(String dict, Object obj, String name, Object value);

}
