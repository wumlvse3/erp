package com.jcloud.easypoi.handler.inter;

import com.jcloud.easypoi.excel.entity.result.ExcelVerifyHandlerResult;

public interface IExcelVerifyHandler<T> {

    public ExcelVerifyHandlerResult verifyHandler(T obj);

}
