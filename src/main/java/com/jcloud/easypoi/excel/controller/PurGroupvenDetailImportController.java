package com.jcloud.easypoi.excel.controller;

import com.jcloud.easypoi.excel.ExcelImportUtil;
import com.jcloud.easypoi.excel.entity.ImportParams;
import com.jcloud.easypoi.excel.entity.result.ExcelImportResult;
import com.jcloud.easypoi.excel.entity.PurGroupvenDetailImport;
import com.jcloud.easypoi.excel.service.PurGroupvenDetailImportService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pur_groupven_detail")
public class PurGroupvenDetailImportController {

    @Autowired
    private PurGroupvenDetailImportService importService;

    @PostMapping("/execute")
    public R importExcel(@RequestParam("file") MultipartFile file) {
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedVerify("Y");

            ExcelImportResult<PurGroupvenDetailImport> importResult = ExcelImportUtil.importExcelMore(
                    file.getInputStream(),
                    PurGroupvenDetailImport.class,
                    params
            );

            // 调用Service处理并插入数据库
            return importService.importData(importResult.getList(), importResult.getFailList(), importResult.isVerifyFail());
        } catch (Exception e) {
            return R.error("导入失败: " + e.getMessage());
        }
    }
}
