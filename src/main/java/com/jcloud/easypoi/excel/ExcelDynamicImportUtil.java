package com.jcloud.easypoi.excel;

import com.jcloud.easypoi.excel.entity.ImportParams;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;

public class ExcelDynamicImportUtil {

    /**
     * 动态导入 Excel（无需预定义实体类）
     * 自动解析表头，返回 List<Map<String, Object>>
     *
     * @param inputStream Excel 输入流
     * @param params      导入参数
     * @return 数据列表，每行一个 Map，key 为列名
     */
    public static List<Map<String, Object>> importExcel(InputStream inputStream, ImportParams params) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // 跳过标题行
        int startRow = params.getTitleRows();

        // 读取表头行
        Row headerRow = sheet.getRow(startRow);
        if (headerRow == null) {
            throw new RuntimeException("Excel 文件为空，未找到表头行");
        }

        // 解析表头
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                headers.add(getCellValueAsString(cell));
            } else {
                headers.add("column_" + i);
            }
        }

        // 读取数据行
        int dataStartRow = startRow + params.getHeadRows();
        for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, Object> rowData = new LinkedHashMap<>();
            rowData.put("_rowNum", i + 1); // 行号

            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String header = headers.get(j);
                Object value = getCellValue(cell);
                rowData.put(header, value);
            }

            resultList.add(rowData);
        }

        workbook.close();
        return resultList;
    }

    /**
     * 获取单元格值（对象类型）
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;

        int cellType = cell.getCellType();
        if (cellType == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
            double val = cell.getNumericCellValue();
            if (val == (int) val) {
                return (int) val;
            }
            return val;
        } else if (cellType == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cellType == Cell.CELL_TYPE_BLANK) {
            return null;
        } else if (cellType == Cell.CELL_TYPE_FORMULA) {
            try {
                return cell.getNumericCellValue();
            } catch (Exception e) {
                return cell.getStringCellValue();
            }
        }
        return null;
    }

    /**
     * 获取单元格值（字符串类型）
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        int cellType = cell.getCellType();
        if (cellType == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue().trim();
        } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == (int) val) {
                return String.valueOf((int) val);
            }
            return String.valueOf(val);
        } else if (cellType == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return "";
    }

    /**
     * 获取 Excel 表头信息（不读取数据）
     *
     * @param inputStream Excel 输入流
     * @param params      导入参数
     * @return 表头列表
     */
    public static List<String> getHeaders(InputStream inputStream, ImportParams params) throws Exception {
        List<String> headers = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int headerRow = params.getTitleRows();
        Row row = sheet.getRow(headerRow);
        if (row != null) {
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    headers.add(getCellValueAsString(cell));
                }
            }
        }

        workbook.close();
        return headers;
    }
}
