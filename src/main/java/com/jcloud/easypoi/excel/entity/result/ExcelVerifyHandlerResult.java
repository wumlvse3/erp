package com.jcloud.easypoi.excel.entity.result;

public class ExcelVerifyHandlerResult {

    private boolean success;

    private String  msg;

    public ExcelVerifyHandlerResult() {

    }

    public ExcelVerifyHandlerResult(boolean success) {
        this.success = success;
    }

    public ExcelVerifyHandlerResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
}
