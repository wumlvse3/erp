package com.jcloud.easypoi.excel.controller;

import com.jcloud.easypoi.excel.config.entity.ExcelImportConfig;
import com.jcloud.easypoi.excel.config.service.ExcelImportConfigService;
import com.jcloud.easypoi.excel.generator.ExcelCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/excel/universal")
public class UniversalExcelImportController {

    @Autowired
    private ExcelImportConfigService configService;

    @PostMapping("/import")
    @ResponseBody
    public Map<String, Object> universalImport(
            @RequestParam("tableName") String tableName,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            ExcelImportConfig config = configService.getByTableName(tableName);
            if (config == null) {
                result.put("code", 404);
                result.put("message", "未找到表 [" + tableName + "] 的导入配置");
                return result;
            }

            String entityCode = ExcelCodeGenerator.generateEntityCode(config);
            String controllerCode = ExcelCodeGenerator.generateControllerCode(config);

            String packageName = config.getPackageName() != null && !config.getPackageName().isEmpty() 
                    ? config.getPackageName() : "com.jcloud";
            String entityName = config.getEntityName() != null && !config.getEntityName().isEmpty() 
                    ? config.getEntityName() : toPascalCaseFromUnderline(config.getTableName());
            String moduleName = config.getModuleName() != null && !config.getModuleName().isEmpty() 
                    ? config.getModuleName() : config.getTableName().split("_")[0];

            String entityPath = packageName.replace(".", "/") + "/entity/" + entityName + "Import.java";
            String controllerPath = packageName.replace(".", "/") + "/controller/" + entityName + "ImportController.java";

            ExcelCodeGenerator.writeToFile(entityCode, entityPath);
            ExcelCodeGenerator.writeToFile(controllerCode, controllerPath);

            result.put("code", 200);
            result.put("message", "代码生成成功，请重新编译项目后使用");
            result.put("entityPath", entityPath);
            result.put("controllerPath", controllerPath);
            result.put("entityCode", entityCode);
            result.put("controllerCode", controllerCode);
            result.put("importUrl", "/api/" + moduleName + "/import/execute");

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/importAndExecute")
    @ResponseBody
    public Map<String, Object> importAndExecute(
            @RequestParam("tableName") String tableName,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            ExcelImportConfig config = configService.getByTableName(tableName);
            if (config == null) {
                result.put("code", 404);
                result.put("message", "未找到表 [" + tableName + "] 的导入配置");
                return result;
            }

            String entityCode = ExcelCodeGenerator.generateEntityCode(config);
            String controllerCode = ExcelCodeGenerator.generateControllerCode(config);

            String packageName = config.getPackageName() != null && !config.getPackageName().isEmpty() 
                    ? config.getPackageName() : "com.jcloud";
            String entityName = config.getEntityName() != null && !config.getEntityName().isEmpty() 
                    ? config.getEntityName() : toPascalCaseFromUnderline(config.getTableName());
            String moduleName = config.getModuleName() != null && !config.getModuleName().isEmpty() 
                    ? config.getModuleName() : config.getTableName().split("_")[0];

            String entityPath = packageName.replace(".", "/") + "/entity/" + entityName + "Import.java";
            String controllerPath = packageName.replace(".", "/") + "/controller/" + entityName + "ImportController.java";

            ExcelCodeGenerator.writeToFile(entityCode, entityPath);
            ExcelCodeGenerator.writeToFile(controllerCode, controllerPath);

            result.put("code", 200);
            result.put("message", "代码已生成，请重新编译项目后使用导入接口");
            result.put("importUrl", "/api/" + moduleName + "/import/execute");
            result.put("entityCode", entityCode);
            result.put("controllerCode", controllerCode);

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> previewCode(@RequestParam("tableName") String tableName) {
        Map<String, Object> result = new HashMap<>();
        try {
            ExcelImportConfig config = configService.getByTableName(tableName);
            if (config == null) {
                result.put("code", 404);
                result.put("message", "未找到表 [" + tableName + "] 的导入配置");
                return result;
            }

            String entityCode = ExcelCodeGenerator.generateEntityCode(config);
            String controllerCode = ExcelCodeGenerator.generateControllerCode(config);

            String moduleName = config.getModuleName() != null && !config.getModuleName().isEmpty() 
                    ? config.getModuleName() : config.getTableName().split("_")[0];

            result.put("code", 200);
            result.put("entityCode", entityCode);
            result.put("controllerCode", controllerCode);
            result.put("importUrl", "/api/" + moduleName + "/import/execute");

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    private static String toPascalCaseFromUnderline(String name) {
        if (name == null || name.isEmpty()) return name;
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }
}
