package com.jcloud.erp.pur.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.pur.entity.PurAgreementHead;
import com.jcloud.erp.pur.mapper.PurAgreementHeadMapper;
import com.jcloud.erp.pur.service.PurAgreementHeadService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class PurAgreementHeadServiceimpl extends ServiceImpl<PurAgreementHeadMapper, PurAgreementHead> implements PurAgreementHeadService {
    @Resource
    private SqlService sqlService;

    @Override
    public R addObj(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("update pur_agreement_head set bill_status = '审核通过' where uu_id = '" + PurAgreementHead.getUu_id() + "'");
            insertsql.add("update pur_agreement_detail set line_status = '审核通过' where uk_agreement_uuid = '" + PurAgreementHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("提交成功");
        }else{
            return R.error("提交失败");
        }
    }

    @Override
    public R orderCreate(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        boolean createflag = true;

        // 获取当前用户信息
        UserEntity user = ShiroUtils.getUserEntity();
        Long userId = user.getUserId();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String prefix = currentDate.format(formatter);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bill_date = currentDate.format(formatter2);

        // 生成订单号
        String maxOrderNoSql = "SELECT MAX(uk_order_no) as max_no FROM pur_order_head WHERE uk_order_no LIKE '" + prefix + "%'";
        Map<String, Object> maxResult = sqlService.getOne(maxOrderNoSql);
        int startSeq = 1;
        if (maxResult != null && maxResult.get("max_no") != null) {
            String maxNo = maxResult.get("max_no").toString();
            String seqStr = maxNo.substring(maxNo.length() - 4);
            startSeq = Integer.parseInt(seqStr) + 1;
        }
        int seq = startSeq;

        for (PurAgreementHead agreement : params) {
            String agreementHeadUuId = agreement.getUu_id();

            // 查询协议头信息
            String agreementHeadSql = "SELECT * FROM pur_agreement_head WHERE uu_id = '" + agreementHeadUuId + "'";
            Map<String, Object> agreementHead = sqlService.getOne(agreementHeadSql);
            if (agreementHead == null) {
                return R.error("协议头不存在");
            }

            // 从协议头获取供应商、币别等信息
            String purOrg = agreementHead.get("pur_org") != null ? agreementHead.get("pur_org").toString() : "";
            String vendorId = agreementHead.get("vendor_id") != null ? agreementHead.get("vendor_id").toString() : "";
            String vendorCode = agreementHead.get("vendor_code") != null ? agreementHead.get("vendor_code").toString() : "";
            String vendorName = agreementHead.get("vendor_name") != null ? agreementHead.get("vendor_name").toString() : "";
            String currencyCode = agreementHead.get("currency_code") != null ? agreementHead.get("currency_code").toString() : "";
            String currencyId = agreementHead.get("currency_id") != null ? agreementHead.get("currency_id").toString() : "";
            String currencyDesc = agreementHead.get("currency_desc") != null ? agreementHead.get("currency_desc").toString() : "";
            String groupId = agreementHead.get("group_id") != null ? agreementHead.get("group_id").toString() : "";
            String deptId = agreementHead.get("dept_id") != null ? agreementHead.get("dept_id").toString() : "";
            String deptCode = agreementHead.get("dept_code") != null ? agreementHead.get("dept_code").toString() : "";
            String deptName = agreementHead.get("dept_name") != null ? agreementHead.get("dept_name").toString() : "";
            String buyerId = agreementHead.get("buyer_person_id") != null ? agreementHead.get("buyer_person_id").toString() : "";
            String buyerCode = agreementHead.get("buyer_person_code") != null ? agreementHead.get("buyer_person_code").toString() : "";
            String buyerMan = agreementHead.get("buyer_person_man") != null ? agreementHead.get("buyer_person_man").toString() : "";
            String roleId = agreementHead.get("role_id") != null ? agreementHead.get("role_id").toString() : "";
            String roleCode = agreementHead.get("role_code") != null ? agreementHead.get("role_code").toString() : "";
            String roleName = agreementHead.get("role_name") != null ? agreementHead.get("role_name").toString() : "";
            String notes = agreementHead.get("notes") != null ? agreementHead.get("notes").toString() : "";

            // 查询币别信息
            String currencySql = "SELECT exchange_rate, decimal_digits_price, decimal_digits_amt FROM epd_currency WHERE currency_code = '" + currencyCode + "' AND valid_flag = 'Y' LIMIT 1";
            Map<String, Object> currencyInfo = sqlService.getOne(currencySql);
            double exchangeRate = 1.0;
            int decimalDigitsPrice = 2;
            int decimalDigitsAmt = 2;
            if (currencyInfo != null) {
                String exchangeRateStr = currencyInfo.get("exchange_rate") != null ? currencyInfo.get("exchange_rate").toString() : "1";
                exchangeRate = Double.parseDouble(exchangeRateStr);
                String decimalDigitsPriceStr = currencyInfo.get("decimal_digits_price") != null ? currencyInfo.get("decimal_digits_price").toString() : "2";
                decimalDigitsPrice = Integer.parseInt(decimalDigitsPriceStr);
                String decimalDigitsAmtStr = currencyInfo.get("decimal_digits_amt") != null ? currencyInfo.get("decimal_digits_amt").toString() : "2";
                decimalDigitsAmt = Integer.parseInt(decimalDigitsAmtStr);
            }

            // 查询协议明细数据（只查询未生成完的明细）
            String agreementDetailSql = "SELECT * FROM pur_agreement_detail WHERE uk_agreement_uuid = '" + agreementHeadUuId + "' AND (is_active = 'Y' OR is_active IS NULL)";
            List<Map<String, Object>> agreementDetails = sqlService.getAll(agreementDetailSql);
            if (agreementDetails == null || agreementDetails.isEmpty()) {
                return R.error("协议明细不存在或已全部生成完毕");
            }

            // 校验所有明细
            for (Map<String, Object> agreementDetail : agreementDetails) {
                String itemCode = agreementDetail.get("item_code") != null ? agreementDetail.get("item_code").toString() : "";
                String applyOrg = agreementDetail.get("apply_org") != null ? agreementDetail.get("apply_org").toString() : "";

                // 校验物料主文件
                String itemSql = "SELECT item_code FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
                Map<String, Object> itemInfo = sqlService.getOne(itemSql);
                if (itemInfo == null) {
                    errorMsg.append("物料编码：").append(itemCode).append(" 在物料主文件中不存在\n");
                    continue;
                }
            }

            // 如果有错误，返回错误信息
            if (errorMsg.length() > 0) {
                return R.error("采购订单生成失败，存在以下问题：\n" + errorMsg.toString());
            }

            // 生成订单号
            String orderNo = prefix + String.format("%04d", seq);
            seq++;

            // 插入订单头（从协议头复制）
            String headInsert = "INSERT INTO pur_order_head(" +
                    "uk_order_no, pur_org, type_code, bill_date, transport_type, generation_type, " +
                    "up_bill, vendor_code, sign_date, sign_person_code, contract_no, contract_name, " +
                    "role_code, dept_code, buyer_person_code, base_tax_money, base_currency_code, " +
                    "currency_code, address, is_collect, settlement, pay_way, " +
                    "internal_sales_order_no, set_mode, cen_po_no, cen_so_no, def_inv_org_no, def_warehouse_no, intransit_own, " +
                    "pay_term, remarks, payable_vendor, payable_po, project_no, work_order, batch_no, " +
                    "is_direct, is_sale, is_begin, bill_status, create_user_id, create_time, " +
                    "modify_user_id, modify_time, notes, approver_user_id, approver_user_code, approver_user_man, approver_time, approver_opinion, " +
                    "project_id, project_name, s_uu_id, group_id, flow_id, is_deleted, " +
                    "vendor_id, vendor_name, sign_person_id, sign_person_man, role_id, currency_id, currency_desc, " +
                    "role_name, dept_id, dept_name, buyer_person_id, buyer_person_man, " +
                    "base_currency_id, base_currency_desc" +
                    ") SELECT " +
                    "'" + orderNo + "', pur_org, type_code, '" + bill_date + "', transport_type, '协议生成', " +
                    "uk_agreement_id, vendor_code, sign_date, sign_person_code, contract_no, contract_name, " +
                    "role_code, dept_code, buyer_person_code, 0, base_currency_code, " +
                    "base_currency_code, address, is_collect, settlement, pay_way, " +
                    "'', '', '', '', '', '', '', " +
                    "pay_term, remarks, payable_vendor, payable_po, project_no, work_order, '', " +
                    "'N', 'N', 'N', '未提交', '" + userId + "', NOW(), " +
                    "'', null, IFNULL(notes, ''), '', '', '', null, '', " +
                    "project_id, project_name, uu_id, group_id, flow_id, is_deleted, " +
                    "vendor_id, vendor_name, sign_person_id, sign_person_man, role_id, base_currency_id, base_currency_desc, " +
                    "role_name, dept_id, dept_name, buyer_person_id, buyer_person_man, " +
                    "base_currency_id, base_currency_desc " +
                    "FROM pur_agreement_head WHERE uu_id = '" + agreementHeadUuId + "'";
            insertsql.add(headInsert);

            // 循环插入订单明细
            int detailSeq = 0;
            for (Map<String, Object> agreementDetail : agreementDetails) {
                String item_id = agreementDetail.get("item_id") != null ? agreementDetail.get("item_id").toString() : "";
                String itemCode = agreementDetail.get("item_code") != null ? agreementDetail.get("item_code").toString() : "";
                String applyOrg = agreementDetail.get("apply_org") != null ? agreementDetail.get("apply_org").toString() : "";
                String needDate = agreementDetail.get("ship_date") != null ? agreementDetail.get("ship_date").toString() : "";

                // 计算可生成数量（agreement_qty - ord_num）
                String dbAgreementQtyStr = agreementDetail.get("agreement_qty") != null ? agreementDetail.get("agreement_qty").toString() : "0";
                double dbAgreementQty = Double.parseDouble(dbAgreementQtyStr);
                String dbPurNumStr = agreementDetail.get("ord_num") != null ? agreementDetail.get("ord_num").toString() : "0";
                double dbPurNum = Double.parseDouble(dbPurNumStr);
                double planQty = dbAgreementQty - dbPurNum;

                // 如果可生成数量小于等于0，跳过该明细
                if (planQty <= 0) {
                    continue;
                }

                // 从协议明细表获取单价和税率
                double guidePrice = agreementDetail.get("unit_price") != null ? Double.parseDouble(agreementDetail.get("unit_price").toString()) : 0;
                String tax_code = agreementDetail.get("tax_code") != null ? agreementDetail.get("tax_code").toString() : "";
                double tax_ratio = 0;

                // 查询税率信息
                String taxSql = "SELECT tax_ratio, tax_code, uu_id as tax_id, tax_name FROM epd_tax WHERE tax_code = '" + tax_code + "' LIMIT 1";
                Map<String, Object> taxInfo = sqlService.getOne(taxSql);
                String tax_id = "";
                String tax_name = "";
                if (taxInfo != null) {
                    tax_ratio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
                    tax_id = taxInfo.get("tax_id") != null ? taxInfo.get("tax_id").toString() : "";
                    tax_name = taxInfo.get("tax_name") != null ? taxInfo.get("tax_name").toString() : "";
                }

                String purUnitId = agreementDetail.get("ord_unit_id") != null ? agreementDetail.get("ord_unit_id").toString() : "";
                String purUnitName = agreementDetail.get("ord_unit_name") != null ? agreementDetail.get("ord_unit_name").toString() : "";
                String mainUnitId = agreementDetail.get("main_unit_id") != null ? agreementDetail.get("main_unit_id").toString() : "";
                String mainUnitName = agreementDetail.get("main_unit_name") != null ? agreementDetail.get("main_unit_name").toString() : "";
                String changeRateStr = agreementDetail.get("change_rate") != null ? agreementDetail.get("change_rate").toString() : "1";
                double changeRate = Double.parseDouble(changeRateStr);
                String projectId = agreementDetail.get("project_id") != null ? agreementDetail.get("project_id").toString() : "";
                String projectNo = agreementDetail.get("project_no") != null ? agreementDetail.get("project_no").toString() : "";
                String projectName = agreementDetail.get("project_name") != null ? agreementDetail.get("project_name").toString() : "";
                String workOrder = agreementDetail.get("work_order") != null ? agreementDetail.get("work_order").toString() : "";
                String detailNotes = agreementDetail.get("notes") != null ? agreementDetail.get("notes").toString() : "";
                String detailUuId = agreementDetail.get("uu_id") != null ? agreementDetail.get("uu_id").toString() : "";
                String ukLineNo = agreementDetail.get("uk_line_no") != null ? agreementDetail.get("uk_line_no").toString() : "";

                // 查询物料主文件获取第二单位信息
                String itemSql = "SELECT second_unit, unit_factor_p FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
                Map<String, Object> itemInfo = sqlService.getOne(itemSql);
                String secondUnit = "";
                double unitFactorP = 1;
                if (itemInfo != null) {
                    secondUnit = itemInfo.get("second_unit") != null ? itemInfo.get("second_unit").toString() : "";
                    String unitFactorPStr = itemInfo.get("unit_factor_p") != null ? itemInfo.get("unit_factor_p").toString() : "1";
                    unitFactorP = Double.parseDouble(unitFactorPStr);
                }

                // 根据币别小数位数计算精度因子
                double priceFactor = Math.pow(10, decimalDigitsPrice);
                double amtFactor = Math.pow(10, decimalDigitsAmt);

                // 计算第二单位数量
                double calcMainQty;
                if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                    calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
                } else {
                    calcMainQty = planQty;
                }

                // 计算金额
                double amt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
                double taxAmount = Math.round(amt * (tax_ratio / 100) / (1 + tax_ratio / 100) * amtFactor) / amtFactor;
                double noTaxPrice = Math.round(guidePrice / (1 + tax_ratio / 100) * priceFactor) / priceFactor;
                double noTaxAmount = Math.round(noTaxPrice * planQty * amtFactor) / amtFactor;

                detailSeq++;
                String detailInsert = "INSERT INTO pur_order_detail(" +
                        "uk_order_no, uk_order_uuid, uk_line_no, apply_org, ship_date, item_code, item_name, " +
                        "item_spec, item_model, warehouse_code, pur_qty, pur_unit_id, main_qty, main_unit_id, " +
                        "change_rate, source_inv_org, source_price, up_bill, up_bill_line, exchange_rate, " +
                        "currency_code, tax_code, unit_price, amt, tax_amount, no_tax_price, no_tax_amount, " +
                        "base_currency_code, base_tax_price, base_tax_money, gift, manufacturer, prod_date, " +
                        "work_order, project_no, batch_no, guide_price, line_status, is_ship, quality_qty, " +
                        "ship_qty, return_qty, is_ship_ret, cost_qty, cost_money, store_qty, return_stock_qty, " +
                        "is_stock_return, is_cost, cost, create_user_id, create_time, modify_user_id, modify_time, " +
                        "notes, tax_id, tax_name, is_close, pur_unit_name, main_unit_name, base_currency_id, " +
                        "base_currency_desc, project_id, project_name, line_opinion, s_uu_id, group_id, flow_id, " +
                        "is_deleted, item_id, warehouse_id, warehouse_name, currency_id, currency_desc" +
                        ") VALUES (" +
                        "'" + orderNo + "', (SELECT uu_id FROM pur_order_head WHERE uk_order_no = '" + orderNo + "'), " +
                        "'" + detailSeq + "', '" + applyOrg + "', '" + needDate + "', '" + itemCode + "', " +
                        "(SELECT item_name FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_spec FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_model FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "'', " + planQty + ", '" + purUnitId + "', " + calcMainQty + ", '" + mainUnitId + "', " +
                        changeRate + ", '" + applyOrg + "', '协议', '" + agreementHeadUuId + "', '" + ukLineNo + "', " + exchangeRate + ", " +
                        "'" + currencyCode + "', '" + tax_code + "', " + guidePrice + ", " + amt + ", " + taxAmount + ", " + noTaxPrice + ", " + noTaxAmount + ", " +
                        "'" + currencyCode + "', " + guidePrice + ", " + amt + ", 'N', '', '', " +
                        "'" + workOrder + "', '" + projectNo + "', '', " + guidePrice + ", '未提交', 'N', 0, " +
                        "0, 0, 'N', 0, 0, 0, 0, " +
                        "'N', 'N', 0, '" + userId + "', NOW(), '', NULL, " +
                        "'" + detailNotes + "', '" + tax_id + "', '" + tax_name + "', 'N', '" + purUnitName + "', '" + mainUnitName + "', '" + currencyId + "', " +
                        "'" + currencyDesc + "', '" + projectId + "', '" + projectName + "', '', '" + detailUuId + "', '" + groupId + "', '', " +
                        "0, '', '', '', '" + currencyId + "', '" + currencyDesc + "'" +
                        ")";
                insertsql.add(detailInsert);

                // 累加已生成数量到 ord_num
                String updatePurNum = "UPDATE pur_agreement_detail SET ord_num = IFNULL(ord_num, 0) + " + planQty + " WHERE uu_id = '" + detailUuId + "'";
                insertsql.add(updatePurNum);

                // 检查该明细行是否已生成完毕（ord_num >= agreement_qty），如果是则标记 is_active = 'N'
                String updateDetailActive = "UPDATE pur_agreement_detail SET is_active = 'N' WHERE IFNULL(ord_num, 0) >= agreement_qty AND uu_id = '" + detailUuId + "'";
                insertsql.add(updateDetailActive);
            }

            // 检查是否所有明细行都已标记为 N，如果是则更新头表 is_active = 'N'
            String updateHeadActive = "UPDATE pur_agreement_head SET is_active = 'N' WHERE uu_id = '" + agreementHeadUuId + "' AND NOT EXISTS(SELECT 1 FROM pur_agreement_detail WHERE is_active = 'Y' AND uk_agreement_uuid = '" + agreementHeadUuId + "')";
            insertsql.add(updateHeadActive);

            // 汇总订单头金额
            String sumAmt = "UPDATE pur_order_head SET base_tax_money = (SELECT IFNULL(SUM(IFNULL(amt, 0)), 0) FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "') WHERE uk_order_no = '" + orderNo + "'";
            insertsql.add(sumAmt);
        }

        // 只有所有校验通过（createflag为true）才提交执行
        if (createflag && !insertsql.isEmpty()) {
            boolean flag = sqlService.insertList(insertsql);
            if (flag) {
                return R.ok("采购订单生成成功");
            } else {
                return R.error("采购订单生成失败");
            }
        } else {
            return R.error("采购订单生成失败");
        }
    }

    @Override
    public R detailCreate(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        boolean createflag = true;

        // 获取当前用户信息
        UserEntity user = ShiroUtils.getUserEntity();
        Long userId = user.getUserId();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String prefix = currentDate.format(formatter);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bill_date = currentDate.format(formatter2);

        // 生成订单号
        String maxOrderNoSql = "SELECT MAX(uk_order_no) as max_no FROM pur_order_head WHERE uk_order_no LIKE '" + prefix + "%'";
        Map<String, Object> maxResult = sqlService.getOne(maxOrderNoSql);
        int startSeq = 1;
        if (maxResult != null && maxResult.get("max_no") != null) {
            String maxNo = maxResult.get("max_no").toString();
            String seqStr = maxNo.substring(maxNo.length() - 4);
            startSeq = Integer.parseInt(seqStr) + 1;
        }
        int seq = startSeq;

        // 收集所有传入的明细uu_id
        List<String> detailUuIds = new ArrayList<>();
        for (PurAgreementHead agreement : params) {
            detailUuIds.add("'" + agreement.getUu_id() + "'");
        }

        // 通过任意一条明细反查协议头表的uu_id
        String headUuIdSql = "SELECT uk_agreement_uuid FROM pur_agreement_detail WHERE uu_id = " + detailUuIds.get(0);
        Map<String, Object> headResult = sqlService.getOne(headUuIdSql);
        if (headResult == null || headResult.get("uk_agreement_uuid") == null) {
            return R.error("明细记录不存在或关联的协议头为空");
        }
        String agreementHeadUuId = headResult.get("uk_agreement_uuid").toString();

        // 查询协议头信息
        String agreementHeadSql = "SELECT * FROM pur_agreement_head WHERE uu_id = '" + agreementHeadUuId + "'";
        Map<String, Object> agreementHead = sqlService.getOne(agreementHeadSql);
        if (agreementHead == null) {
            return R.error("协议头不存在");
        }

        // 从协议头获取供应商、币别等信息
        String purOrg = agreementHead.get("pur_org") != null ? agreementHead.get("pur_org").toString() : "";
        String vendorId = agreementHead.get("vendor_id") != null ? agreementHead.get("vendor_id").toString() : "";
        String vendorCode = agreementHead.get("vendor_code") != null ? agreementHead.get("vendor_code").toString() : "";
        String vendorName = agreementHead.get("vendor_name") != null ? agreementHead.get("vendor_name").toString() : "";
        String currencyCode = agreementHead.get("currency_code") != null ? agreementHead.get("currency_code").toString() : "";
        String currencyId = agreementHead.get("currency_id") != null ? agreementHead.get("currency_id").toString() : "";
        String currencyDesc = agreementHead.get("currency_desc") != null ? agreementHead.get("currency_desc").toString() : "";
        String groupId = agreementHead.get("group_id") != null ? agreementHead.get("group_id").toString() : "";
        String deptId = agreementHead.get("dept_id") != null ? agreementHead.get("dept_id").toString() : "";
        String deptCode = agreementHead.get("dept_code") != null ? agreementHead.get("dept_code").toString() : "";
        String deptName = agreementHead.get("dept_name") != null ? agreementHead.get("dept_name").toString() : "";
        String buyerId = agreementHead.get("buyer_person_id") != null ? agreementHead.get("buyer_person_id").toString() : "";
        String buyerCode = agreementHead.get("buyer_person_code") != null ? agreementHead.get("buyer_person_code").toString() : "";
        String buyerMan = agreementHead.get("buyer_person_man") != null ? agreementHead.get("buyer_person_man").toString() : "";
        String roleId = agreementHead.get("role_id") != null ? agreementHead.get("role_id").toString() : "";
        String roleCode = agreementHead.get("role_code") != null ? agreementHead.get("role_code").toString() : "";
        String roleName = agreementHead.get("role_name") != null ? agreementHead.get("role_name").toString() : "";
        String notes = agreementHead.get("notes") != null ? agreementHead.get("notes").toString() : "";

        // 查询币别信息
        String currencySql = "SELECT exchange_rate, decimal_digits_price, decimal_digits_amt FROM epd_currency WHERE currency_code = '" + currencyCode + "' AND valid_flag = 'Y' LIMIT 1";
        Map<String, Object> currencyInfo = sqlService.getOne(currencySql);
        double exchangeRate = 1.0;
        int decimalDigitsPrice = 2;
        int decimalDigitsAmt = 2;
        if (currencyInfo != null) {
            String exchangeRateStr = currencyInfo.get("exchange_rate") != null ? currencyInfo.get("exchange_rate").toString() : "1";
            exchangeRate = Double.parseDouble(exchangeRateStr);
            String decimalDigitsPriceStr = currencyInfo.get("decimal_digits_price") != null ? currencyInfo.get("decimal_digits_price").toString() : "2";
            decimalDigitsPrice = Integer.parseInt(decimalDigitsPriceStr);
            String decimalDigitsAmtStr = currencyInfo.get("decimal_digits_amt") != null ? currencyInfo.get("decimal_digits_amt").toString() : "2";
            decimalDigitsAmt = Integer.parseInt(decimalDigitsAmtStr);
        }

        // 查询选中的明细数据
        String detailIdsStr = String.join(",", detailUuIds);
        String agreementDetailSql = "SELECT * FROM pur_agreement_detail WHERE uu_id IN (" + detailIdsStr + ")";
        List<Map<String, Object>> agreementDetails = sqlService.getAll(agreementDetailSql);
        if (agreementDetails == null || agreementDetails.isEmpty()) {
            return R.error("未找到选中的协议明细");
        }

        // 校验所有选中的明细
        for (Map<String, Object> agreementDetail : agreementDetails) {
            String itemCode = agreementDetail.get("item_code") != null ? agreementDetail.get("item_code").toString() : "";
            String applyOrg = agreementDetail.get("apply_org") != null ? agreementDetail.get("apply_org").toString() : "";

            // 校验物料主文件
            String itemSql = "SELECT item_code FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
            Map<String, Object> itemInfo = sqlService.getOne(itemSql);
            if (itemInfo == null) {
                errorMsg.append("物料编码：").append(itemCode).append(" 在物料主文件中不存在\n");
                continue;
            }

            // 从页面传入的 PurAgreementHead 中获取 create_qty
            String detailUuIdForQty = agreementDetail.get("uu_id") != null ? agreementDetail.get("uu_id").toString() : "";
            double createQty = 0;
            for (PurAgreementHead agreement : params) {
                if (agreement.getUu_id() != null && agreement.getUu_id().equals(detailUuIdForQty)) {
                    String createQtyStr = agreement.getCreate_qty() != null ? agreement.getCreate_qty() : "0";
                    createQty = Double.parseDouble(createQtyStr);
                    break;
                }
            }

            // 校验：create_qty 不能超过剩余可生成数量（agreement_qty - ord_num）
            String dbAgreementQtyStr = agreementDetail.get("agreement_qty") != null ? agreementDetail.get("agreement_qty").toString() : "0";
            double dbAgreementQty = Double.parseDouble(dbAgreementQtyStr);
            String dbPurNumStr = agreementDetail.get("ord_num") != null ? agreementDetail.get("ord_num").toString() : "0";
            double dbPurNum = Double.parseDouble(dbPurNumStr);
            double remainQty = dbAgreementQty - dbPurNum;
            if (createQty > remainQty) {
                errorMsg.append("物料：").append(itemCode).append(" 生成订单数量（").append(createQty)
                        .append("）超过剩余可生成数量（").append(remainQty).append("）\n");
                createflag = false;
                continue;
            }
            if (createQty <= 0) {
                errorMsg.append("物料：").append(itemCode).append("的生成订单数量必须大于0\n");
                createflag = false;
                continue;
            }
        }

        // 如果有错误，返回错误信息
        if (errorMsg.length() > 0) {
            return R.error("采购订单生成失败，存在以下问题：\n" + errorMsg.toString());
        }

        // 生成订单号
        String orderNo = prefix + String.format("%04d", seq);

        // 插入订单头（从协议头复制）
        String headInsert = "INSERT INTO pur_order_head(" +
                "uk_order_no, pur_org, type_code, bill_date, transport_type, generation_type, " +
                "up_bill, vendor_code, sign_date, sign_person_code, contract_no, contract_name, " +
                "role_code, dept_code, buyer_person_code, base_tax_money, base_currency_code, " +
                "currency_code, address, is_collect, settlement, pay_way, " +
                "internal_sales_order_no, set_mode, cen_po_no, cen_so_no, def_inv_org_no, def_warehouse_no, intransit_own, " +
                "pay_term, remarks, payable_vendor, payable_po, project_no, work_order, batch_no, " +
                "is_direct, is_sale, is_begin, bill_status, create_user_id, create_time, " +
                "modify_user_id, modify_time, notes, approver_user_id, approver_user_code, approver_user_man, approver_time, approver_opinion, " +
                "project_id, project_name, s_uu_id, group_id, flow_id, is_deleted, " +
                "vendor_id, vendor_name, sign_person_id, sign_person_man, role_id, currency_id, currency_desc, " +
                "role_name, dept_id, dept_name, buyer_person_id, buyer_person_man, " +
                "base_currency_id, base_currency_desc" +
                ") SELECT " +
                "'" + orderNo + "', pur_org, type_code, '" + bill_date + "', transport_type, '协议生成', " +
                "uk_agreement_id, vendor_code, sign_date, sign_person_code, contract_no, contract_name, " +
                "role_code, dept_code, buyer_person_code, 0, base_currency_code, " +
                "base_currency_code, address, is_collect, settlement, pay_way, " +
                "'', '', '', '', '', '', '', " +
                "pay_term, remarks, payable_vendor, payable_po, project_no, work_order, '', " +
                "'N', 'N', 'N', '未提交', '" + userId + "', NOW(), " +
                "'', null, IFNULL(notes, ''), '', '', '', null, '', " +
                "project_id, project_name, uu_id, group_id, flow_id, is_deleted, " +
                "vendor_id, vendor_name, sign_person_id, sign_person_man, role_id, base_currency_id, base_currency_desc, " +
                "role_name, dept_id, dept_name, buyer_person_id, buyer_person_man, " +
                "base_currency_id, base_currency_desc " +
                "FROM pur_agreement_head WHERE uu_id = '" + agreementHeadUuId + "'";
        insertsql.add(headInsert);

        // 循环插入订单明细
        int detailSeq = 0;
        for (Map<String, Object> agreementDetail : agreementDetails) {
            String item_id = agreementDetail.get("item_id") != null ? agreementDetail.get("item_id").toString() : "";
            String itemCode = agreementDetail.get("item_code") != null ? agreementDetail.get("item_code").toString() : "";
            String applyOrg = agreementDetail.get("apply_org") != null ? agreementDetail.get("apply_org").toString() : "";
            String needDate = agreementDetail.get("ship_date") != null ? agreementDetail.get("ship_date").toString() : "";

            // 从页面传入的 PurAgreementHead 中获取 create_qty
            String detailUuIdForQty = agreementDetail.get("uu_id") != null ? agreementDetail.get("uu_id").toString() : "";
            double planQty = 0;
            for (PurAgreementHead agreement : params) {
                if (agreement.getUu_id() != null && agreement.getUu_id().equals(detailUuIdForQty)) {
                    String createQtyStr = agreement.getCreate_qty() != null ? agreement.getCreate_qty() : "0";
                    planQty = Double.parseDouble(createQtyStr);
                    break;
                }
            }

            // 从协议明细表获取单价和税率
            double guidePrice = agreementDetail.get("unit_price") != null ? Double.parseDouble(agreementDetail.get("unit_price").toString()) : 0;
            String tax_code = agreementDetail.get("tax_code") != null ? agreementDetail.get("tax_code").toString() : "";
            double tax_ratio = 0;

            // 查询税率信息
            String taxSql = "SELECT tax_ratio, tax_code, uu_id as tax_id, tax_name FROM epd_tax WHERE tax_code = '" + tax_code + "' LIMIT 1";
            Map<String, Object> taxInfo = sqlService.getOne(taxSql);
            String tax_id = "";
            String tax_name = "";
            if (taxInfo != null) {
                tax_ratio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
                tax_id = taxInfo.get("tax_id") != null ? taxInfo.get("tax_id").toString() : "";
                tax_name = taxInfo.get("tax_name") != null ? taxInfo.get("tax_name").toString() : "";
            }

            String purUnitId = agreementDetail.get("ord_unit_id") != null ? agreementDetail.get("ord_unit_id").toString() : "";
            String purUnitName = agreementDetail.get("ord_unit_name") != null ? agreementDetail.get("ord_unit_name").toString() : "";
            String mainUnitId = agreementDetail.get("main_unit_id") != null ? agreementDetail.get("main_unit_id").toString() : "";
            String mainUnitName = agreementDetail.get("main_unit_name") != null ? agreementDetail.get("main_unit_name").toString() : "";
            String changeRateStr = agreementDetail.get("change_rate") != null ? agreementDetail.get("change_rate").toString() : "1";
            double changeRate = Double.parseDouble(changeRateStr);
            String projectId = agreementDetail.get("project_id") != null ? agreementDetail.get("project_id").toString() : "";
            String projectNo = agreementDetail.get("project_no") != null ? agreementDetail.get("project_no").toString() : "";
            String projectName = agreementDetail.get("project_name") != null ? agreementDetail.get("project_name").toString() : "";
            String workOrder = agreementDetail.get("work_order") != null ? agreementDetail.get("work_order").toString() : "";
            String detailNotes = agreementDetail.get("notes") != null ? agreementDetail.get("notes").toString() : "";
            String detailUuId = agreementDetail.get("uu_id") != null ? agreementDetail.get("uu_id").toString() : "";
            String ukLineNo = agreementDetail.get("uk_line_no") != null ? agreementDetail.get("uk_line_no").toString() : "";

            // 查询物料主文件获取第二单位信息
            String itemSql = "SELECT second_unit, unit_factor_p FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
            Map<String, Object> itemInfo = sqlService.getOne(itemSql);
            String secondUnit = "";
            double unitFactorP = 1;
            if (itemInfo != null) {
                secondUnit = itemInfo.get("second_unit") != null ? itemInfo.get("second_unit").toString() : "";
                String unitFactorPStr = itemInfo.get("unit_factor_p") != null ? itemInfo.get("unit_factor_p").toString() : "1";
                unitFactorP = Double.parseDouble(unitFactorPStr);
            }

            // 根据币别小数位数计算精度因子
            double priceFactor = Math.pow(10, decimalDigitsPrice);
            double amtFactor = Math.pow(10, decimalDigitsAmt);

            // 计算第二单位数量
            double calcMainQty;
            if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
            } else {
                calcMainQty = planQty;
            }

            // 计算金额
            double amt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
            double taxAmount = Math.round(amt * (tax_ratio / 100) / (1 + tax_ratio / 100) * amtFactor) / amtFactor;
            double noTaxPrice = Math.round(guidePrice / (1 + tax_ratio / 100) * priceFactor) / priceFactor;
            double noTaxAmount = Math.round(noTaxPrice * planQty * amtFactor) / amtFactor;

            detailSeq++;
            String detailInsert = "INSERT INTO pur_order_detail(" +
                    "uk_order_no, uk_order_uuid, uk_line_no, apply_org, ship_date, item_code, item_name, " +
                    "item_spec, item_model, warehouse_code, pur_qty, pur_unit_id, main_qty, main_unit_id, " +
                    "change_rate, source_inv_org, source_price, up_bill, up_bill_line, exchange_rate, " +
                    "currency_code, tax_code, unit_price, amt, tax_amount, no_tax_price, no_tax_amount, " +
                    "base_currency_code, base_tax_price, base_tax_money, gift, manufacturer, prod_date, " +
                    "work_order, project_no, batch_no, guide_price, line_status, is_ship, quality_qty, " +
                    "ship_qty, return_qty, is_ship_ret, cost_qty, cost_money, store_qty, return_stock_qty, " +
                    "is_stock_return, is_cost, cost, create_user_id, create_time, modify_user_id, modify_time, " +
                    "notes, tax_id, tax_name, is_close, pur_unit_name, main_unit_name, base_currency_id, " +
                    "base_currency_desc, project_id, project_name, line_opinion, s_uu_id, group_id, flow_id, " +
                    "is_deleted, item_id, warehouse_id, warehouse_name, currency_id, currency_desc" +
                    ") VALUES (" +
                    "'" + orderNo + "', (SELECT uu_id FROM pur_order_head WHERE uk_order_no = '" + orderNo + "'), " +
                    "'" + detailSeq + "', '" + applyOrg + "', '" + needDate + "', '" + itemCode + "', " +
                    "(SELECT item_name FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                    "(SELECT item_spec FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                    "(SELECT item_model FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                    "'', " + planQty + ", '" + purUnitId + "', " + calcMainQty + ", '" + mainUnitId + "', " +
                    changeRate + ", '" + applyOrg + "', '协议', '" + agreementHeadUuId + "', '" + ukLineNo + "', " + exchangeRate + ", " +
                    "'" + currencyCode + "', '" + tax_code + "', " + guidePrice + ", " + amt + ", " + taxAmount + ", " + noTaxPrice + ", " + noTaxAmount + ", " +
                    "'" + currencyCode + "', " + guidePrice + ", " + amt + ", 'N', '', '', " +
                    "'" + workOrder + "', '" + projectNo + "', '', " + guidePrice + ", '未提交', 'N', 0, " +
                    "0, 0, 'N', 0, 0, 0, 0, " +
                    "'N', 'N', 0, '" + userId + "', NOW(), '', NULL, " +
                    "'" + detailNotes + "', '" + tax_id + "', '" + tax_name + "', 'N', '" + purUnitName + "', '" + mainUnitName + "', '" + currencyId + "', " +
                    "'" + currencyDesc + "', '" + projectId + "', '" + projectName + "', '', '" + detailUuId + "', '" + groupId + "', '', " +
                    "0, '', '', '', '" + currencyId + "', '" + currencyDesc + "'" +
                    ")";
            insertsql.add(detailInsert);

            // 累加已生成数量到 ord_num
            String updatePurNum = "UPDATE pur_agreement_detail SET ord_num = IFNULL(ord_num, 0) + " + planQty + " WHERE uu_id = '" + detailUuId + "'";
            insertsql.add(updatePurNum);

            // 检查该明细行是否已生成完毕（ord_num >= agreement_qty），如果是则标记 is_active = 'N'
            String updateDetailActive = "UPDATE pur_agreement_detail SET is_active = 'N' WHERE IFNULL(ord_num, 0) >= agreement_qty AND uu_id = '" + detailUuId + "'";
            insertsql.add(updateDetailActive);
        }

        // 检查是否所有明细行都已标记为 N，如果是则更新头表 is_active = 'N'
        String updateHeadActive = "UPDATE pur_agreement_head SET is_active = 'N' WHERE uu_id = '" + agreementHeadUuId + "' AND NOT EXISTS(SELECT 1 FROM pur_agreement_detail WHERE is_active = 'Y' AND uk_agreement_uuid = '" + agreementHeadUuId + "')";
        insertsql.add(updateHeadActive);

        // 汇总订单头金额
        String sumAmt = "UPDATE pur_order_head SET base_tax_money = (SELECT IFNULL(SUM(IFNULL(amt, 0)), 0) FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "') WHERE uk_order_no = '" + orderNo + "'";
        insertsql.add(sumAmt);

        // 只有所有校验通过（createflag为true）才提交执行
        if (createflag && !insertsql.isEmpty()) {
            boolean flag = sqlService.insertList(insertsql);
            if (flag) {
                return R.ok("采购订单生成成功");
            } else {
                return R.error("采购订单生成失败");
            }
        } else {
            return R.error("采购订单生成失败");
        }
    }
    @Override
    public R zjdetailCreate(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        boolean createflag = true;

        // 获取当前用户信息
        UserEntity user = ShiroUtils.getUserEntity();
        Long userId = user.getUserId();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bill_date = currentDate.format(formatter2);

        // 从页面传入的 PurAgreementHead 中获取订单号
        String orderNo = "";
        for (PurAgreementHead agreement : params) {
            if (agreement.getUk_order_no() != null && !agreement.getUk_order_no().isEmpty()) {
                orderNo = agreement.getUk_order_no();
                break;
            }
        }
        if (orderNo == null || orderNo.isEmpty()) {
            return R.error("订单号不能为空");
        }

        // 查询已有订单头信息
        String orderHeadSql = "SELECT * FROM pur_order_head WHERE uk_order_no = '" + orderNo + "'";
        Map<String, Object> orderHead = sqlService.getOne(orderHeadSql);
        if (orderHead == null) {
            return R.error("订单不存在");
        }
        String orderVendorCode = orderHead.get("vendor_code") != null ? orderHead.get("vendor_code").toString() : "";
        String currencyCode = orderHead.get("currency_code") != null ? orderHead.get("currency_code").toString() : "";
        String currencyId = orderHead.get("currency_id") != null ? orderHead.get("currency_id").toString() : "";
        String currencyDesc = orderHead.get("currency_desc") != null ? orderHead.get("currency_desc").toString() : "";
        String groupId = orderHead.get("group_id") != null ? orderHead.get("group_id").toString() : "";

        // 查询币别信息
        String currencySql = "SELECT exchange_rate, decimal_digits_price, decimal_digits_amt FROM epd_currency WHERE currency_code = '" + currencyCode + "' AND valid_flag = 'Y' LIMIT 1";
        Map<String, Object> currencyInfo = sqlService.getOne(currencySql);
        double exchangeRate = 1.0;
        int decimalDigitsPrice = 2;
        int decimalDigitsAmt = 2;
        if (currencyInfo != null) {
            String exchangeRateStr = currencyInfo.get("exchange_rate") != null ? currencyInfo.get("exchange_rate").toString() : "1";
            exchangeRate = Double.parseDouble(exchangeRateStr);
            String decimalDigitsPriceStr = currencyInfo.get("decimal_digits_price") != null ? currencyInfo.get("decimal_digits_price").toString() : "2";
            decimalDigitsPrice = Integer.parseInt(decimalDigitsPriceStr);
            String decimalDigitsAmtStr = currencyInfo.get("decimal_digits_amt") != null ? currencyInfo.get("decimal_digits_amt").toString() : "2";
            decimalDigitsAmt = Integer.parseInt(decimalDigitsAmtStr);
        }

        // 收集所有传入的协议明细uu_id
        List<String> detailUuIds = new ArrayList<>();
        for (PurAgreementHead agreement : params) {
            detailUuIds.add("'" + agreement.getUu_id() + "'");
        }

        // 查询选中的协议明细数据
        String detailIdsStr = String.join(",", detailUuIds);
        String agreementDetailSql = "SELECT * FROM pur_agreement_detail WHERE uu_id IN (" + detailIdsStr + ")";
        List<Map<String, Object>> agreementDetails = sqlService.getAll(agreementDetailSql);
        if (agreementDetails == null || agreementDetails.isEmpty()) {
            return R.error("未找到选中的协议明细");
        }

        // 获取当前订单最大行号
        String maxLineNoSql = "SELECT IFNULL(MAX(CAST(uk_line_no AS UNSIGNED)), 0) as max_line FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "'";
        Map<String, Object> maxLineResult = sqlService.getOne(maxLineNoSql);
        int detailSeq = maxLineResult != null && maxLineResult.get("max_line") != null ? Integer.parseInt(maxLineResult.get("max_line").toString()) : 0;

        // 校验并处理每条明细
        for (Map<String, Object> agreementDetail : agreementDetails) {
            String itemCode = agreementDetail.get("item_code") != null ? agreementDetail.get("item_code").toString() : "";
            String applyOrg = agreementDetail.get("apply_org") != null ? agreementDetail.get("apply_org").toString() : "";
            String detailUuId = agreementDetail.get("uu_id") != null ? agreementDetail.get("uu_id").toString() : "";
            String agreementHeadUuId = agreementDetail.get("uk_agreement_uuid") != null ? agreementDetail.get("uk_agreement_uuid").toString() : "";

            // 从页面传入的 PurAgreementHead 中获取 create_qty
            double planQty = 0;
            for (PurAgreementHead agreement : params) {
                if (agreement.getUu_id() != null && agreement.getUu_id().equals(detailUuId)) {
                    String createQtyStr = agreement.getCreate_qty() != null ? agreement.getCreate_qty() : "0";
                    planQty = Double.parseDouble(createQtyStr);
                    break;
                }
            }

            // 校验：create_qty 不能超过剩余可生成数量（agreement_qty - ord_num）
            String dbAgreementQtyStr = agreementDetail.get("agreement_qty") != null ? agreementDetail.get("agreement_qty").toString() : "0";
            double dbAgreementQty = Double.parseDouble(dbAgreementQtyStr);
            String dbPurNumStr = agreementDetail.get("ord_num") != null ? agreementDetail.get("ord_num").toString() : "0";
            double dbPurNum = Double.parseDouble(dbPurNumStr);
            double remainQty = dbAgreementQty - dbPurNum;
            if (planQty > remainQty) {
                errorMsg.append("物料：").append(itemCode).append(" 追加数量（").append(planQty)
                        .append("）超过剩余可生成数量（").append(remainQty).append("）\n");
                createflag = false;
                continue;
            }
            if (planQty <= 0) {
                errorMsg.append("物料：").append(itemCode).append("的追加数量必须大于0\n");
                createflag = false;
                continue;
            }

            // 校验物料主文件
            String itemSql = "SELECT item_code FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
            Map<String, Object> itemInfo = sqlService.getOne(itemSql);
            if (itemInfo == null) {
                errorMsg.append("物料编码：").append(itemCode).append(" 在物料主文件中不存在\n");
                createflag = false;
                continue;
            }

            // 从协议明细表获取单价和税率
            double guidePrice = agreementDetail.get("unit_price") != null ? Double.parseDouble(agreementDetail.get("unit_price").toString()) : 0;
            String tax_code = agreementDetail.get("tax_code") != null ? agreementDetail.get("tax_code").toString() : "";
            double tax_ratio = 0;

            // 查询税率信息
            String taxSql = "SELECT tax_ratio, tax_code, uu_id as tax_id, tax_name FROM epd_tax WHERE tax_code = '" + tax_code + "' LIMIT 1";
            Map<String, Object> taxInfo = sqlService.getOne(taxSql);
            String tax_id = "";
            String tax_name = "";
            if (taxInfo != null) {
                tax_ratio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
                tax_id = taxInfo.get("tax_id") != null ? taxInfo.get("tax_id").toString() : "";
                tax_name = taxInfo.get("tax_name") != null ? taxInfo.get("tax_name").toString() : "";
            }

            String purUnitId = agreementDetail.get("ord_unit_id") != null ? agreementDetail.get("ord_unit_id").toString() : "";
            String purUnitName = agreementDetail.get("ord_unit_name") != null ? agreementDetail.get("ord_unit_name").toString() : "";
            String mainUnitId = agreementDetail.get("main_unit_id") != null ? agreementDetail.get("main_unit_id").toString() : "";
            String mainUnitName = agreementDetail.get("main_unit_name") != null ? agreementDetail.get("main_unit_name").toString() : "";
            String changeRateStr = agreementDetail.get("change_rate") != null ? agreementDetail.get("change_rate").toString() : "1";
            double changeRate = Double.parseDouble(changeRateStr);
            String projectId = agreementDetail.get("project_id") != null ? agreementDetail.get("project_id").toString() : "";
            String projectNo = agreementDetail.get("project_no") != null ? agreementDetail.get("project_no").toString() : "";
            String projectName = agreementDetail.get("project_name") != null ? agreementDetail.get("project_name").toString() : "";
            String workOrder = agreementDetail.get("work_order") != null ? agreementDetail.get("work_order").toString() : "";
            String detailNotes = agreementDetail.get("notes") != null ? agreementDetail.get("notes").toString() : "";
            String needDate = agreementDetail.get("ship_date") != null ? agreementDetail.get("ship_date").toString() : "";
            String ukLineNo = agreementDetail.get("uk_line_no") != null ? agreementDetail.get("uk_line_no").toString() : "";

            // 查询物料主文件获取第二单位信息
            String itemSql2 = "SELECT second_unit, unit_factor_p FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
            Map<String, Object> itemInfo2 = sqlService.getOne(itemSql2);
            String secondUnit = "";
            double unitFactorP = 1;
            if (itemInfo2 != null) {
                secondUnit = itemInfo2.get("second_unit") != null ? itemInfo2.get("second_unit").toString() : "";
                String unitFactorPStr = itemInfo2.get("unit_factor_p") != null ? itemInfo2.get("unit_factor_p").toString() : "1";
                unitFactorP = Double.parseDouble(unitFactorPStr);
            }

            // 根据币别小数位数计算精度因子
            double priceFactor = Math.pow(10, decimalDigitsPrice);
            double amtFactor = Math.pow(10, decimalDigitsAmt);

            // 计算第二单位数量
            double calcMainQty;
            if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
            } else {
                calcMainQty = planQty;
            }

            // 计算金额
            double amt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
            double taxAmount = Math.round(amt * (tax_ratio / 100) / (1 + tax_ratio / 100) * amtFactor) / amtFactor;
            double noTaxPrice = Math.round(guidePrice / (1 + tax_ratio / 100) * priceFactor) / priceFactor;
            double noTaxAmount = Math.round(noTaxPrice * planQty * amtFactor) / amtFactor;

            // 检查该协议明细是否已在订单中存在（通过 s_uu_id 关联）
            String existDetailSql = "SELECT uu_id, pur_qty, amt FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "' AND s_uu_id = '" + detailUuId + "' LIMIT 1";
            Map<String, Object> existDetail = sqlService.getOne(existDetailSql);

            if (existDetail != null) {
                // 已存在，累加数量和金额
                String existUuId = existDetail.get("uu_id") != null ? existDetail.get("uu_id").toString() : "";
                double oldPurQty = existDetail.get("pur_qty") != null ? Double.parseDouble(existDetail.get("pur_qty").toString()) : 0;
                double oldAmt = existDetail.get("amt") != null ? Double.parseDouble(existDetail.get("amt").toString()) : 0;
                double newPurQty = oldPurQty + planQty;
                double newAmt = oldAmt + amt;

                // 重新计算第二单位数量
                double newCalcMainQty;
                if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                    newCalcMainQty = Math.round(newPurQty / unitFactorP * amtFactor) / amtFactor;
                } else {
                    newCalcMainQty = newPurQty;
                }

                double newTaxAmount = Math.round(newAmt * (tax_ratio / 100) / (1 + tax_ratio / 100) * amtFactor) / amtFactor;
                double newNoTaxAmount = Math.round(noTaxPrice * newPurQty * amtFactor) / amtFactor;

                String updateDetail = "UPDATE pur_order_detail SET " +
                        "pur_qty = " + newPurQty + ", " +
                        "main_qty = " + newCalcMainQty + ", " +
                        "amt = " + newAmt + ", " +
                        "tax_amount = " + newTaxAmount + ", " +
                        "no_tax_amount = " + newNoTaxAmount + ", " +
                        "base_tax_money = " + newAmt + " " +
                        "WHERE uu_id = '" + existUuId + "'";
                insertsql.add(updateDetail);
            } else {
                // 不存在，插入新行
                detailSeq++;
                String detailInsert = "INSERT INTO pur_order_detail(" +
                        "uk_order_no, uk_order_uuid, uk_line_no, apply_org, ship_date, item_code, item_name, " +
                        "item_spec, item_model, warehouse_code, pur_qty, pur_unit_id, main_qty, main_unit_id, " +
                        "change_rate, source_inv_org, source_price, up_bill, up_bill_line, exchange_rate, " +
                        "currency_code, tax_code, unit_price, amt, tax_amount, no_tax_price, no_tax_amount, " +
                        "base_currency_code, base_tax_price, base_tax_money, gift, manufacturer, prod_date, " +
                        "work_order, project_no, batch_no, guide_price, line_status, is_ship, quality_qty, " +
                        "ship_qty, return_qty, is_ship_ret, cost_qty, cost_money, store_qty, return_stock_qty, " +
                        "is_stock_return, is_cost, cost, create_user_id, create_time, modify_user_id, modify_time, " +
                        "notes, tax_id, tax_name, is_close, pur_unit_name, main_unit_name, base_currency_id, " +
                        "base_currency_desc, project_id, project_name, line_opinion, s_uu_id, group_id, flow_id, " +
                        "is_deleted, item_id, warehouse_id, warehouse_name, currency_id, currency_desc" +
                        ") VALUES (" +
                        "'" + orderNo + "', (SELECT uu_id FROM pur_order_head WHERE uk_order_no = '" + orderNo + "'), " +
                        "'" + detailSeq + "', '" + applyOrg + "', '" + needDate + "', '" + itemCode + "', " +
                        "(SELECT item_name FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_spec FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_model FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "'', " + planQty + ", '" + purUnitId + "', " + calcMainQty + ", '" + mainUnitId + "', " +
                        changeRate + ", '" + applyOrg + "', '协议', '" + agreementHeadUuId + "', '" + ukLineNo + "', " + exchangeRate + ", " +
                        "'" + currencyCode + "', '" + tax_code + "', " + guidePrice + ", " + amt + ", " + taxAmount + ", " + noTaxPrice + ", " + noTaxAmount + ", " +
                        "'" + currencyCode + "', " + guidePrice + ", " + amt + ", 'N', '', '', " +
                        "'" + workOrder + "', '" + projectNo + "', '', " + guidePrice + ", '未提交', 'N', 0, " +
                        "0, 0, 'N', 0, 0, 0, 0, " +
                        "'N', 'N', 0, '" + userId + "', NOW(), '', NULL, " +
                        "'" + detailNotes + "', '" + tax_id + "', '" + tax_name + "', 'N', '" + purUnitName + "', '" + mainUnitName + "', '" + currencyId + "', " +
                        "'" + currencyDesc + "', '" + projectId + "', '" + projectName + "', '', '" + detailUuId + "', '" + groupId + "', '', " +
                        "0, '', '', '', '" + currencyId + "', '" + currencyDesc + "'" +
                        ")";
                insertsql.add(detailInsert);
            }

            // 累加已生成数量到 ord_num
            String updatePurNum = "UPDATE pur_agreement_detail SET ord_num = IFNULL(ord_num, 0) + " + planQty + " WHERE uu_id = '" + detailUuId + "'";
            insertsql.add(updatePurNum);

            // 检查该明细行是否已生成完毕（ord_num >= agreement_qty），如果是则标记 is_active = 'N'
            String updateDetailActive = "UPDATE pur_agreement_detail SET is_active = 'N' WHERE IFNULL(ord_num, 0) >= agreement_qty AND uu_id = '" + detailUuId + "'";
            insertsql.add(updateDetailActive);
        }

        // 检查是否所有明细行都已标记为 N，如果是则更新头表 is_active = 'N'
        // 通过协议明细反查协议头
        String agrHeadUuIdSql = "SELECT uk_agreement_uuid FROM pur_agreement_detail WHERE uu_id = " + detailUuIds.get(0);
        Map<String, Object> agrHeadResult = sqlService.getOne(agrHeadUuIdSql);
        if (agrHeadResult != null && agrHeadResult.get("uk_agreement_uuid") != null) {
            String agrHeadUuId = agrHeadResult.get("uk_agreement_uuid").toString();
            String updateHeadActive = "UPDATE pur_agreement_head SET is_active = 'N' WHERE uu_id = '" + agrHeadUuId + "' AND NOT EXISTS(SELECT 1 FROM pur_agreement_detail WHERE is_active = 'Y' AND uk_agreement_uuid = '" + agrHeadUuId + "')";
            insertsql.add(updateHeadActive);
        }

        // 汇总订单头金额
        String sumAmt = "UPDATE pur_order_head SET base_tax_money = (SELECT IFNULL(SUM(IFNULL(amt, 0)), 0) FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "') WHERE uk_order_no = '" + orderNo + "'";
        insertsql.add(sumAmt);

        // 只有所有校验通过（createflag为true）才提交执行
        if (createflag && !insertsql.isEmpty()) {
            boolean flag = sqlService.insertList(insertsql);
            if (flag) {
                return R.ok("采购订单追加成功");
            } else {
                return R.error("采购订单追加失败");
            }
        } else {
            return R.error("采购订单追加失败：" + errorMsg.toString());
        }
    }

    @Override
    public R unaddObj(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("update pur_agreement_head set bill_status = '未提交' where uu_id = '" + PurAgreementHead.getUu_id() + "'");
            insertsql.add("update pur_agreement_detail set line_status = '未提交' where uk_agreement_uuid = '" + PurAgreementHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消提交成功");
        }else{
            return R.error("取消提交失败");
        }
    }

    @Override
    public R detaillist(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("delete from pur_agreement_detail  where uu_id = '" + PurAgreementHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("删除成功");
        }else{
            return R.error("删除失败");
        }
    }

    @Override
    public R enable(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("update pur_agreement_head set is_active = 'Y' where uu_id = '" + PurAgreementHead.getUu_id() + "'");
            insertsql.add("update pur_agreement_detail set is_active = 'Y' where uk_agreement_uuid = '" + PurAgreementHead.getUu_id() + "' and agreement_qty>ord_num");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R disabl(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("update pur_agreement_head set is_active = 'N' where uu_id = '" + PurAgreementHead.getUu_id() + "'");
            insertsql.add("update pur_agreement_detail set is_active = 'N' where uk_agreement_uuid = '" + PurAgreementHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }

    @Override
    public R detailenable(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("update pur_agreement_detail set is_active = 'Y' where uu_id = '" + PurAgreementHead.getUu_id() + "'");
            insertsql.add("update pur_agreement_head set is_active = 'Y' where uu_id = '" + PurAgreementHead.getUk_agreement_uuid() + "' and  exists( select 1 from pur_agreement_detail where is_active = 'Y' and uk_agreement_uuid = '" + PurAgreementHead.getUk_agreement_uuid() + "' and pur_agreement_detail.uk_agreement_uuid=pur_agreement_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R detaildisabl(List<PurAgreementHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurAgreementHead PurAgreementHead : params) {
            insertsql.add("update pur_agreement_detail set is_active = 'N' where uu_id = '" + PurAgreementHead.getUu_id() + "'");
            insertsql.add("update pur_agreement_head set is_active = 'N' where uu_id = '" + PurAgreementHead.getUk_agreement_uuid() + "' and not exists( select 1 from pur_agreement_detail where is_active = 'Y' and uk_agreement_uuid = '" + PurAgreementHead.getUk_agreement_uuid() + "' and pur_agreement_detail.uk_agreement_uuid=pur_agreement_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }
}
