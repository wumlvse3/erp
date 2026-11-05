package com.jcloud.easypoi.excel.imports;

import com.jcloud.easypoi.excel.entity.params.ExcelImportEntity;
import com.jcloud.easypoi.exception.excel.ExcelImportException;
import com.jcloud.easypoi.exception.excel.enums.ExcelImportEnum;
import com.jcloud.easypoi.handler.inter.IExcelDataHandler;
import com.jcloud.easypoi.handler.inter.IExcelDictHandler;
import com.jcloud.easypoi.util.PoiPublicUtil;
import com.jcloud.easypoi.util.PoiReflectorUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CellValueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellValueService.class);

    private List<String> handlerList = null;

    private Object getCellValue(String classFullName, Cell cell, ExcelImportEntity entity) {
        if (cell == null) {
            LOGGER.warn("getCellValue: cell is null, classFullName={}, entityName={}", classFullName, entity != null ? entity.getName() : "null");
            return "";
        }
        Object result = null;
        int cellType = cell.getCellType();

        boolean isDateType = "class java.util.Date".equals(classFullName)
                || "class java.sql.Date".equals(classFullName)
                || "class java.sql.Time".equals(classFullName)
                || "class java.time.Instant".equals(classFullName)
                || "class java.time.LocalDate".equals(classFullName)
                || "class java.time.LocalDateTime".equals(classFullName)
                || "class java.sql.Timestamp".equals(classFullName);

        // 调试日志：记录日期类型单元格的解析过程
        if (isDateType) {
            String cellValuePreview = "";
            try {
                if (cellType == Cell.CELL_TYPE_NUMERIC) {
                    cellValuePreview = String.valueOf(cell.getNumericCellValue());
                } else if (cellType == Cell.CELL_TYPE_STRING) {
                    cellValuePreview = cell.getStringCellValue();
                } else if (cellType == Cell.CELL_TYPE_BLANK) {
                    cellValuePreview = "[BLANK]";
                } else {
                    cellValuePreview = "[type=" + cellType + "]";
                }
            } catch (Exception e) {
                cellValuePreview = "[ERROR: " + e.getMessage() + "]";
            }
            LOGGER.info("getCellValue [日期类型]: classFullName={}, cellType={}, cellValue={}, entityFormat={}, entityName={}",
                    classFullName, cellType, cellValuePreview, entity != null ? entity.getFormat() : "null",
                    entity != null ? entity.getName() : "null");
        }

        if (isDateType && cellType == Cell.CELL_TYPE_NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            // Excel 日期通常以序列号形式存储（如 45292 = 2023-12-25）
            // 如果值在合理范围内（1900-2100年），尝试解析为 Excel 序列号
            if (numericValue > 1 && numericValue < 100000) {
                result = DateUtil.getJavaDate(numericValue);
            } else {
                // 可能是 yyyyMMdd 格式的数字（如 20231225）
                String dateStr = String.valueOf((long) numericValue);
                result = getDateData(entity, dateStr);
                if (result == null) {
                    LOGGER.error("日期解析失败,值:{}", numericValue);
                    throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
                }
            }
        } else if (isDateType) {
            String val = "";
            try {
                val = cell.getStringCellValue();
            } catch (Exception e) {
                LOGGER.error("读取日期单元格失败");
                throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
            }
            if (StringUtils.isBlank(val)) {
                return null;
            }
            result = getDateData(entity, val);
            if (result == null) {
                LOGGER.error("日期解析失败,值:{}", val);
                throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
            }
        } else {
            if (cellType == Cell.CELL_TYPE_STRING) {
                result = cell.getRichStringCellValue() == null ? ""
                        : cell.getRichStringCellValue().getString();
            } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
                result = readNumericCell(cell);
                double numericValue = cell.getNumericCellValue();
                // 处理日期类型的数字
                if (DateUtil.isCellDateFormatted(cell)) {
                    if ("class java.lang.String".equals(classFullName)) {
                        if (StringUtils.isNotEmpty(entity.getFormat()) && cell.getDateCellValue() != null) {
                            SimpleDateFormat format = new SimpleDateFormat(entity.getFormat());
                            result = format.format(cell.getDateCellValue());
                        }
                    }
                } else if (numericValue > 1 && numericValue < 100000 && StringUtils.isNotEmpty(entity.getFormat())) {
                    // 可能是 Excel 日期序列号，但没有设置日期格式
                    try {
                        Date dateValue = DateUtil.getJavaDate(numericValue);
                        if ("class java.lang.String".equals(classFullName)) {
                            SimpleDateFormat format = new SimpleDateFormat(entity.getFormat());
                            result = format.format(dateValue);
                        } else {
                            result = dateValue;
                        }
                    } catch (Exception e) {
                        // 忽略，保持原始数字
                    }
                }
            } else if (cellType == Cell.CELL_TYPE_BOOLEAN) {
                result = Boolean.toString(cell.getBooleanCellValue());
            } else if (cellType == Cell.CELL_TYPE_BLANK) {
                result = "";
            } else if (cellType == Cell.CELL_TYPE_FORMULA) {
                try {
                    result = readNumericCell(cell);
                } catch (Exception e1) {
                    try {
                        result = cell.getRichStringCellValue() == null ? ""
                                : cell.getRichStringCellValue().getString();
                    } catch (Exception e2) {
                        throw new RuntimeException("获取公式类型的单元格失败", e2);
                    }
                }
            } else {
                result = "";
            }
        }

        if (isDateType && result instanceof Date) {
            if ("class java.time.Instant".equals(classFullName)) {
                result = ((Date) result).toInstant();
            } else if ("class java.time.LocalDate".equals(classFullName)) {
                result = ((Date) result).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if ("class java.time.LocalDateTime".equals(classFullName)) {
                result = ((Date) result).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if ("class java.sql.Date".equals(classFullName)) {
                result = new java.sql.Date(((Date) result).getTime());
            } else if ("class java.sql.Time".equals(classFullName)) {
                result = new Time(((Date) result).getTime());
            } else if ("class java.sql.Timestamp".equals(classFullName)) {
                result = new Timestamp(((Date) result).getTime());
            }
        }

        return result;
    }

    private Object readNumericCell(Cell cell) {
        Object result = null;
        double value = cell.getNumericCellValue();
        if (((int) value) == value) {
            result = (int) value;
        } else {
            result = value;
        }
        return result;
    }

    private Date getDateData(ExcelImportEntity entity, String value) {
        if (StringUtils.isEmpty(value)) {
            LOGGER.warn("getDateData: value is empty, entityFormat={}", entity != null ? entity.getFormat() : "null");
            return null;
        }
        // 先尝试用配置的格式解析（严格模式）
        if (StringUtils.isNotEmpty(entity.getFormat())) {
            SimpleDateFormat format = new SimpleDateFormat(entity.getFormat());
            format.setLenient(false); // 严格校验日期格式
            try {
                Date parsed = format.parse(value);
                LOGGER.info("getDateData: 使用entityFormat解析成功, value={}, format={}, result={}", value, entity.getFormat(), parsed);
                return parsed;
            } catch (ParseException e) {
                LOGGER.warn("getDateData: entityFormat解析失败, value={}, format={}", value, entity.getFormat());
                // 忽略，继续尝试其他格式
            }
        }
        // 尝试常见格式（严格模式）
        String[] formats = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyyMMdd"};
        for (String fmt : formats) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(fmt);
                format.setLenient(false); // 严格校验日期格式
                Date parsed = format.parse(value);
                LOGGER.info("getDateData: 使用常见格式解析成功, value={}, format={}, result={}", value, fmt, parsed);
                return parsed;
            } catch (ParseException e) {
                // 忽略，继续尝试下一个格式
            }
        }
        // 尝试解析为 Excel 序列号
        try {
            Date parsed = DateUtil.getJavaDate(Double.parseDouble(value));
            LOGGER.info("getDateData: 使用Excel序列号解析成功, value={}, result={}", value, parsed);
            return parsed;
        } catch (NumberFormatException ex) {
            LOGGER.error("getDateData: 时间格式化失败,值:{}", value);
            return null;
        }
    }

    public Object getValue(IExcelDataHandler<?> dataHandler, Object object, Object cell,
                           Map<String, ExcelImportEntity> excelParams,
                           String titleString, IExcelDictHandler dictHandler) throws Exception {

        LOGGER.info("=== getValue 开始 === titleString={}, excelParamsKeys={}", titleString, excelParams.keySet());
        ExcelImportEntity entity = excelParams.get(titleString);
        if (entity == null) {
            LOGGER.warn("getValue: entity is NULL for titleString={}, this field will be SKIPPED!", titleString);
            return null;
        }
        String classFullName = "class java.lang.Object";
        Class clazz = null;
        if (!(object instanceof Map)) {
            Method setMethod = entity.getMethods() != null && entity.getMethods().size() > 0
                    ? entity.getMethods().get(entity.getMethods().size() - 1) : entity.getMethod();
            Type[] ts = setMethod.getGenericParameterTypes();
            classFullName = ts[0].toString();
            clazz = (Class) ts[0];
        }
        Object result = null;
        if (cell instanceof Cell) {
            result = getCellValue(classFullName, (Cell) cell, entity);
        } else {
            result = cell;
        }
        if (entity != null) {
            result = handlerSuffix(entity.getSuffix(), result);
            result = replaceValue(entity.getReplace(), result);
            if (dictHandler != null && StringUtils.isNoneBlank(entity.getDict())) {
                result = dictHandler.toValue(entity.getDict(), object, entity.getName(), result);
            }
        }
        result = handlerValue(dataHandler, object, result, titleString);
        Object finalResult = getValueByType(classFullName, result, entity, clazz);
        LOGGER.info("getValue: titleString={}, classFullName={}, resultBeforeType={}, resultAfterType={}, entityFormat={}",
                titleString, classFullName,
                result != null ? (result.getClass().getSimpleName() + ":" + result) : "null",
                finalResult != null ? (finalResult.getClass().getSimpleName() + ":" + finalResult) : "null",
                entity != null ? entity.getFormat() : "null");
        return finalResult;
    }

    private Object handlerSuffix(String suffix, Object result) {
        if (StringUtils.isNotEmpty(suffix) && result != null
                && result.toString().endsWith(suffix)) {
            String temp = result.toString();
            return temp.substring(0, temp.length() - suffix.length());
        }
        return result;
    }

    private Object getValueByType(String classFullName, Object result, ExcelImportEntity entity, Class clazz) {
        try {
            if (result == null || StringUtils.isBlank(result.toString())) {
                LOGGER.warn("getValueByType: result is null or blank, classFullName={}, result={}", classFullName, result);
                return null;
            }
            if ("class java.util.Date".equals(classFullName) && result instanceof String) {
                String dateStr = result.toString();
                // 如果 entity 有 format，优先使用 format 解析（严格模式）
                if (StringUtils.isNotEmpty(entity.getFormat())) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(entity.getFormat());
                        sdf.setLenient(false); // 严格校验日期格式
                        return sdf.parse(dateStr);
                    } catch (ParseException e) {
                        // 如果 format 解析失败，尝试常见格式
                    }
                }
                // 尝试常见格式（严格模式）
                String[] formats = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyyMMdd"};
                for (String fmt : formats) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
                        sdf.setLenient(false); // 严格校验日期格式
                        return sdf.parse(dateStr);
                    } catch (ParseException e) {
                        // 忽略
                    }
                }
                LOGGER.error("日期解析失败,值:{},格式:{}", dateStr, entity.getFormat());
                throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
            }
            if ("class java.lang.Boolean".equals(classFullName) || "boolean".equals(classFullName)) {
                return Boolean.valueOf(String.valueOf(result));
            }
            if ("class java.lang.Double".equals(classFullName) || "double".equals(classFullName)) {
                return Double.valueOf(String.valueOf(result));
            }
            if ("class java.lang.Long".equals(classFullName) || "long".equals(classFullName)) {
                try {
                    return Long.valueOf(String.valueOf(result));
                } catch (Exception e) {
                    return Double.valueOf(String.valueOf(result)).longValue();
                }
            }
            if ("class java.lang.Float".equals(classFullName) || "float".equals(classFullName)) {
                return Float.valueOf(String.valueOf(result));
            }
            if ("class java.lang.Integer".equals(classFullName) || "int".equals(classFullName)) {
                try {
                    return Integer.valueOf(String.valueOf(result));
                } catch (Exception e) {
                    return Double.valueOf(String.valueOf(result)).intValue();
                }
            }
            if ("class java.math.BigDecimal".equals(classFullName)) {
                return new BigDecimal(String.valueOf(result));
            }
            if ("class java.lang.String".equals(classFullName)) {
                if (result instanceof String) {
                    return result;
                }
                if (result instanceof Double) {
                    return PoiPublicUtil.doubleToString((Double) result);
                }
                return String.valueOf(result);
            }
            if (clazz != null && clazz.isEnum()) {
                if (StringUtils.isNotEmpty(entity.getEnumImportMethod())) {
                    return PoiReflectorUtil.fromCache(clazz).execEnumStaticMethod(entity.getEnumImportMethod(), result);
                } else {
                    return Enum.valueOf(clazz, result.toString());
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object handlerValue(IExcelDataHandler dataHandler, Object object, Object result,
                                String titleString) {
        if (dataHandler == null || dataHandler.getNeedHandlerFields() == null
                || dataHandler.getNeedHandlerFields().length == 0) {
            return result;
        }
        if (handlerList == null) {
            handlerList = Arrays.asList(dataHandler.getNeedHandlerFields());
        }
        if (handlerList.contains(titleString)) {
            return dataHandler.importHandler(object, titleString, result);
        }
        return result;
    }

    private Object replaceValue(String[] replace, Object result) {
        if (replace != null && replace.length > 0) {
            String temp = String.valueOf(result);
            String[] tempArr;
            for (int i = 0; i < replace.length; i++) {
                tempArr = replace[i].split("_");
                if (temp.equals(tempArr[0])) {
                    return tempArr[1];
                }
            }
        }
        return result;
    }
}
