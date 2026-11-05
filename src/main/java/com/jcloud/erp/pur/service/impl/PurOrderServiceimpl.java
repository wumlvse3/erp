package com.jcloud.erp.pur.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.pur.entity.PurOrderHead;
import com.jcloud.erp.pur.mapper.PurOrderHeadMapper;
import com.jcloud.erp.pur.service.PurOrderHeadService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class PurOrderServiceimpl extends ServiceImpl<PurOrderHeadMapper, PurOrderHead> implements PurOrderHeadService {
    @Resource
    private SqlService sqlService;

    @Override
    public R addObj(List<PurOrderHead> params) {
        StringBuilder errorMsg = new StringBuilder();
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            Long thecount=sqlService.getCount("select 1 from pur_order_detail where uk_order_uuid = '" + PurOrderHead.getUu_id() + "' and (tax_code is null or tax_code='' or unit_price is null or unit_price=0)");
            if(thecount>0){
                errorMsg.append("订单号：").append(PurOrderHead.getUk_order_no()).append("存在明细税码或价格为空，无法提交！").append("\n");
                continue;
            }
            insertsql.add("update pur_order_head set bill_status = '已提交',base_tax_money = (SELECT IFNULL(SUM(IFNULL(amt, 0)), 0) FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "')  where uu_id = '" + PurOrderHead.getUu_id() + "'  and (bill_status='未提交' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_order_head.pur_org and pur_check='Y' )) ");
            insertsql.add("update pur_order_detail set line_status = '已提交' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "'");
        }
        if (errorMsg.length() > 0) {
            return R.error("提交失败，以下订单存在问题：\n" + errorMsg.toString());
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("提交成功");
        }else{
            return R.error("提交失败");
        }
    }

    @Override
    public R auditlist(List<PurOrderHead> params) {
        // 审核前校验：检查采购数量是否超过采购计划数量和采购协议数量
        // 跨所有头订单统一按 s_uu_id 汇总，不同头订单的明细也可能关联同一个来源表明细
        StringBuilder errorMsg = new StringBuilder();
        java.util.Map<String, Double> submitQtyMap = new java.util.HashMap<>();
        java.util.Map<String, List<String>> orderInfoMap = new java.util.HashMap<>();

        for (PurOrderHead PurOrderHead : params) {
            // 查询本次提交的订单明细（未审核通过的）
            String detailSql = "SELECT uu_id, s_uu_id, pur_qty, uk_order_no, uk_line_no,concat(up_bill,'—',up_bill_line) as  up_bill FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND line_status='已提交' ";
            List<Map<String, Object>> details = sqlService.getAll(detailSql);

            if (details != null && !details.isEmpty()) {
                for (Map<String, Object> detail : details) {
                    String sUuId = detail.get("up_bill") != null ? detail.get("up_bill").toString() : "";
                    // s_uu_id 为空时跳过校验，不报错
                    if (!"".equals(sUuId)) {
                        Double purQty = detail.get("pur_qty") != null ? Double.parseDouble(detail.get("pur_qty").toString()) : 0.0;
                        submitQtyMap.put(sUuId, submitQtyMap.getOrDefault(sUuId, 0.0) + purQty);

                        String orderNo = detail.get("uk_order_no") != null ? detail.get("uk_order_no").toString() : "";
                        String lineNo = detail.get("uk_line_no") != null ? detail.get("uk_line_no").toString() : "";
                        String detailInfo = "订单号：" + orderNo + "，行号：" + lineNo;

                        if (!orderInfoMap.containsKey(sUuId)) {
                            orderInfoMap.put(sUuId, new ArrayList<>());
                        }
                        orderInfoMap.get(sUuId).add(detailInfo);
                    }
                }
            }
        }

        // 对每个来源表明细进行校验（跨所有头订单汇总后统一校验）
        for (java.util.Map.Entry<String, Double> entry : submitQtyMap.entrySet()) {
            String sUuId = entry.getKey();
            Double submitQty = entry.getValue();
            // 查询已审核通过的 pur_qty 总和
            String approvedSql = "SELECT IFNULL(SUM(IFNULL(pur_qty, 0)), 0) as approved_qty FROM pur_order_detail WHERE concat(up_bill,'—',up_bill_line) = '" + sUuId + "' AND line_status = '审核通过'";
            Double approvedQty = 0.0;
            Map<String, Object> approvedResult = sqlService.getOne(approvedSql);
            if (Objects.nonNull(approvedResult)) {
                approvedQty = approvedResult.get("approved_qty") != null ? Double.parseDouble(approvedResult.get("approved_qty").toString()) : 0.0;

            }

            // 查询采购计划表的 plan_qty
            Double planQty = 0.0;
            String planSql = "SELECT IFNULL(plan_qty, 0) as plan_qty FROM pur_purchase_plan_detail WHERE concat(uk_plan_id,'—',uk_line_no) = '" + sUuId + "'";
            Map<String, Object> planResult = sqlService.getOne(planSql);
            if (Objects.nonNull(planResult)) {
                planQty = planResult.get("plan_qty") != null ? Double.parseDouble(planResult.get("plan_qty").toString()) : 0.0;
            }

            // 查询采购协议明细表的 agreement_qty
            Double agreementQty = 0.0;
            String agreementSql = "SELECT IFNULL(agreement_qty, 0) as agreement_qty FROM pur_agreement_detail WHERE concat(uk_agreement_id,'—',uk_line_no) = '" + sUuId + "'";
            Map<String, Object> agreementResult = sqlService.getOne(agreementSql);
            if (Objects.nonNull(agreementResult)) {
                agreementQty = agreementResult.get("agreement_qty") != null ? Double.parseDouble(agreementResult.get("agreement_qty").toString()) : 0.0;
            }

            // 判断是否超过限制
            Double totalQty = submitQty + approvedQty;
            List<String> orderInfoList = orderInfoMap.get(sUuId);
            String orderInfoStr = String.join("；", orderInfoList);

            if (totalQty > planQty&&planQty>0) {
                errorMsg.append(orderInfoStr).append("，采购数量超过采购计划数量，计划数量：").append(planQty).append("，已审核数量：").append(approvedQty).append("，本次提交数量：").append(submitQty).append("\n");
            }

            if (totalQty > agreementQty&&agreementQty>0) {
                errorMsg.append(orderInfoStr).append("，采购数量超过采购协议数量，协议数量：").append(agreementQty).append("，已审核数量：").append(approvedQty).append("，本次提交数量：").append(submitQty).append("\n");
            }
        }

        if (errorMsg.length() > 0) {
            return R.error("审核失败，以下明细数量超限：\n" + errorMsg.toString());
        }

        // 原有的审核逻辑
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurOrderHead PurOrderHead : params) {
            String approver_opinion=PurOrderHead.getApprover_opinion();
            if(approver_opinion==null||"null".equals(approver_opinion)){
                PurOrderHead.setApprover_opinion("");
            }

            insertsql.add("update pur_order_head set bill_status = '审核通过',approver_opinion='"+PurOrderHead.getApprover_opinion()+"',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where uu_id = '" + PurOrderHead.getUu_id() + "'   and (bill_status='已提交' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_order_head.pur_org and pur_check='Y' )) ");
            insertsql.add("update pur_order_detail set line_status = '审核通过' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "' ");
     }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("审核成功");
        }else{
            return R.error("审核失败");
        }
    }

    @Override
    public R unaddObj(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            insertsql.add("update pur_order_head set bill_status = '未提交' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_detail set line_status = '未提交' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消提交成功");
        }else{
            return R.error("取消提交失败");
        }
    }

    @Override
    public R passlist(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurOrderHead PurOrderHead : params) {
            String approver_opinion=PurOrderHead.getApprover_opinion();
            if(approver_opinion==null||"null".equals(approver_opinion)){
                PurOrderHead.setApprover_opinion("");
            }
            insertsql.add("update pur_order_head set bill_status = '已提交' where uu_id = '" + PurOrderHead.getUu_id() + "'  and (bill_status='审核通过' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_order_head.pur_org and pur_check='Y' )) ");
            insertsql.add("update pur_order_detail set line_status = '已提交' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "' ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消审核成功");
        }else{
            return R.error("取消审核失败");
        }
    }

    @Override
    public R bhpasslist(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurOrderHead PurOrderHead : params) {
            String approver_opinion=PurOrderHead.getApprover_opinion();
            if(approver_opinion==null||"null".equals(approver_opinion)){
                PurOrderHead.setApprover_opinion("");
            }
            insertsql.add("update pur_order_head set bill_status = '驳回',approver_opinion='"+PurOrderHead.getApprover_opinion()+"',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where uu_id = '" + PurOrderHead.getUu_id() + "' and bill_status='审核通过'  ");
            insertsql.add("update pur_order_detail set line_status = '驳回' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "' ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("驳回成功");
        }else{
            return R.error("驳回失败");
        }
    }

    @Override
    public R auditdetaillist(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurOrderHead PurOrderHead : params) {
            String line_opinion=PurOrderHead.getLine_opinion();
            if(line_opinion==null||"null".equals(line_opinion)){
                PurOrderHead.setLine_opinion("");
            }
            insertsql.add("update pur_order_detail set line_status = '审核通过',line_opinion='"+PurOrderHead.getLine_opinion()+"' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_head set bill_status = '审核通过',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where not exists (select 1 from pur_order_detail where uk_order_uuid = '" + PurOrderHead.getUk_order_uuid() + "' and line_status<>'审核通过' and pur_order_detail.uk_order_uuid=pur_order_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("审核成功");
        }else{
            return R.error("审核失败");
        }
    }

    @Override
    public R passdetaillist(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurOrderHead PurOrderHead : params) {
            String line_opinion=PurOrderHead.getLine_opinion();
            if(line_opinion==null||"null".equals(line_opinion)){
                PurOrderHead.setLine_opinion("");
            }
            insertsql.add("update pur_order_detail set line_status = '驳回',line_opinion='' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_head set bill_status = '驳回',approver_user_man='',approver_user_id='',approver_user_code='',approver_time=null where uu_id='" + PurOrderHead.getUk_order_uuid()+ "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("驳回成功");
        }else{
            return R.error("驳回失败");
        }
    }
    @Override
    public R detaillist(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            String sql = "select s_uu_id from pur_order_detail where uu_id = '" + PurOrderHead.getUu_id()  + "'";
            Map<String, Object> theuu_id = sqlService.getOne(sql);
            if(theuu_id!=null&&theuu_id.get("s_uu_id")!=null&&!"".equals(theuu_id.get("s_uu_id"))){
                insertsql.add("update pur_purchase_plan_detail set is_active='Y',is_pur='Y',pur_num=(select ifnull(sum(ifnull(pur_qty,0)),0) from pur_order_detail where s_uu_id='"+theuu_id.get("s_uu_id")+"' and source_price='采购计划'  and uu_id<>'"+PurOrderHead.getUu_id() +"')  where  uu_id='"+theuu_id.get("s_uu_id")+"'");
                insertsql.add("update pur_purchase_plan_detail set is_pur='N' where uu_id='"+theuu_id.get("s_uu_id")+"' and ifnull(pur_num,0)<=0");
                insertsql.add("update pur_purchase_plan_head set is_active='Y' where  uu_id in(select uk_plan_uuid from pur_purchase_plan_detail where uu_id='"+theuu_id.get("s_uu_id")+"')");

                insertsql.add("update pur_agreement_detail set is_active='Y',is_ord='Y',ord_num=(select ifnull(sum(ifnull(pur_qty,0)),0) from pur_order_detail where s_uu_id='"+theuu_id.get("s_uu_id")+"' and source_price='协议'   and uu_id<>'"+PurOrderHead.getUu_id() +"')  where  uu_id='"+theuu_id.get("s_uu_id")+"'");
                insertsql.add("update pur_agreement_detail set is_ord='N'  where uu_id='"+theuu_id.get("s_uu_id")+"' and ifnull(ord_num,0)<=0");
                insertsql.add("update pur_agreement_head set is_active='Y' where  uu_id in(select uk_agreement_uuid from pur_agreement_detail where uu_id='"+theuu_id.get("s_uu_id")+"')");

            }
            insertsql.add("delete from pur_order_detail  where uu_id = '" + PurOrderHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("删除成功");
        }else{
            return R.error("删除失败");
        }
    }

    @Override
    public R createHead(List<PurOrderHead> params) {
        // 校验：检查是否有积累到货数量大于等于采购订单数量的明细
        StringBuilder errorMsg = new StringBuilder();
        for (PurOrderHead PurOrderHead : params) {
            Long headActive = sqlService.getCount("select 1 from pur_order_head where uu_id = '" + PurOrderHead.getUu_id() + "' and is_active='Y'");
            if (headActive == null || headActive == 0) {
                errorMsg.append("订单：").append(PurOrderHead.getUu_id()).append("不存在或已作废（is_active<>'Y'）\n");
                continue;
            }
            String checkSql = "SELECT uk_order_no, uk_line_no FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND (IFNULL(pur_qty, 0) - IFNULL(ship_qty, 0)) <= 0 and is_active='Y'";
            List<Map<String, Object>> checkResults = sqlService.getAll(checkSql);
            if (checkResults != null && !checkResults.isEmpty()) {
                for (Map<String, Object> row : checkResults) {
                    String orderNo = row.get("uk_order_no") != null ? row.get("uk_order_no").toString() : "";
                    String lineNo = row.get("uk_line_no") != null ? row.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，积累到货数量大于等于采购订单数量\n");
                }
            }
        }
        if (errorMsg.length() > 0) {
            return R.error("不能生成采购到货单，以下明细行存在问题：\n" + errorMsg.toString());
        }

        List<String> insertsql = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tehdate = currentDate.format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String create_time = sdf.format(date);
        UserEntity user = ShiroUtils.getUserEntity();
        String bilsql="SELECT CASE WHEN MAX(uk_bill_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0001') ELSE LPAD(CAST(MAX(uk_bill_no) AS UNSIGNED) + 1, 12, '0') END AS uk_bill_no FROM pur_ship_head WHERE LEFT(uk_bill_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> uk_bill_no=sqlService.getOne(bilsql);
        String bill_no=uk_bill_no.get("uk_bill_no").toString();
        String prefix = bill_no.substring(0, bill_no.length() - 4);
        String lastFourStr = bill_no.substring(bill_no.length() - 4);
        int i=0;
        for (PurOrderHead PurOrderHead : params) {
            int  serialNum = Integer.parseInt(lastFourStr)+i;
            i++;
            String newLastFour = String.format("%04d", serialNum);
            bill_no=prefix + newLastFour;
            String headinsert="insert into pur_ship_head(pur_org,uk_bill_no,type_code,bill_date,transport_type,up_bill,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,is_ship_ret,is_stock_return,is_outsource,back_reason,bill_status,create_user_id,create_time,modify_user_id,modify_time,notes,approver_user_id,approver_user_code,approver_user_man,approver_time,approver_opinion,uk_order_no,s_uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc) select pur_org,'"+bill_no+"',type_code,'"+tehdate+"',transport_type,uk_order_no,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,'Y','N','N','','未提交','"+user.getUserId()+"','"+create_time+"','',null,'','','','',null,'',uk_order_no,uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc from pur_order_head where uu_id='"+PurOrderHead.getUu_id()+"' and is_active='Y'";
            insertsql.add(headinsert);

            // 查询订单明细，重新计算数量和金额
            String orderDetailSql = "SELECT * FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            List<Map<String, Object>> orderDetails = sqlService.getAll(orderDetailSql);

            int detailSeq = 0;
            for (Map<String, Object> orderDetail : orderDetails) {
                double purQty = orderDetail.get("pur_qty") != null ? Double.parseDouble(orderDetail.get("pur_qty").toString()) : 0;
                double shipQty = orderDetail.get("ship_qty") != null ? Double.parseDouble(orderDetail.get("ship_qty").toString()) : 0;
                double returnQty = orderDetail.get("return_qty") != null ? Double.parseDouble(orderDetail.get("return_qty").toString()) : 0;
                double returnStockQty = orderDetail.get("return_stock_qty") != null ? Double.parseDouble(orderDetail.get("return_stock_qty").toString()) : 0;
                double arriveQty = purQty - shipQty + returnQty + returnStockQty;
                double shipQtyNew = purQty - shipQty;

                if (shipQtyNew <= 0) {
                    continue;
                }

                // 获取单价和税率
                double unitPrice = orderDetail.get("unit_price") != null ? Double.parseDouble(orderDetail.get("unit_price").toString()) : 0;
                String taxCode = orderDetail.get("tax_code") != null ? orderDetail.get("tax_code").toString() : "";
                double taxRatio = 0;

                // 查询税率信息
                String taxSql = "SELECT tax_ratio FROM epd_tax WHERE tax_code = '" + taxCode + "' LIMIT 1";
                Map<String, Object> taxInfo = sqlService.getOne(taxSql);
                if (taxInfo != null) {
                    taxRatio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
                }

                // 计算金额
                double amt = Math.round(shipQtyNew * unitPrice * 100) / 100.0;
                double taxAmount = Math.round(amt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
                double noTaxPrice = Math.round(unitPrice / (1 + taxRatio / 100) * 100) / 100.0;
                double noTaxAmount = Math.round(noTaxPrice * shipQtyNew * 100) / 100.0;

                // 计算主数量和价税合计（本位币）
                double changeRate = orderDetail.get("change_rate") != null ? Double.parseDouble(orderDetail.get("change_rate").toString()) : 1;
                double exchangeRate = orderDetail.get("exchange_rate") != null ? Double.parseDouble(orderDetail.get("exchange_rate").toString()) : 1;
                double mainQty = Math.round(shipQtyNew * changeRate * 100) / 100.0;
                double baseTaxPrice = Math.round(unitPrice * exchangeRate * 100) / 100.0;
                double baseTaxMoney = Math.round(baseTaxPrice * shipQtyNew * 100) / 100.0;

                detailSeq++;
                String detailinsert = "insert into pur_ship_detail(pur_org,uk_bill_no,apply_org,up_bill,up_bill_line,item_code,item_name,item_spec,item_model,is_quality,request_date,ship_date,arrive_qty,ship_qty,warehouse_code,rack,pur_unit_id,main_qty,main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,amt,tax_amount,no_tax_price,no_tax_amount,base_currency_code,base_tax_price,base_tax_money,gift,is_quality_bill,is_stock,work_order,project_no,batch_no,acc_tolerance,manufacturer,prod_date,back_reason,inspection_qty,store_qty,unquality_qty,return_stock_qty,return_qty,quality_qty,line_status,create_user_id,create_time,modify_user_id,modify_time,notes,pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,is_close,s_uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id) select pur_org,'"+bill_no+"',apply_org,uk_order_no,uk_line_no,item_code,item_name,item_spec,item_model,'N','"+tehdate+"',ship_date,"+arriveQty+","+shipQtyNew+",warehouse_code,'',pur_unit_id,"+mainQty+",main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,"+amt+","+taxAmount+","+noTaxPrice+","+noTaxAmount+",base_currency_code,"+baseTaxPrice+","+baseTaxMoney+",gift,'N','N',work_order,project_no,batch_no,0,manufacturer,prod_date,'',0,store_qty,0,0,0,quality_qty,'未提交','"+user.getUserId()+"','"+create_time+"','',null,'',pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,'',uu_id,group_id,flow_id,is_deleted,'"+detailSeq+"',item_id from pur_order_detail where uu_id='"+orderDetail.get("uu_id")+"' and is_active='Y' ";
                insertsql.add(detailinsert);
            }

            // 累加到货数量到 ship_qty（仅处理有效明细，到货不更新 is_active，退库完成后才置 N）
            String updateShipQty = "UPDATE pur_order_detail SET ship_qty = IFNULL(ship_qty, 0) + (IFNULL(pur_qty, 0) - IFNULL(ship_qty, 0)) WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            insertsql.add(updateShipQty);
            String updatesql="update pur_ship_detail set uk_bill_uuid=(select uu_id from pur_ship_head where pur_ship_detail.uk_bill_no=pur_ship_head.uk_bill_no) where  uk_bill_uuid is null";
            insertsql.add(updatesql);
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("采购到货单生成成功");
        }else{
            return R.error("采购到货单生成失败");
        }
    }

    @Override
    public R createDetail(List<PurOrderHead> params) {
        // 校验：检查是否有积累到货数量大于等于采购订单数量的明细
        StringBuilder errorMsg = new StringBuilder();
        for (PurOrderHead PurOrderHead : params) {
            double createQty = PurOrderHead.getCreate_qty() != null ? Double.parseDouble(PurOrderHead.getCreate_qty()) : 0;
            String checkSql = "SELECT uk_order_no, uk_line_no, IFNULL(pur_qty, 0) as pur_qty, IFNULL(ship_qty, 0) as ship_qty FROM pur_order_detail WHERE uu_id = '" + PurOrderHead.getUu_id() + "' and is_active='Y'";
            Map<String, Object> checkResult = sqlService.getOne(checkSql);
            if (checkResult != null) {
                double purQty = checkResult.get("pur_qty") != null ? Double.parseDouble(checkResult.get("pur_qty").toString()) : 0;
                double shipQty = checkResult.get("ship_qty") != null ? Double.parseDouble(checkResult.get("ship_qty").toString()) : 0;
                double remainQty = purQty - shipQty;
                
                if (remainQty < 0) {
                    String orderNo = checkResult.get("uk_order_no") != null ? checkResult.get("uk_order_no").toString() : "";
                    String lineNo = checkResult.get("uk_line_no") != null ? checkResult.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，积累到货数量大于采购订单数量\n");
                } else if (createQty > remainQty) {
                    String orderNo = checkResult.get("uk_order_no") != null ? checkResult.get("uk_order_no").toString() : "";
                    String lineNo = checkResult.get("uk_line_no") != null ? checkResult.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，到货数量（").append(createQty).append("）超过剩余可到货数量（").append(remainQty).append("）\n");
                }
            } else {
                errorMsg.append("明细行：").append(PurOrderHead.getUu_id()).append("，不存在或已作废（is_active<>'Y'），不能生成到货单\n");
            }
        }
        if (errorMsg.length() > 0) {
            return R.error("不能生成采购到货单，以下明细行存在问题：\n" + errorMsg.toString());
        }

        List<String> insertsql = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tehdate = currentDate.format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String create_time = sdf.format(date);
        UserEntity user = ShiroUtils.getUserEntity();
        String bilsql="SELECT CASE WHEN MAX(uk_bill_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0001') ELSE LPAD(CAST(MAX(uk_bill_no) AS UNSIGNED) + 1, 12, '0') END AS uk_bill_no FROM pur_ship_head WHERE LEFT(uk_bill_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> uk_bill_no=sqlService.getOne(bilsql);
        String bill_no=uk_bill_no.get("uk_bill_no").toString();
        int i=0;
        for (PurOrderHead PurOrderHead : params) {
            if(i==0){
                String headinsert="insert into pur_ship_head(pur_org,uk_bill_no,type_code,bill_date,transport_type,up_bill,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,is_ship_ret,is_stock_return,is_outsource,back_reason,bill_status,create_user_id,create_time,modify_user_id,modify_time,notes,approver_user_id,approver_user_code,approver_user_man,approver_time,approver_opinion,uk_order_no,s_uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc) select pur_org,'"+bill_no+"',type_code,'"+tehdate+"',transport_type,uk_order_no,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,'Y','N','N','','未提交','"+user.getUserId()+"','"+create_time+"','',null,'','','','',null,'',uk_order_no,uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc from pur_order_head where pur_order_head.is_active='Y' and exists(select 1 from pur_order_detail where pur_order_detail.uk_order_uuid=pur_order_head.uu_id and pur_order_detail.uu_id='"+PurOrderHead.getUu_id()+"' and pur_order_detail.is_active='Y')";
                insertsql.add(headinsert);
            }
            
            // 从页面获取 create_qty
            double createQty = PurOrderHead.getCreate_qty() != null ? Double.parseDouble(PurOrderHead.getCreate_qty()) : 0;
            
            // 查询订单明细，重新计算数量和金额
            String orderDetailSql = "SELECT * FROM pur_order_detail WHERE uu_id = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            Map<String, Object> orderDetail = sqlService.getOne(orderDetailSql);
            
            double purQty = orderDetail.get("pur_qty") != null ? Double.parseDouble(orderDetail.get("pur_qty").toString()) : 0;
            double shipQty = orderDetail.get("ship_qty") != null ? Double.parseDouble(orderDetail.get("ship_qty").toString()) : 0;
            double returnQty = orderDetail.get("return_qty") != null ? Double.parseDouble(orderDetail.get("return_qty").toString()) : 0;
            double returnStockQty = orderDetail.get("return_stock_qty") != null ? Double.parseDouble(orderDetail.get("return_stock_qty").toString()) : 0;
            double arriveQty = createQty + returnQty + returnStockQty;
            
            // 获取单价和税率
            double unitPrice = orderDetail.get("unit_price") != null ? Double.parseDouble(orderDetail.get("unit_price").toString()) : 0;
            String taxCode = orderDetail.get("tax_code") != null ? orderDetail.get("tax_code").toString() : "";
            double taxRatio = 0;
            
            // 查询税率信息
            String taxSql = "SELECT tax_ratio FROM epd_tax WHERE tax_code = '" + taxCode + "' LIMIT 1";
            Map<String, Object> taxInfo = sqlService.getOne(taxSql);
            if (taxInfo != null) {
                taxRatio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
            }
            
            // 计算金额
            double amt = Math.round(createQty * unitPrice * 100) / 100.0;
            double taxAmount = Math.round(amt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxPrice = Math.round(unitPrice / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxAmount = Math.round(noTaxPrice * createQty * 100) / 100.0;
            
            // 计算主数量和价税合计（本位币）
            double changeRate = orderDetail.get("change_rate") != null ? Double.parseDouble(orderDetail.get("change_rate").toString()) : 1;
            double exchangeRate = orderDetail.get("exchange_rate") != null ? Double.parseDouble(orderDetail.get("exchange_rate").toString()) : 1;
            double mainQty = Math.round(createQty * changeRate * 100) / 100.0;
            double baseTaxPrice = Math.round(unitPrice * exchangeRate * 100) / 100.0;
            double baseTaxMoney = Math.round(baseTaxPrice * createQty * 100) / 100.0;
            
            String detailinsert="insert into pur_ship_detail(pur_org,uk_bill_no,apply_org,up_bill,up_bill_line,item_code,item_name,item_spec,item_model,is_quality,request_date,ship_date,arrive_qty,ship_qty,warehouse_code,rack,pur_unit_id,main_qty,main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,amt,tax_amount,no_tax_price,no_tax_amount,base_currency_code,base_tax_price,base_tax_money,gift,is_quality_bill,is_stock,work_order,project_no,batch_no,acc_tolerance,manufacturer,prod_date,back_reason,inspection_qty,store_qty,unquality_qty,return_stock_qty,return_qty,quality_qty,line_status,create_user_id,create_time,modify_user_id,modify_time,notes,pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,is_close,s_uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id) select pur_org,'"+bill_no+"',apply_org,uk_order_no,uk_line_no,item_code,item_name,item_spec,item_model,'N','"+tehdate+"',ship_date,"+arriveQty+","+createQty+",warehouse_code,'',pur_unit_id,"+mainQty+",main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,"+amt+","+taxAmount+","+noTaxPrice+","+noTaxAmount+",base_currency_code,"+baseTaxPrice+","+baseTaxMoney+",gift,'N','N',work_order,project_no,batch_no,0,manufacturer,prod_date,'',0,store_qty,0,0,0,quality_qty,'未提交','"+user.getUserId()+"','"+create_time+"','',null,'',pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,'',uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id from pur_order_detail where uu_id='"+PurOrderHead.getUu_id()+"' and is_active='Y' ";
            insertsql.add(detailinsert);
            
            // 累加到货数量到 ship_qty（仅处理有效明细，到货不更新 is_active，退库完成后才置 N）
            String updateShipQty = "UPDATE pur_order_detail SET ship_qty = IFNULL(ship_qty, 0) + " + createQty + " WHERE uu_id = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            insertsql.add(updateShipQty);
            
            String updatesql="update pur_ship_detail set uk_bill_uuid=(select uu_id from pur_ship_head where pur_ship_detail.uk_bill_no=pur_ship_head.uk_bill_no) where  uk_bill_uuid is null";
            insertsql.add(updatesql);
            i++;
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("采购到货单生成成功");
        }else{
            return R.error("采购到货单生成失败");
        }
    }

    @Override
    public R createDrHead(List<PurOrderHead> params) {
        StringBuilder errorMsg = new StringBuilder();
        for (PurOrderHead PurOrderHead : params) {
            Long headActive = sqlService.getCount("select 1 from pur_order_head where uu_id = '" + PurOrderHead.getUu_id() + "' and is_active='Y'");
            if (headActive == null || headActive == 0) {
                errorMsg.append("订单：").append(PurOrderHead.getUu_id()).append("不存在或已作废（is_active<>'Y'）\n");
                continue;
            }
            String checkSql = "SELECT uk_order_no, uk_line_no FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND (IFNULL(store_qty, 0) - IFNULL(return_stock_qty, 0)) <= 0 and is_active='Y'";
            List<Map<String, Object>> checkResults = sqlService.getAll(checkSql);
            if (checkResults != null && !checkResults.isEmpty()) {
                for (Map<String, Object> row : checkResults) {
                    String orderNo = row.get("uk_order_no") != null ? row.get("uk_order_no").toString() : "";
                    String lineNo = row.get("uk_line_no") != null ? row.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，可退库数量不足\n");
                }
            }
        }
        if (errorMsg.length() > 0) {
            return R.error("不能生成退库单，以下明细行存在问题：\n" + errorMsg.toString());
        }

        List<String> insertsql = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tehdate = currentDate.format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String create_time = sdf.format(date);
        UserEntity user = ShiroUtils.getUserEntity();
        String bilsql="SELECT CASE WHEN MAX(uk_bill_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0001') ELSE LPAD(CAST(MAX(uk_bill_no) AS UNSIGNED) + 1, 12, '0') END AS uk_bill_no FROM pur_ship_head WHERE LEFT(uk_bill_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> uk_bill_no=sqlService.getOne(bilsql);
        String bill_no=uk_bill_no.get("uk_bill_no").toString();
        String prefix = bill_no.substring(0, bill_no.length() - 4);
        String lastFourStr = bill_no.substring(bill_no.length() - 4);
        int i=0;
        for (PurOrderHead PurOrderHead : params) {
            int  serialNum = Integer.parseInt(lastFourStr)+i;
            i++;
            String newLastFour = String.format("%04d", serialNum);
            bill_no=prefix + newLastFour;
            String headinsert="insert into pur_ship_head(pur_org,uk_bill_no,type_code,bill_date,transport_type,up_bill,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,is_ship_ret,is_stock_return,is_outsource,back_reason,bill_status,create_user_id,create_time,modify_user_id,modify_time,notes,approver_user_id,approver_user_code,approver_user_man,approver_time,approver_opinion,uk_order_no,s_uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc) select pur_org,'"+bill_no+"',type_code,'"+tehdate+"',transport_type,uk_order_no,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,'N','Y','N','','未提交','"+user.getUserId()+"','"+create_time+"','',null,'','','','',null,'',uk_order_no,uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc from pur_order_head where uu_id='"+PurOrderHead.getUu_id()+"' and is_active='Y'";
            insertsql.add(headinsert);

            // 查询订单明细，重新计算金额
            String orderDetailSql = "SELECT * FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            List<Map<String, Object>> orderDetails = sqlService.getAll(orderDetailSql);

            int detailSeq = 0;
            for (Map<String, Object> orderDetail : orderDetails) {
                double shipQty = orderDetail.get("store_qty") != null ? Double.parseDouble(orderDetail.get("store_qty").toString()) : 0;
                double returnStockQty = orderDetail.get("return_stock_qty") != null ? Double.parseDouble(orderDetail.get("return_stock_qty").toString()) : 0;
                double returnQty = shipQty - returnStockQty;

                if (returnQty <= 0) {
                    continue;
                }

                // 获取单价和税率
                double unitPrice = orderDetail.get("unit_price") != null ? Double.parseDouble(orderDetail.get("unit_price").toString()) : 0;
                String taxCode = orderDetail.get("tax_code") != null ? orderDetail.get("tax_code").toString() : "";
                double taxRatio = 0;

                // 查询税率信息
                String taxSql = "SELECT tax_ratio FROM epd_tax WHERE tax_code = '" + taxCode + "' LIMIT 1";
                Map<String, Object> taxInfo = sqlService.getOne(taxSql);
                if (taxInfo != null) {
                    taxRatio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
                }

                // 计算金额（参照协议生成采购订单）
                double amt = Math.round(returnQty * unitPrice * 100) / 100.0;
                double taxAmount = Math.round(amt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
                double noTaxPrice = Math.round(unitPrice / (1 + taxRatio / 100) * 100) / 100.0;
                double noTaxAmount = Math.round(noTaxPrice * returnQty * 100) / 100.0;

                // 计算主数量和价税合计（本位币）
                double changeRate = orderDetail.get("change_rate") != null ? Double.parseDouble(orderDetail.get("change_rate").toString()) : 1;
                double exchangeRate = orderDetail.get("exchange_rate") != null ? Double.parseDouble(orderDetail.get("exchange_rate").toString()) : 1;
                double mainQty = Math.round(returnQty * changeRate * 100) / 100.0;
                double baseTaxPrice = Math.round(unitPrice * exchangeRate * 100) / 100.0;
                double baseTaxMoney = Math.round(baseTaxPrice * returnQty * 100) / 100.0;

                String ukLineNo = orderDetail.get("uk_line_no") != null ? orderDetail.get("uk_line_no").toString() : "";
                detailSeq++;

                String detailinsert = "insert into pur_ship_detail(pur_org,uk_bill_no,apply_org,up_bill,up_bill_line,item_code,item_name,item_spec,item_model,is_quality,request_date,ship_date,arrive_qty,ship_qty,warehouse_code,rack,pur_unit_id,main_qty,main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,amt,tax_amount,no_tax_price,no_tax_amount,base_currency_code,base_tax_price,base_tax_money,gift,is_quality_bill,is_stock,work_order,project_no,batch_no,acc_tolerance,manufacturer,prod_date,back_reason,inspection_qty,store_qty,unquality_qty,return_stock_qty,return_qty,quality_qty,line_status,create_user_id,create_time,modify_user_id,modify_time,notes,pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,is_close,s_uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id) select pur_org,'"+bill_no+"',apply_org,uk_order_no,uk_line_no,item_code,item_name,item_spec,item_model,'N','','"+tehdate+"',0,"+returnQty+",warehouse_code,'',pur_unit_id,"+mainQty+",main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,"+amt+","+taxAmount+","+noTaxPrice+","+noTaxAmount+",base_currency_code,"+baseTaxPrice+","+baseTaxMoney+",gift,'N','N',work_order,project_no,batch_no,0,manufacturer,prod_date,'',0,ship_qty,0,0,0,quality_qty,'未提交','"+user.getUserId()+"','"+create_time+"','',null,'',pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,'',uu_id,group_id,flow_id,is_deleted,'"+detailSeq+"',item_id from pur_order_detail where uu_id='"+orderDetail.get("uu_id")+"' and is_active='Y' ";
                insertsql.add(detailinsert);
            }

            // 累加退库数量到 return_stock_qty（仅处理有效明细）
            String updateReturnStockQty = "UPDATE pur_order_detail SET return_stock_qty = IFNULL(return_stock_qty, 0) + (IFNULL(store_qty, 0) - IFNULL(return_stock_qty, 0)) WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            insertsql.add(updateReturnStockQty);
            // 退库数量已完全生成用完的订单明细，is_active 更新为 N
            String detailInactive = "UPDATE pur_order_detail SET is_active = 'N' WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND is_active = 'Y' AND (IFNULL(store_qty, 0) - IFNULL(return_stock_qty, 0)) <= 0";
            insertsql.add(detailInactive);
            // 订单下所有明细 is_active 都为 N 时，订单头 is_active 更新为 N
            String headInactive = "UPDATE pur_order_head SET is_active = 'N' WHERE uu_id = '" + PurOrderHead.getUu_id() + "' AND is_active = 'Y' AND NOT EXISTS (SELECT 1 FROM pur_order_detail WHERE uk_order_uuid = '" + PurOrderHead.getUu_id() + "' AND is_active = 'Y')";
            insertsql.add(headInactive);
            String updatesql="update pur_ship_detail set uk_bill_uuid=(select uu_id from pur_ship_head where pur_ship_detail.uk_bill_no=pur_ship_head.uk_bill_no) where  uk_bill_uuid is null";
            insertsql.add(updatesql);
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("退库单生成成功");
        }else{
            return R.error("退库单生成失败");
        }
    }

    @Override
    public R createDrDetail(List<PurOrderHead> params) {
        // 校验：检查是否有可退库数量（store_qty - return_stock_qty）<= 0 的明细
        StringBuilder errorMsg = new StringBuilder();
        for (PurOrderHead PurOrderHead : params) {
            double createQty = PurOrderHead.getCreate_qty() != null ? Double.parseDouble(PurOrderHead.getCreate_qty()) : 0;
            String checkSql = "SELECT uk_order_no, uk_line_no, IFNULL(store_qty, 0) as store_qty, IFNULL(return_stock_qty, 0) as return_stock_qty FROM pur_order_detail WHERE uu_id = '" + PurOrderHead.getUu_id() + "' and is_active='Y'";
            Map<String, Object> checkResult = sqlService.getOne(checkSql);
            if (checkResult != null) {
                double storeQty = checkResult.get("store_qty") != null ? Double.parseDouble(checkResult.get("store_qty").toString()) : 0;
                double returnStockQty = checkResult.get("return_stock_qty") != null ? Double.parseDouble(checkResult.get("return_stock_qty").toString()) : 0;
                double remainQty = storeQty - returnStockQty;
                
                if (remainQty <= 0) {
                    String orderNo = checkResult.get("uk_order_no") != null ? checkResult.get("uk_order_no").toString() : "";
                    String lineNo = checkResult.get("uk_line_no") != null ? checkResult.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，可退库数量不足\n");
                } else if (createQty > remainQty) {
                    String orderNo = checkResult.get("uk_order_no") != null ? checkResult.get("uk_order_no").toString() : "";
                    String lineNo = checkResult.get("uk_line_no") != null ? checkResult.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，退库数量（").append(createQty).append("）超过剩余可退库数量（").append(remainQty).append("）\n");
                }
            } else {
                errorMsg.append("明细行：").append(PurOrderHead.getUu_id()).append("，不存在或已作废（is_active<>'Y'），不能生成退库单\n");
            }
        }
        if (errorMsg.length() > 0) {
            return R.error("不能生成退库单，以下明细行存在问题：\n" + errorMsg.toString());
        }

        List<String> insertsql = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tehdate = currentDate.format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String create_time = sdf.format(date);
        UserEntity user = ShiroUtils.getUserEntity();
        String bilsql="SELECT CASE WHEN MAX(uk_bill_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0001') ELSE LPAD(CAST(MAX(uk_bill_no) AS UNSIGNED) + 1, 12, '0') END AS uk_bill_no FROM pur_ship_head WHERE LEFT(uk_bill_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> uk_bill_no=sqlService.getOne(bilsql);
        String bill_no=uk_bill_no.get("uk_bill_no").toString();
        int i=0;
        for (PurOrderHead PurOrderHead : params) {
            if(i==0){
                String headinsert="insert into pur_ship_head(pur_org,uk_bill_no,type_code,bill_date,transport_type,up_bill,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,is_ship_ret,is_stock_return,is_outsource,back_reason,bill_status,create_user_id,create_time,modify_user_id,modify_time,notes,approver_user_id,approver_user_code,approver_user_man,approver_time,approver_opinion,uk_order_no,s_uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc) select pur_org,'"+bill_no+"',type_code,'"+tehdate+"',transport_type,uk_order_no,vendor_code,role_code,dept_code,buyer_person_code,base_tax_money,base_currency_code,address,settlement,pay_way,'N','Y','N','','未提交','"+user.getUserId()+"','"+create_time+"','',null,'','','','',null,'',uk_order_no,uu_id,group_id,flow_id,is_deleted,vendor_id,vendor_name,role_id,role_name,dept_id,dept_name,buyer_person_id,buyer_person_man,base_currency_id,base_currency_desc from pur_order_head where pur_order_head.is_active='Y' and exists(select 1 from pur_order_detail where pur_order_detail.uk_order_uuid=pur_order_head.uu_id and pur_order_detail.uu_id='"+PurOrderHead.getUu_id()+"' and pur_order_detail.is_active='Y')";
                insertsql.add(headinsert);
            }
            // 按明细生成：从页面 create_qty 取值
            double createQty = PurOrderHead.getCreate_qty() != null ? Double.parseDouble(PurOrderHead.getCreate_qty()) : 0;

            // 查询订单明细，重新计算金额
            String orderDetailSql = "SELECT * FROM pur_order_detail WHERE uu_id = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            Map<String, Object> orderDetail = sqlService.getOne(orderDetailSql);

            // 获取单价和税率
            double unitPrice = orderDetail.get("unit_price") != null ? Double.parseDouble(orderDetail.get("unit_price").toString()) : 0;
            String taxCode = orderDetail.get("tax_code") != null ? orderDetail.get("tax_code").toString() : "";
            double taxRatio = 0;

            // 查询税率信息
            String taxSql = "SELECT tax_ratio FROM epd_tax WHERE tax_code = '" + taxCode + "' LIMIT 1";
            Map<String, Object> taxInfo = sqlService.getOne(taxSql);
            if (taxInfo != null) {
                taxRatio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
            }

            // 计算金额（参照协议生成采购订单）
            double amt = Math.round(createQty * unitPrice * 100) / 100.0;
            double taxAmount = Math.round(amt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxPrice = Math.round(unitPrice / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxAmount = Math.round(noTaxPrice * createQty * 100) / 100.0;

            // 计算主数量和价税合计（本位币）
            double changeRate = orderDetail.get("change_rate") != null ? Double.parseDouble(orderDetail.get("change_rate").toString()) : 1;
            double exchangeRate = orderDetail.get("exchange_rate") != null ? Double.parseDouble(orderDetail.get("exchange_rate").toString()) : 1;
            double mainQty = Math.round(createQty * changeRate * 100) / 100.0;
            double baseTaxPrice = Math.round(unitPrice * exchangeRate * 100) / 100.0;
            double baseTaxMoney = Math.round(baseTaxPrice * createQty * 100) / 100.0;

            String detailinsert="insert into pur_ship_detail(pur_org,uk_bill_no,apply_org,up_bill,up_bill_line,item_code,item_name,item_spec,item_model,is_quality,request_date,ship_date,arrive_qty,ship_qty,warehouse_code,rack,pur_unit_id,main_qty,main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,amt,tax_amount,no_tax_price,no_tax_amount,base_currency_code,base_tax_price,base_tax_money,gift,is_quality_bill,is_stock,work_order,project_no,batch_no,acc_tolerance,manufacturer,prod_date,back_reason,inspection_qty,store_qty,unquality_qty,return_stock_qty,return_qty,quality_qty,line_status,create_user_id,create_time,modify_user_id,modify_time,notes,pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,is_close,s_uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id) select pur_org,'"+bill_no+"',apply_org,uk_order_no,uk_line_no,item_code,item_name,item_spec,item_model,'N','','"+tehdate+"',0,"+createQty+",warehouse_code,'',pur_unit_id,"+mainQty+",main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,"+amt+","+taxAmount+","+noTaxPrice+","+noTaxAmount+",base_currency_code,"+baseTaxPrice+","+baseTaxMoney+",gift,'N','N',work_order,project_no,batch_no,0,manufacturer,prod_date,'',0,ship_qty,0,0,0,quality_qty,'未提交','"+user.getUserId()+"','"+create_time+"','',null,'',pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,'',uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id from pur_order_detail where uu_id='"+PurOrderHead.getUu_id()+"' and is_active='Y' ";
            insertsql.add(detailinsert);
            // 累加退库数量到 return_stock_qty（仅处理有效明细）
            String updateReturnStockQty = "UPDATE pur_order_detail SET return_stock_qty = IFNULL(return_stock_qty, 0) + " + createQty + " WHERE uu_id = '" + PurOrderHead.getUu_id() + "' AND is_active='Y'";
            insertsql.add(updateReturnStockQty);
            // 退库数量已完全生成用完的订单明细，is_active 更新为 N
            String detailInactive = "UPDATE pur_order_detail SET is_active = 'N' WHERE uu_id = '" + PurOrderHead.getUu_id() + "' AND is_active = 'Y' AND (IFNULL(store_qty, 0) - IFNULL(return_stock_qty, 0)) <= 0";
            insertsql.add(detailInactive);
            // 订单下所有明细 is_active 都为 N 时，订单头 is_active 更新为 N
            String orderUuid = orderDetail.get("uk_order_uuid") != null ? orderDetail.get("uk_order_uuid").toString() : "";
            if (!"".equals(orderUuid)) {
                String headInactive = "UPDATE pur_order_head SET is_active = 'N' WHERE uu_id = '" + orderUuid + "' AND is_active = 'Y' AND NOT EXISTS (SELECT 1 FROM pur_order_detail WHERE uk_order_uuid = '" + orderUuid + "' AND is_active = 'Y')";
                insertsql.add(headInactive);
            }
            String updatesql="update pur_ship_detail set uk_bill_uuid=(select uu_id from pur_ship_head where pur_ship_detail.uk_bill_no=pur_ship_head.uk_bill_no) where  uk_bill_uuid is null";
            insertsql.add(updatesql);
            i++;
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("退库单生成成功");
        }else{
            return R.error("退库单生成失败");
        }
    }

    @Override
    public R zjDrDetail(List<PurOrderHead> params) {
        // 从页面传入的 PurOrderHead 中获取退库单号
        String billNo = "";
        for (PurOrderHead order : params) {
            if (order.getUk_bill_no() != null && !order.getUk_bill_no().isEmpty()) {
                billNo = order.getUk_bill_no();
                break;
            }
        }
        if (billNo == null || billNo.isEmpty()) {
            return R.error("退库单号不能为空");
        }

        // 查询已有退库单头信息
        String shipHeadSql = "SELECT * FROM pur_ship_head WHERE uk_bill_no = '" + billNo + "'";
        Map<String, Object> shipHead = sqlService.getOne(shipHeadSql);
        if (shipHead == null) {
            return R.error("退库单不存在");
        }
        String shipVendorCode = shipHead.get("vendor_code") != null ? shipHead.get("vendor_code").toString() : "";

        // 校验是否为退库单（is_stock_return='Y'）
        String isStockReturn = shipHead.get("is_stock_return") != null ? shipHead.get("is_stock_return").toString() : "";
        if (!"Y".equals(isStockReturn)) {
            return R.error("该单据不是退库单，无法追加退库明细");
        }

        StringBuilder errorMsg = new StringBuilder();
        List<String> insertsql = new ArrayList<>();
        List<String> tempInsertSql = new ArrayList<>();  // 暂存插入语句
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tehdate = currentDate.format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String create_time = sdf.format(date);
        UserEntity user = ShiroUtils.getUserEntity();

        // 获取当前退库单最大行号
        String maxLineNoSql = "SELECT IFNULL(MAX(CAST(uk_line_no AS UNSIGNED)), 0) as max_line FROM pur_ship_detail WHERE uk_bill_no = '" + billNo + "'";
        Map<String, Object> maxLineResult = sqlService.getOne(maxLineNoSql);
        int detailSeq = maxLineResult != null && maxLineResult.get("max_line") != null ? Integer.parseInt(maxLineResult.get("max_line").toString()) : 0;

        for (PurOrderHead PurOrderHead : params) {
            String detailUuId = PurOrderHead.getUu_id();
            double createQty = PurOrderHead.getCreate_qty() != null ? Double.parseDouble(PurOrderHead.getCreate_qty()) : 0;

            if (createQty <= 0) {
                errorMsg.append("明细行：").append(detailUuId).append("，退库数量必须大于0\n");
                continue;
            }

            // 查询采购订单明细信息
            String orderDetailSql = "SELECT * FROM pur_order_detail WHERE uu_id = '" + detailUuId + "' AND is_active='Y'";
            Map<String, Object> orderDetail = sqlService.getOne(orderDetailSql);
            if (orderDetail == null) {
                errorMsg.append("明细行：").append(detailUuId).append("，采购订单明细不存在或已作废（is_active<>'Y'）\n");
                continue;
            }

            String orderNo = orderDetail.get("uk_order_no") != null ? orderDetail.get("uk_order_no").toString() : "";
            String lineNo = orderDetail.get("uk_line_no") != null ? orderDetail.get("uk_line_no").toString() : "";
            String itemCode = orderDetail.get("item_code") != null ? orderDetail.get("item_code").toString() : "";


            // 查询当前已退库数量
            double shipQty = orderDetail.get("store_qty") != null ? Double.parseDouble(orderDetail.get("store_qty").toString()) : 0;
            double returnStockQty = orderDetail.get("return_stock_qty") != null ? Double.parseDouble(orderDetail.get("return_stock_qty").toString()) : 0;
            double remainQty = shipQty - returnStockQty;

            // 校验数量不超过剩余可退库数量
            if (createQty > remainQty) {
                errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，退库数量（").append(createQty).append("）超过剩余可退库数量（").append(remainQty).append("）\n");
                continue;
            }

            // 获取单价和税率
            double unitPrice = orderDetail.get("unit_price") != null ? Double.parseDouble(orderDetail.get("unit_price").toString()) : 0;
            String taxCode = orderDetail.get("tax_code") != null ? orderDetail.get("tax_code").toString() : "";
            double taxRatio = 0;

            // 查询税率信息
            String taxSql = "SELECT tax_ratio FROM epd_tax WHERE tax_code = '" + taxCode + "' LIMIT 1";
            Map<String, Object> taxInfo = sqlService.getOne(taxSql);
            if (taxInfo != null) {
                taxRatio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
            }

            // 计算金额（参照协议生成采购订单）
            double amt = Math.round(createQty * unitPrice * 100) / 100.0;
            double taxAmount = Math.round(amt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxPrice = Math.round(unitPrice / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxAmount = Math.round(noTaxPrice * createQty * 100) / 100.0;

            // 若退库单中已存在该采购订单明细（此前被追加过），累加数量并重新计算金额，不重复插入明细
            String existSql = "SELECT uu_id, IFNULL(ship_qty,0) as ship_qty, IFNULL(unit_price,0) as unit_price, IFNULL(change_rate,0) as change_rate, IFNULL(exchange_rate,0) as exchange_rate FROM pur_ship_detail WHERE uk_bill_no = '" + billNo + "' AND s_uu_id = '" + detailUuId + "' LIMIT 1";
            Map<String, Object> existDetail = sqlService.getOne(existSql);
            if (existDetail != null) {
                double newQty = Double.parseDouble(existDetail.get("ship_qty").toString()) + createQty;
                double existUnitPrice = Double.parseDouble(existDetail.get("unit_price").toString());
                double existAmt = Math.round(newQty * existUnitPrice * 100) / 100.0;
                double existTaxAmount = Math.round(existAmt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
                double existNoTaxAmount = Math.round(noTaxPrice * newQty * 100) / 100.0;
                double existMainQty = Math.round(Double.parseDouble(existDetail.get("change_rate").toString()) * newQty * 100) / 100.0;
                double existBaseTaxMoney = Math.round(existUnitPrice * Double.parseDouble(existDetail.get("exchange_rate").toString()) * newQty * 100) / 100.0;
                String detailupdate = "UPDATE pur_ship_detail SET ship_qty = " + newQty + ", main_qty = " + existMainQty + ", amt = " + existAmt + ", tax_amount = " + existTaxAmount + ", no_tax_amount = " + existNoTaxAmount + ", base_tax_money = " + existBaseTaxMoney + ", modify_user_id = '" + user.getUserId() + "', modify_time = '" + create_time + "' WHERE uu_id = '" + existDetail.get("uu_id") + "'";
                tempInsertSql.add(detailupdate);
            } else {
            // 插入退库明细
            detailSeq++;
            String detailinsert = "insert into pur_ship_detail(pur_org,uk_bill_no,apply_org,up_bill,up_bill_line,item_code,item_name,item_spec,item_model,is_quality,request_date,ship_date,arrive_qty,ship_qty,warehouse_code,rack,pur_unit_id,main_qty,main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,amt,tax_amount,no_tax_price,no_tax_amount,base_currency_code,base_tax_price,base_tax_money,gift,is_quality_bill,is_stock,work_order,project_no,batch_no,acc_tolerance,manufacturer,prod_date,back_reason,inspection_qty,store_qty,unquality_qty,return_stock_qty,return_qty,quality_qty,line_status,create_user_id,create_time,modify_user_id,modify_time,notes,pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,is_close,s_uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id) select pur_org,'" + billNo + "',apply_org,uk_order_no,uk_line_no,item_code,item_name,item_spec,item_model,'N','','" + tehdate + "',0," + createQty + ",warehouse_code,'',pur_unit_id,change_rate*" + createQty + ",main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price," + amt + "," + taxAmount + "," + noTaxPrice + "," + noTaxAmount + ",base_currency_code,unit_price*exchange_rate,unit_price*exchange_rate*" + createQty + ",gift,'N','N',work_order,project_no,batch_no,0,manufacturer,prod_date,''," + createQty + ",ship_qty,0,0,0,quality_qty,'未提交','" + user.getUserId() + "','" + create_time + "','',null,'',pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,'',uu_id,group_id,flow_id,is_deleted,'" + detailSeq + "',item_id from pur_order_detail where uu_id='" + detailUuId + "' and is_active='Y' ";
            tempInsertSql.add(detailinsert);
            }

            // 累加退库数量到 return_stock_qty（仅处理有效明细）
            String updateReturnStockQty = "UPDATE pur_order_detail SET return_stock_qty = IFNULL(return_stock_qty, 0) + " + createQty + " WHERE uu_id = '" + detailUuId + "' AND is_active='Y'";
            tempInsertSql.add(updateReturnStockQty);
            // 退库数量已完全生成用完的订单明细，is_active 更新为 N
            tempInsertSql.add("UPDATE pur_order_detail SET is_active = 'N' WHERE uu_id = '" + detailUuId + "' AND is_active = 'Y' AND (IFNULL(store_qty, 0) - IFNULL(return_stock_qty, 0)) <= 0");
            // 订单下所有明细 is_active 都为 N 时，订单头 is_active 更新为 N
            String orderUuid = orderDetail.get("uk_order_uuid") != null ? orderDetail.get("uk_order_uuid").toString() : "";
            if (!"".equals(orderUuid)) {
                tempInsertSql.add("UPDATE pur_order_head SET is_active = 'N' WHERE uu_id = '" + orderUuid + "' AND is_active = 'Y' AND NOT EXISTS (SELECT 1 FROM pur_order_detail WHERE uk_order_uuid = '" + orderUuid + "' AND is_active = 'Y')");
            }
        }

        // 先检查是否有错误，有错误则不执行任何插入
        if (errorMsg.length() > 0) {
            return R.error("退库明细追加失败，存在以下问题：\n" + errorMsg.toString());
        }

        // 将所有暂存的SQL语句添加到执行列表
        insertsql.addAll(tempInsertSql);

        // 更新退库单明细的 uk_bill_uuid
        String updatesql = "update pur_ship_detail set uk_bill_uuid=(select uu_id from pur_ship_head where pur_ship_detail.uk_bill_no=pur_ship_head.uk_bill_no) where uk_bill_no='" + billNo + "' and uk_bill_uuid is null";
        insertsql.add(updatesql);

        // 汇总退库单头金额
        String sumAmt = "UPDATE pur_ship_head SET base_tax_money = (SELECT IFNULL(SUM(IFNULL(amt, 0)), 0) FROM pur_ship_detail WHERE uk_bill_no = '" + billNo + "') WHERE uk_bill_no = '" + billNo + "'";
        insertsql.add(sumAmt);

        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("退库明细追加成功");
        } else {
            return R.error("退库明细追加失败");
        }
    }

    @Override
    public R zjThDetail(List<PurOrderHead> params) {
        // 从页面传入的 PurOrderHead 中获取到货单号
        String billNo = "";
        for (PurOrderHead order : params) {
            if (order.getUk_bill_no() != null && !order.getUk_bill_no().isEmpty()) {
                billNo = order.getUk_bill_no();
                break;
            }
        }
        if (billNo == null || billNo.isEmpty()) {
            return R.error("到货单号不能为空");
        }

        // 查询已有到货单头信息
        String shipHeadSql = "SELECT * FROM pur_ship_head WHERE uk_bill_no = '" + billNo + "'";
        Map<String, Object> shipHead = sqlService.getOne(shipHeadSql);
        if (shipHead == null) {
            return R.error("到货单不存在");
        }
        String shipVendorCode = shipHead.get("vendor_code") != null ? shipHead.get("vendor_code").toString() : "";

        // 校验是否为到货单（is_ship_ret='Y'）
        String isShipRet = shipHead.get("is_ship_ret") != null ? shipHead.get("is_ship_ret").toString() : "";
        if (!"Y".equals(isShipRet)) {
            return R.error("该单据不是到货单，无法追加到货明细");
        }

        StringBuilder errorMsg = new StringBuilder();
        List<String> insertsql = new ArrayList<>();
        List<String> tempInsertSql = new ArrayList<>();  // 暂存插入语句
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tehdate = currentDate.format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String create_time = sdf.format(date);
        UserEntity user = ShiroUtils.getUserEntity();

        // 获取当前到货单最大行号
        String maxLineNoSql = "SELECT IFNULL(MAX(CAST(uk_line_no AS UNSIGNED)), 0) as max_line FROM pur_ship_detail WHERE uk_bill_no = '" + billNo + "'";
        Map<String, Object> maxLineResult = sqlService.getOne(maxLineNoSql);
        int detailSeq = maxLineResult != null && maxLineResult.get("max_line") != null ? Integer.parseInt(maxLineResult.get("max_line").toString()) : 0;

        for (PurOrderHead PurOrderHead : params) {
            String detailUuId = PurOrderHead.getUu_id();
            double createQty = PurOrderHead.getCreate_qty() != null ? Double.parseDouble(PurOrderHead.getCreate_qty()) : 0;

            if (createQty <= 0) {
                errorMsg.append("明细行：").append(detailUuId).append("，到货数量必须大于0\n");
                continue;
            }

            // 查询采购订单明细信息
            String orderDetailSql = "SELECT * FROM pur_order_detail WHERE uu_id = '" + detailUuId + "' AND is_active='Y'";
            Map<String, Object> orderDetail = sqlService.getOne(orderDetailSql);
            if (orderDetail == null) {
                errorMsg.append("明细行：").append(detailUuId).append("，采购订单明细不存在或已关闭（is_active<>'Y'）\n");
                continue;
            }

            String orderNo = orderDetail.get("uk_order_no") != null ? orderDetail.get("uk_order_no").toString() : "";
            String lineNo = orderDetail.get("uk_line_no") != null ? orderDetail.get("uk_line_no").toString() : "";
            String itemCode = orderDetail.get("item_code") != null ? orderDetail.get("item_code").toString() : "";

            // 查询当前已到货数量
            double shipQty = orderDetail.get("ship_qty") != null ? Double.parseDouble(orderDetail.get("ship_qty").toString()) : 0;
            double pur_qty = orderDetail.get("pur_qty") != null ? Double.parseDouble(orderDetail.get("pur_qty").toString()) : 0;
            double remainQty = pur_qty - shipQty;

            // 校验数量不超过剩余可到货数量
            if (createQty > remainQty) {
                errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，到货数量（").append(createQty).append("）超过剩余可到货数量（").append(remainQty).append("）\n");
                continue;
            }

            // 获取单价和税率
            double unitPrice = orderDetail.get("unit_price") != null ? Double.parseDouble(orderDetail.get("unit_price").toString()) : 0;
            String taxCode = orderDetail.get("tax_code") != null ? orderDetail.get("tax_code").toString() : "";
            double taxRatio = 0;

            // 查询税率信息
            String taxSql = "SELECT tax_ratio FROM epd_tax WHERE tax_code = '" + taxCode + "' LIMIT 1";
            Map<String, Object> taxInfo = sqlService.getOne(taxSql);
            if (taxInfo != null) {
                taxRatio = Double.parseDouble(taxInfo.get("tax_ratio").toString());
            }

            // 计算金额（参照协议生成采购订单）
            double amt = Math.round(createQty * unitPrice * 100) / 100.0;
            double taxAmount = Math.round(amt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxPrice = Math.round(unitPrice / (1 + taxRatio / 100) * 100) / 100.0;
            double noTaxAmount = Math.round(noTaxPrice * createQty * 100) / 100.0;

            // 若到货单中已存在该采购订单明细（此前被追加过），累加数量并重新计算金额，不重复插入明细
            String existSql = "SELECT uu_id, IFNULL(ship_qty,0) as ship_qty, IFNULL(unit_price,0) as unit_price, IFNULL(change_rate,0) as change_rate, IFNULL(exchange_rate,0) as exchange_rate FROM pur_ship_detail WHERE uk_bill_no = '" + billNo + "' AND s_uu_id = '" + detailUuId + "' LIMIT 1";
            Map<String, Object> existDetail = sqlService.getOne(existSql);
            if (existDetail != null) {
                double newQty = Double.parseDouble(existDetail.get("ship_qty").toString()) + createQty;
                double existUnitPrice = Double.parseDouble(existDetail.get("unit_price").toString());
                double existAmt = Math.round(newQty * existUnitPrice * 100) / 100.0;
                double existTaxAmount = Math.round(existAmt * (taxRatio / 100) / (1 + taxRatio / 100) * 100) / 100.0;
                double existNoTaxAmount = Math.round(noTaxPrice * newQty * 100) / 100.0;
                double existMainQty = Math.round(Double.parseDouble(existDetail.get("change_rate").toString()) * newQty * 100) / 100.0;
                double existBaseTaxMoney = Math.round(existUnitPrice * Double.parseDouble(existDetail.get("exchange_rate").toString()) * newQty * 100) / 100.0;
                String detailupdate = "UPDATE pur_ship_detail SET arrive_qty="+newQty+",ship_qty = " + newQty + ", main_qty = " + existMainQty + ", amt = " + existAmt + ", tax_amount = " + existTaxAmount + ", no_tax_amount = " + existNoTaxAmount + ", base_tax_money = " + existBaseTaxMoney + ", inspection_qty = IFNULL(inspection_qty,0) + " + createQty + ", modify_user_id = '" + user.getUserId() + "', modify_time = '" + create_time + "' WHERE uu_id = '" + existDetail.get("uu_id") + "'";
                tempInsertSql.add(detailupdate);
            } else {
            // 插入到货明细
            detailSeq++;
            String detailinsert = "insert into pur_ship_detail(pur_org,uk_bill_no,apply_org,up_bill,up_bill_line,item_code,item_name,item_spec,item_model,is_quality,request_date,ship_date,arrive_qty,ship_qty,warehouse_code,rack,pur_unit_id,main_qty,main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price,amt,tax_amount,no_tax_price,no_tax_amount,base_currency_code,base_tax_price,base_tax_money,gift,is_quality_bill,is_stock,work_order,project_no,batch_no,acc_tolerance,manufacturer,prod_date,back_reason,inspection_qty,store_qty,unquality_qty,return_stock_qty,return_qty,quality_qty,line_status,create_user_id,create_time,modify_user_id,modify_time,notes,pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,is_close,s_uu_id,group_id,flow_id,is_deleted,uk_line_no,item_id) select pur_org,'" + billNo + "',apply_org,uk_order_no,uk_line_no,item_code,item_name,item_spec,item_model,'N','','" + tehdate + "',0," + createQty + ",warehouse_code,'',pur_unit_id,change_rate*" + createQty + ",main_unit_id,change_rate,exchange_rate,tax_code,currency_code,unit_price," + amt + "," + taxAmount + "," + noTaxPrice + "," + noTaxAmount + ",base_currency_code,unit_price*exchange_rate,unit_price*exchange_rate*" + createQty + ",gift,'N','N',work_order,project_no,batch_no,0,manufacturer,prod_date,''," + createQty + ",ship_qty,0,0,0,quality_qty,'未提交','" + user.getUserId() + "','" + create_time + "','',null,'',pur_unit_name,main_unit_name,tax_id,tax_name,currency_id,currency_desc,base_currency_id,base_currency_desc,warehouse_id,warehouse_name,project_id,project_name,'',uu_id,group_id,flow_id,is_deleted,'" + detailSeq + "',item_id from pur_order_detail where uu_id='" + detailUuId + "' and is_active='Y' ";
            tempInsertSql.add(detailinsert);
            }

            // 累加到货数量到 ship_qty（与到货单生成口径一致；到货不更新 is_active，退库完成后才置 N）
            String updateShipQty = "UPDATE pur_order_detail SET ship_qty = IFNULL(ship_qty, 0) + " + createQty + " WHERE uu_id = '" + detailUuId + "' AND is_active='Y'";
            tempInsertSql.add(updateShipQty);
        }

        // 先检查是否有错误，有错误则不执行任何插入
        if (errorMsg.length() > 0) {
            return R.error("到货明细追加失败，存在以下问题：\n" + errorMsg.toString());
        }

        // 将所有暂存的SQL语句添加到执行列表
        insertsql.addAll(tempInsertSql);

        // 更新到货单明细的 uk_bill_uuid
        String updatesql = "update pur_ship_detail set uk_bill_uuid=(select uu_id from pur_ship_head where pur_ship_detail.uk_bill_no=pur_ship_head.uk_bill_no) where uk_bill_no='" + billNo + "' and uk_bill_uuid is null";
        insertsql.add(updatesql);

        // 汇总到货单头金额
        String sumAmt = "UPDATE pur_ship_head SET base_tax_money = (SELECT IFNULL(SUM(IFNULL(amt, 0)), 0) FROM pur_ship_detail WHERE uk_bill_no = '" + billNo + "') WHERE uk_bill_no = '" + billNo + "'";
        insertsql.add(sumAmt);

        boolean flag = sqlService.insertList(insertsql);
        if (flag) {
            return R.ok("到货明细追加成功");
        } else {
            return R.error("到货明细追加失败");
        }
    }

    @Override
    public R enable(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            insertsql.add("update pur_order_head set is_active = 'Y' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_detail set is_active = 'Y' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "' and (pur_qty>store_qty or (store_qty>return_stock_qty and return_stock_qty>0) )");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R disabl(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            insertsql.add("update pur_order_head set is_active = 'N' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_detail set is_active = 'N' where uk_order_uuid = '" + PurOrderHead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }

    @Override
    public R detailenable(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            insertsql.add("update pur_order_detail set is_active = 'Y' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_head set is_active = 'Y' where uu_id = '" + PurOrderHead.getUk_order_uuid() + "' and  exists( select 1 from pur_order_detail where is_active = 'Y' and uk_order_uuid = '" + PurOrderHead.getUk_order_uuid() + "' and pur_order_detail.uk_order_uuid=pur_order_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R detaildisabl(List<PurOrderHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurOrderHead PurOrderHead : params) {
            insertsql.add("update pur_order_detail set is_active = 'N' where uu_id = '" + PurOrderHead.getUu_id() + "'");
            insertsql.add("update pur_order_head set is_active = 'N' where uu_id = '" + PurOrderHead.getUk_order_uuid() + "' and not exists( select 1 from pur_order_detail where is_active = 'Y' and uk_order_uuid = '" + PurOrderHead.getUk_order_uuid() + "' and pur_order_detail.uk_order_uuid=pur_order_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }
}
