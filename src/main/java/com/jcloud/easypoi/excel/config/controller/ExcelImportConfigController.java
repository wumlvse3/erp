package com.jcloud.easypoi.excel.config.controller;

import com.jcloud.easypoi.excel.config.entity.ExcelImportConfig;
import com.jcloud.easypoi.excel.config.service.ExcelImportConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/excel")
public class ExcelImportConfigController {

    @Autowired
    private ExcelImportConfigService configService;

    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ExcelImportConfig> list = configService.listAll();
            result.put("code", 200);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            ExcelImportConfig config = configService.getById(id);
            result.put("code", 200);
            result.put("data", config);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@RequestBody ExcelImportConfig config) {
        Map<String, Object> result = new HashMap<>();
        try {
            int id = Math.toIntExact(configService.save(config));
            result.put("code", 200);
            result.put("data", id);
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> update(@RequestBody ExcelImportConfig config) {
        Map<String, Object> result = new HashMap<>();
        try {
            configService.update(config);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            configService.delete(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/getTableColumns/{tableName}")
    @ResponseBody
    public Map<String, Object> getTableColumns(@PathVariable String tableName) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<java.util.Map<String, Object>> columns = configService.getTableColumns(tableName);
            result.put("code", 200);
            result.put("data", columns);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/getByTableName/{tableName}")
    @ResponseBody
    public Map<String, Object> getByTableName(@PathVariable String tableName) {
        Map<String, Object> result = new HashMap<>();
        try {
            ExcelImportConfig config = configService.getByTableName(tableName);
            result.put("code", 200);
            result.put("data", config);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
