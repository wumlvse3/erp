package com.jcloud.epd.service.impl;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.epd.entity.EpdItemMaster;
import com.jcloud.epd.entity.EpdItemMasterHistory;
import com.jcloud.epd.mapper.EpdItemMapper;
import com.jcloud.epd.mapper.EpdItemMapperHistory;
import com.jcloud.epd.service.AdditemService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;

@Service
public class AdditemServiceimpl extends ServiceImpl<EpdItemMapper, EpdItemMaster> implements AdditemService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AdditemService.class);
    @Resource
    private SqlService sqlService;
    @Autowired
    private EpdItemMapper epdItemMapper;

    @Transactional
    public R addOrgItems(List<List<Map<String, Object>>> list){
        try{
            List<Map<String, Object>> orgData = list.get(0);
            List<Map<String, Object>> itemsData = list.get(1);
            List<EpdItemMaster> insertBeans=new ArrayList<>();
            UserEntity user = ShiroUtils.getUserEntity();
            List<String> insertString = new ArrayList<>();
            for(Map<String,Object> items:itemsData){
                EpdItemMaster EpdItemMaster0 = epdItemMapper.selectOne(new QueryWrapper<EpdItemMaster>().eq("uu_id", items.get("uu_id").toString()));
                for(Map<String,Object> orgMap:orgData){
                    String itemCode = items.get("item_code").toString();
                    String orgCode= orgMap.get("org_code").toString();
                    QueryWrapper<EpdItemMaster> EpdItemMasterWrapper = new QueryWrapper();
                    EpdItemMasterWrapper.eq("inv_org",orgCode);
                    EpdItemMasterWrapper.eq("item_code",itemCode);

                    EpdItemMaster EpdItemMaster = epdItemMapper.selectOne(EpdItemMasterWrapper);
                    if(EpdItemMaster==null){
                        EpdItemMaster = new EpdItemMaster();
                        BeanUtils.copyProperties(EpdItemMaster0, EpdItemMaster);
                        EpdItemMaster.setUuId(null);
                        EpdItemMaster.setGroupId(null);
                        EpdItemMaster.setCreateTime(new Date());
                        EpdItemMaster.setCreateUserId(user.getUserId().toString());
                        EpdItemMaster.setModifyTime(null);
                        EpdItemMaster.setModifyUserId(null);
                        EpdItemMaster.setInvOrg(orgCode);
                        EpdItemMaster.setCreateType("F");
                        EpdItemMaster.setAssignUserId(user.getUserId());
                        EpdItemMaster.setAssignUserMan(user.getUsername());
                        EpdItemMaster.setAssignFromOrg(items.get("inv_org").toString());
                        EpdItemMaster.setAssignTime(new Date());
                        EpdItemMaster.setInvOrg(orgMap.get("org_code").toString());
                        insertBeans.add(EpdItemMaster);
                        insertString.add("insert into epd_item_master_history( group_id, create_user_id, create_time, modify_user_id, modify_time, flow_id, is_deleted, inv_org, select_flag, seq_no, operate_type, item_code, item_name, item_abb, item_spec, item_model, draw_no, nation_code, mnemonic_code, ref_text, item_size, size_unit, item_weight, weight_unit, lead_time, lead_time_type, lead_dynamic, plan_makeup, llc, lot_policy, lot_qty, lot_size, item_type, out_op_db, m_p_flag, mps_flag, mrp_flag, abc_code, item_class, pur_class, sale_class, stock_class, quality_class, finance_class, quality_flag, specialized_class_code, gl_class, variance_code, std_cost, unit_price, plan_price, actual_cost, price_system, planner_code, buyer_code, order_point, reorder_qty, pur_unit, sale_unit, stock_unit, assist_unit, second_unit, valid_stock_days, validity_day, dept_code, routing_no, bulk_issue_id, scrap_rate, warehouse, bin, outbound_mode, lot_flag, validity_flag, single_flag, safety_stock, max_stock, min_stock, cycle_count, tolenance, tax_code, round_factor, unit_factor_p, unit_factor_s, unit_factor_2, assign_user_id, assign_user_code, assign_user_man, assign_time, assign_from_org, master_part_flag, op_pick_flag, limite_flag, excess_material, backflush_flag, backflush_warehouse, backflush_bin, routing_flag, shop_flag, out_op_id, master_part_class, qc_track_type, norm_hour, special_code, deviation_days, if_nosum_lot, pur_flag, sale_flag, if_merge_mp, if_merge_work_order, integrity_flag, valid_flag, create_type, pur_group_ratio, pur_group_flag, pur_org,file_path) select  group_id, create_user_id, create_time, modify_user_id, modify_time, flow_id, is_deleted, inv_org, select_flag, (select ifnull(max(seq_no),0)+1 from epd_item_master_history where item_code='"+EpdItemMaster.getItemCode()+"' and inv_org='"+orgMap.get("org_code")+"' ),'新增', item_code, item_name, item_abb, item_spec, item_model, draw_no, nation_code, mnemonic_code, ref_text, item_size, size_unit, item_weight, weight_unit, lead_time, lead_time_type, lead_dynamic, plan_makeup, llc, lot_policy, lot_qty, lot_size, item_type, out_op_db, m_p_flag, mps_flag, mrp_flag, abc_code, item_class, pur_class, sale_class, stock_class, quality_class, finance_class, quality_flag, specialized_class_code, gl_class, variance_code, std_cost, unit_price, plan_price, actual_cost, price_system, planner_code, buyer_code, order_point, reorder_qty, pur_unit, sale_unit, stock_unit, assist_unit, second_unit, valid_stock_days, validity_day, dept_code, routing_no, bulk_issue_id, scrap_rate, warehouse, bin, outbound_mode, lot_flag, validity_flag, single_flag, safety_stock, max_stock, min_stock, cycle_count, tolenance, tax_code, round_factor, unit_factor_p, unit_factor_s, unit_factor_2, assign_user_id, assign_user_code, assign_user_man, assign_time, assign_from_org, master_part_flag, op_pick_flag, limite_flag, excess_material, backflush_flag, backflush_warehouse, backflush_bin, routing_flag, shop_flag, out_op_id, master_part_class, qc_track_type, norm_hour, special_code, deviation_days, if_nosum_lot, pur_flag, sale_flag, if_merge_mp, if_merge_work_order, integrity_flag, valid_flag, create_type, pur_group_ratio, pur_group_flag, pur_org,file_path from epd_item_master where item_code='"+EpdItemMaster.getItemCode()+"' and inv_org='"+orgMap.get("org_code")+"' ");
                    }
                }
            }
            this.saveBatch(insertBeans);
            sqlService.insertList(insertString);
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error(e.getMessage());
        }
    }
}
