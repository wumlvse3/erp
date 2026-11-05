package com.jcloud.easypoi.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.easypoi.excel.entity.PurAuditedImport;
import com.jcloud.easypoi.excel.mapper.PurAuditedImportMapper;
import com.jcloud.easypoi.excel.service.PurAuditedImportService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PurAuditedImportServiceImpl extends ServiceImpl<PurAuditedImportMapper, PurAuditedImport> implements PurAuditedImportService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PurAuditedImportServiceImpl.class);

    @Autowired
    private SqlService sqlService;

    @Override
    @Transactional
    public R importData(List<PurAuditedImport> dataList, List<PurAuditedImport> failList, boolean isVerifyFail) {
        // 校验失败，收集错误信息并返回
        if (isVerifyFail && failList != null && !failList.isEmpty()) {
            List<String> errorLines = new ArrayList<>();
            errorLines.add("数据校验失败，共" + failList.size() + "条数据有误：");
            for (int i = 0; i < failList.size(); i++) {
                PurAuditedImport fail = failList.get(i);
                String msg = fail.getErrorMsg();
                if (msg != null && !msg.isEmpty()) {
                    // 替换EasyPOI的通用错误提示为更友好的提示
                    msg = msg.replace("Excel 值获取失败", "格式不正确");
                    Integer rowNum = fail.getRowNum() != null ? fail.getRowNum() : (i + 2);
                    errorLines.add("第" + rowNum + "行：" + msg);
                }
            }
            return R.error(String.join("\n", errorLines));
        }

        if (dataList == null || dataList.isEmpty()) {
            return R.error("导入数据为空");
        }

        // 获取当前用户和时间
        UserEntity user = ShiroUtils.getUserEntity();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new java.util.Date());

        // 数据处理和校验
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            PurAuditedImport data = dataList.get(i);
            int rowNum = i + 2; // Excel行号（从2开始，第1行是表头）

            // TODO: 在此添加其他数据处理和校验逻辑

            // 示例1：必输字段校验
            // if (data.getCode() == null || data.getCode().isEmpty()) {
            //     errors.add("第" + rowNum + "行：编码不能为空");
            // }

            // 示例2：根据code查询name并赋值
            // if (data.getCode() != null && !data.getCode().isEmpty()) {
            //     Map<String, Object> exist = sqlService.getOne("select name from some_table where code = '" + data.getCode() + "'");
            //     if (exist == null) {
            //         errors.add("第" + rowNum + "行：编码[" + data.getCode() + "]不存在");
            //     } else {
            //         data.setName(exist.get("name") != null ? exist.get("name").toString() : "");
            //     }
            // }

            // 示例3：校验code是否在数据库中存在
            // if (data.getCode() != null && !data.getCode().isEmpty()) {
            //     Object count = sqlService.getOnef("select count(1) from some_table where code = '" + data.getCode() + "'");
            //     long cnt = count != null ? Long.parseLong(count.toString()) : 0;
            //     if (cnt == 0) {
            //         errors.add("第" + rowNum + "行：编码[" + data.getCode() + "]在数据库中不存在");
            //     }
            // }

            // 示例4：设置默认值
            // if (data.getStatus() == null || data.getStatus().isEmpty()) {
            //     data.setStatus("1");
            // }

            // 设置生成人和生成时间
            data.setCreateUserId(user.getUserId().toString());
            data.setCreateTime(new java.util.Date());
        }

        // 处理校验失败，阻止导入
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("数据处理失败，共" + errors.size() + "个错误：\n");
            for (String error : errors) {
                sb.append(error).append("\n");
            }
            return R.error(sb.toString().trim());
        }

        // 批量插入数据库
        boolean flag = this.saveBatch(dataList);
        if (flag) {
            LOGGER.info("批量插入成功，共插入" + dataList.size() + "条数据");
            return R.ok("导入成功，共插入" + dataList.size() + "条数据");
        } else {
            return R.error("数据插入失败");
        }
    }
}
