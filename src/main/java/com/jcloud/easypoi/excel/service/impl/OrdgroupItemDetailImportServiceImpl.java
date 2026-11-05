package com.jcloud.easypoi.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.easypoi.excel.entity.OrdgroupItemDetailImport;
import com.jcloud.easypoi.excel.mapper.OrdgroupItemDetailImportMapper;
import com.jcloud.easypoi.excel.service.OrdgroupItemDetailImportService;
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
public class OrdgroupItemDetailImportServiceImpl extends ServiceImpl<OrdgroupItemDetailImportMapper, OrdgroupItemDetailImport> implements OrdgroupItemDetailImportService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrdgroupItemDetailImportServiceImpl.class);

    @Autowired
    private SqlService sqlService;

    @Override
    @Transactional
    public R importData(List<OrdgroupItemDetailImport> dataList, List<OrdgroupItemDetailImport> failList, boolean isVerifyFail) {
        // 校验失败，收集错误信息并返回
        if (isVerifyFail && failList != null && !failList.isEmpty()) {
            List<String> errorLines = new ArrayList<>();
            errorLines.add("数据校验失败，共" + failList.size() + "条数据有误：");
            for (int i = 0; i < failList.size(); i++) {
                OrdgroupItemDetailImport fail = failList.get(i);
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
        List<String> ordGroupItemInsertSqlList = new ArrayList<>();

        String lastSalesOrg = "";
        String lastCustomerCode = "";
        String lastCustomerName = "";

        // 数据处理和校验
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            OrdgroupItemDetailImport data = dataList.get(i);
            Integer rowNum = data.getRowNum(); // 从实体类获取真实的 Excel 行号

            // 销售组织验证
            if (data.getSalesOrg() != null && !data.getSalesOrg().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from sys_org where org_code = '" + data.getSalesOrg() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "]在组织表中不存在，不能导入！");
                }
            }
            // 库存组织验证
            if (data.getInvOrg() != null && !data.getInvOrg().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from sys_org where org_code = '" + data.getInvOrg() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：库存组织[" + data.getSalesOrg() + "]在组织表中不存在，不能导入！");
                }
            }
            // 销售组及名称验证
            if (data.getRoleCode() != null && !data.getRoleCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ord_group where role_code = '" + data.getRoleCode() + "' and role_name = '" + data.getRoleName() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode == 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],销售组编码[" + data.getRoleCode() + "],销售组名称[" + data.getRoleName() + "]在销售表中不存在，不能导入！");
                }
            }
            // 客户验证
            if (data.getCustomerCode() != null && !data.getCustomerCode().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from epd_customer_def where customer_code = '" + data.getCustomerCode() + "'  and customer_name = '" + data.getCustomerName() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],客户编码[" + data.getCustomerCode() + "],客户名称[" + data.getCustomerName() + "]在客户表中不存在，不能导入！");
                }
            }
            // 物料验证
            if (data.getItemCode()!= null && !data.getItemCode().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from epd_item_master where item_code = '" + data.getItemCode() + "' and item_name = '" + data.getItemName() + "' and inv_org='" + data.getInvOrg() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：库存组织[" + data.getInvOrg() + "],物料编码[" + data.getItemCode() + "],物料编码[" + data.getItemName() + "]在物料表中不存在，不能导入！");
                }
            }
            // 头表存在验证
            if (data.getCustomerCode() != null && !data.getCustomerCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ordgroup_item where customer_code = '" + data.getCustomerCode() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode > 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],客户编码[" + data.getCustomerCode() + "]在头表中已存在，不允许重复！");
                }
            }
            // 明细表存在验证
            if (data.getItemCode() != null && !data.getItemCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ordgroup_item_detail where item_code = '" + data.getItemCode() + "' and sales_org='" + data.getSalesOrg() + "' and customer_code = '" + data.getCustomerCode() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode > 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],客户编码[" + data.getCustomerCode() + "],物料编码[" + data.getItemCode() + "]在明细表中已存在，不允许重复！");
                }
            }

            // 构建 ordgroup_item 表的 INSERT SQL（独占字段：dept_id, dept_code, dept_name, notes + 共享字段：role_id, sales_org, role_code, role_name, is_default）
            if (!(lastSalesOrg.equals(data.getSalesOrg()) && lastCustomerCode.equals(data.getCustomerCode()) && lastCustomerName.equals(data.getCustomerName()))) {
                String salesOrg = data.getSalesOrg() != null ? "'" + data.getSalesOrg().replace("'", "''") + "'" : "NULL";
                String customerId = data.getCustomerId() != null ? data.getCustomerId().toString() : "NULL";
                String customerCode = data.getCustomerCode() != null ? "'" + data.getCustomerCode().replace("'", "''") + "'" : "NULL";
                String customerName = data.getCustomerName() != null ? "'" + data.getCustomerName().replace("'", "''") + "'" : "NULL";

                String classId = data.getClass_id() != null ? data.getClass_id().toString() : "NULL";
                String classCode = data.getClass_code() != null ? "'" + data.getClass_code().replace("'", "''") + "'" : "NULL";
                String className = data.getClass_name() != null ? "'" + data.getClass_name().replace("'", "''") + "'" : "NULL";
                String notes = data.getNotes() != null ? "'" + data.getNotes().replace("'", "''") + "'" : "NULL";
                String distribute = data.getDistribute() != null ? "'" + data.getDistribute().replace("'", "''") + "'" : "NULL";

                String ordGroupItemSql = "INSERT INTO ordgroup_item (class_id, class_code, class_name, distribute, notes, sales_org, customer_id, customer_code, customer_name, create_user_id, create_time) " +
                        "VALUES (" + classId + ", " + classCode + ", " + className + ", " + distribute + "," + notes + ", " + salesOrg + ", " + customerId + "," + customerCode + ", " + customerName + ", '" + user.getUserId() + "', NOW())";
                ordGroupItemInsertSqlList.add(ordGroupItemSql);

                lastSalesOrg = data.getSalesOrg();
                lastCustomerCode = data.getCustomerCode();
                lastCustomerName = data.getCustomerName();
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

        // 批量插入 ordgroup_item 表
        boolean ordUserFlag = sqlService.insertList(ordGroupItemInsertSqlList);
        if (!ordUserFlag) {
            LOGGER.error("批量插入ordgroup_item表失败");
            return R.error("批量插入ordgroup_item表失败");
        }

        // 批量插入 ordgroup_item_detail 表
        boolean ordUserDetailFlag = this.saveBatch(dataList);
        if (!ordUserDetailFlag) {
            LOGGER.error("批量插入ordgroup_item_detail表失败");
            return R.error("批量插入ordgroup_item_detail表失败");
        }

        LOGGER.info("批量插入成功，ordgroup_item表插入" + ordGroupItemInsertSqlList.size() + "条数据，ordgroup_item_detail表插入" + dataList.size() + "条数据");
        return R.ok("导入成功，ordgroup_item表插入" + ordGroupItemInsertSqlList.size() + "条数据，ordgroup_item_detail表插入" + dataList.size() + "条数据");

    }
}
