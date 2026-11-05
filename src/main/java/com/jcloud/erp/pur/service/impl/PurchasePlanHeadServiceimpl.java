package com.jcloud.erp.pur.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.pur.entity.PurApplyHead;
import com.jcloud.erp.pur.entity.PurPlanHead;
import com.jcloud.erp.pur.mapper.PurchasePlanHeadMapper;
import com.jcloud.erp.pur.service.PurchasePlanHeadService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class PurchasePlanHeadServiceimpl extends ServiceImpl<PurchasePlanHeadMapper, PurPlanHead> implements PurchasePlanHeadService {
    @Resource
    private SqlService sqlService;

    @Override
    public R addObj(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead PurPlanHead : params) {
            insertsql.add("update pur_purchase_plan_head set bill_status = '已提交' where uu_id = '" + PurPlanHead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_detail set line_status = '已提交' where uk_plan_uuid = '" + PurPlanHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("提交成功");
        } else {
            return R.error("提交失败");
        }
    }

    @Override
    public R unaddObj(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead PurPlanHead : params) {
            insertsql.add("update pur_purchase_plan_head set bill_status = '未提交' where uu_id = '" + PurPlanHead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_detail set line_status = '未提交' where uk_plan_uuid = '" + PurPlanHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("取消提交成功");
        } else {
            return R.error("取消提交失败");
        }
    }


    @Override
    public R auditlist(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurPlanHead PurPlanHead : params) {
            String approver_opinion = PurPlanHead.getApprover_opinion();
            if (approver_opinion == null || "null".equals(approver_opinion)) {
                PurPlanHead.setApprover_opinion("");
            }
            insertsql.add("update pur_purchase_plan_head set bill_status = '审核通过',approver_opinion='" + PurPlanHead.getApprover_opinion() + "',approver_user_man='" + user.getName() + "',approver_user_id='" + user.getUserId() + "',approver_user_code='" + user.getUsername() + "',approver_time=NOW() where uu_id = '" + PurPlanHead.getUu_id() + "' and (bill_status='已提交' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_purchase_plan_head.pur_org and plan_check='Y' )) ");
            insertsql.add("update pur_purchase_plan_detail set line_status = '审核通过' where uk_plan_uuid = '" + PurPlanHead.getUu_id() + "' ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("审核成功");
        } else {
            return R.error("审核失败");
        }
    }

    @Override
    public R passlist(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurPlanHead PurPlanHead : params) {
            String approver_opinion = PurPlanHead.getApprover_opinion();
            if (approver_opinion == null || "null".equals(approver_opinion)) {
                PurPlanHead.setApprover_opinion("");
            }
            insertsql.add("update pur_purchase_plan_head set bill_status = '已提交' where uu_id = '" + PurPlanHead.getUu_id() + "'  and (bill_status='审核通过' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_purchase_plan_head.pur_org and plan_check='Y' )) ");
            insertsql.add("update pur_purchase_plan_detail set line_status = '已提交' where uk_plan_uuid = '" + PurPlanHead.getUu_id() + "' ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("取消审核成功");
        } else {
            return R.error("取消审核失败");
        }
    }


    @Override
    public R bhpasslist(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurPlanHead PurPlanHead : params) {
            String approver_opinion = PurPlanHead.getApprover_opinion();
            if (approver_opinion == null || "null".equals(approver_opinion)) {
                PurPlanHead.setApprover_opinion("");
            }
            insertsql.add("update pur_purchase_plan_head set bill_status = '驳回',approver_opinion='" + PurPlanHead.getApprover_opinion() + "',approver_user_man='" + user.getName() + "',approver_user_id='" + user.getUserId() + "',approver_user_code='" + user.getUsername() + "',approver_time=NOW() where uu_id = '" + PurPlanHead.getUu_id() + "' and bill_status='审核通过'  ");
            insertsql.add("update pur_purchase_plan_detail set line_status = '驳回' where uk_plan_uuid = '" + PurPlanHead.getUu_id() + "' ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("驳回成功");
        } else {
            return R.error("驳回失败");
        }
    }

    @Override
    public R auditdetaillist(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurPlanHead PurPlanHead : params) {
            String line_opinion = PurPlanHead.getLine_opinion();
            if (line_opinion == null || "null".equals(line_opinion)) {
                PurPlanHead.setLine_opinion("");
            }
            insertsql.add("update pur_purchase_plan_detail set line_status = '审核通过',line_opinion='" + PurPlanHead.getLine_opinion() + "' where uu_id = '" + PurPlanHead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_head set bill_status = '审核通过',approver_user_man='" + user.getName() + "',approver_user_id='" + user.getUserId() + "',approver_user_code='" + user.getUsername() + "',approver_time=NOW() where not exists (select 1 from pur_purchase_plan_detail where uk_plan_uuid = '" + PurPlanHead.getUk_plan_uuid() + "' and line_status<>'审核通过' and pur_purchase_plan_detail.uk_plan_uuid=pur_purchase_plan_head.uu_id)");

        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("审核成功");
        } else {
            return R.error("审核失败");
        }
    }

    @Override
    public R passdetaillist(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurPlanHead PurPlanHead : params) {
            String line_opinion = PurPlanHead.getLine_opinion();
            if (line_opinion == null || "null".equals(line_opinion)) {
                PurPlanHead.setLine_opinion("");
            }
            insertsql.add("update pur_purchase_plan_detail set line_status = '驳回',line_opinion='" + PurPlanHead.getLine_opinion() + "' where uu_id = '" + PurPlanHead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_head set bill_status = '驳回',approver_user_man='" + user.getName() + "',approver_user_id='" + user.getUserId() + "',approver_user_code='" + user.getUsername() + "',approver_time=NOW() where uu_id='" + PurPlanHead.getUk_plan_uuid() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("驳回成功");
        } else {
            return R.error("驳回失败");
        }
    }

    @Override
    public R detaillist(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead PurPlanHead : params) {
            String sql = "select s_uu_id from pur_purchase_plan_detail where uu_id = '" + PurPlanHead.getUu_id() + "'";
            Map<String, Object> theuu_id = sqlService.getOne(sql);
            if (theuu_id != null && theuu_id.get("s_uu_id") != null && !"".equals(theuu_id.get("s_uu_id"))) {
                insertsql.add("update pur_apply_detail set is_geneplan='Y',sum_plan=(select ifnull(sum(ifnull(plan_qty,0)),0) from pur_purchase_plan_detail where s_uu_id='" + theuu_id.get("s_uu_id") + "' and uu_id<>'" + PurPlanHead.getUu_id() + "')  where  uu_id='" + theuu_id.get("s_uu_id") + "'");
                insertsql.add("update pur_apply_detail set is_geneplan='N'  where uu_id='" + theuu_id.get("s_uu_id") + "' and ifnull(sum_plan,0)<=0");
            }
            insertsql.add("delete from pur_purchase_plan_detail  where uu_id = '" + PurPlanHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }


    @Override
    public R detailCreate(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();

        // 获取当前用户信息
        UserEntity user = ShiroUtils.getUserEntity();
        Long userId = user.getUserId();
        boolean createflag=true;
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bill_date = currentDate.format(formatter);

        // 生成订单号（年月日+4位流水）
        String orderNoSql = "SELECT CASE WHEN MAX(uk_order_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0001') " +
                "ELSE LPAD(CAST(MAX(uk_order_no) AS UNSIGNED) + 1, 12, '0') END AS order_no " +
                "FROM pur_order_head WHERE LEFT(uk_order_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> orderNoResult = sqlService.getOne(orderNoSql);
        String orderNo = orderNoResult.get("order_no").toString();
        String prefix = orderNo.substring(0, orderNo.length() - 4);
        String lastFourStr = orderNo.substring(orderNo.length() - 4);
        int seq = Integer.parseInt(lastFourStr);

        // 查询RMB币别信息
        String currencySql = "SELECT uu_id, currency_code, currency_desc, exchange_rate, decimal_digits_price, decimal_digits_amt " +
                "FROM epd_currency WHERE currency_code = 'RMB' AND valid_flag = 'Y' LIMIT 1";
        Map<String, Object> currencyInfo = sqlService.getOne(currencySql);
        String currencyId = "";
        String currencyCode = "RMB";
        String currencyDesc = "人民币";
        double exchangeRate = 1.0;
        int decimalDigitsPrice = 2;
        int decimalDigitsAmt = 2;
        if (currencyInfo != null) {
            currencyId = currencyInfo.get("uu_id") != null ? currencyInfo.get("uu_id").toString() : "";
            currencyCode = currencyInfo.get("currency_code") != null ? currencyInfo.get("currency_code").toString() : "RMB";
            currencyDesc = currencyInfo.get("currency_desc") != null ? currencyInfo.get("currency_desc").toString() : "人民币";
            String exchangeRateStr = currencyInfo.get("exchange_rate") != null ? currencyInfo.get("exchange_rate").toString() : "1";
            exchangeRate = Double.parseDouble(exchangeRateStr);
            String decimalDigitsPriceStr = currencyInfo.get("decimal_digits_price") != null ? currencyInfo.get("decimal_digits_price").toString() : "2";
            decimalDigitsPrice = Integer.parseInt(decimalDigitsPriceStr);
            String decimalDigitsAmtStr = currencyInfo.get("decimal_digits_amt") != null ? currencyInfo.get("decimal_digits_amt").toString() : "2";
            decimalDigitsAmt = Integer.parseInt(decimalDigitsAmtStr);
        }

        // 收集所有传入的明细uu_id
        List<String> detailUuIds = new ArrayList<>();
        for (PurPlanHead plan : params) {
            detailUuIds.add(plan.getUu_id());
        }

        if (detailUuIds.isEmpty()) {
            return R.error("未选择任何采购计划明细");
        }

        // 1. 通过第一条明细反查采购计划头uu_id
        String firstDetailSql = "SELECT uk_plan_uuid FROM pur_purchase_plan_detail WHERE uu_id = '" + detailUuIds.get(0) + "'";
        Map<String, Object> firstDetailResult = sqlService.getOne(firstDetailSql);
        if (firstDetailResult == null) {
            return R.error("采购计划明细不存在");
        }
        String planHeadUuId = firstDetailResult.get("uk_plan_uuid") != null ? firstDetailResult.get("uk_plan_uuid").toString() : "";
        if (planHeadUuId.isEmpty()) {
            return R.error("采购计划明细关联的采购计划头不存在");
        }

        // 2. 查询采购计划头信息
        String planHeadSql = "SELECT * FROM pur_purchase_plan_head WHERE uu_id = '" + planHeadUuId + "'";
        Map<String, Object> planHead = sqlService.getOne(planHeadSql);
        if (planHead == null) {
            return R.error("采购计划头不存在");
        }

        String purOrg = planHead.get("pur_org") != null ? planHead.get("pur_org").toString() : "";
        String groupId = planHead.get("group_id") != null ? planHead.get("group_id").toString() : "";
        String deptId = planHead.get("dept_id") != null ? planHead.get("dept_id").toString() : "";
        String deptCode = planHead.get("dept_code") != null ? planHead.get("dept_code").toString() : "";
        String deptName = planHead.get("dept_name") != null ? planHead.get("dept_name").toString() : "";
        String ukPlanId = planHead.get("uk_plan_id") != null ? planHead.get("uk_plan_id").toString() : "";
        String notes = planHead.get("notes") != null ? planHead.get("notes").toString() : "";

        // 3. 查询选中的明细数据
        String detailIdsStr = String.join("','", detailUuIds);
        String planDetailSql = "SELECT * FROM pur_purchase_plan_detail WHERE uu_id IN ('" + detailIdsStr + "')";
        List<Map<String, Object>> planDetails = sqlService.getAll(planDetailSql);
        if (planDetails == null || planDetails.isEmpty()) {
            return R.error("未找到选中的采购计划明细");
        }

        // 4. 校验所有选中的明细
        for (Map<String, Object> planDetail : planDetails) {
            String itemCode = planDetail.get("item_code") != null ? planDetail.get("item_code").toString() : "";
            String applyOrg = planDetail.get("apply_org") != null ? planDetail.get("apply_org").toString() : "";
            String vendorCode = planDetail.get("vendor_code") != null ? planDetail.get("vendor_code").toString() : "";
            String guide_price = planDetail.get("guide_price") != null ? planDetail.get("guide_price").toString() : "0";
            // 校验物料主文件
            String itemSql = "SELECT item_code FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
            Map<String, Object> itemInfo = sqlService.getOne(itemSql);
            if (itemInfo == null) {
                errorMsg.append("物料编码：").append(itemCode).append(" 在物料主文件中不存在\n");
                continue;
            }

            // 校验供应商
            if (vendorCode == null || vendorCode.isEmpty()) {
                errorMsg.append("物料：").append(itemCode).append(" 的供应商未定义，不能生成订单\n");
                continue;
            }
        }

        // 如果有错误，返回错误信息
        if (errorMsg.length() > 0) {
            return R.error("采购订单生成失败，存在以下问题：\n" + errorMsg.toString());
        }

        // 5. 按供应商分组
        Map<String, List<Map<String, Object>>> vendorGroupMap = new LinkedHashMap<>();
        for (Map<String, Object> planDetail : planDetails) {
            String vendorCode = planDetail.get("vendor_code") != null ? planDetail.get("vendor_code").toString() : "";
            if (!vendorGroupMap.containsKey(vendorCode)) {
                vendorGroupMap.put(vendorCode, new ArrayList<>());
            }
            vendorGroupMap.get(vendorCode).add(planDetail);
        }

        // 6. 按供应商分组生成订单
        for (Map.Entry<String, List<Map<String, Object>>> entry : vendorGroupMap.entrySet()) {
            List<Map<String, Object>> vendorDetails = entry.getValue();
            if (vendorDetails.isEmpty()) {
                continue;
            }

            // 取该供应商组的第一条明细获取供应商信息
            Map<String, Object> firstDetail = vendorDetails.get(0);
            String vendorCode = firstDetail.get("vendor_code") != null ? firstDetail.get("vendor_code").toString() : "";
            String vendorId = firstDetail.get("vendor_id") != null ? firstDetail.get("vendor_id").toString() : "";
            String vendorName = firstDetail.get("vendor_name") != null ? firstDetail.get("vendor_name").toString() : "";
            String buyerCode = firstDetail.get("buyer_code") != null ? firstDetail.get("buyer_code").toString() : "";
            String buyerId = firstDetail.get("buyer_id") != null ? firstDetail.get("buyer_id").toString() : "";
            String buyerMan = firstDetail.get("buyer_man") != null ? firstDetail.get("buyer_man").toString() : "";
            String roleId = firstDetail.get("role_id") != null ? firstDetail.get("role_id").toString() : "";
            String roleCode = firstDetail.get("role_code") != null ? firstDetail.get("role_code").toString() : "";
            String roleName = firstDetail.get("role_name") != null ? firstDetail.get("role_name").toString() : "";

            // 生成订单号
            String currentOrderNo = prefix + String.format("%04d", seq);
            seq++;

            // 插入订单头
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
                    ") VALUES (" +
                    "'" + currentOrderNo + "', '" + purOrg + "', 'CG', '" + bill_date + "', '02', '采购计划生成', " +
                    "'" + ukPlanId + "', '" + vendorCode + "', '" + bill_date + "', '', '', '', " +
                    "'" + roleCode + "', '" + deptCode + "', '" + buyerCode + "', 0, '" + currencyCode + "', " +
                    "'" + currencyCode + "', '', 'N', '', '', " +
                    "'', '', '', '', '', '', '', " +
                    "'', '" + notes + "', '', '', '', '', '', " +
                    "'N', 'N', 'N', '未提交', '" + userId + "', NOW(), " +
                    "'', NULL, '" + notes + "', '', '', '', NULL, '', " +
                    "'', '', '"+planHeadUuId+"', '" + groupId + "', '', 0, " +
                    "'" + vendorId + "', '" + vendorName + "', '', '', '" + roleId + "', '" + currencyId + "', '" + currencyDesc + "', " +
                    "'" + roleName + "', '" + deptId + "', '" + deptName + "', '" + buyerId + "', '" + buyerMan + "', " +
                    "'" + currencyId + "', '" + currencyDesc + "'" +
                    ")";
            insertsql.add(headInsert);

            // 循环插入该供应商下的订单明细
            int detailSeq = 0;
            for (Map<String, Object> planDetail : vendorDetails) {
                String item_id = planDetail.get("item_id") != null ? planDetail.get("item_id").toString() : "";
                String itemCode = planDetail.get("item_code") != null ? planDetail.get("item_code").toString() : "";
                String applyOrg = planDetail.get("apply_org") != null ? planDetail.get("apply_org").toString() : "";
                String needDate = planDetail.get("need_date") != null ? planDetail.get("need_date").toString() : "";
                String guide_price = planDetail.get("guide_price") != null ? planDetail.get("guide_price").toString() : "0";

                // 从页面传入的 PurPlanHead 中获取 create_qty，而不是从数据库取 plan_qty
                String detailUuIdForQty = planDetail.get("uu_id") != null ? planDetail.get("uu_id").toString() : "";
                double planQty = 0;
                for (PurPlanHead plan : params) {
                    if (plan.getUu_id() != null && plan.getUu_id().equals(detailUuIdForQty)) {
                        String createQtyStr = plan.getCreate_qty() != null ? plan.getCreate_qty() : "0";
                        planQty = Double.parseDouble(createQtyStr);
                        break;
                    }
                }

                // 校验：create_qty 不能超过剩余可生成数量（plan_qty - pur_num）
                String dbPlanQtyStr = planDetail.get("plan_qty") != null ? planDetail.get("plan_qty").toString() : "0";
                double dbPlanQty = Double.parseDouble(dbPlanQtyStr);
                String dbPurNumStr = planDetail.get("pur_num") != null ? planDetail.get("pur_num").toString() : "0";
                double dbPurNum = Double.parseDouble(dbPurNumStr);
                double remainQty = dbPlanQty - dbPurNum;
                if (planQty > remainQty) {
                    errorMsg.append("物料：").append(itemCode).append(" 生成订单数量（").append(planQty)
                            .append("）超过剩余可生成数量（").append(remainQty).append("）\n");
                    createflag = false;
                    continue;
                }
                if (planQty <= 0) {
                    errorMsg.append("物料：").append(itemCode).append("的生成订单数量必须大于0\n");
                    createflag = false;
                    continue;
                }

                // 从采购协议明细表查询单价（通过采购组织、供应商、物料编码关联）
                String agreementSql = "SELECT pur_agreement_head.vendor_code,pur_agreement_detail.unit_price,tax_ratio,IFNULL(pur_agreement_detail.tax_code,epd_tax.tax_code) as tax_code,epd_tax.uu_id as tax_id,epd_tax.tax_name FROM pur_agreement_detail,pur_agreement_head,epd_tax  WHERE  pur_agreement_detail.tax_code=epd_tax.tax_code and  pur_agreement_detail.uk_agreement_uuid=pur_agreement_head.uu_id and pur_agreement_head.pur_org = '" + purOrg + "' AND pur_agreement_head.vendor_code = '" + vendorCode + "' AND pur_agreement_detail.item_id = '" + item_id + "' LIMIT 1";
                Map<String, Object> agreementInfo = sqlService.getOne(agreementSql);
                double guidePrice = Double.parseDouble(guide_price);
                if (agreementInfo != null ) {
                    guidePrice = Double.parseDouble(agreementInfo.get("unit_price").toString());
                }
                double tax_ratio =0;
                if (agreementInfo != null ) {
                    tax_ratio = Double.parseDouble(agreementInfo.get("tax_ratio").toString());
                }
                String tax_code ="";
                String tax_id ="";
                String tax_name ="";
                if (agreementInfo != null) {
                    tax_code = agreementInfo.get("tax_code").toString();
                    tax_id = agreementInfo.get("tax_id").toString();
                    tax_name = agreementInfo.get("tax_name").toString();
                }
                if("".equals(tax_code)||tax_ratio==0){
                    String agreement = "SELECT tax_ratio,epd_item_master.tax_code,epd_tax.uu_id as tax_id,epd_tax.tax_name FROM epd_item_master,epd_tax  WHERE epd_item_master.tax_code=epd_tax.tax_code  AND epd_item_master.uu_id = '" + item_id + "' LIMIT 1";
                    Map<String, Object> agreementb = sqlService.getOne(agreement);
                    if (agreementb != null ) {
                        tax_code = agreementb.get("tax_code").toString();
                        tax_id = agreementb.get("tax_id").toString();
                        tax_name = agreementb.get("tax_name").toString();
                        tax_ratio = Double.parseDouble(agreementb.get("tax_ratio").toString());

                    }
                }

                String purUnitId = planDetail.get("pur_unit_id") != null ? planDetail.get("pur_unit_id").toString() : "";
                String purUnitName = planDetail.get("pur_unit_name") != null ? planDetail.get("pur_unit_name").toString() : "";
                String mainUnitId = planDetail.get("main_unit_id") != null ? planDetail.get("main_unit_id").toString() : "";
                String mainUnitName = planDetail.get("main_unit_name") != null ? planDetail.get("main_unit_name").toString() : "";
                String changeRateStr = planDetail.get("change_rate") != null ? planDetail.get("change_rate").toString() : "1";
                double changeRate = Double.parseDouble(changeRateStr);
                String projectId = planDetail.get("project_id") != null ? planDetail.get("project_id").toString() : "";
                String projectNo = planDetail.get("project_no") != null ? planDetail.get("project_no").toString() : "";
                String projectName = planDetail.get("project_name") != null ? planDetail.get("project_name").toString() : "";
                String workOrder = planDetail.get("work_order") != null ? planDetail.get("work_order").toString() : "";
                String detailNotes = planDetail.get("notes") != null ? planDetail.get("notes").toString() : "";
                String detailUuId = planDetail.get("uu_id") != null ? planDetail.get("uu_id").toString() : "";
                String ukLineNo = planDetail.get("uk_line_no") != null ? planDetail.get("uk_line_no").toString() : "";

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

                // 计算第二单位数量（保留金额小数位）
                double calcMainQty;
                if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                    calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
                } else {
                    calcMainQty = planQty;
                }

                // 计算金额（单价保留price小数位，金额保留amt小数位）
                double amt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
                double taxAmount = Math.round(amt * (tax_ratio/100) / (1 + tax_ratio/100) * amtFactor) / amtFactor;
                double noTaxPrice = Math.round(guidePrice / (1 + tax_ratio/100) * priceFactor) / priceFactor;
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
                        "'" + currentOrderNo + "', (SELECT uu_id FROM pur_order_head WHERE uk_order_no = '" + currentOrderNo + "'), " +
                        "'" + detailSeq + "', '" + applyOrg + "', '" + needDate + "', '" + itemCode + "', " +
                        "(SELECT item_name FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_spec FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_model FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "'', " + planQty + ", '" + purUnitId + "', " + calcMainQty + ", '" + mainUnitId + "', " +
                        changeRate + ", '" + applyOrg + "', '采购计划', '" + ukPlanId + "', '" + ukLineNo + "', " + exchangeRate + ", " +
                        "'" + currencyCode + "', '"+tax_code+"', " + guidePrice + ", " + amt + ", " + taxAmount + ", " + noTaxPrice + ", " + noTaxAmount + ", " +
                        "'" + currencyCode + "', " + guidePrice + ", " + amt + ", 'N', '', '', " +
                        "'" + workOrder + "', '" + projectNo + "', '', " + guidePrice + ", '未提交', 'N', 0, " +
                        "0, 0, 'N', 0, 0, 0, 0, " +
                        "'N', 'N', 0, '" + userId + "', NOW(), '', NULL, " +
                        "'" + detailNotes + "', '"+tax_id+"', '"+tax_name+"', 'N', '" + purUnitName + "', '" + mainUnitName + "', '" + currencyId + "', " +
                        "'" + currencyDesc + "', '" + projectId + "', '" + projectName + "', '', '" + detailUuId + "', '" + groupId + "', '', " +
                        "0, '', '', '', '" + currencyId + "', '" + currencyDesc + "'" +
                        ")";
                insertsql.add(detailInsert);

                // 累加已生成数量到 pur_num
                String updatePurNum = "UPDATE pur_purchase_plan_detail SET pur_num = IFNULL(pur_num, 0) + " + planQty + " WHERE uu_id = '" + detailUuId + "'";
                insertsql.add(updatePurNum);

                // 检查该明细行是否已生成完毕（pur_num >= plan_qty），如果是则标记 is_active = 'N'
                String updateDetailActive = "UPDATE pur_purchase_plan_detail SET is_active = 'N' WHERE IFNULL(pur_num, 0)>=plan_qty and uu_id = '" + detailUuId + "'";
                insertsql.add(updateDetailActive);
            }

            // 检查是否所有明细行都已标记为 N，如果是则更新头表 is_active = 'N'
             String updateHeadActive = "UPDATE pur_purchase_plan_head SET is_active = 'N' WHERE uu_id = '" + planHeadUuId + "' and not exists(select 1 from pur_purchase_plan_detail where is_active='Y' and uk_plan_uuid='"+planHeadUuId+"')";
             insertsql.add(updateHeadActive);

        }

        // 只有所有校验通过（createflag为true）才提交执行
        if (createflag && insertsql.size() > 0) {
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
    public R zjdetailCreate(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();

        // 获取当前用户信息
        UserEntity user = ShiroUtils.getUserEntity();
        Long userId = user.getUserId();
        boolean createflag=true;
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bill_date = currentDate.format(formatter);

        // 从页面传入的 PurPlanHead 中获取订单号
        String orderNo = "";
        for (PurPlanHead plan : params) {
            if (plan.getUk_order_no() != null && !plan.getUk_order_no().isEmpty()) {
                orderNo = plan.getUk_order_no();
                break;
            }
        }
        if (orderNo.isEmpty()) {
            return R.error("未提供订单号");
        }

        // 查询订单头信息，获取订单头的供应商
        String orderHeadSql = "SELECT * FROM pur_order_head WHERE uk_order_no = '" + orderNo + "'";
        Map<String, Object> orderHead = sqlService.getOne(orderHeadSql);
        if (orderHead == null) {
            return R.error("订单不存在：" + orderNo);
        }
        String orderVendorCode = orderHead.get("vendor_code") != null ? orderHead.get("vendor_code").toString() : "";
        String orderVendorId = orderHead.get("vendor_id") != null ? orderHead.get("vendor_id").toString() : "";
        String orderVendorName = orderHead.get("vendor_name") != null ? orderHead.get("vendor_name").toString() : "";
        String groupId = orderHead.get("group_id") != null ? orderHead.get("group_id").toString() : "";
        String purOrg = orderHead.get("pur_org") != null ? orderHead.get("pur_org").toString() : "";
        String currencyCode = orderHead.get("currency_code") != null ? orderHead.get("currency_code").toString() : "RMB";
        String currencyId = orderHead.get("currency_id") != null ? orderHead.get("currency_id").toString() : "";
        String currencyDesc = orderHead.get("currency_desc") != null ? orderHead.get("currency_desc").toString() : "人民币";

        // 查询币别信息
        String currencySql = "SELECT uu_id, currency_code, currency_desc, exchange_rate, decimal_digits_price, decimal_digits_amt " +
                "FROM epd_currency WHERE currency_code = '" + currencyCode + "' AND valid_flag = 'Y' LIMIT 1";
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

        // 收集所有传入的明细uu_id
        List<String> detailUuIds = new ArrayList<>();
        for (PurPlanHead plan : params) {
            detailUuIds.add(plan.getUu_id());
        }

        if (detailUuIds.isEmpty()) {
            return R.error("未选择任何采购计划明细");
        }

        // 查询选中的明细数据
        String detailIdsStr = String.join("','", detailUuIds);
        String planDetailSql = "SELECT * FROM pur_purchase_plan_detail WHERE uu_id IN ('" + detailIdsStr + "')";
        List<Map<String, Object>> planDetails = sqlService.getAll(planDetailSql);
        if (planDetails == null || planDetails.isEmpty()) {
            return R.error("未找到选中的采购计划明细");
        }

        // 校验所有选中的明细：供应商必须与订单头一致
        for (Map<String, Object> planDetail : planDetails) {
            String itemCode = planDetail.get("item_code") != null ? planDetail.get("item_code").toString() : "";
            String applyOrg = planDetail.get("apply_org") != null ? planDetail.get("apply_org").toString() : "";
            String vendorCode = planDetail.get("vendor_code") != null ? planDetail.get("vendor_code").toString() : "";

            // 校验物料主文件
            String itemSql = "SELECT item_code FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'";
            Map<String, Object> itemInfo = sqlService.getOne(itemSql);
            if (itemInfo == null) {
                errorMsg.append("物料编码：").append(itemCode).append(" 在物料主文件中不存在\n");
                continue;
            }

            // 校验供应商
            if (vendorCode == null || vendorCode.isEmpty()) {
                errorMsg.append("物料：").append(itemCode).append(" 的供应商未定义，不能生成订单\n");
                continue;
            }

            // 校验供应商是否与订单头一致
            if (!vendorCode.equals(orderVendorCode)) {
                errorMsg.append("物料：").append(itemCode).append(" 的供应商（").append(vendorCode)
                        .append("）与订单头供应商（").append(orderVendorCode).append("）不一致\n");
                continue;
            }
        }

        // 如果有错误，返回错误信息
        if (errorMsg.length() > 0) {
            return R.error("采购订单追加失败，存在以下问题：\n" + errorMsg.toString());
        }

        // 获取当前订单明细的最大行号
        String maxLineNoSql = "SELECT IFNULL(MAX(uk_line_no), 0) as max_line_no FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "'";
        Map<String, Object> maxLineNoResult = sqlService.getOne(maxLineNoSql);
        int detailSeq = maxLineNoResult.get("max_line_no") != null ? Integer.parseInt(maxLineNoResult.get("max_line_no").toString()) : 0;

        // 循环处理每条明细
        for (Map<String, Object> planDetail : planDetails) {
            String item_id = planDetail.get("item_id") != null ? planDetail.get("item_id").toString() : "";
            String itemCode = planDetail.get("item_code") != null ? planDetail.get("item_code").toString() : "";
            String applyOrg = planDetail.get("apply_org") != null ? planDetail.get("apply_org").toString() : "";
            String needDate = planDetail.get("need_date") != null ? planDetail.get("need_date").toString() : "";
            String guide_price = planDetail.get("guide_price") != null ? planDetail.get("guide_price").toString() : "0";
            String vendorCode = planDetail.get("vendor_code") != null ? planDetail.get("vendor_code").toString() : "";

            // 从页面传入的 PurPlanHead 中获取 create_qty
            String detailUuIdForQty = planDetail.get("uu_id") != null ? planDetail.get("uu_id").toString() : "";
            double planQty = 0;
            for (PurPlanHead plan : params) {
                if (plan.getUu_id() != null && plan.getUu_id().equals(detailUuIdForQty)) {
                    String createQtyStr = plan.getCreate_qty() != null ? plan.getCreate_qty() : "0";
                    planQty = Double.parseDouble(createQtyStr);
                    break;
                }
            }

            // 校验：create_qty 不能超过剩余可生成数量（plan_qty - pur_num）
            String dbPlanQtyStr = planDetail.get("plan_qty") != null ? planDetail.get("plan_qty").toString() : "0";
            double dbPlanQty = Double.parseDouble(dbPlanQtyStr);
            String dbPurNumStr = planDetail.get("pur_num") != null ? planDetail.get("pur_num").toString() : "0";
            double dbPurNum = Double.parseDouble(dbPurNumStr);
            double remainQty = dbPlanQty - dbPurNum;
            if (planQty > remainQty) {
                errorMsg.append("物料：").append(itemCode).append(" 生成订单数量（").append(planQty)
                        .append("）超过剩余可生成数量（").append(remainQty).append("）\n");
                createflag = false;
                continue;
            }
            if (planQty <= 0) {
                errorMsg.append("物料：").append(itemCode).append("的生成订单数量必须大于0\n");
                createflag = false;
                continue;
            }

            // 从采购协议明细表查询单价
            String agreementSql = "SELECT pur_agreement_head.vendor_code,pur_agreement_detail.unit_price,tax_ratio,IFNULL(pur_agreement_detail.tax_code,epd_tax.tax_code) as tax_code,epd_tax.uu_id as tax_id,epd_tax.tax_name FROM pur_agreement_detail,pur_agreement_head,epd_tax  WHERE  pur_agreement_detail.tax_code=epd_tax.tax_code and  pur_agreement_detail.uk_agreement_uuid=pur_agreement_head.uu_id and pur_agreement_head.pur_org = '" + purOrg + "' AND pur_agreement_head.vendor_code = '" + vendorCode + "' AND pur_agreement_detail.item_id = '" + item_id + "' LIMIT 1";
            Map<String, Object> agreementInfo = sqlService.getOne(agreementSql);
            double guidePrice = Double.parseDouble(guide_price);
            if (agreementInfo != null ) {
                guidePrice = Double.parseDouble(agreementInfo.get("unit_price").toString());
            }
            double tax_ratio =0;
            if (agreementInfo != null ) {
                tax_ratio = Double.parseDouble(agreementInfo.get("tax_ratio").toString());
            }
            String tax_code ="";
            String tax_id ="";
            String tax_name ="";
            if (agreementInfo != null) {
                tax_code = agreementInfo.get("tax_code").toString();
                tax_id = agreementInfo.get("tax_id").toString();
                tax_name = agreementInfo.get("tax_name").toString();
            }
            if("".equals(tax_code)||tax_ratio==0){
                String agreement = "SELECT tax_ratio,epd_item_master.tax_code,epd_tax.uu_id as tax_id,epd_tax.tax_name FROM epd_item_master,epd_tax  WHERE epd_item_master.tax_code=epd_tax.tax_code  AND epd_item_master.uu_id = '" + item_id + "' LIMIT 1";
                Map<String, Object> agreementb = sqlService.getOne(agreement);
                if (agreementb != null ) {
                    tax_code = agreementb.get("tax_code").toString();
                    tax_id = agreementb.get("tax_id").toString();
                    tax_name = agreementb.get("tax_name").toString();
                    tax_ratio = Double.parseDouble(agreementb.get("tax_ratio").toString());
                }
            }

            String purUnitId = planDetail.get("pur_unit_id") != null ? planDetail.get("pur_unit_id").toString() : "";
            String purUnitName = planDetail.get("pur_unit_name") != null ? planDetail.get("pur_unit_name").toString() : "";
            String mainUnitId = planDetail.get("main_unit_id") != null ? planDetail.get("main_unit_id").toString() : "";
            String mainUnitName = planDetail.get("main_unit_name") != null ? planDetail.get("main_unit_name").toString() : "";
            String changeRateStr = planDetail.get("change_rate") != null ? planDetail.get("change_rate").toString() : "1";
            double changeRate = Double.parseDouble(changeRateStr);
            String projectId = planDetail.get("project_id") != null ? planDetail.get("project_id").toString() : "";
            String projectNo = planDetail.get("project_no") != null ? planDetail.get("project_no").toString() : "";
            String projectName = planDetail.get("project_name") != null ? planDetail.get("project_name").toString() : "";
            String workOrder = planDetail.get("work_order") != null ? planDetail.get("work_order").toString() : "";
            String detailNotes = planDetail.get("notes") != null ? planDetail.get("notes").toString() : "";
            String detailUuId = planDetail.get("uu_id") != null ? planDetail.get("uu_id").toString() : "";
            String ukLineNo = planDetail.get("uk_line_no") != null ? planDetail.get("uk_line_no").toString() : "";
            String uk_plan_id = planDetail.get("uk_plan_id") != null ? planDetail.get("uk_plan_id").toString() : "";
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

            // 检查该采购计划明细是否已经在订单明细中存在（通过 s_uu_id 关联）
            String existDetailSql = "SELECT uu_id, pur_qty, main_qty, amt, tax_amount, no_tax_amount FROM pur_order_detail WHERE uk_order_no = '" + orderNo + "' AND s_uu_id = '" + detailUuId + "' LIMIT 1";
            Map<String, Object> existDetail = sqlService.getOne(existDetailSql);

            if (existDetail != null) {
                // 已存在，累加数量和金额
                String existDetailId = existDetail.get("uu_id") != null ? existDetail.get("uu_id").toString() : "";
                double existPurQty = existDetail.get("pur_qty") != null ? Double.parseDouble(existDetail.get("pur_qty").toString()) : 0;
                double existMainQty = existDetail.get("main_qty") != null ? Double.parseDouble(existDetail.get("main_qty").toString()) : 0;
                double existAmt = existDetail.get("amt") != null ? Double.parseDouble(existDetail.get("amt").toString()) : 0;
                double existTaxAmount = existDetail.get("tax_amount") != null ? Double.parseDouble(existDetail.get("tax_amount").toString()) : 0;
                double existNoTaxAmount = existDetail.get("no_tax_amount") != null ? Double.parseDouble(existDetail.get("no_tax_amount").toString()) : 0;

                // 计算新增的数量和金额
                double calcMainQty;
                if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                    calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
                } else {
                    calcMainQty = planQty;
                }
                double newAmt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
                double newTaxAmount = Math.round(newAmt * (tax_ratio/100) / (1 + tax_ratio/100) * amtFactor) / amtFactor;
                double newNoTaxAmount = Math.round(guidePrice / (1 + tax_ratio/100) * priceFactor) / priceFactor * planQty;
                newNoTaxAmount = Math.round(newNoTaxAmount * amtFactor) / amtFactor;

                // 累加后的总量
                double totalPurQty = existPurQty + planQty;
                double totalMainQty = existMainQty + calcMainQty;
                double totalAmt = existAmt + newAmt;
                double totalTaxAmount = existTaxAmount + newTaxAmount;
                double totalNoTaxAmount = existNoTaxAmount + newNoTaxAmount;

                // 更新订单明细
                String updateDetail = "UPDATE pur_order_detail SET " +
                        "pur_qty = " + totalPurQty + ", " +
                        "main_qty = " + totalMainQty + ", " +
                        "amt = " + totalAmt + ", " +
                        "tax_amount = " + totalTaxAmount + ", " +
                        "no_tax_amount = " + totalNoTaxAmount + ", " +
                        "modify_user_id = '" + userId + "', " +
                        "modify_time = NOW() " +
                        "WHERE uu_id = '" + existDetailId + "'";
                insertsql.add(updateDetail);
            } else {
                // 不存在，插入新行
                double calcMainQty;
                if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                    calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
                } else {
                    calcMainQty = planQty;
                }
                double amt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
                double taxAmount = Math.round(amt * (tax_ratio/100) / (1 + tax_ratio/100) * amtFactor) / amtFactor;
                double noTaxPrice = Math.round(guidePrice / (1 + tax_ratio/100) * priceFactor) / priceFactor;
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
                        changeRate + ", '" + applyOrg + "', '采购计划', '" + uk_plan_id + "', '" + ukLineNo + "', " + exchangeRate + ", " +
                        "'" + currencyCode + "', '"+tax_code+"', " + guidePrice + ", " + amt + ", " + taxAmount + ", " + noTaxPrice + ", " + noTaxAmount + ", " +
                        "'" + currencyCode + "', " + guidePrice + ", " + amt + ", 'N', '', '', " +
                        "'" + workOrder + "', '" + projectNo + "', '', " + guidePrice + ", '未提交', 'N', 0, " +
                        "0, 0, 'N', 0, 0, 0, 0, " +
                        "'N', 'N', 0, '" + userId + "', NOW(), '', NULL, " +
                        "'" + detailNotes + "', '"+tax_id+"', '"+tax_name+"', 'N', '" + purUnitName + "', '" + mainUnitName + "', '" + currencyId + "', " +
                        "'" + currencyDesc + "', '" + projectId + "', '" + projectName + "', '', '" + detailUuId + "', '" + groupId + "', '', " +
                        "0, '', '', '', '" + currencyId + "', '" + currencyDesc + "'" +
                        ")";
                insertsql.add(detailInsert);
            }

            // 累加已生成数量到 pur_num
            String updatePurNum = "UPDATE pur_purchase_plan_detail SET pur_num = IFNULL(pur_num, 0) + " + planQty + " WHERE uu_id = '" + detailUuId + "'";
            insertsql.add(updatePurNum);

            // 检查该明细行是否已生成完毕（pur_num >= plan_qty），如果是则标记 is_active = 'N'
            String updateDetailActive = "UPDATE pur_purchase_plan_detail SET is_active = 'N' WHERE IFNULL(pur_num, 0)>=plan_qty and uu_id = '" + detailUuId + "'";
            insertsql.add(updateDetailActive);
        }

        // 检查是否所有明细行都已标记为 N，如果是则更新头表 is_active = 'N'
        String planHeadUuId = "";
        if (planDetails.size() > 0) {
            planHeadUuId = planDetails.get(0).get("uk_plan_uuid") != null ? planDetails.get(0).get("uk_plan_uuid").toString() : "";
        }
        if (!planHeadUuId.isEmpty()) {
            String updateHeadActive = "UPDATE pur_purchase_plan_head SET is_active = 'N' WHERE uu_id = '" + planHeadUuId + "' and not exists(select 1 from pur_purchase_plan_detail where is_active='Y' and uk_plan_uuid='"+planHeadUuId+"')";
            insertsql.add(updateHeadActive);
        }

        // 只有所有校验通过（createflag为true）才提交执行
        if (createflag && insertsql.size() > 0) {
            boolean flag = sqlService.insertList(insertsql);
            if (flag) {
                return R.ok("采购订单追加成功");
            } else {
                return R.error("采购订单追加失败");
            }
        } else {
            return R.error("采购订单追加失败");
        }
    }

    @Override
    public R orderCreate(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        boolean createflag=true;
        // 获取当前用户信息
        UserEntity user = ShiroUtils.getUserEntity();
        Long userId = user.getUserId();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bill_date = currentDate.format(formatter);

        // 生成订单号（年月日+4位流水）
        String orderNoSql = "SELECT CASE WHEN MAX(uk_order_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0001') " +
                "ELSE LPAD(CAST(MAX(uk_order_no) AS UNSIGNED) + 1, 12, '0') END AS order_no " +
                "FROM pur_order_head WHERE LEFT(uk_order_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> orderNoResult = sqlService.getOne(orderNoSql);
        String orderNo = orderNoResult.get("order_no").toString();
        String prefix = orderNo.substring(0, orderNo.length() - 4);
        String lastFourStr = orderNo.substring(orderNo.length() - 4);
        int seq = Integer.parseInt(lastFourStr);

        // 查询RMB币别信息
        String currencySql = "SELECT uu_id, currency_code, currency_desc, exchange_rate, decimal_digits_price, decimal_digits_amt " +
                "FROM epd_currency WHERE currency_code = 'RMB' AND valid_flag = 'Y' LIMIT 1";
        Map<String, Object> currencyInfo = sqlService.getOne(currencySql);
        String currencyId = "";
        String currencyCode = "RMB";
        String currencyDesc = "人民币";
        double exchangeRate = 1.0;
        int decimalDigitsPrice = 2;
        int decimalDigitsAmt = 2;
        if (currencyInfo != null) {
            currencyId = currencyInfo.get("uu_id") != null ? currencyInfo.get("uu_id").toString() : "";
            currencyCode = currencyInfo.get("currency_code") != null ? currencyInfo.get("currency_code").toString() : "RMB";
            currencyDesc = currencyInfo.get("currency_desc") != null ? currencyInfo.get("currency_desc").toString() : "人民币";
            String exchangeRateStr = currencyInfo.get("exchange_rate") != null ? currencyInfo.get("exchange_rate").toString() : "1";
            exchangeRate = Double.parseDouble(exchangeRateStr);
            String decimalDigitsPriceStr = currencyInfo.get("decimal_digits_price") != null ? currencyInfo.get("decimal_digits_price").toString() : "2";
            decimalDigitsPrice = Integer.parseInt(decimalDigitsPriceStr);
            String decimalDigitsAmtStr = currencyInfo.get("decimal_digits_amt") != null ? currencyInfo.get("decimal_digits_amt").toString() : "2";
            decimalDigitsAmt = Integer.parseInt(decimalDigitsAmtStr);
        }

        // 逐条处理采购计划头（每个头生成一个订单）
        for (PurPlanHead plan : params) {
            String planHeadUuId = plan.getUu_id();

            // 1. 查询采购计划头信息
            String planHeadSql = "SELECT * FROM pur_purchase_plan_head WHERE uu_id = '" + planHeadUuId + "'";
            Map<String, Object> planHead = sqlService.getOne(planHeadSql);
            if (planHead == null) {
                errorMsg.append("采购计划头不存在：").append(planHeadUuId).append("\n");
                continue;
            }

            String purOrg = planHead.get("pur_org") != null ? planHead.get("pur_org").toString() : "";
            String groupId = planHead.get("group_id") != null ? planHead.get("group_id").toString() : "";
            String deptId = planHead.get("dept_id") != null ? planHead.get("dept_id").toString() : "";
            String deptCode = planHead.get("dept_code") != null ? planHead.get("dept_code").toString() : "";
            String deptName = planHead.get("dept_name") != null ? planHead.get("dept_name").toString() : "";
            String ukPlanId = planHead.get("uk_plan_id") != null ? planHead.get("uk_plan_id").toString() : "";
            String notes = planHead.get("notes") != null ? planHead.get("notes").toString() : "";

            // 2. 查询该头下的所有明细
            String planDetailSql = "SELECT * FROM pur_purchase_plan_detail WHERE uk_plan_uuid = '" + planHeadUuId + "'";
            List<Map<String, Object>> planDetails = sqlService.getAll(planDetailSql);
            if (planDetails == null || planDetails.isEmpty()) {
                errorMsg.append("采购计划头：").append(ukPlanId).append(" 下没有明细数据\n");
                continue;
            }

            // 3. 先校验所有明细
            boolean hasError = false;
            for (Map<String, Object> planDetail : planDetails) {
                String itemCode = planDetail.get("item_code") != null ? planDetail.get("item_code").toString() : "";
                String purOrgl = planDetail.get("pur_org") != null ? planDetail.get("pur_org").toString() : "";
                String vendorCode = planDetail.get("vendor_code") != null ? planDetail.get("vendor_code").toString() : "";
                String guide_price = planDetail.get("guide_price") != null ? planDetail.get("guide_price").toString() : "0";
                // 校验物料主文件
                String itemSql = "SELECT item_code FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + purOrgl + "'";
                Map<String, Object> itemInfo = sqlService.getOne(itemSql);
                if (itemInfo == null) {
                    errorMsg.append("物料编码：").append(itemCode).append(" 在物料主文件中不存在\n");
                    hasError = true;
                    continue;
                }

                // 校验供应商
                if (vendorCode == null || vendorCode.isEmpty()) {
                    errorMsg.append("物料：").append(itemCode).append(" 的供应商未定义，不能生成订单\n");
                    hasError = true;
                    continue;
                }

            }

            if (hasError) {
                createflag = false;
                continue;
            }

            // 4. 按供应商分组
            Map<String, List<Map<String, Object>>> vendorGroupMap = new LinkedHashMap<>();
            for (Map<String, Object> planDetail : planDetails) {
                String vendorCode = planDetail.get("vendor_code") != null ? planDetail.get("vendor_code").toString() : "";
                if (!vendorGroupMap.containsKey(vendorCode)) {
                    vendorGroupMap.put(vendorCode, new ArrayList<>());
                }
                vendorGroupMap.get(vendorCode).add(planDetail);
            }

            // 5. 按供应商分组生成订单
            for (Map.Entry<String, List<Map<String, Object>>> vendorEntry : vendorGroupMap.entrySet()) {
                List<Map<String, Object>> vendorDetails = vendorEntry.getValue();
                if (vendorDetails.isEmpty()) {
                    continue;
                }

                // 取该供应商组的第一条明细获取供应商信息
                Map<String, Object> firstDetail = vendorDetails.get(0);
                String vendorCode = firstDetail.get("vendor_code") != null ? firstDetail.get("vendor_code").toString() : "";
                String vendorId = firstDetail.get("vendor_id") != null ? firstDetail.get("vendor_id").toString() : "";
                String vendorName = firstDetail.get("vendor_name") != null ? firstDetail.get("vendor_name").toString() : "";
                String buyerCode = firstDetail.get("buyer_code") != null ? firstDetail.get("buyer_code").toString() : "";
                String buyerId = firstDetail.get("buyer_id") != null ? firstDetail.get("buyer_id").toString() : "";
                String buyerMan = firstDetail.get("buyer_man") != null ? firstDetail.get("buyer_man").toString() : "";
                String roleId = firstDetail.get("role_id") != null ? firstDetail.get("role_id").toString() : "";
                String roleCode = firstDetail.get("role_code") != null ? firstDetail.get("role_code").toString() : "";
                String roleName = firstDetail.get("role_name") != null ? firstDetail.get("role_name").toString() : "";

                // 生成订单号
                String currentOrderNo = prefix + String.format("%04d", seq);
                seq++;

                // 插入订单头
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
                        ") VALUES (" +
                        "'" + currentOrderNo + "', '" + purOrg + "', 'CG', '" + bill_date + "', '02', '采购计划生成', " +
                        "'" + ukPlanId + "', '" + vendorCode + "', '" + bill_date + "', '', '', '', " +
                        "'" + roleCode + "', '" + deptCode + "', '" + buyerCode + "', 0, '" + currencyCode + "', " +
                        "'" + currencyCode + "', '', 'N', '', '', " +
                        "'', '', '', '', '', '', '', " +
                        "'', '" + notes + "', '', '', '', '', '', " +
                        "'N', 'N', 'N', '未提交', '" + userId + "', NOW(), " +
                        "'', NULL, '" + notes + "', '', '', '', NULL, '', " +
                        "'', '', '"+planHeadUuId+"', '" + groupId + "', '',null, " +
                        "'" + vendorId + "', '" + vendorName + "', '', '', '" + roleId + "', '" + currencyId + "', '" + currencyDesc + "', " +
                        "'" + roleName + "', '" + deptId + "', '" + deptName + "', '" + buyerId + "', '" + buyerMan + "', " +
                        "'" + currencyId + "', '" + currencyDesc + "'" +
                        ")";
                insertsql.add(headInsert);

                // 6. 循环插入该供应商下的订单明细
                int detailSeq = 0;
                for (Map<String, Object> planDetail : vendorDetails) {
                String item_id = planDetail.get("item_id") != null ? planDetail.get("item_id").toString() : "";
                String itemCode = planDetail.get("item_code") != null ? planDetail.get("item_code").toString() : "";
                String guide_price = planDetail.get("guide_price") != null ? planDetail.get("guide_price").toString() : "0";
                String applyOrg = planDetail.get("apply_org") != null ? planDetail.get("apply_org").toString() : "";
                String needDate = planDetail.get("need_date") != null ? planDetail.get("need_date").toString() : "";
                // 按头生成时，数量 = plan_qty - pur_num（已生成数量）
                String planQtyStr = planDetail.get("plan_qty") != null ? planDetail.get("plan_qty").toString() : "0";
                double planQtyTotal = Double.parseDouble(planQtyStr);
                String purNumStr = planDetail.get("pur_num") != null ? planDetail.get("pur_num").toString() : "0";
                double purNum = Double.parseDouble(purNumStr);
                double planQty = planQtyTotal - purNum;
                // 如果可生成数量小于等于0，直接跳过该明细
                if (planQty <= 0) {
                    continue;
                }
                // 从采购协议明细表查询单价（通过采购组织、供应商、物料编码关联）
                    String agreementSql = "SELECT pur_agreement_head.vendor_code,pur_agreement_detail.unit_price,tax_ratio,IFNULL(pur_agreement_detail.tax_code,epd_tax.tax_code) as tax_code,epd_tax.uu_id as tax_id,epd_tax.tax_name FROM pur_agreement_detail,pur_agreement_head,epd_tax  WHERE  pur_agreement_detail.tax_code=epd_tax.tax_code and  pur_agreement_detail.uk_agreement_uuid=pur_agreement_head.uu_id and pur_agreement_head.pur_org = '" + purOrg + "' AND pur_agreement_head.vendor_code = '" + vendorCode + "' AND pur_agreement_detail.item_id = '" + item_id + "' LIMIT 1";
                    Map<String, Object> agreementInfo = sqlService.getOne(agreementSql);
                    double guidePrice = Double.parseDouble(guide_price);
                    if (agreementInfo != null ) {
                        guidePrice = Double.parseDouble(agreementInfo.get("unit_price").toString());
                    }
                    double tax_ratio =0;
                    if (agreementInfo != null ) {
                        tax_ratio = Double.parseDouble(agreementInfo.get("tax_ratio").toString());
                    }
                    String tax_code ="";
                    String tax_id ="";
                    String tax_name ="";
                    if (agreementInfo != null) {
                        tax_code = agreementInfo.get("tax_code").toString();
                        tax_id = agreementInfo.get("tax_id").toString();
                        tax_name = agreementInfo.get("tax_name").toString();
                    }
                    if("".equals(tax_code)||tax_ratio==0){
                        String agreement = "SELECT tax_ratio,epd_item_master.tax_code,epd_tax.uu_id as tax_id,epd_tax.tax_name FROM epd_item_master,epd_tax  WHERE epd_item_master.tax_code=epd_tax.tax_code  AND epd_item_master.uu_id = '" + item_id + "' LIMIT 1";
                        Map<String, Object> agreementb = sqlService.getOne(agreement);
                        if (agreementb != null ) {
                            tax_code = agreementb.get("tax_code").toString();
                            tax_id = agreementb.get("tax_id").toString();
                            tax_name = agreementb.get("tax_name").toString();
                            tax_ratio = Double.parseDouble(agreementb.get("tax_ratio").toString());

                        }
                    }
                String purUnitId = planDetail.get("pur_unit_id") != null ? planDetail.get("pur_unit_id").toString() : "";
                String purUnitName = planDetail.get("pur_unit_name") != null ? planDetail.get("pur_unit_name").toString() : "";
                String mainUnitId = planDetail.get("main_unit_id") != null ? planDetail.get("main_unit_id").toString() : "";
                String mainUnitName = planDetail.get("main_unit_name") != null ? planDetail.get("main_unit_name").toString() : "";
                String changeRateStr = planDetail.get("change_rate") != null ? planDetail.get("change_rate").toString() : "1";
                double changeRate = Double.parseDouble(changeRateStr);
                String projectId = planDetail.get("project_id") != null ? planDetail.get("project_id").toString() : "";
                String projectNo = planDetail.get("project_no") != null ? planDetail.get("project_no").toString() : "";
                String projectName = planDetail.get("project_name") != null ? planDetail.get("project_name").toString() : "";
                String workOrder = planDetail.get("work_order") != null ? planDetail.get("work_order").toString() : "";
                String detailNotes = planDetail.get("notes") != null ? planDetail.get("notes").toString() : "";
                String detailUuId = planDetail.get("uu_id") != null ? planDetail.get("uu_id").toString() : "";
                String ukLineNo = planDetail.get("uk_line_no") != null ? planDetail.get("uk_line_no").toString() : "";

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

                // 计算第二单位数量（保留金额小数位）
                double calcMainQty;
                if (secondUnit != null && !secondUnit.isEmpty() && unitFactorP > 0) {
                    calcMainQty = Math.round(planQty / unitFactorP * amtFactor) / amtFactor;
                } else {
                    calcMainQty = planQty;
                }

                // 计算金额（单价保留price小数位，金额保留amt小数位）
                double amt = Math.round(planQty * guidePrice * amtFactor) / amtFactor;
                double taxAmount = Math.round(amt * (tax_ratio/100) / (1 + tax_ratio/100) * amtFactor) / amtFactor;
                double noTaxPrice = Math.round(guidePrice / (1 + tax_ratio/100) * priceFactor) / priceFactor;
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
                        "'" + currentOrderNo + "', (SELECT uu_id FROM pur_order_head WHERE uk_order_no = '" + currentOrderNo + "'), " +
                        "'" + detailSeq + "', '" + applyOrg + "', '" + needDate + "', '" + itemCode + "', " +
                        "(SELECT item_name FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_spec FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "(SELECT item_model FROM epd_item_master WHERE item_code = '" + itemCode + "' AND inv_org = '" + applyOrg + "'), " +
                        "'', " + planQty + ", '" + purUnitId + "', " + calcMainQty + ", '" + mainUnitId + "', " +
                        changeRate + ", '" + applyOrg + "', '采购计划', '" + ukPlanId + "', '" + ukLineNo + "', " + exchangeRate + ", " +
                        "'" + currencyCode + "', '"+tax_code+"', " + guidePrice + ", " + amt + ", " + taxAmount + ", " + noTaxPrice + ", " + noTaxAmount + ", " +
                        "'" + currencyCode + "', " + guidePrice + ", " + amt + ", 'N', '', '', " +
                        "'" + workOrder + "', '" + projectNo + "', '', " + guidePrice + ", '未提交', 'N', 0, " +
                        "0, 0, 'N', 0, 0, 0, 0, " +
                        "'N', 'N',null, '" + userId + "', NOW(), '', NULL, " +
                        "'" + detailNotes + "', '"+tax_id+"', '"+tax_name+"', 'N', '" + purUnitName + "', '" + mainUnitName + "', '" + currencyId + "', " +
                        "'" + currencyDesc + "', '" + projectId + "', '" + projectName + "', '', '" + detailUuId + "', '" + groupId + "', '', " +
                        "0, '', '', '', '" + currencyId + "', '" + currencyDesc + "'" +
                        ")";
                insertsql.add(detailInsert);

                // 累加已生成数量到 pur_num
                String updatePurNum = "UPDATE pur_purchase_plan_detail SET pur_num = IFNULL(pur_num, 0) + " + planQty + " WHERE uu_id = '" + detailUuId + "'";
                insertsql.add(updatePurNum);

                // 检查该明细行是否已生成完毕（pur_num >= plan_qty），如果是则标记 is_active = 'N'
                String updateDetailActive = "UPDATE pur_purchase_plan_detail SET is_active = 'N' WHERE IFNULL(pur_num, 0)>=plan_qty and uu_id = '" + detailUuId + "'";
                insertsql.add(updateDetailActive);
                }

            }

            // 检查是否所有明细行都已标记为 N，如果是则更新头表 is_active = 'N'
           String updateHeadActive = "UPDATE pur_purchase_plan_head SET is_active = 'N' WHERE uu_id = '" + planHeadUuId + "' and not exists(select 1 from pur_purchase_plan_detail where is_active='Y' and uk_plan_uuid='"+planHeadUuId+"')";
            insertsql.add(updateHeadActive);

        }

        // 如果有错误，返回错误信息
        if (errorMsg.length() > 0) {
            createflag = false;
        }

        // 只有所有校验通过（createflag为true）才提交执行
        if (createflag && insertsql.size() > 0) {
            boolean flag = sqlService.insertList(insertsql);
            if (flag) {
                return R.ok("采购订单生成成功");
            } else {
                return R.error("采购订单生成失败");
            }
        } else if (errorMsg.length() > 0) {
            return R.error("采购订单生成失败，存在以下问题：\n" + errorMsg.toString());
        } else {
            return R.error("采购订单生成失败");
        }
    }

    @Override
    public R planCreate() {
        List<String> insertsql = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();

        // 获取当前用户信息（计划员ID、CODE、NAME）
        UserEntity user = ShiroUtils.getUserEntity();
        String personId = user.getUserId().toString();
        String personCode = user.getUsername();
        String personName = user.getName();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String curDateStr = currentDate.format(fmt);

        // 从PUR_MRP_ORDER表一次性获取所有参数（该表永远只有一条数据）
        String purOrgCode = "";
        String lotNo = "";
        String reorderPoint = "N";
        String isPlan = "N";
        String isIntransit = "N";
        String isApply = "N";
        String applyDate = "9999-12-31";
        int timeInterval = 7;
        String isSale = "N";
        String isSupply = "N";
        String isBatch = "N";

        Map<String, Object> mrpOrder = sqlService.getOne("SELECT pur_org, lot_no, reorder_point, is_plan, is_intransit, " +
                "is_apply, apply_date, time_interval, is_sale, is_supply, is_batch FROM pur_mrp_order LIMIT 1");
        if (mrpOrder != null) {
            purOrgCode = mrpOrder.get("pur_org") != null ? mrpOrder.get("pur_org").toString() : "";
            lotNo = mrpOrder.get("lot_no") != null ? mrpOrder.get("lot_no").toString() : "";
            reorderPoint = mrpOrder.get("reorder_point") != null ? mrpOrder.get("reorder_point").toString() : "N";
            isPlan = mrpOrder.get("is_plan") != null ? mrpOrder.get("is_plan").toString() : "N";
            isIntransit = mrpOrder.get("is_intransit") != null ? mrpOrder.get("is_intransit").toString() : "N";
            isApply = mrpOrder.get("is_apply") != null ? mrpOrder.get("is_apply").toString() : "N";
            applyDate = mrpOrder.get("apply_date") != null ? mrpOrder.get("apply_date").toString() : "9999-12-31";
            String timeIntervalStr = mrpOrder.get("time_interval") != null ? mrpOrder.get("time_interval").toString() : "7";
            timeInterval = Integer.parseInt(timeIntervalStr);
            isSale = mrpOrder.get("is_sale") != null ? mrpOrder.get("is_sale").toString() : "N";
            isSupply = mrpOrder.get("is_supply") != null ? mrpOrder.get("is_supply").toString() : "N";
            isBatch = mrpOrder.get("is_batch") != null ? mrpOrder.get("is_batch").toString() : "N";
        }
        if (purOrgCode.isEmpty() || lotNo.isEmpty()) {
            return R.error("采购组织或计划号不能为空");
        }

        // 1. 删除异常记录
        insertsql.add("DELETE FROM pur_plan_exception WHERE uk_plan_id = '" + lotNo +
                "' AND plan_user_id = '" + personId + "' AND pur_org = '" + purOrgCode + "'");

        // 2. 系统参数取值
        String paramSql = "SELECT plan_check, plan_method FROM pur_parameter WHERE pur_org = '" + purOrgCode + "'";
        Map<String, Object> paramResult = sqlService.getOne(paramSql);
        String planCheck = "N";
        String planMethod = "N";
        if (paramResult != null) {
          planCheck = paramResult.get("plan_check") != null ? paramResult.get("plan_check").toString() : "N";
          planMethod = paramResult.get("plan_method") != null ? paramResult.get("plan_method").toString() : "N";
        }

        int periodDays = timeInterval - 1;

        // ========== 再订货点逻辑 ==========
        if ("Y".equals(reorderPoint)) {
            String itemFilterSql;
            if ("N".equals(planMethod)) {
                // 不控制
                itemFilterSql = "SELECT item_code, IFNULL(unit_factor_p, 1) as unit_factor_p, IFNULL(lead_time, 0) as lead_time, " +
                        "pur_unit, planner_code, reorder_qty, item_name, item_model, item_spec, " +
                        "order_point, plan_price, IFNULL(unit_factor_s, 1) as unit_factor_s " +
                        "FROM epd_item_master WHERE inv_org = '" + purOrgCode + "' " +
                        "AND mrp_flag = 'O' AND m_p_flag = 'P' AND IFNULL(order_point, 0) <> 0 AND valid_flag = 'Y'";
            } else {
                // 按计划员
                itemFilterSql = "SELECT item_code, IFNULL(unit_factor_p, 1) as unit_factor_p, IFNULL(lead_time, 0) as lead_time, " +
                        "pur_unit, planner_code, reorder_qty, item_name, item_model, item_spec, " +
                        "order_point, plan_price, IFNULL(unit_factor_s, 1) as unit_factor_s " +
                        "FROM epd_item_master WHERE inv_org = '" + purOrgCode + "' " +
                        "AND mrp_flag = 'O' AND m_p_flag = 'P' AND IFNULL(order_point, 0) <> 0 AND valid_flag = 'Y' " +
                        "AND planner_code = '" + personCode + "'";
            }
            // 再订货点逻辑：取到的物料可作为需求来源之一（此处暂不处理，与老系统保持一致的框架）
        }

        // ========== 采购申请逻辑 ==========
        if ("Y".equals(isApply)) {
            // 查询需求申请明细
            String demandSql;
            if ("N".equals(planMethod)) {
                // 不控制
                demandSql = "SELECT a.item_code, MIN(a.end_date) as start_date, MAX(a.end_date) as max_date, " +
                        "IFNULL(a.project_no, ' ') as project_no, IFNULL(a.notes, ' ') as demand_note, " +
                        "a.work_order, a.pur_org as apply_org, a.uk_apply_no, a.item_name, a.item_spec, " +
                        "a.item_model, c.type_code, a.uk_apply_uuid, a.uk_line_no " +
                        "FROM pur_apply_detail a, pur_apply_head c " +
                        "WHERE a.pur_org = '" + purOrgCode + "' " +
                        "AND a.line_status = '审批通过' AND a.is_geneplan = 'N' " +
                        "AND a.end_date <= IFNULL('" + applyDate + "', '9999-12-31') " +
                        "AND a.uk_apply_uuid = c.uu_id " +
                        "GROUP BY a.pur_unit_id, a.item_code, IFNULL(a.project_no, ' '), a.work_order";
            } else {
                // 按计划员
                demandSql = "SELECT a.item_code, MIN(a.end_date) as start_date, MAX(a.end_date) as max_date, " +
                        "IFNULL(a.project_no, ' ') as project_no, IFNULL(a.notes, ' ') as demand_note, " +
                        "a.work_order, a.pur_org as apply_org, a.uk_apply_no, a.item_name, a.item_spec, " +
                        "a.item_model, c.type_code, a.uk_apply_uuid, a.uk_line_no " +
                        "FROM pur_apply_detail a, epd_item_master b, pur_apply_head c " +
                        "WHERE a.pur_org = '" + purOrgCode + "' " +
                        "AND a.apply_org = b.inv_org AND a.end_date <= IFNULL('" + applyDate + "', '9999-12-31') " +
                        "AND a.item_code = b.item_code AND a.line_status = '审批通过' AND a.is_geneplan = 'N' " +
                        "AND b.planner_code = '" + personCode + "' AND a.uk_apply_uuid = c.uu_id " +
                        "GROUP BY a.pur_unit_id, a.item_code, IFNULL(a.project_no, ' '), a.work_order";
            }

            List<Map<String, Object>> demandItems = sqlService.getAll(demandSql);
            if (demandItems == null || demandItems.isEmpty()) {
                if ("N".equals(planMethod)) {
                    errorMsg.append("无需求申请!\n");
                } else {
                    errorMsg.append("按计划员权限做计划:当前登录人无需求申请物料的权限或无需求申请!\n");
                }
                return R.error(errorMsg.toString());
            }

            // 插入PM_PLAN_LOT_DETAIL并更新申请明细状态
            if ("N".equals(planMethod)) {
                insertsql.add("INSERT INTO pm_plan_lot_detail (company_code, lot_no, demand_no) " +
                        "SELECT DISTINCT '" + purOrgCode + "', '" + lotNo + "', a.uk_apply_no " +
                        "FROM pur_apply_detail a WHERE a.pur_org = '" + purOrgCode + "' " +
                        "AND a.line_status = '审批通过' AND a.is_geneplan = 'N' " +
                        "AND a.end_date <= IFNULL('" + applyDate + "', '9999-12-31')");

                insertsql.add("UPDATE pur_apply_detail a SET a.is_geneplan = '2' " +
                        "WHERE a.pur_org = '" + purOrgCode + "' AND a.line_status = '审批通过' " +
                        "AND a.is_geneplan = 'N' AND a.end_date <= IFNULL('" + applyDate + "', '9999-12-31')");
            } else {
                insertsql.add("INSERT INTO pm_plan_lot_detail (company_code, lot_no, demand_no) " +
                        "SELECT DISTINCT '" + purOrgCode + "', '" + lotNo + "', a.uk_apply_no " +
                        "FROM pur_apply_detail a, epd_item_master b " +
                        "WHERE a.pur_org = '" + purOrgCode + "' AND a.apply_org = b.inv_org " +
                        "AND a.end_date <= IFNULL('" + applyDate + "', '9999-12-31') " +
                        "AND a.item_code = b.item_code AND a.line_status = '审批通过' " +
                        "AND a.is_geneplan = 'N' AND b.planner_code = '" + personCode + "'");

                insertsql.add("UPDATE pur_apply_detail a SET a.is_geneplan = '2' " +
                        "WHERE a.pur_org = '" + purOrgCode + "' AND a.line_status = '审批通过' " +
                        "AND a.is_geneplan = 'N' AND a.end_date <= IFNULL('" + applyDate + "', '9999-12-31') " +
                        "AND EXISTS (SELECT NULL FROM epd_item_master b WHERE a.apply_org = b.inv_org " +
                        "AND a.item_code = b.item_code AND b.planner_code = '" + personCode + "')");
            }

            // 行号计数器
            int lotSeq = 0;

            // 遍历每个需求物料
            for (Map<String, Object> demand : demandItems) {
                String dItemCode = demand.get("item_code") != null ? demand.get("item_code").toString() : "";
                String dStartDate = demand.get("start_date") != null ? demand.get("start_date").toString() : "";
                String dMaxDate = demand.get("max_date") != null ? demand.get("max_date").toString() : "";
                String dProjectNo = demand.get("project_no") != null ? demand.get("project_no").toString() : "";
                String dWorkOrder = demand.get("work_order") != null ? demand.get("work_order").toString() : "";
                String dApplyOrg = demand.get("apply_org") != null ? demand.get("apply_org").toString() : "";
                String dItemName = demand.get("item_name") != null ? demand.get("item_name").toString() : "";
                String dItemSpec = demand.get("item_spec") != null ? demand.get("item_spec").toString() : "";
                String dItemModel = demand.get("item_model") != null ? demand.get("item_model").toString() : "";
                String dTypeCode = demand.get("type_code") != null ? demand.get("type_code").toString() : "";

                if (dStartDate.isEmpty() || dMaxDate.isEmpty()) {
                    continue;
                }

                // 查询物料主文件信息
                String itemMasterSql = "SELECT item_code, IFNULL(unit_factor_p, 1) as unit_factor_p, " +
                        "IFNULL(lead_time, 0) as lead_time, pur_unit, planner_code, reorder_qty, " +
                        "item_name, item_model, item_spec, order_point, plan_price, IFNULL(unit_factor_s, 1) as unit_factor_s " +
                        "FROM epd_item_master WHERE inv_org = '" + dApplyOrg + "' AND item_code = '" + dItemCode + "'";
                Map<String, Object> itemMaster = sqlService.getOne(itemMasterSql);
                if (itemMaster == null) continue;

                double unitFactorP = Double.parseDouble(itemMaster.get("unit_factor_p") != null ? itemMaster.get("unit_factor_p").toString() : "1");
                int leadTime = Integer.parseInt(itemMaster.get("lead_time") != null ? itemMaster.get("lead_time").toString() : "0");
                String purUnit = itemMaster.get("pur_unit") != null ? itemMaster.get("pur_unit").toString() : "";

                // 计算结束日期 = 开始日期 + 时间间隔 + 1
                LocalDate startDateLc = LocalDate.parse(dStartDate, fmt);
                LocalDate maxDateLc = LocalDate.parse(dMaxDate, fmt);
                LocalDate endDateLc = startDateLc.plusDays(timeInterval + 1);

                // 按时间段循环
                while (!startDateLc.isAfter(maxDateLc)) {
                    if (endDateLc.isAfter(maxDateLc)) {
                        endDateLc = maxDateLc;
                    }
                    String endDateStr = endDateLc.format(fmt);
                    String startDateStr = startDateLc.format(fmt);

                    // 查询该时间段内的需求数量
                    String periodSql = "SELECT COUNT(*) as cnt, ROUND(SUM(IFNULL(apply_qty, 0)), 4) as demand_qty " +
                            "FROM pur_apply_detail WHERE pur_org = '" + purOrgCode + "' " +
                            "AND item_code = '" + dItemCode + "' " +
                            "AND IFNULL(project_no, ' ') = '" + dProjectNo + "' " +
                            "AND end_date >= '" + startDateStr + "' AND end_date <= '" + endDateStr + "' " +
                            "AND line_status = '审批通过' AND is_geneplan = '2'";
                    Map<String, Object> periodResult = sqlService.getOne(periodSql);
                    int recordCount = 0;
                    double demandQty = 0;
                    if (periodResult != null) {
                        recordCount = Integer.parseInt(periodResult.get("cnt") != null ? periodResult.get("cnt").toString() : "0");
                        demandQty = Double.parseDouble(periodResult.get("demand_qty") != null ? periodResult.get("demand_qty").toString() : "0");
                    }

                    if (recordCount <= 0) {
                        // 跳到下一时间段
                        startDateLc = endDateLc.plusDays(1);
                        endDateLc = startDateLc.plusDays(periodDays);
                        continue;
                    }

                    // 采购数量 = 需求数量 / 转换比
                    demandQty = Math.round(demandQty / unitFactorP * 10000.0) / 10000.0;
                    // 采购日期 = 需求日期 - 提前期
                    String purchaseDateTmp = startDateLc.minusDays(leadTime).format(fmt);

                    // 判断有没有配套份额
                    String allocCheckSql = "SELECT COUNT(1) as cnt FROM pur_item_vendor_head a, pur_item_vendor b " +
                            "WHERE a.pur_org = b.pur_org AND a.inv_org = b.inv_org AND a.item_code = b.item_code " +
                            "AND a.pur_org = '" + purOrgCode + "' AND a.inv_org = '" + dApplyOrg + "' " +
                            "AND a.item_code = '" + dItemCode + "' AND b.is_allocation = 'Y'";
                    Map<String, Object> allocCheck = sqlService.getOne(allocCheckSql);
                    int allocCount = 0;
                    if (allocCheck != null) {
                        allocCount = Integer.parseInt(allocCheck.get("cnt") != null ? allocCheck.get("cnt").toString() : "0");
                    }

                    if (allocCount > 0) {
                        // 有配套份额，从配套份额中取数据
                        String vendorSql = "SELECT vendor_code, vendor_name, IFNULL(lead_time, 0) as lead_time, " +
                                "IFNULL(allocation_per, 0) / 100 as allot_rate, min_number " +
                                "FROM pur_item_vendor WHERE pur_org = '" + purOrgCode + "' " +
                                "AND inv_org = '" + dApplyOrg + "' AND item_code = '" + dItemCode + "' " +
                                "AND is_allocation = 'Y'";
                        List<Map<String, Object>> vendors = sqlService.getAll(vendorSql);

                        double leftQty = demandQty;
                        for (int j = 0; j < vendors.size(); j++) {
                            Map<String, Object> vendor = vendors.get(j);
                            String vVendorCode = vendor.get("vendor_code") != null ? vendor.get("vendor_code").toString() : "";
                            String vVendorName = vendor.get("vendor_name") != null ? vendor.get("vendor_name").toString() : "";
                            int vLeadTime = Integer.parseInt(vendor.get("lead_time") != null ? vendor.get("lead_time").toString() : "0");
                            double vAllotRate = Double.parseDouble(vendor.get("allot_rate") != null ? vendor.get("allot_rate").toString() : "0");

                            // 取价格（协议价）
                            double itemPrice = 0;
                            String priceSql = "SELECT d.unit_price FROM pur_agreement_detail d, pur_agreement_head h " +
                                    "WHERE d.uk_agreement_id = h.uk_agreement_id " +
                                    "AND h.effective_date <= '" + curDateStr + "' AND h.expiring_date >= '" + curDateStr + "' " +
                                    "AND h.bill_status = '审核通过' AND h.pur_org = '" + purOrgCode + "' " +
                                    "AND d.apply_org = '" + dApplyOrg + "' AND d.item_code = '" + dItemCode + "' " +
                                    "AND h.vendor_code = '" + vVendorCode + "' LIMIT 1";
                            Map<String, Object> priceResult = sqlService.getOne(priceSql);
                            if (priceResult != null) {
                                itemPrice = Double.parseDouble(priceResult.get("unit_price") != null ? priceResult.get("unit_price").toString() : "0");
                            }

                            // 取采购组及采购员
                            String groupId = "";
                            String groupName = "";
                            String buyerId = "";
                            String buyerCode = "";
                            String buyerName = "";

                            // 1. 供应商物料权限
                            String groupItemSql = "SELECT COUNT(1) as cnt FROM purgroup_item_detail " +
                                    "WHERE pur_org = '" + purOrgCode + "' AND inv_org = '" + dApplyOrg + "' " +
                                    "AND vendor_code = '" + vVendorCode + "' AND item_code = '" + dItemCode + "'";
                            Map<String, Object> groupItemResult = sqlService.getOne(groupItemSql);
                            int groupItemCount = groupItemResult != null ? Integer.parseInt(groupItemResult.get("cnt") != null ? groupItemResult.get("cnt").toString() : "0") : 0;

                            if (groupItemCount > 0) {
                                String giSql = "SELECT role_code, role_name FROM purgroup_item_detail " +
                                        "WHERE pur_org = '" + purOrgCode + "' AND inv_org = '" + dApplyOrg + "' " +
                                        "AND vendor_code = '" + vVendorCode + "' AND item_code = '" + dItemCode + "'";
                                Map<String, Object> giResult = sqlService.getOne(giSql);
                                if (giResult != null) {
                                    groupId = giResult.get("role_code") != null ? giResult.get("role_code").toString() : "";
                                    groupName = giResult.get("role_name") != null ? giResult.get("role_name").toString() : "";
                                }
                                String buSql = "SELECT user_id, user_code, user_man FROM pur_user_detail " +
                                        "WHERE pur_org = '" + purOrgCode + "' AND role_code = '" + groupId + "'";
                                Map<String, Object> buResult = sqlService.getOne(buSql);
                                if (buResult != null) {
                                    buyerId = buResult.get("user_id") != null ? buResult.get("user_id").toString() : "";
                                    buyerCode = buResult.get("user_code") != null ? buResult.get("user_code").toString() : "";
                                    buyerName = buResult.get("user_man") != null ? buResult.get("user_man").toString() : "";
                                }
                            } else {
                                // 2. 小组供应商权限
                                String gvSql = "SELECT COUNT(1) as cnt FROM pur_groupven_detail " +
                                        "WHERE pur_org = '" + purOrgCode + "' AND vendor_code = '" + vVendorCode + "' AND is_default = 'Y'";
                                Map<String, Object> gvResult = sqlService.getOne(gvSql);
                                int gvCount = gvResult != null ? Integer.parseInt(gvResult.get("cnt") != null ? gvResult.get("cnt").toString() : "0") : 0;

                                if (gvCount > 0) {
                                    String gvDetailSql = "SELECT role_code, role_name FROM pur_groupven_detail " +
                                            "WHERE pur_org = '" + purOrgCode + "' AND vendor_code = '" + vVendorCode + "' AND is_default = 'Y'";
                                    Map<String, Object> gvDetailResult = sqlService.getOne(gvDetailSql);
                                    if (gvDetailResult != null) {
                                        groupId = gvDetailResult.get("role_code") != null ? gvDetailResult.get("role_code").toString() : "";
                                        groupName = gvDetailResult.get("role_name") != null ? gvDetailResult.get("role_name").toString() : "";
                                    }
                                    String buSql2 = "SELECT user_id, user_code, user_man FROM pur_user_detail " +
                                            "WHERE pur_org = '" + purOrgCode + "' AND role_code = '" + groupId + "'";
                                    Map<String, Object> buResult2 = sqlService.getOne(buSql2);
                                    if (buResult2 != null) {
                                        buyerId = buResult2.get("user_id") != null ? buResult2.get("user_id").toString() : "";
                                        buyerCode = buResult2.get("user_code") != null ? buResult2.get("user_code").toString() : "";
                                        buyerName = buResult2.get("user_man") != null ? buResult2.get("user_man").toString() : "";
                                    }
                                } else {
                                    // 3. 物料定义的采购员
                                    String buyerItemSql = "SELECT buyer_code FROM epd_item_master " +
                                            "WHERE inv_org = '" + dApplyOrg + "' AND item_code = '" + dItemCode + "' AND buyer_code IS NOT NULL";
                                    Map<String, Object> buyerItemResult = sqlService.getOne(buyerItemSql);
                                    if (buyerItemResult != null) {
                                        buyerCode = buyerItemResult.get("buyer_code") != null ? buyerItemResult.get("buyer_code").toString() : "";
                                        String buSql3 = "SELECT user_id, user_man, role_code, role_name FROM pur_user_detail " +
                                                "WHERE pur_org = '" + purOrgCode + "' AND user_code = '" + buyerCode + "'";
                                        Map<String, Object> buResult3 = sqlService.getOne(buSql3);
                                        if (buResult3 != null) {
                                            buyerId = buResult3.get("user_id") != null ? buResult3.get("user_id").toString() : "";
                                            buyerName = buResult3.get("user_man") != null ? buResult3.get("user_man").toString() : "";
                                            groupId = buResult3.get("role_code") != null ? buResult3.get("role_code").toString() : "";
                                            groupName = buResult3.get("role_name") != null ? buResult3.get("role_name").toString() : "";
                                        }
                                    }
                                }
                            }

                            // 计算供应商分配数量
                            double vendorQty;
                            if (j == vendors.size() - 1) {
                                vendorQty = leftQty;
                            } else {
                                vendorQty = Math.round(demandQty * vAllotRate * 10000.0) / 10000.0;
                            }
                            leftQty = Math.round((leftQty - vendorQty) * 10000.0) / 10000.0;

                            // 处理配套中的提前期
                            String vPurchaseDate;
                            if (vLeadTime == 0) {
                                vPurchaseDate = purchaseDateTmp;
                            } else {
                                vPurchaseDate = startDateLc.minusDays(vLeadTime).format(fmt);
                            }

                            // 查询采购单位名称
                            String purUnitNameSql = "SELECT unit_name FROM epd_unit_measure WHERE unit_id = '" + purUnit + "'";
                            Map<String, Object> purUnitNameResult = sqlService.getOne(purUnitNameSql);
                            String purUnitName = purUnitNameResult != null && purUnitNameResult.get("unit_name") != null ? purUnitNameResult.get("unit_name").toString() : "";

                            // 检查是否已存在相同明细
                            String existSql = "SELECT COUNT(*) as cnt FROM pur_purchase_plan_detail " +
                                    "WHERE pur_org = '" + purOrgCode + "' AND uk_plan_id = '" + lotNo + "' " +
                                    "AND need_date = '" + startDateStr + "' AND item_code = '" + dItemCode + "' " +
                                    "AND IFNULL(project_no, ' ') = '" + dProjectNo + "' " +
                                    "AND IFNULL(work_order, ' ') = '" + dWorkOrder + "' " +
                                    "AND vendor_code = '" + vVendorCode + "'";
                            Map<String, Object> existResult = sqlService.getOne(existSql);
                            int existCount = existResult != null ? Integer.parseInt(existResult.get("cnt") != null ? existResult.get("cnt").toString() : "0") : 0;

                            if (existCount > 0) {
                                // 更新已有明细
                                insertsql.add("UPDATE pur_purchase_plan_detail SET non_prod_qty = " + vendorQty +
                                        " WHERE pur_org = '" + purOrgCode + "' AND item_code = '" + dItemCode + "' " +
                                        "AND IFNULL(project_no, ' ') = '" + dProjectNo + "' " +
                                        "AND IFNULL(work_order, ' ') = '" + dWorkOrder + "' " +
                                        "AND uk_plan_id = '" + lotNo + "' AND need_date = '" + startDateStr + "' " +
                                        "AND vendor_code = '" + vVendorCode + "'");
                            } else {
                                // 插入新明细
                                lotSeq++;
                                String detailInsert = "INSERT INTO pur_purchase_plan_detail(" +
                                        "s_uu_id, group_id, create_user_id, create_time, uk_plan_id, uk_line_no, up_bill, up_bill_line, " +
                                        "pur_org, apply_org, source_inv_org, lot_no, item_id, item_code, item_name, item_spec, item_model, " +
                                        "need_date, mrp_qty, non_prod_qty, reorder_qty, plan_qty, " +
                                        "buyer_id, buyer_code, buyer_man, vendor_id, vendor_code, vendor_name, " +
                                        "notes, type_code, pur_unit_id, pur_unit_name, change_rate, main_qty, main_unit_id, main_unit_name, " +
                                        "role_id, role_code, role_name, work_order, project_id, project_no, project_name, " +
                                        "guide_price, mrp_adjust, non_prod_adjust, reorder_adjust, pur_num, " +
                                        "is_pur, pur_date, pur_id, line_status, line_opinion, is_close" +
                                        ") VALUES (" +
                                        "'', '00', '" + personId + "', NOW(), '" + lotNo + "', " + lotSeq + ", " +
                                        "'', '', " +
                                        "'" + purOrgCode + "', '" + dApplyOrg + "', '" + dApplyOrg + "', '', '', " +
                                        "'" + dItemCode + "', '" + dItemName + "', '" + dItemSpec + "', '" + dItemModel + "', " +
                                        "'" + startDateStr + "', 0, " + vendorQty + ", 0, " + vendorQty + ", " +
                                        "'" + personId + "', '" + personCode + "', '" + personName + "', '', '" + vVendorCode + "', '" + vVendorName + "', " +
                                        "'', '" + dTypeCode + "', " +
                                        "'" + purUnit + "', '" + purUnitName + "', 1, " + vendorQty + ", " +
                                        "'" + purUnit + "', '" + purUnitName + "', " +
                                        "'' , '" + groupId + "', '" + groupName + "', " +
                                        "'" + dWorkOrder + "', '', '" + dProjectNo + "', '', " +
                                        itemPrice + ", 0, 0, 0, 0, " +
                                        "'N', NULL, NULL, '未提交', '', 'N')";
                                insertsql.add(detailInsert);
                            }
                        }
                    }

                    // 取下一段日期
                    startDateLc = endDateLc.plusDays(1);

                    // 根据计划方式取下一个开始日期
                    String nextStartSql;
                    if ("N".equals(planMethod)) {
                        nextStartSql = "SELECT MIN(end_date) as next_start FROM pur_apply_detail " +
                                "WHERE pur_org = '" + purOrgCode + "' AND item_code = '" + dItemCode + "' " +
                                "AND IFNULL(project_no, ' ') = '" + dProjectNo + "' " +
                                "AND line_status = '审批通过' AND is_geneplan = '2' " +
                                "AND end_date >= '" + startDateLc.format(fmt) + "' " +
                                "AND end_date <= '" + dMaxDate + "'";
                    } else {
                        nextStartSql = "SELECT MIN(a.end_date) as next_start FROM pur_apply_detail a, epd_item_master b " +
                                "WHERE a.pur_org = '" + purOrgCode + "' AND a.apply_org = b.inv_org " +
                                "AND a.item_code = '" + dItemCode + "' " +
                                "AND IFNULL(a.project_no, ' ') = '" + dProjectNo + "' " +
                                "AND a.item_code = b.item_code AND a.line_status = '审批通过' AND a.is_geneplan = '2' " +
                                "AND b.planner_code = '" + personCode + "' " +
                                "AND a.end_date >= '" + startDateLc.format(fmt) + "' " +
                                "AND a.end_date <= '" + dMaxDate + "'";
                    }
                    Map<String, Object> nextStartResult = sqlService.getOne(nextStartSql);
                    if (nextStartResult != null && nextStartResult.get("next_start") != null) {
                        startDateLc = LocalDate.parse(nextStartResult.get("next_start").toString(), fmt);
                    } else {
                        break;
                    }
                    endDateLc = startDateLc.plusDays(periodDays);
                }
            }
        }

        // 执行SQL
        if (!insertsql.isEmpty()) {
            boolean flag = sqlService.insertList(insertsql);
            if (flag) {
                return R.ok("采购计划生成成功");
            } else {
                return R.error("采购计划生成失败");
            }
        }

        if (errorMsg.length() > 0) {
            return R.error(errorMsg.toString());
        }
        return R.ok("采购计划生成成功");
    }

    @Override
    public R enable(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead purhead : params) {
            insertsql.add("update pur_purchase_plan_head set is_active = 'Y' where uu_id = '" + purhead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_detail set is_active = 'Y' where uk_plan_uuid = '" + purhead.getUu_id() + "' and plan_qty>pur_num");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R disabl(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead purhead : params) {
            insertsql.add("update pur_purchase_plan_head set is_active = 'N' where uu_id = '" + purhead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_detail set is_active = 'N' where uk_plan_uuid = '" + purhead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }

    @Override
    public R detailenable(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead purhead : params) {
            insertsql.add("update pur_purchase_plan_detail set is_active = 'Y' where uu_id = '" + purhead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_head set is_active = 'Y' where uu_id = '" + purhead.getUk_plan_uuid()+ "' and  exists( select 1 from pur_purchase_plan_detail where is_active = 'Y' and uk_plan_uuid = '" + purhead.getUk_plan_uuid() + "' and pur_purchase_plan_detail.uk_plan_uuid=pur_purchase_plan_head.uu_id)");
        }

        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R detaildisabl(List<PurPlanHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurPlanHead purhead : params) {
            insertsql.add("update pur_purchase_plan_detail set is_active = 'N' where uu_id = '" + purhead.getUu_id() + "'");
            insertsql.add("update pur_purchase_plan_head set is_active = 'N' where uu_id = '" + purhead.getUk_plan_uuid()+ "' and not exists( select 1 from pur_purchase_plan_detail where is_active = 'Y' and uk_plan_uuid = '" + purhead.getUk_plan_uuid() + "' and pur_purchase_plan_detail.uk_plan_uuid=pur_purchase_plan_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }
}
