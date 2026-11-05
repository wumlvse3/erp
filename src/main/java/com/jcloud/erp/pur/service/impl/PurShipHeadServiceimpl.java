package com.jcloud.erp.pur.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.pur.entity.PurShipHead;
import com.jcloud.erp.pur.mapper.PurShipHeadMapper;
import com.jcloud.erp.pur.service.PurShipHeadService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PurShipHeadServiceimpl extends ServiceImpl<PurShipHeadMapper, PurShipHead> implements PurShipHeadService {
    @Resource
    private SqlService sqlService;

    // 按单据类型反填重算订单明细累积数量；退库单重算后同步维护订单明细与订单头的 is_active 状态
    // 仅在删除/修改单据时调用；生成时已即时累加，审核、驳回、提交、取消提交等状态变化不反填原采购单据
    // isReturn=true：汇总退库单（is_stock_return='Y'）全部明细，重算 return_stock_qty 并更新 is_active
    // isReturn=false：汇总到货单（is_ship_ret='Y'）全部明细，重算 ship_qty（到货不更新 is_active，退库完成后才置 N）
    private List<String> buildRecalcSql(String sUuId, boolean isReturn) {
        List<String> sqls = new ArrayList<>();
        if (isReturn) {
            sqls.add("update pur_order_detail set is_stock_return='Y',return_stock_qty=(select ifnull(sum(ifnull(d.ship_qty,0)),0) from pur_ship_detail d join pur_ship_head h on d.uk_bill_no=h.uk_bill_no where d.s_uu_id='"+sUuId+"' and h.is_stock_return='Y') where uu_id='"+sUuId+"'");
            sqls.add("update pur_order_detail set is_stock_return='N' where uu_id='"+sUuId+"' and ifnull(return_stock_qty,0)<=0");
            sqls.add("update pur_order_detail set is_active='Y' where uu_id='"+sUuId+"' and is_active='N' and (IFNULL(store_qty,0) - IFNULL(return_stock_qty,0)) > 0");
            sqls.add("update pur_order_detail set is_active='N' where uu_id='"+sUuId+"' and is_active='Y' and IFNULL(store_qty,0) > 0 and (IFNULL(store_qty,0) - IFNULL(return_stock_qty,0)) <= 0");

        } else {
            sqls.add("update pur_order_detail set is_ship='Y',ship_qty=(select ifnull(sum(ifnull(d.ship_qty,0)),0) from pur_ship_detail d join pur_ship_head h on d.uk_bill_no=h.uk_bill_no where d.s_uu_id='"+sUuId+"' and h.is_ship_ret='Y') where uu_id='"+sUuId+"'");
            sqls.add("update pur_order_detail set is_ship='N' where uu_id='"+sUuId+"' and ifnull(ship_qty,0)<=0");
        }
        // 订单头联动：所有明细都为 N 时订单头置 N，存在有效明细时恢复为 Y
        sqls.add("update pur_order_head set is_active=(case when exists(select 1 from pur_order_detail where uk_order_uuid=pur_order_head.uu_id and is_active='Y') then 'Y' else 'N' end) where uu_id=(select uk_order_uuid from pur_order_detail where uu_id='"+sUuId+"')");
        return sqls;
    }

    @Override
    public R addObj(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_Ship_head set bill_status = '未提交' where uu_id = '" + purShiphead.getUu_id() + "'");
            insertsql.add("update pur_Ship_detail set line_status = '未提交' where uk_bill_uuid = '" + purShiphead.getUu_id() + "'");
            // 状态变化不反填原采购单据，反填仅在删除/修改单据时进行
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消提交成功");
        }else{
            return R.error("取消提交失败");
        }
    }

    @Override
    public R passlist(List<PurShipHead> params) {
        // 校验：检查提交后到货数量是否会超过采购数量（按s_uu_id分组汇总）
        StringBuilder errorMsg = new StringBuilder();
        // 收集本次提交所有单据的明细，按s_uu_id汇总本次要提交的ship_qty
        Map<String, Double> thisShipQtyMap = new HashMap<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurShipHead purShiphead : params) {
            String detailSql = "SELECT s_uu_id, IFNULL(ship_qty, 0) as ship_qty FROM pur_ship_detail WHERE uk_bill_uuid = '" + purShiphead.getUu_id() + "'";
            List<Map<String, Object>> details = sqlService.getAll(detailSql);
            for (Map<String, Object> detail : details) {
                String sUuId = detail.get("s_uu_id") != null ? detail.get("s_uu_id").toString() : "";
                if (sUuId.isEmpty()) {
                    continue;
                }
                double shipQty = Double.parseDouble(detail.get("ship_qty") != null ? detail.get("ship_qty").toString() : "0");
                thisShipQtyMap.put(sUuId, thisShipQtyMap.getOrDefault(sUuId, 0.0) + shipQty);
            }
        }
        // 按s_uu_id逐条校验：该订单明细所有到货单累计数量（生成即占用，不区分状态）是否超过采购数量
        for (Map.Entry<String, Double> entry : thisShipQtyMap.entrySet()) {
            String sUuId = entry.getKey();
            String checkSql = "SELECT IFNULL((SELECT SUM(IFNULL(d.ship_qty, 0)) FROM pur_ship_detail d join pur_ship_head h on d.uk_bill_no = h.uk_bill_no WHERE d.s_uu_id = '" + sUuId + "' AND h.is_ship_ret = 'Y'), 0) as current_ship_qty, " +
                    "IFNULL(pur_qty, 0) as pur_qty, uk_order_no, uk_line_no " +
                    "FROM pur_order_detail WHERE uu_id = '" + sUuId + "'";
            Map<String, Object> checkResult = sqlService.getOne(checkSql);
            if (checkResult != null) {
                double currentShipQty = Double.parseDouble(checkResult.get("current_ship_qty") != null ? checkResult.get("current_ship_qty").toString() : "0");
                double purQty = Double.parseDouble(checkResult.get("pur_qty") != null ? checkResult.get("pur_qty").toString() : "0");
                if (currentShipQty > purQty) {
                    String orderNo = checkResult.get("uk_order_no") != null ? checkResult.get("uk_order_no").toString() : "";
                    String lineNo = checkResult.get("uk_line_no") != null ? checkResult.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，到货数量(").append(currentShipQty).append(")大于采购数量(").append(purQty).append(")\n");
                }
            }
        }
        if (errorMsg.length() > 0) {
            return R.error("不能提交，以下明细行存在问题：\n" + errorMsg.toString());
        }
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_Ship_head set bill_status = '审核通过',approver_opinion='"+purShiphead.getApprover_opinion()+"',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where uu_id = '" + purShiphead.getUu_id() + "' and bill_status<>'审核通过' ");
            insertsql.add("update pur_Ship_detail set line_status = '审核通过' where uk_bill_uuid = '" + purShiphead.getUu_id() + "'");
            // 状态变化不反填原采购单据，反填仅在删除/修改单据时进行
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("提交成功");
        }else{
            return R.error("提交失败");
        }
    }

    @Override
    public R draddObj(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_Ship_head set bill_status = '未提交' where uu_id = '" + purShiphead.getUu_id() + "'");
            insertsql.add("update pur_Ship_detail set line_status = '未提交' where uk_bill_uuid = '" + purShiphead.getUu_id() + "'");
            // 状态变化不反填原采购单据，反填仅在删除/修改单据时进行
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消提交成功");
        }else{
            return R.error("取消提交失败");
        }
    }

    @Override
    public R drpasslist(List<PurShipHead> params) {
        // 校验：检查提交后退库数量是否会超过入库数量（按s_uu_id分组汇总）
        StringBuilder errorMsg = new StringBuilder();
        // 收集本次提交所有单据的明细，按s_uu_id汇总本次要提交的ship_qty
        Map<String, Double> thisShipQtyMap = new HashMap<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurShipHead purShiphead : params) {
            String detailSql = "SELECT s_uu_id, IFNULL(ship_qty, 0) as ship_qty FROM pur_ship_detail WHERE uk_bill_uuid = '" + purShiphead.getUu_id() + "'";
            List<Map<String, Object>> details = sqlService.getAll(detailSql);
            for (Map<String, Object> detail : details) {
                String sUuId = detail.get("s_uu_id") != null ? detail.get("s_uu_id").toString() : "";
                if (sUuId.isEmpty()) {
                    continue;
                }
                double shipQty = Double.parseDouble(detail.get("ship_qty") != null ? detail.get("ship_qty").toString() : "0");
                thisShipQtyMap.put(sUuId, thisShipQtyMap.getOrDefault(sUuId, 0.0) + shipQty);
            }
        }
        // 按s_uu_id逐条校验：该订单明细所有退库单累计数量（生成即占用，不区分状态）是否超过入库数量
        for (Map.Entry<String, Double> entry : thisShipQtyMap.entrySet()) {
            String sUuId = entry.getKey();
            String checkSql = "SELECT IFNULL((SELECT SUM(IFNULL(d.ship_qty, 0)) FROM pur_ship_detail d join pur_ship_head h on d.uk_bill_no = h.uk_bill_no WHERE d.s_uu_id = '" + sUuId + "' AND h.is_stock_return = 'Y'), 0) as current_ship_qty, " +
                    "IFNULL(store_qty, 0) as store_qty, uk_order_no, uk_line_no " +
                    "FROM pur_order_detail WHERE uu_id = '" + sUuId + "'";
            Map<String, Object> checkResult = sqlService.getOne(checkSql);
            if (checkResult != null) {
                double currentShipQty = Double.parseDouble(checkResult.get("current_ship_qty") != null ? checkResult.get("current_ship_qty").toString() : "0");
                double purQty = Double.parseDouble(checkResult.get("store_qty") != null ? checkResult.get("store_qty").toString() : "0");
                if (currentShipQty > purQty) {
                    String orderNo = checkResult.get("uk_order_no") != null ? checkResult.get("uk_order_no").toString() : "";
                    String lineNo = checkResult.get("uk_line_no") != null ? checkResult.get("uk_line_no").toString() : "";
                    errorMsg.append("订单号：").append(orderNo).append("，行号：").append(lineNo).append("，退库数量(").append(currentShipQty).append(")大于入库数量(").append(purQty).append(")\n");
                }
            }
        }
        if (errorMsg.length() > 0) {
            return R.error("不能提交，以下明细行存在问题：\n" + errorMsg.toString());
        }

        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_Ship_head set bill_status = '审核通过',approver_opinion='"+purShiphead.getApprover_opinion()+"',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where uu_id = '" + purShiphead.getUu_id() + "' and bill_status<>'审核通过' ");
            insertsql.add("update pur_Ship_detail set line_status = '审核通过' where uk_bill_uuid = '" + purShiphead.getUu_id() + "'");
            // 状态变化不反填原采购单据，反填仅在删除/修改单据时进行
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("提交成功");
        }else{
            return R.error("提交失败");
        }
    }

    @Override
    public R detaillist(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead PurShipHead : params) {
            // 删除前查询该明细的来源订单明细及所属单据类型，删除后重算累积数量
            String qsql = "select d.s_uu_id, h.is_stock_return from pur_ship_detail d left join pur_ship_head h on d.uk_bill_no = h.uk_bill_no where d.uu_id = '" + PurShipHead.getUu_id() + "'";
            Map<String, Object> row = sqlService.getOne(qsql);
            insertsql.add("delete from pur_Ship_detail  where uu_id = '" + PurShipHead.getUu_id() + "'");
            if (row != null && row.get("s_uu_id") != null && !"".equals(row.get("s_uu_id").toString())) {
                boolean isReturn = row.get("is_stock_return") != null && "Y".equals(row.get("is_stock_return").toString());
                insertsql.addAll(buildRecalcSql(row.get("s_uu_id").toString(), isReturn));
            }
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("删除成功");
        }else{
            return R.error("删除失败");
        }
    }

    @Override
    public R enable(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_ship_head set is_active = 'Y' where uu_id = '" + purShiphead.getUu_id() + "'");
            insertsql.add("update pur_ship_detail set is_active = 'Y' where uk_bill_uuid = '" + purShiphead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R disabl(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_ship_head set is_active = 'N' where uu_id = '" + purShiphead.getUu_id() + "'");
            insertsql.add("update pur_ship_detail set is_active = 'N' where uk_bill_uuid = '" + purShiphead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }

    @Override
    public R detailenable(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_ship_detail set is_active = 'Y' where uu_id = '" + purShiphead.getUu_id() + "'");
            insertsql.add("update pur_ship_head set is_active = 'Y' where uu_id = '" + purShiphead.getUk_bill_uuid() + "' and  exists( select 1 from pur_ship_detail where is_active = 'Y' and uk_bill_uuid = '" + purShiphead.getUk_bill_uuid() + "' and pur_ship_detail.uk_bill_uuid=pur_ship_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R detaildisabl(List<PurShipHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurShipHead purShiphead : params) {
            insertsql.add("update pur_ship_detail set is_active = 'N' where uu_id = '" + purShiphead.getUu_id() + "'");
            insertsql.add("update pur_ship_head set is_active = 'N' where uu_id = '" + purShiphead.getUk_bill_uuid() + "' and not exists( select 1 from pur_ship_detail where is_active = 'Y' and uk_bill_uuid = '" + purShiphead.getUk_bill_uuid() + "' and pur_ship_detail.uk_bill_uuid=pur_ship_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }

}
