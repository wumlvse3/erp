package com.jcloud.pur.service.impl;
import com.jcloud.pur.service.PurCountService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurCountServiceimpl implements PurCountService {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Resource
    private SqlService sqlService;
    @Override
    public R getApplyData(List<Map<String, Object>> queryList, String offset, String size) {
        try {
            StringBuffer condStrAll = new StringBuffer();
            condStrAll.append(" and 1=1 ");
            if (queryList != null && !queryList.isEmpty()) {
                for (Map<String, Object> queryKey : queryList) {
                    if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                        String condStr =" ";
                        String andStr =" and ";
                        if(queryKey.get("field").toString().equals("pur_org") || queryKey.get("field").toString().equals("apply_user_code"))
                        {
                            andStr = " and t1.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                        } else {
                            andStr = " and t2.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                        }
                        condStrAll.append(condStr);
                    }
                }
            }
            String selAll = "select pur_org,apply_user_man,item_code,item_name,item_spec,item_model,apply_qty,pur_unit_name,main_qty,main_unit_name,sum_plan,work_order,project_name,guide_price,sort_flag,apply_org\n" +
                    "from " +
                    "(select t1.pur_org,t1.apply_user_man,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,SUM(IFNULL(t2.apply_qty, 0)) AS apply_qty,t2.pur_unit_name,SUM(IFNULL(t2.main_qty, 0)) AS main_qty,t2.main_unit_name,SUM(IFNULL(t2.sum_plan, 0)) AS sum_plan ,t2.work_order,t2.project_name,t2.guide_price,1 as sort_flag,t2.apply_org from pur_apply_head t1,pur_apply_detail t2" +
                    " where t1.uu_id=t2.uk_apply_uuid  " + condStrAll+ "" +
                    " group by t1.pur_org,t1.apply_user_man,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.pur_unit_name,t2.main_unit_name,t2.work_order,t2.project_name,t2.guide_price,t2.apply_org " +
                    " union  select '小计','',t2.item_code,'','','',SUM(IFNULL(t2.apply_qty, 0)) AS apply_qty,'',SUM(IFNULL(t2.main_qty, 0)) AS main_qty,'',SUM(IFNULL(t2.sum_plan, 0)) AS sum_plan,'','','', 1 as sort_flag,''  from pur_apply_head t1,pur_apply_detail t2   where t1.uu_id=t2.uk_apply_uuid  " + condStrAll + "  group BY t2.item_code "+
                    " union  select '合计','','','','','',SUM(IFNULL(t2.apply_qty, 0)) AS apply_qty,'',SUM(IFNULL(t2.main_qty, 0)) AS main_qty,'',SUM(IFNULL(t2.sum_plan, 0)) AS sum_plan,'','', '', 2 as sort_flag,''  from pur_apply_head t1,pur_apply_detail t2   where t1.uu_id=t2.uk_apply_uuid  " + condStrAll + "   ) t2 order by sort_flag, t2.item_code ";
            List<Map<String, Object>> allStat = sqlService.getAll(selAll);
            int total = allStat != null ? allStat.size() : 0;
            int offsetInt = Integer.parseInt(offset);
            int sizeInt = Integer.parseInt(size);
            if (allStat != null && offsetInt < allStat.size()) {
                int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                allStat = allStat.subList(offsetInt, endIndex);
            } else {
                    allStat = new ArrayList<>();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("rows", allStat);
            result.put("total", total);

            return R.ok().setdata(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    @Override
    public R getPurchaseData(List<Map<String, Object>> queryList, String offset, String size) {
        try {
            StringBuffer condStrAll = new StringBuffer();
            condStrAll.append(" and 1=1 ");
            if (queryList != null && !queryList.isEmpty()) {
                for (Map<String, Object> queryKey : queryList) {
                    if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                        String condStr =" ";
                        String andStr =" and ";
                        if(queryKey.get("field").toString().equals("pur_org") || queryKey.get("field").toString().equals("bill_status"))
                        {
                            andStr = " and t2.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                        } else {
                            andStr = " and t1.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                        }
                        condStrAll.append(condStr);
                    }
                }
            }
            String selAll = "SELECT pur_org, item_code, bill_status, plan_date, plan_user_man, create_user_id, create_time, apply_org, item_name, item_spec, item_model, mrp_qty, plan_qty, pur_num, mrp_adjust, non_prod_adjust, reorder_adjust, pur_unit_name, work_order, project_name FROM ( SELECT t2.bill_status, t2.plan_date, t2.plan_user_man, t2.create_user_id, t2.create_time, t1.pur_org, t1.apply_org, t1.item_code, t1.item_name, t1.item_spec, t1.item_model, SUM(IFNULL(t1.mrp_qty, 0)) AS mrp_qty, SUM(IFNULL(t1.plan_qty, 0)) AS plan_qty, SUM(IFNULL(t1.pur_num, 0)) AS pur_num, SUM(IFNULL(t1.mrp_adjust, 0)) AS mrp_adjust, SUM(IFNULL(t1.non_prod_adjust, 0)) AS non_prod_adjust, SUM(IFNULL(t1.reorder_adjust, 0)) AS reorder_adjust, t1.pur_unit_name, t1.work_order, t1.project_name, 1 AS sort_flag FROM pur_purchase_plan_detail t1,pur_purchase_plan_head t2 where t1.uk_plan_uuid = t2.uu_id " + condStrAll+ " GROUP BY t2.bill_status, t2.plan_date, t2.plan_user_man, t2.create_user_id, t2.create_time, t1.pur_org, t1.apply_org, t1.item_code, t1.item_name, t1.item_spec, t1.item_model, t1.pur_unit_name, t1.work_order, t1.project_name UNION ALL SELECT '' AS bill_status, '' AS plan_date, '' AS plan_user_man, '' AS create_user_id, '' AS create_time, '小计' AS pur_org, '' AS apply_org, t1.item_code, '' AS item_name, '' AS item_spec, '' AS item_model, SUM(IFNULL(t1.mrp_qty, 0)) AS mrp_qty, SUM(IFNULL(t1.plan_qty, 0)) AS plan_qty, SUM(IFNULL(t1.pur_num, 0)) AS pur_num, SUM(IFNULL(t1.mrp_adjust, 0)) AS mrp_adjust, SUM(IFNULL(t1.non_prod_adjust, 0)) AS non_prod_adjust, SUM(IFNULL(t1.reorder_adjust, 0)) AS reorder_adjust, '' AS pur_unit_name, '' AS work_order, '' AS project_name, 1 AS sort_flag FROM pur_purchase_plan_detail t1,pur_purchase_plan_head t2 where t1.uk_plan_uuid = t2.uu_id " + condStrAll+ " GROUP BY t1.item_code UNION ALL SELECT '' AS bill_status, '' AS plan_date, '' AS plan_user_man, '' AS create_user_id, '' AS create_time, '合计' AS pur_org, '' AS apply_org, '' AS item_code, '' AS item_name, '' AS item_spec, '' AS item_model, SUM(IFNULL(t1.mrp_qty, 0)) AS mrp_qty, SUM(IFNULL(t1.plan_qty, 0)) AS plan_qty, SUM(IFNULL(t1.pur_num, 0)) AS pur_num, SUM(IFNULL(t1.mrp_adjust, 0)) AS mrp_adjust, SUM(IFNULL(t1.non_prod_adjust, 0)) AS non_prod_adjust, SUM(IFNULL(t1.reorder_adjust, 0)) AS reorder_adjust, '' AS pur_unit_name, '' AS work_order, '' AS project_name, 2 AS sort_flag FROM pur_purchase_plan_detail t1,pur_purchase_plan_head t2 where t1.uk_plan_uuid = t2.uu_id " + condStrAll+ ") t_result ORDER BY sort_flag, item_code ";
            List<Map<String, Object>> allStat = sqlService.getAll(selAll);
            int total = allStat != null ? allStat.size() : 0;
            int offsetInt = Integer.parseInt(offset);
            int sizeInt = Integer.parseInt(size);
            if (allStat != null && offsetInt < allStat.size()) {
                int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                allStat = allStat.subList(offsetInt, endIndex);
            } else {
                    allStat = new ArrayList<>();
                }

            Map<String, Object> result = new HashMap<>();
            result.put("rows", allStat);
            result.put("total", total);

            return R.ok().setdata(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    @Override
    public R getShipData(List<Map<String, Object>> queryList, String offset, String size) {
        try {
            StringBuffer condStrAll = new StringBuffer();
            condStrAll.append(" and 1=1 ");
            if (queryList != null && !queryList.isEmpty()) {
                for (Map<String, Object> queryKey : queryList) {
                    if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                        String condStr =" ";
                        String andStr =" and ";
                        if(queryKey.get("field").toString().equals("pur_org") || queryKey.get("field").toString().equals("bill_status"))
                        {
                            andStr = " and t2.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                           } else {
                            andStr = " and t1.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                           }
                        condStrAll.append(condStr);
                    }
                }
            }
            String selAll = "SELECT pur_org, apply_org, vendor_name, bill_status, item_code, item_name, item_spec, item_model, pur_unit_name, ship_qty, inspection_qty, quality_qty, store_qty, return_qty, project_no, work_order FROM ( SELECT t2.pur_org, t1.apply_org, t2.vendor_name, t2.bill_status, t1.item_code, t1.item_name, t1.item_spec, t1.item_model, t1.pur_unit_name, SUM(IFNULL(t1.ship_qty, 0)) AS ship_qty, SUM(IFNULL(t1.inspection_qty, 0)) AS inspection_qty, SUM(IFNULL(t1.quality_qty, 0)) AS quality_qty, SUM(IFNULL(t1.store_qty, 0)) AS store_qty, SUM(IFNULL(t1.return_qty, 0)) AS return_qty, t1.project_no, t1.work_order, 1 AS sort_flag, t1.item_code AS sort_item_code FROM pur_ship_detail t1,pur_ship_head t2 WHERE t1.uk_bill_uuid = t2.uu_id and t2.is_stock_return = 'N' " + condStrAll + " GROUP BY t2.pur_org, t1.apply_org, t2.vendor_name, t2.bill_status, t1.item_code, t1.item_name, t1.item_spec, t1.item_model, t1.pur_unit_name, t1.project_no, t1.work_order UNION ALL SELECT '小计' AS pur_org, '' AS apply_org, '' AS vendor_name, '' AS bill_status, t1.item_code, '' AS item_name, '' AS item_spec, '' AS item_model, '' AS pur_unit_name, SUM(IFNULL(t1.ship_qty, 0)) AS ship_qty, SUM(IFNULL(t1.inspection_qty, 0)) AS inspection_qty, SUM(IFNULL(t1.quality_qty, 0)) AS quality_qty, SUM(IFNULL(t1.store_qty, 0)) AS store_qty, SUM(IFNULL(t1.return_qty, 0)) AS return_qty, '' AS project_no, '' AS work_order, 2 AS sort_flag, t1.item_code AS sort_item_code FROM pur_ship_detail t1,pur_ship_head t2 WHERE t1.uk_bill_uuid = t2.uu_id and t2.is_stock_return = 'N' " + condStrAll + " GROUP BY t1.item_code UNION ALL SELECT '合计' AS pur_org, '' AS apply_org, '' AS vendor_name, '' AS bill_status, '' AS item_code, '' AS item_name, '' AS item_spec, '' AS item_model, '' AS pur_unit_name, SUM(IFNULL(t1.ship_qty, 0)) AS ship_qty, SUM(IFNULL(t1.inspection_qty, 0)) AS inspection_qty, SUM(IFNULL(t1.quality_qty, 0)) AS quality_qty, SUM(IFNULL(t1.store_qty, 0)) AS store_qty, SUM(IFNULL(t1.return_qty, 0)) AS return_qty, '' AS project_no, '' AS work_order, 3 AS sort_flag, null AS sort_item_code FROM pur_ship_detail t1,pur_ship_head t2 WHERE t1.uk_bill_uuid = t2.uu_id and t2.is_stock_return = 'N' " + condStrAll + " ) t_result ORDER BY CASE WHEN sort_item_code IS NULL THEN 1 ELSE 0 END, sort_item_code, sort_flag";
            List<Map<String, Object>> allStat = sqlService.getAll(selAll);
            int total = allStat != null ? allStat.size() : 0;
            int offsetInt = Integer.parseInt(offset);
            int sizeInt = Integer.parseInt(size);
            if (allStat != null && offsetInt < allStat.size()) {
                int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                allStat = allStat.subList(offsetInt, endIndex);
            } else {
                    allStat = new ArrayList<>();
                }
            Map<String, Object> result = new HashMap<>();
            result.put("rows", allStat);
            result.put("total", total);

            return R.ok().setdata(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }
    @Override
    public R getShipReturnData(List<Map<String, Object>> queryList, String offset, String size) {
        try {
            StringBuffer condStrAll = new StringBuffer();
            condStrAll.append(" and 1=1 ");
            if (queryList != null && !queryList.isEmpty()) {
                for (Map<String, Object> queryKey : queryList) {
                    if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                        String condStr =" ";
                        String andStr =" and ";
                        if(queryKey.get("field").toString().equals("pur_org") || queryKey.get("field").toString().equals("bill_status"))
                        {
                            andStr = " and t2.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                          } else {
                            andStr = " and t1.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                          }
                        condStrAll.append(condStr);
                    }
                }
            }
            String selAll = "SELECT pur_org, apply_org, vendor_name, bill_status, item_code, item_name, item_spec, item_model, pur_unit_name, ship_qty, inspection_qty, quality_qty, store_qty, return_qty, project_no, work_order FROM ( SELECT t2.pur_org, t1.apply_org, t2.vendor_name, t2.bill_status, t1.item_code, t1.item_name, t1.item_spec, t1.item_model, t1.pur_unit_name, SUM(IFNULL(t1.ship_qty, 0)) AS ship_qty, SUM(IFNULL(t1.inspection_qty, 0)) AS inspection_qty, SUM(IFNULL(t1.quality_qty, 0)) AS quality_qty, SUM(IFNULL(t1.store_qty, 0)) AS store_qty, SUM(IFNULL(t1.return_qty, 0)) AS return_qty, t1.project_no, t1.work_order, 1 AS sort_flag, t1.item_code AS sort_item_code FROM pur_ship_detail t1,pur_ship_head t2 WHERE t1.uk_bill_uuid = t2.uu_id and t2.is_stock_return = 'Y' " + condStrAll + " GROUP BY t2.pur_org, t1.apply_org, t2.vendor_name, t2.bill_status, t1.item_code, t1.item_name, t1.item_spec, t1.item_model, t1.pur_unit_name, t1.project_no, t1.work_order UNION ALL SELECT '小计' AS pur_org, '' AS apply_org, '' AS vendor_name, '' AS bill_status, t1.item_code, '' AS item_name, '' AS item_spec, '' AS item_model, '' AS pur_unit_name, SUM(IFNULL(t1.ship_qty, 0)) AS ship_qty, SUM(IFNULL(t1.inspection_qty, 0)) AS inspection_qty, SUM(IFNULL(t1.quality_qty, 0)) AS quality_qty, SUM(IFNULL(t1.store_qty, 0)) AS store_qty, SUM(IFNULL(t1.return_qty, 0)) AS return_qty, '' AS project_no, '' AS work_order, 2 AS sort_flag, t1.item_code AS sort_item_code FROM pur_ship_detail t1,pur_ship_head t2 WHERE t1.uk_bill_uuid = t2.uu_id and t2.is_stock_return = 'Y' " + condStrAll + " GROUP BY t1.item_code UNION ALL SELECT '合计' AS pur_org, '' AS apply_org, '' AS vendor_name, '' AS bill_status, '' AS item_code, '' AS item_name, '' AS item_spec, '' AS item_model, '' AS pur_unit_name, SUM(IFNULL(t1.ship_qty, 0)) AS ship_qty, SUM(IFNULL(t1.inspection_qty, 0)) AS inspection_qty, SUM(IFNULL(t1.quality_qty, 0)) AS quality_qty, SUM(IFNULL(t1.store_qty, 0)) AS store_qty, SUM(IFNULL(t1.return_qty, 0)) AS return_qty, '' AS project_no, '' AS work_order, 3 AS sort_flag, null AS sort_item_code FROM pur_ship_detail t1,pur_ship_head t2 WHERE t1.uk_bill_uuid = t2.uu_id and t2.is_stock_return = 'Y' " + condStrAll + " ) t_result ORDER BY CASE WHEN sort_item_code IS NULL THEN 1 ELSE 0 END, sort_item_code, sort_flag ";
            List<Map<String, Object>> allStat = sqlService.getAll(selAll);
            int total = allStat != null ? allStat.size() : 0;
            int offsetInt = Integer.parseInt(offset);
            int sizeInt = Integer.parseInt(size);
            if (allStat != null && offsetInt < allStat.size()) {
                int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                allStat = allStat.subList(offsetInt, endIndex);
            } else {
                    allStat = new ArrayList<>();
                }
            Map<String, Object> result = new HashMap<>();
            result.put("rows", allStat);
            result.put("total", total);
            return R.ok().setdata(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    @Override
    public R getOrderData(List<Map<String, Object>> queryList, String offset, String size) {
        try {
            StringBuffer condStrAll = new StringBuffer();
            condStrAll.append(" and 1=1 ");
            if (queryList != null && !queryList.isEmpty()) {
                for (Map<String, Object> queryKey : queryList) {
                    if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                        String condStr =" ";
                        String andStr =" and ";
                        if(queryKey.get("field").toString().equals("pur_org") || queryKey.get("field").toString().equals("bill_status"))
                        {
                            andStr = " and t2.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                             } else {
                            andStr = " and t1.";
                            String operator = queryKey.get("operator").toString();
                            if("=".equals(operator) || "<>".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
                            }
                            if("like".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
                            }
                            if("notLike".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
                            }
                            if("in".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
                            }
                            if("notIn".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
                            }
                            if("isNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + " is null ";
                            }
                            if("notIsNull".equals(operator)) {
                                condStr = andStr + queryKey.get("field") + "  is not null ";
                            }
                          }
                        condStrAll.append(condStr);
                    }
                }
            }
            String selAll = "SELECT pur_org,apply_org,vendor_name,role_name,buyer_person_man,bill_status,item_code,item_name,item_spec,item_model,pur_qty,pur_unit_name,ship_qty,quality_qty,store_qty,return_qty,bill_date,project_no,work_order FROM(SELECT t2.pur_org,t1.apply_org,t2.vendor_name,t2.role_name,t2.buyer_person_man,t2.bill_status,t1.item_code,t1.item_name,t1.item_spec,t1.item_model,SUM(IFNULL(t1.pur_qty,0))AS pur_qty,t1.pur_unit_name,SUM(IFNULL(t1.ship_qty,0))AS ship_qty,SUM(IFNULL(t1.quality_qty,0))AS quality_qty,SUM(IFNULL(t1.store_qty,0))AS store_qty,SUM(IFNULL(t1.return_qty,0))AS return_qty,t2.bill_date,t1.project_no,t1.work_order,1 AS sort_flag,t1.item_code AS sort_item_code FROM pur_order_detail t1,pur_order_head t2 WHERE t1.uk_order_uuid=t2.uu_id AND t1.uk_order_no=t2.uk_order_no " + condStrAll + "  GROUP BY t2.pur_org,t1.apply_org,t2.vendor_name,t2.role_name,t2.buyer_person_man,t2.bill_status,t1.item_code,t1.item_name,t1.item_spec,t1.item_model,t1.pur_unit_name,t2.bill_date,t1.project_no,t1.work_order UNION ALL SELECT '小计'AS pur_org,''AS apply_org,''AS vendor_name,''AS role_name,''AS buyer_person_man,''AS bill_status,t1.item_code,''AS item_name,''AS item_spec,''AS item_model,SUM(IFNULL(t1.pur_qty,0))AS pur_qty,''AS pur_unit_name,SUM(IFNULL(t1.ship_qty,0))AS ship_qty,SUM(IFNULL(t1.quality_qty,0))AS quality_qty,SUM(IFNULL(t1.store_qty,0))AS store_qty,SUM(IFNULL(t1.return_qty,0))AS return_qty,''AS bill_date,''AS project_no,''AS work_order,2 AS sort_flag,t1.item_code AS sort_item_code FROM pur_order_detail t1,pur_order_head t2 WHERE t1.uk_order_uuid=t2.uu_id AND t1.uk_order_no=t2.uk_order_no " + condStrAll + "  GROUP BY t1.item_code UNION ALL SELECT '合计'AS pur_org,''AS apply_org,''AS vendor_name,''AS role_name,''AS buyer_person_man,''AS bill_status,''AS item_code,''AS item_name,''AS item_spec,''AS item_model,SUM(IFNULL(t1.pur_qty,0))AS pur_qty,''AS pur_unit_name,SUM(IFNULL(t1.ship_qty,0))AS ship_qty,SUM(IFNULL(t1.quality_qty,0))AS quality_qty,SUM(IFNULL(t1.store_qty,0))AS store_qty,SUM(IFNULL(t1.return_qty,0))AS return_qty,''AS bill_date,''AS project_no,''AS work_order,3 AS sort_flag,NULL AS sort_item_code FROM pur_order_detail t1,pur_order_head t2 WHERE t1.uk_order_uuid=t2.uu_id AND t1.uk_order_no=t2.uk_order_no " + condStrAll + "  )t_result ORDER BY CASE WHEN sort_item_code IS NULL THEN 1 ELSE 0 END,sort_item_code,sort_flag ";
            List<Map<String, Object>> allStat = sqlService.getAll(selAll);
            int total = allStat != null ? allStat.size() : 0;
            int offsetInt = Integer.parseInt(offset);
            int sizeInt = Integer.parseInt(size);
            if (allStat != null && offsetInt < allStat.size()) {
                int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                allStat = allStat.subList(offsetInt, endIndex);
            } else {
                    allStat = new ArrayList<>();
                }
            Map<String, Object> result = new HashMap<>();
            result.put("rows", allStat);
            result.put("total", total);

            return R.ok().setdata(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }
}
