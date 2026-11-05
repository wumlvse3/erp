package com.jcloud.easypoi.demo.controller;

import com.jcloud.easypoi.util.ExcelToEntityGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/excel/generator")
public class EntityGeneratorController {

    /**
     * 通过上传 Excel 文件生成实体类代码
     */
    @PostMapping("/generate")
    @ResponseBody
    public Map<String, Object> generateEntity(
            @RequestParam("file") MultipartFile file,
            @RequestParam("className") String className,
            @RequestParam("packageName") String packageName) {
        Map<String, Object> result = new HashMap<>();
        try {
            String code = ExcelToEntityGenerator.generateFromInputStream(
                    file.getInputStream(),
                    className,
                    packageName,
                    file.getOriginalFilename().endsWith(".xlsx")
            );

            result.put("code", 200);
            result.put("message", "生成成功");
            result.put("data", code);
            result.put("fileName", className + ".java");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成失败: " + e.getMessage());
        }
        return result;
    }
}
