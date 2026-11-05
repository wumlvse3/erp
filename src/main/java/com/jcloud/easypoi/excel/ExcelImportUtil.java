package com.jcloud.easypoi.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.jcloud.easypoi.excel.entity.ImportParams;
import com.jcloud.easypoi.excel.entity.result.ExcelImportResult;
import com.jcloud.easypoi.excel.imports.ExcelImportService;
import com.jcloud.easypoi.exception.excel.ExcelImportException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelImportUtil {

    private ExcelImportUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelImportUtil.class);

    /**
     * Excel 导入 数据源本地文件,不返回校验结果
     * 导入字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param file Excel文件
     * @param pojoClass 实体类
     * @param params 导入参数
     * @return 导入结果列表
     */
    public static <T> List<T> importExcel(File file, Class<?> pojoClass, ImportParams params) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return new ExcelImportService().importExcelByIs(in, pojoClass, params, false).getList();
        } catch (ExcelImportException e) {
            throw new ExcelImportException(e.getType(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Excel 导入 数据源IO流,不返回校验结果
     * 导入字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param inputstream 输入流
     * @param pojoClass 实体类
     * @param params 导入参数
     * @return 导入结果列表
     * @throws Exception 导入异常
     */
    public static <T> List<T> importExcel(InputStream inputstream, Class<?> pojoClass,
                                          ImportParams params) throws Exception {
        return new ExcelImportService().importExcelByIs(inputstream, pojoClass, params, false).getList();
    }

    /**
     * Excel 导入 数据源IO流 字段类型 Integer,Long,Double,Date,String,Boolean
     * 支持校验,支持Key-Value
     *
     * @param inputstream 输入流
     * @param pojoClass 实体类
     * @param params 导入参数
     * @return 导入结果(包含失败数据)
     * @throws Exception 导入异常
     */
    public static <T> ExcelImportResult<T> importExcelMore(InputStream inputstream,
                                                             Class<?> pojoClass,
                                                             ImportParams params) throws Exception {
        return new ExcelImportService().importExcelByIs(inputstream, pojoClass, params, true);
    }

    /**
     * Excel 导入 数据源本地文件 字段类型 Integer,Long,Double,Date,String,Boolean
     * 支持校验,支持Key-Value
     *
     * @param file Excel文件
     * @param pojoClass 实体类
     * @param params 导入参数
     * @return 导入结果(包含失败数据)
     */
    public static <T> ExcelImportResult<T> importExcelMore(File file, Class<?> pojoClass,
                                                             ImportParams params) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return new ExcelImportService().importExcelByIs(in, pojoClass, params, true);
        } catch (ExcelImportException e) {
            throw new ExcelImportException(e.getType(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
