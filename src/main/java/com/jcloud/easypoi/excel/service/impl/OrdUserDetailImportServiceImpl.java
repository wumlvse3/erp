package com.jcloud.easypoi.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.easypoi.excel.entity.OrdUserDetailImport;
import com.jcloud.easypoi.excel.mapper.OrdUserDetailImportMapper;
import com.jcloud.easypoi.excel.service.OrdUserDetailImportService;
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
public class OrdUserDetailImportServiceImpl extends ServiceImpl<OrdUserDetailImportMapper, OrdUserDetailImport> implements OrdUserDetailImportService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrdUserDetailImportServiceImpl.class);

    @Autowired
    private SqlService sqlService;

    @Override
    @Transactional
    public R importData(List<OrdUserDetailImport> dataList, List<OrdUserDetailImport> failList, boolean isVerifyFail) {
        // 校验失败，收集错误信息并返回
        if (isVerifyFail && failList != null && !failList.isEmpty()) {
            List<String> errorLines = new ArrayList<>();
            errorLines.add("数据校验失败，共" + failList.size() + "条数据有误：");
            for (int i = 0; i < failList.size(); i++) {
                OrdUserDetailImport fail = failList.get(i);
                String msg = fail.getErrorMsg();
                if (msg != null && !msg.isEmpty()) {
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

        // 获取当前用户
        UserEntity user = ShiroUtils.getUserEntity();

        // 数据处理和校验
        List<String> errors = new ArrayList<>();
        List<String> ordUserInsertSqlList = new ArrayList<>();

        String salesOrg ="";
        String roleCode ="";
        String roleName ="";

        for (int i = 0; i < dataList.size(); i++) {
            OrdUserDetailImport data = dataList.get(i);
            Integer rowNum = data.getRowNum();

            // 销售组织验证
            if (data.getSalesOrg() != null && !data.getSalesOrg().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from sys_org where org_code = '" + data.getSalesOrg() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "]在组织表中不存在，不能导入！");
                }
            }
            // 销售小组和名称验证
            if (data.getRoleCode() != null && !data.getRoleCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ord_group where role_code = '" + data.getRoleCode() + "' and role_name = '" + data.getRoleName() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode == 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],销售组编码[" + data.getRoleCode() + "],销售组名称[" + data.getRoleName() + "]在销售表中不存在，不能导入！");
                }
            }
            // 销售用户验证
            if (data.getUserCode() != null && !data.getUserCode().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from sys_user where username = '" + data.getUserCode() + "' and name = '" + data.getUserMan() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：用户编码[" + data.getUserCode() + "],用户名称[" + data.getUserMan() + "]在用户表中不存在，不能导入！");
                }
            }
            // 销售部门验证
            if (data.getDept_code() != null && !data.getDept_code().isEmpty()) {
                Object countsalesOrg = sqlService.getOnef("select count(1) from sys_dept where dept_code = '" + data.getDept_code() + "' and name = '" + data.getDept_name() + "'");
                long cntsalesOrg = countsalesOrg != null ? Long.parseLong(countsalesOrg.toString()) : 0;
                if (cntsalesOrg == 0) {
                    errors.add("第" + rowNum + "行：部门编码[" + data.getDept_code() + "],部门名称[" + data.getDept_name() + "]在部门表中不存在，不能导入！");
                }
            }
            // 头表已存在验证
            if (data.getRoleCode() != null && !data.getRoleCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ord_user where role_code = '" + data.getRoleCode() + "' and sales_org='" + data.getSalesOrg() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode > 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],销售组编码[" + data.getRoleCode() + "]在头表中已存在，不允许重复！");
                }
            }
            // 明细表已存在验证
            if (data.getRoleCode() != null && !data.getRoleCode().isEmpty()) {
                Object countroleCode = sqlService.getOnef("select count(1) from ord_user_detail where role_code = '" + data.getRoleCode() + "' and sales_org='" + data.getSalesOrg() + "' and user_code = '" + data.getUserCode() + "'");
                long cntroleCode = countroleCode != null ? Long.parseLong(countroleCode.toString()) : 0;
                if (cntroleCode > 0) {
                    errors.add("第" + rowNum + "行：销售组织[" + data.getSalesOrg() + "],销售组编码[" + data.getRoleCode() + "],用户编码[" + data.getUserCode() + "]在明细表中已存在，不允许重复！");
                }
            }

            // ord_user头表生成
            if (!(salesOrg.equals(data.getSalesOrg()) && roleCode.equals(data.getRoleCode()) && roleName.equals(data.getRoleName()))) {
                String deptId = data.getDept_id() != null ? data.getDept_id().toString() : "NULL";
                String deptCode = data.getDept_code() != null ? "'" + data.getDept_code().replace("'", "''") + "'" : "NULL";
                String deptName = data.getDept_name() != null ? "'" + data.getDept_name().replace("'", "''") + "'" : "NULL";
                String notes = data.getNotes() != null ? "'" + data.getNotes().replace("'", "''") + "'" : "NULL";
                String roleId = data.getRoleId() != null ? data.getRoleId().toString() : "NULL";
                String salesOrgSql = data.getSalesOrg() != null ? "'" + data.getSalesOrg().replace("'", "''") + "'" : "NULL";
                String roleCodeSql = data.getRoleCode() != null ? "'" + data.getRoleCode().replace("'", "''") + "'" : "NULL";
                String roleNameSql = data.getRoleName() != null ? "'" + data.getRoleName().replace("'", "''") + "'" : "NULL";
                //String isDefault = data.getIsDefault() != null ? "'" + data.getIsDefault().replace("'", "''") + "'" : "NULL";

                String ordUserSql = "INSERT INTO ord_user (dept_id, dept_code, dept_name, notes, role_id, sales_org, role_code, role_name, create_user_id, create_time) " +
                        "VALUES (" + deptId + ", " + deptCode + ", " + deptName + ", " + notes + ", " + roleId + ", " + salesOrgSql + ", " + roleCodeSql + ", " + roleNameSql + ", '" + user.getUserId() + "', NOW())";
                ordUserInsertSqlList.add(ordUserSql);

                salesOrg = data.getSalesOrg();
                roleCode = data.getRoleCode();
                roleName = data.getRoleName();
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

        // 批量插入 ord_user 表
        boolean ordUserFlag = sqlService.insertList(ordUserInsertSqlList);
        if (!ordUserFlag) {
            LOGGER.error("批量插入ord_user表失败");
            return R.error("批量插入ord_user表失败");
        }

        // 批量插入 ord_user_detail 表
        boolean ordUserDetailFlag = this.saveBatch(dataList);
        if (!ordUserDetailFlag) {
            LOGGER.error("批量插入ord_user_detail表失败");
            return R.error("批量插入ord_user_detail表失败");
        }

        LOGGER.info("批量插入成功，ord_user表插入" + ordUserInsertSqlList.size() + "条数据，ord_user_detail表插入" + dataList.size() + "条数据");
        return R.ok("导入成功，ord_user表插入" + ordUserInsertSqlList.size() + "条数据，ord_user_detail表插入" + dataList.size() + "条数据");
    }
}
