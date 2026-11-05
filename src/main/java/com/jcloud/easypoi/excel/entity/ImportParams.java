package com.jcloud.easypoi.excel.entity;

import com.jcloud.easypoi.handler.inter.IExcelDataHandler;
import com.jcloud.easypoi.handler.inter.IExcelDictHandler;
import com.jcloud.easypoi.handler.inter.IExcelVerifyHandler;
import com.jcloud.easypoi.handler.inter.II18nHandler;

public class ImportParams extends ExcelBaseParams {

    public static final String SAVE_URL = "/excel/upload/excelUpload";

    private int                 titleRows        = 0;

    private int                 headRows         = 1;

    private int                 startRows        = 0;

    private Integer             keyIndex         = null;

    private int                 startSheetIndex  = 0;

    private int                 sheetNum         = 1;

    private String              sheetName;

    private boolean             needSave         = false;

    private Class[]             verifyGroup      = null;

    private String             needVerify       = "N";

    private boolean             verifyFileSplit  = true;

    private IExcelVerifyHandler verifyHandler;

    private IExcelDataHandler   dataHandler;

    private IExcelDictHandler   dictHandler;

    private II18nHandler        i18nHandler;

    private String              saveUrl          = SAVE_URL;

    private int                 lastOfInvalidRow = 0;

    private int                 readRows         = 0;

    private String[]            importFields;

    private boolean             needCheckOrder   = false;

    private String              keyMark          = ":";

    private boolean             readSingleCell   = false;

    private boolean             concurrentTask   = false;

    private Integer             critical         = 1000;

    /**
     * 需要唯一性校验的字段列表（字段名）
     */
    private String[]            uniqueFields;

    public int getTitleRows() { return titleRows; }
    public void setTitleRows(int titleRows) { this.titleRows = titleRows; }
    public int getHeadRows() { return headRows; }
    public void setHeadRows(int headRows) { this.headRows = headRows; }
    public int getStartRows() { return startRows; }
    public void setStartRows(int startRows) { this.startRows = startRows; }
    public Integer getKeyIndex() { return keyIndex; }
    public void setKeyIndex(Integer keyIndex) { this.keyIndex = keyIndex; }
    public int getStartSheetIndex() { return startSheetIndex; }
    public void setStartSheetIndex(int startSheetIndex) { this.startSheetIndex = startSheetIndex; }
    public int getSheetNum() { return sheetNum; }
    public void setSheetNum(int sheetNum) { this.sheetNum = sheetNum; }
    public String getSheetName() { return sheetName; }
    public void setSheetName(String sheetName) { this.sheetName = sheetName; }
    public boolean isNeedSave() { return needSave; }
    public void setNeedSave(boolean needSave) { this.needSave = needSave; }
    public Class[] getVerifyGroup() { return verifyGroup; }
    public void setVerifyGroup(Class[] verifyGroup) { this.verifyGroup = verifyGroup; }
    public boolean isNeedVerify() { return "Y".equals(needVerify); }
    public void setNeedVerify(String needVerify) { this.needVerify = needVerify; }
    public boolean isVerifyFileSplit() { return verifyFileSplit; }
    public void setVerifyFileSplit(boolean verifyFileSplit) { this.verifyFileSplit = verifyFileSplit; }
    public IExcelVerifyHandler getVerifyHandler() { return verifyHandler; }
    public void setVerifyHandler(IExcelVerifyHandler verifyHandler) { this.verifyHandler = verifyHandler; }
    public IExcelDataHandler getDataHandler() { return dataHandler; }
    public void setDataHandler(IExcelDataHandler dataHandler) { this.dataHandler = dataHandler; }
    public IExcelDictHandler getDictHandler() { return dictHandler; }
    public void setDictHandler(IExcelDictHandler dictHandler) { this.dictHandler = dictHandler; }
    public II18nHandler getI18nHandler() { return i18nHandler; }
    public void setI18nHandler(II18nHandler i18nHandler) { this.i18nHandler = i18nHandler; }
    public String getSaveUrl() { return saveUrl; }
    public void setSaveUrl(String saveUrl) { this.saveUrl = saveUrl; }
    public int getLastOfInvalidRow() { return lastOfInvalidRow; }
    public void setLastOfInvalidRow(int lastOfInvalidRow) { this.lastOfInvalidRow = lastOfInvalidRow; }
    public int getReadRows() { return readRows; }
    public void setReadRows(int readRows) { this.readRows = readRows; }
    public String[] getImportFields() { return importFields; }
    public void setImportFields(String[] importFields) { this.importFields = importFields; }
    public boolean isNeedCheckOrder() { return needCheckOrder; }
    public void setNeedCheckOrder(boolean needCheckOrder) { this.needCheckOrder = needCheckOrder; }
    public String getKeyMark() { return keyMark; }
    public void setKeyMark(String keyMark) { this.keyMark = keyMark; }
    public boolean isReadSingleCell() { return readSingleCell; }
    public void setReadSingleCell(boolean readSingleCell) { this.readSingleCell = readSingleCell; }
    public boolean isConcurrentTask() { return concurrentTask; }
    public void setConcurrentTask(boolean concurrentTask) { this.concurrentTask = concurrentTask; }
    public Integer getCritical() { return critical; }
    public void setCritical(Integer critical) { this.critical = critical; }

    public String[] getUniqueFields() { return uniqueFields; }
    public void setUniqueFields(String[] uniqueFields) { this.uniqueFields = uniqueFields; }
}
