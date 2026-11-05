package com.jcloud.easypoi.excel.imports;

import com.jcloud.easypoi.excel.annotation.ExcelTarget;
import com.jcloud.easypoi.excel.entity.ImportParams;
import com.jcloud.easypoi.excel.entity.params.ExcelCollectionParams;
import com.jcloud.easypoi.excel.entity.params.ExcelImportEntity;
import com.jcloud.easypoi.excel.entity.result.ExcelImportResult;
import com.jcloud.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import com.jcloud.easypoi.entity.BaseTypeConstants;
import com.jcloud.easypoi.excel.imports.base.ImportBaseService;
import com.jcloud.easypoi.exception.excel.ExcelImportException;
import com.jcloud.easypoi.exception.excel.enums.ExcelImportEnum;
import com.jcloud.easypoi.handler.inter.IExcelDataHandler;
import com.jcloud.easypoi.handler.inter.IExcelModel;
import com.jcloud.easypoi.util.PoiCellUtil;
import com.jcloud.easypoi.util.PoiPublicUtil;
import com.jcloud.easypoi.util.PoiReflectorUtil;
import com.jcloud.easypoi.util.PoiValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ExcelImportService extends ImportBaseService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExcelImportService.class);

    private CellValueService cellValueServer;

    private boolean verifyFail = false;

    private CellStyle errorCellStyle;

    private List<Row> successRow;
    private List<Row> failRow;
    private List failCollection;

    public ExcelImportService() {
        successRow = new ArrayList<>();
        failRow = new ArrayList<>();
        failCollection = new ArrayList();
        this.cellValueServer = new CellValueService();
    }

    public void addListContinue(Object object, ExcelCollectionParams param, Row row,
                                Map<Integer, String> titlemap, String targetId,
                                Map<String, PictureData> pictures,
                                ImportParams params, StringBuilder errorMsg) throws Exception {
        Collection collection = (Collection) PoiReflectorUtil.fromCache(object.getClass())
                .getValue(object, param.getName());
        Object entity = PoiPublicUtil.createObject(param.getType(), targetId);
        String picId;
        boolean isUsed = false;
        for (int i = row.getFirstCellNum(); i < titlemap.size(); i++) {
            Cell cell = row.getCell(i);
            String titleString = (String) titlemap.get(i);
            if (param.getExcelParams().containsKey(titleString)) {
                if (param.getExcelParams().get(titleString).getType() == BaseTypeConstants.IMAGE_TYPE) {
                    picId = row.getRowNum() + "_" + i;
                } else {
                    try {
                        saveFieldValue(params, entity, cell, param.getExcelParams(), titleString, row);
                    } catch (ExcelImportException e) {
                        if (params.isNeedVerify() && ExcelImportEnum.GET_VALUE_ERROR.equals(e.getType())) {
                            errorMsg.append(" ").append(titleString).append(ExcelImportEnum.GET_VALUE_ERROR.getMsg());
                        }
                    }
                }
                isUsed = true;
            }
        }
        if (isUsed) {
            collection.add(entity);
        }
    }

    private String getKeyValue(Cell cell) {
        Object obj = PoiCellUtil.getCellValue(cell);
        return obj == null ? null : obj.toString().trim();
    }

    public ExcelImportResult importExcelByIs(InputStream inputstream, Class<?> pojoClass,
                                             ImportParams params, boolean needMore) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel import start ,class is {}", pojoClass);
        }
        List result = new ArrayList();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelImportResult importResult;
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputstream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            InputStream userIs = new java.io.ByteArrayInputStream(baos.toByteArray());
            Workbook book = WorkbookFactory.create(userIs);

            boolean isXSSFWorkbook = !(book instanceof org.apache.poi.hssf.usermodel.HSSFWorkbook);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Workbook create success");
            }
            importResult = new ExcelImportResult();
            createErrorCellStyle(book);
            Map<String, PictureData> pictures;
            if (StringUtils.isNotEmpty(params.getSheetName())) {
                Sheet sheet = book.getSheet(params.getSheetName());
                if (isXSSFWorkbook) {
                    pictures = getSheetPictures07((org.apache.poi.xssf.usermodel.XSSFSheet) sheet,
                            (org.apache.poi.xssf.usermodel.XSSFWorkbook) book);
                } else {
                    pictures = new HashMap<>();
                }
                importResult.setList(importExcel(result, sheet, pojoClass, params, pictures));
            } else {
                for (int i = params.getStartSheetIndex(); i < params.getStartSheetIndex() + params.getSheetNum(); i++) {
                    Sheet sheet = book.getSheetAt(i);
                    if (isXSSFWorkbook) {
                        pictures = getSheetPictures07((org.apache.poi.xssf.usermodel.XSSFSheet) sheet,
                                (org.apache.poi.xssf.usermodel.XSSFWorkbook) book);
                    } else {
                        pictures = new HashMap<>();
                    }
                    List list = importExcel(result, sheet, pojoClass, params, pictures);
                    result.addAll(list);
                }
                importResult.setList(result);
            }
            importResult.setVerifyFail(verifyFail);
            importResult.setWorkbook(book);
            if (needMore) {
                importResult.setFailList(failCollection);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return importResult;
    }

    private List importExcel(Collection result, Sheet sheet, Class<?> pojoClass,
                             ImportParams params, Map<String, PictureData> pictures) throws Exception {
        List collection = new ArrayList();
        Map<String, ExcelImportEntity> excelParams = new HashMap<>();
        List<ExcelCollectionParams> excelCollection = new ArrayList<>();
        String targetId = null;
        boolean isMap = Map.class.equals(pojoClass);
        if (!isMap) {
            Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            if (etarget != null) {
                targetId = etarget.value();
            }
            getAllExcelField(targetId, fileds, excelParams, excelCollection, pojoClass, null, null);
        }
        Iterator<Row> rows = sheet.rowIterator();
        for (int j = 0; j < params.getTitleRows(); j++) {
            rows.next();
        }
        Map<Integer, String> titlemap = getTitleMap(rows, params, excelCollection, excelParams);
        LOGGER.info("=== Excel导入调试信息 ===");
        LOGGER.info("excelParams keys: {}", excelParams.keySet());
        LOGGER.info("titlemap: {}", titlemap);
        checkIsValidTemplate(titlemap, excelParams, params, excelCollection);

        // 构建唯一性校验字段映射：Excel列名 -> ExcelImportEntity（自动从注解中获取unique=true的字段）
        Map<String, ExcelImportEntity> uniqueEntityMap = new HashMap<>();
        for (Map.Entry<String, ExcelImportEntity> entry : excelParams.entrySet()) {
            if (entry.getValue().isUnique()) {
                uniqueEntityMap.put(entry.getKey(), entry.getValue());
            }
        }
        // 兼容手动设置的uniqueFields
        if (params.getUniqueFields() != null && params.getUniqueFields().length > 0) {
            for (String fieldName : params.getUniqueFields()) {
                if (excelParams.containsKey(fieldName)) {
                    uniqueEntityMap.put(fieldName, excelParams.get(fieldName));
                }
            }
        }
        if (!uniqueEntityMap.isEmpty()) {
            LOGGER.info("检测到唯一性校验字段: {}", uniqueEntityMap.keySet());
        }

        // 记录唯一字段的值 -> 第一次出现的行号
        Map<String, Integer> uniqueValueMap = new HashMap<>();
        // 记录重复的行号 -> 错误信息
        Map<Integer, String> duplicateErrors = new HashMap<>();

        Row row = null;
        Object object = null;
        int readRow = 1;
        for (int i = 0; i < params.getStartRows(); i++) {
            rows.next();
        }
        if (excelCollection.size() > 0 && params.getKeyIndex() == null) {
            params.setKeyIndex(0);
        }
        int endRow = sheet.getLastRowNum() - params.getLastOfInvalidRow();
        if (params.getReadRows() > 0) {
            endRow = Math.min(params.getReadRows(), endRow);
        }
        StringBuilder errorMsg;
        while (rows.hasNext()) {
            row = rows.next();
            if (row.getRowNum() > endRow) {
                break;
            }
            if (row.getLastCellNum() < 0) {
                continue;
            }
            errorMsg = new StringBuilder();
            if (params.getKeyIndex() != null
                    && (row.getCell(params.getKeyIndex()) == null
                    || StringUtils.isEmpty(getKeyValue(row.getCell(params.getKeyIndex()))))
                    && object != null) {
                for (ExcelCollectionParams param : excelCollection) {
                    addListContinue(object, param, row, titlemap, targetId, pictures, params, errorMsg);
                }
            } else {
                object = PoiPublicUtil.createObject(pojoClass, targetId);
                // 设置真实的 Excel 行号（rowNum 从 0 开始，Excel 行号从 1 开始，加上表头行数）
                if (object instanceof IExcelModel) {
                    try {
                        Method setRowNum = object.getClass().getMethod("setRowNum", Integer.class);
                        setRowNum.invoke(object, row.getRowNum() + 1);
                    } catch (Exception e) {
                        // 忽略，实体类可能没有 rowNum 字段
                    }
                }
                try {
                    Set<Integer> keys = titlemap.keySet();
                    for (Integer cn : keys) {
                        Cell cell = row.getCell(cn);
                        String titleString = (String) titlemap.get(cn);
                        if (excelParams.containsKey(titleString) || isMap) {
                            if (excelParams.get(titleString) != null
                                    && excelParams.get(titleString).getType() == BaseTypeConstants.IMAGE_TYPE) {
                            } else {
                                try {
                                    saveFieldValue(params, object, cell, excelParams, titleString, row);
                                } catch (ExcelImportException e) {
                                    if (params.isNeedVerify() && ExcelImportEnum.GET_VALUE_ERROR.equals(e.getType())) {
                                        errorMsg.append(" ").append(titleString).append(ExcelImportEnum.GET_VALUE_ERROR.getMsg());
                                    }
                                }
                            }
                        }
                    }
                    for (ExcelCollectionParams param : excelCollection) {
                        addListContinue(object, param, row, titlemap, targetId, pictures, params, errorMsg);
                    }

                    // 唯一性校验：在行处理时就检查，如果重复则标记错误
                    if (!uniqueEntityMap.isEmpty()) {
                        // 构建 titleString -> cell 的映射
                        Map<String, Cell> titleCellMap = new HashMap<>();
                        for (Integer cn : keys) {
                            String titleString = (String) titlemap.get(cn);
                            Cell cell = row.getCell(cn);
                            if (cell != null) {
                                titleCellMap.put(titleString, cell);
                            }
                        }

                        for (Map.Entry<String, ExcelImportEntity> entry : uniqueEntityMap.entrySet()) {
                            String excelColumnName = entry.getKey();
                            Cell cell = titleCellMap.get(excelColumnName);
                            if (cell != null) {
                                String valStr = getKeyValue(cell);
                                if (StringUtils.isNotEmpty(valStr)) {
                                    String uniqueKey = excelColumnName + "=" + valStr;
                                    if (uniqueValueMap.containsKey(uniqueKey)) {
                                        // 发现重复，记录错误
                                        int firstRow = uniqueValueMap.get(uniqueKey);
                                        String dupMsg = excelColumnName + "值[" + valStr + "]在第" + (firstRow + 1) + "行和第" + (row.getRowNum() + 1) + "行重复";
                                        LOGGER.info("唯一性校验失败: {}", dupMsg);

                                        // 标记当前行错误
                                        errorMsg.append(" ").append(dupMsg);

                                        // 如果第一次出现的行还没有被标记错误，也需要标记
                                        if (!duplicateErrors.containsKey(firstRow)) {
                                            duplicateErrors.put(firstRow, dupMsg);
                                            Sheet sheet2 = row.getSheet();
                                            Row firstRowObj = sheet2.getRow(firstRow - 1);
                                            if (firstRowObj != null) {
                                                Cell errorCell = firstRowObj.getCell(firstRowObj.getLastCellNum());
                                                if (errorCell == null) {
                                                    errorCell = firstRowObj.createCell(firstRowObj.getLastCellNum());
                                                }
                                                String existingMsg = errorCell.getStringCellValue();
                                                if (StringUtils.isEmpty(existingMsg)) {
                                                    errorCell.setCellValue(dupMsg);
                                                } else {
                                                    errorCell.setCellValue(existingMsg + "," + dupMsg);
                                                }
                                                errorCell.setCellStyle(errorCellStyle);
                                                failRow.add(firstRowObj);
                                            }
                                        }

                                        verifyFail = true;
                                    } else {
                                        uniqueValueMap.put(uniqueKey, row.getRowNum());
                                    }
                                }
                            }
                        }
                    }

                    if (verifyingDataValidity(object, row, params, isMap, errorMsg)) {
                        collection.add(object);
                    } else {
                        failCollection.add(object);
                    }
                } catch (ExcelImportException e) {
                    LOGGER.error("excel import error , row num:{}", readRow);
                    if (!e.getType().equals(ExcelImportEnum.VERIFY_ERROR)) {
                        throw new ExcelImportException(e.getType(), e);
                    }
                } catch (Exception e) {
                    LOGGER.error("excel import error , row num:{}", readRow);
                    throw new RuntimeException(e);
                }
            }
            readRow++;
        }

        return collection;
    }

    public boolean verifyingDataValidity(Object object, Row row, ImportParams params,
                                         boolean isMap, StringBuilder fieldErrorMsg) {
        boolean isAdd = true;
        Cell cell = null;
        if (params.isNeedVerify()) {
            String errorMsg = PoiValidationUtil.validation(object, params.getVerifyGroup());
            if (StringUtils.isNotEmpty(errorMsg)) {
                LOGGER.info("verifyingDataValidity: validation failed, row={}, errorMsg={}, objectClass={}",
                        row.getRowNum(), errorMsg, object.getClass().getSimpleName());
                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(errorMsg);
                if (object instanceof IExcelModel) {
                    IExcelModel model = (IExcelModel) object;
                    model.setErrorMsg(errorMsg);
                }
                isAdd = false;
                verifyFail = true;
            }
        }
        if (params.getVerifyHandler() != null) {
            ExcelVerifyHandlerResult result = params.getVerifyHandler().verifyHandler(object);
            if (!result.isSuccess()) {
                if (cell == null) {
                    cell = row.createCell(row.getLastCellNum());
                }
                cell.setCellValue((StringUtils.isNoneBlank(cell.getStringCellValue())
                        ? cell.getStringCellValue() + "," : "") + result.getMsg());
                if (object instanceof IExcelModel) {
                    IExcelModel model = (IExcelModel) object;
                    model.setErrorMsg((StringUtils.isNoneBlank(model.getErrorMsg())
                            ? model.getErrorMsg() + "," : "") + result.getMsg());
                }
                isAdd = false;
                verifyFail = true;
            }
        }
        if ((params.isNeedVerify() || params.getVerifyHandler() != null) && fieldErrorMsg.length() > 0) {
            if (object instanceof IExcelModel) {
                IExcelModel model = (IExcelModel) object;
                model.setErrorMsg((StringUtils.isNoneBlank(model.getErrorMsg())
                        ? model.getErrorMsg() + "," : "") + fieldErrorMsg.toString());
            }
            if (cell == null) {
                cell = row.createCell(row.getLastCellNum());
            }
            cell.setCellValue((StringUtils.isNoneBlank(cell.getStringCellValue())
                    ? cell.getStringCellValue() + "," : "") + fieldErrorMsg.toString());
            isAdd = false;
            verifyFail = true;
        }
        if (cell != null) {
            cell.setCellStyle(errorCellStyle);
            failRow.add(row);
        } else {
            successRow.add(row);
        }
        return isAdd;
    }

    private Map<Integer, String> getTitleMap(Iterator<Row> rows, ImportParams params,
                                             List<ExcelCollectionParams> excelCollection,
                                             Map<String, ExcelImportEntity> excelParams) {
        Map<Integer, String> titlemap = new LinkedHashMap<>();
        Iterator<Cell> cellTitle;
        String collectionName = null;
        ExcelCollectionParams collectionParams = null;
        Row row = null;
        for (int j = 0; j < params.getHeadRows(); j++) {
            row = rows.next();
            if (row == null) {
                continue;
            }
            cellTitle = row.cellIterator();
            while (cellTitle.hasNext()) {
                Cell cell = cellTitle.next();
                String value = getKeyValue(cell);
                value = value.replace("\n", "");
                // 去除必填标记 * 前缀，以便与 @Excel 注解的 name 匹配
                if (value.startsWith("*")) {
                    value = value.substring(1);
                }
                int i = cell.getColumnIndex();
                if (StringUtils.isNotEmpty(value)) {
                    if (titlemap.containsKey(i)) {
                        collectionName = titlemap.get(i);
                        collectionParams = getCollectionParams(excelCollection, collectionName);
                        titlemap.put(i, collectionName + "_" + value);
                    } else if (StringUtils.isNotEmpty(collectionName) && collectionParams != null
                            && collectionParams.getExcelParams()
                            .containsKey(collectionName + "_" + value)) {
                        titlemap.put(i, collectionName + "_" + value);
                    } else {
                        collectionName = null;
                        collectionParams = null;
                    }
                    if (StringUtils.isEmpty(collectionName)) {
                        titlemap.put(i, value);
                    }
                }
            }
        }
        Set<String> keys = excelParams.keySet();
        for (String key : keys) {
            if (key.startsWith("FIXED_")) {
                String[] arr = key.split("_");
                titlemap.put(Integer.parseInt(arr[1]), key);
            }
        }
        return titlemap;
    }

    private ExcelCollectionParams getCollectionParams(List<ExcelCollectionParams> excelCollection,
                                                      String collectionName) {
        for (ExcelCollectionParams excelCollectionParams : excelCollection) {
            if (collectionName.equals(excelCollectionParams.getExcelName())) {
                return excelCollectionParams;
            }
        }
        return null;
    }

    private void checkIsValidTemplate(Map<Integer, String> titlemap,
                                      Map<String, ExcelImportEntity> excelParams,
                                      ImportParams params,
                                      List<ExcelCollectionParams> excelCollection) {
        if (params.getImportFields() != null) {
            for (int i = 0; i < params.getImportFields().length; i++) {
                if (!titlemap.containsValue(params.getImportFields()[i])) {
                    throw new ExcelImportException(ExcelImportEnum.IS_NOT_A_VALID_TEMPLATE);
                }
            }
        }
    }

    private void createErrorCellStyle(Workbook book) {
        errorCellStyle = book.createCellStyle();
        Font font = book.createFont();
        font.setColor(Font.COLOR_RED);
        errorCellStyle.setFont(font);
    }

    private void saveFieldValue(ImportParams params, Object object, Cell cell,
                                Map<String, ExcelImportEntity> excelParams,
                                String titleString, Row row) throws Exception {
        Object value = cellValueServer.getValue(params.getDataHandler(), object, cell,
                excelParams, titleString, params.getDictHandler());
        LOGGER.info("saveFieldValue: titleString={}, value={}, valueType={}, cellType={}",
                titleString,
                value != null ? value.toString() : "null",
                value != null ? value.getClass().getSimpleName() : "null",
                cell != null ? cell.getCellType() : "null");
        if (object instanceof Map) {
            ((Map) object).put(titleString, value);
        } else {
            ExcelImportEntity entity = excelParams.get(titleString);
            setValues(entity, object, value);
        }
    }

    private Map<String, PictureData> getSheetPictures07(org.apache.poi.xssf.usermodel.XSSFSheet sheet,
                                                        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook) {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<>();
        try {
            List<?> relations = sheet.getRelations();
            for (Object dr : relations) {
                if (dr instanceof org.apache.poi.xssf.usermodel.XSSFDrawing) {
                    org.apache.poi.xssf.usermodel.XSSFDrawing drawing = (org.apache.poi.xssf.usermodel.XSSFDrawing) dr;
                    List<org.apache.poi.xssf.usermodel.XSSFShape> shapes = drawing.getShapes();
                    for (org.apache.poi.xssf.usermodel.XSSFShape shape : shapes) {
                        if (shape instanceof org.apache.poi.xssf.usermodel.XSSFPicture) {
                            org.apache.poi.xssf.usermodel.XSSFPicture pic = (org.apache.poi.xssf.usermodel.XSSFPicture) shape;
                            org.apache.poi.xssf.usermodel.XSSFClientAnchor anchor = pic.getPreferredSize();
                            String picIndex = anchor.getRow1() + "_" + anchor.getCol1();
                            sheetIndexPicMap.put(picIndex, pic.getPictureData());
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("读取Excel图片时发生异常，跳过图片处理", e);
        }
        return sheetIndexPicMap;
    }
}
