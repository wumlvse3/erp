package com.jcloud.easypoi.excel.entity.result;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

public class ExcelImportResult<T> {

    private List<T>  list;

    private List<T>  failList;

    private boolean  verifyFail;

    private Workbook workbook;

    private Workbook failWorkbook;

    private Map<String,Object> map;

    public ExcelImportResult() {

    }

    public ExcelImportResult(List<T> list, boolean verifyFail, Workbook workbook) {
        this.list = list;
        this.verifyFail = verifyFail;
        this.workbook = workbook;
    }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public List<T> getFailList() { return failList; }
    public void setFailList(List<T> failList) { this.failList = failList; }
    public boolean isVerifyFail() { return verifyFail; }
    public void setVerifyFail(boolean verifyFail) { this.verifyFail = verifyFail; }
    public Workbook getWorkbook() { return workbook; }
    public void setWorkbook(Workbook workbook) { this.workbook = workbook; }
    public Workbook getFailWorkbook() { return failWorkbook; }
    public void setFailWorkbook(Workbook failWorkbook) { this.failWorkbook = failWorkbook; }
    public Map<String, Object> getMap() { return map; }
    public void setMap(Map<String, Object> map) { this.map = map; }
}
