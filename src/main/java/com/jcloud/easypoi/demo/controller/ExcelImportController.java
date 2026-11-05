package com.jcloud.easypoi.demo.controller;

import com.jcloud.easypoi.demo.entity.UserImportEntity;
import com.jcloud.easypoi.excel.ExcelDynamicImportUtil;
import com.jcloud.easypoi.excel.ExcelImportUtil;
import com.jcloud.easypoi.excel.entity.ImportParams;
import com.jcloud.easypoi.excel.entity.result.ExcelImportResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/excel/import")
public class ExcelImportController {

    /**
     * 动态导入 - 无需预定义实体类（推荐）
     * 自动解析表头，返回 List<Map<String, Object>>
     */
    @PostMapping("/dynamic")
    @ResponseBody
    public Map<String, Object> dynamicImport(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);

            List<Map<String, Object>> list = ExcelDynamicImportUtil.importExcel(
                    file.getInputStream(),
                    params
            );

            result.put("code", 200);
            result.put("message", "导入成功");
            result.put("data", list);
            result.put("count", list.size());
            result.put("headers", list.isEmpty() ? new java.util.ArrayList<>() : new java.util.ArrayList<>(list.get(0).keySet()));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取 Excel 表头信息（不读取数据）
     */
    @PostMapping("/getHeaders")
    @ResponseBody
    public Map<String, Object> getHeaders(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);

            List<String> headers = ExcelDynamicImportUtil.getHeaders(
                    file.getInputStream(),
                    params
            );

            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", headers);
            result.put("count", headers.size());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 基础导入示例 - 不返回校验结果
     */
    @PostMapping("/simple")
    @ResponseBody
    public Map<String, Object> simpleImport(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setStartRows(0);
            params.setNeedVerify("N");

            List<UserImportEntity> list = ExcelImportUtil.importExcel(
                    file.getInputStream(),
                    UserImportEntity.class,
                    params
            );

            result.put("code", 200);
            result.put("message", "导入成功");
            result.put("data", list);
            result.put("count", list.size());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 高级导入示例 - 返回校验结果，支持失败数据
     */
    @PostMapping("/advanced")
    @ResponseBody
    public Map<String, Object> advancedImport(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setStartRows(0);
            params.setNeedVerify("Y");

            ExcelImportResult<UserImportEntity> importResult = ExcelImportUtil.importExcelMore(
                    file.getInputStream(),
                    UserImportEntity.class,
                    params
            );

            result.put("code", 200);
            result.put("message", "导入完成");
            result.put("successList", importResult.getList());
            result.put("failList", importResult.getFailList());
            result.put("successCount", importResult.getList().size());
            result.put("failCount", importResult.getFailList().size());
            result.put("hasVerifyFail", importResult.isVerifyFail());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 从本地文件导入示例
     */
    @PostMapping("/fromFile")
    @ResponseBody
    public Map<String, Object> importFromFile(@RequestParam("filePath") String filePath) {
        Map<String, Object> result = new HashMap<>();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);

            List<UserImportEntity> list = ExcelImportUtil.importExcel(
                    new java.io.File(filePath),
                    UserImportEntity.class,
                    params
            );

            result.put("code", 200);
            result.put("message", "导入成功");
            result.put("data", list);
            result.put("count", list.size());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }
}
