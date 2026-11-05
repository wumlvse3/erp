package com.jcloud.easypoi.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.easypoi.excel.entity.OrdGroupImport;
import com.jcloud.easypoi.excel.mapper.OrdGroupImportMapper;
import com.jcloud.easypoi.excel.service.OrdGroupImportService;
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
public class OrdGroupImportServiceImpl extends ServiceImpl<OrdGroupImportMapper, OrdGroupImport> implements OrdGroupImportService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrdGroupImportServiceImpl.class);

    @Autowired
    private SqlService sqlService;

    @Override
    @Transactional
    public R importData(List<OrdGroupImport> dataList, List<OrdGroupImport> failList, boolean isVerifyFail) {
        // 校验失败，收集错误信息并返回
        if (isVerifyFail && failList != null && !failList.isEmpty()) {
            List<String> errorLines = new ArrayList<>();
            errorLines.add("数据校验失败，共" + failList.size() + "条数据有误：");
            for (int i = 0; i < failList.size(); i++) {
                OrdGroupImport fail = failList.get(i);
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
            OrdGroupImport data = dataList.get(i);
            Integer rowNum = data.getRowNum(); // 从实体类获取真实的 Excel 行号

            // 销售组织不存在验证
            if (data.getSalesOrg() != null && !data.getSalesOrg().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from sys_org where org_code = '" + data.getSalesOrg() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "]在组织表中不存在，不能导入！");
                }
            }

            // 唯一性校验：销售组编码
            if (data.getRoleCode() != null && !data.getRoleCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ord_group where role_code = '" + data.getRoleCode() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode > 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],销售组编码[" + data.getRoleCode() + "]已存在，不允许重复！");
                }
            }

            // 唯一性校验：销售组名称
            if (data.getRoleName() != null && !data.getRoleName().isEmpty()) {
                Object countroleName = sqlService.getOnef("select count(1) from ord_group where role_name = '" + data.getRoleName() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntroleName = countroleName != null ? Long.parseLong(countroleName.toString()) : 0;
                if (cntroleName > 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],销售组名称[" + data.getRoleName() + "]已存在，不允许重复");
                }
            }

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
