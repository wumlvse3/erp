package com.jcloud.easypoi.exception.excel.enums;

public enum ExcelImportEnum {
    PARAMETER_ERROR("参数错误"),
    IS_NOT_A_VALID_TEMPLATE("不是合法的Excel模板"),
    IS_NOT_A_VALID_CSV_TEMPLATE("不是合法的CSV模板"),
    GET_VALUE_ERROR("Excel 值获取失败"),
    VERIFY_ERROR("值校验失败");

    private String msg;

    ExcelImportEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
