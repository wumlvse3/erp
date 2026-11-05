package com.jcloud.erp.epd.service.Impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcloud.erp.epd.entity.EpdItemMasterHistory;
import com.jcloud.erp.epd.mapper.EpdItemMapperHistory;
import com.jcloud.erp.epd.service.AdditemHistoryService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdditemHistoryServiceimpl extends ServiceImpl<EpdItemMapperHistory, EpdItemMasterHistory> implements AdditemHistoryService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AdditemHistoryService.class);
    @Autowired
    private EpdItemMapperHistory epdItemMapperHistory;
    @Resource
    private SqlService sqlService;
    @Transactional
    public R addObj(List<List<Map<String,Object>>> params) {
        List<Map<String, Object>> itemsData = params.get(0);
        List<EpdItemMasterHistory> rulinsertBeans=new ArrayList<EpdItemMasterHistory>();
        List<EpdItemMasterHistory> insertBeans = itemsData.stream()
                .map(itemMap -> new ObjectMapper().convertValue(itemMap, EpdItemMasterHistory.class))
                .collect(Collectors.toList());
        List<String> errorLines = new ArrayList<>();
        for(EpdItemMasterHistory item:insertBeans){
             Long thecount=sqlService.getCount("SELECT item_code from (" +
                                                      "    SELECT item_code FROM pur_order_detail where item_code='" + item.getItem_code() + "'" +
                                                      "    UNION" +
                                                      "    SELECT item_code FROM pur_agreement_detail where item_code='" + item.getItem_code() + "'" +
                                                      "    UNION" +
                                                      "    SELECT item_code FROM pur_ship_detail pur_purchase_plan_detail where item_code='" + item.getItem_code() + "'" +
                                                      "     UNION" +
                                                      "    SELECT item_code FROM pur_ship_detail pur_apply_detail where item_code='" + item.getItem_code() + "'" +
                                                      "     UNION" +
                                                      "    SELECT item_code FROM pur_ship_detail ord_order_detail where item_code='" + item.getItem_code() + "'" +
                                                      "     UNION" +
                                                      "    SELECT item_code FROM pur_ship_detail ord_agreement_detail where item_code='" + item.getItem_code() + "'" +
                                                      "     UNION" +
                                                      "    SELECT item_code FROM pur_ship_detail ord_invoice_detail where item_code='" + item.getItem_code() + "'" +
                                                      "    " +
                                                      ")  theable");
             if(thecount>0){
                 errorLines.add("物料编码:"+item.getItem_code() +"已使用，不能删除！");
                 continue;
             }
            Long maxSeqNo = sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_item_master_history where item_code ='" + item.getItem_code() + "' and inv_org='" + item.getInv_org() + "'");
            item.setUu_id(null);
            item.setSeq_no(String.valueOf(maxSeqNo + 1)); // Changed from item.setSeq_no(maxSeqNo + 1); to item.setSeq_no(String.valueOf(maxSeqNo + 1));
            item.setOperate_type("删除");
            sqlService.updateOne("update epd_routing_head set item_code=null where item_code='"+item.getItem_code() +"'");
            sqlService.deleteOne("delete from epd_item_master where item_code='"+item.getItem_code() +"' and inv_org='" + item.getInv_org() + "'");
            rulinsertBeans.add(item);
        }
        boolean flag=this.saveBatch(rulinsertBeans);
        if(errorLines.size()>0){
            return R.error(String.join("\n", errorLines));
        }
        if (flag){
            return R.ok("删除成功");
        }else{
            return R.error("删除失败");
        }

    }

    @Override
    public R insertHistoryObj(EpdItemMasterHistory item) {
            Long maxSeqNo =     sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_item_master_history where item_code ='" + item.getItem_code() + "' and inv_org='" + item.getInv_org() + "'");
            item.setUu_id(null);
            if(maxSeqNo>0){
                item.setSeq_no(String.valueOf(maxSeqNo + 1)); // Changed from item.setSeq_no(maxSeqNo + 1); to item.setSeq_no(String.valueOf(maxSeqNo + 1));
                item.setOperate_type("修改");
            }else{
                item.setSeq_no(String.valueOf(maxSeqNo + 1)); // Changed from item.setSeq_no(maxSeqNo + 1); to item.setSeq_no(String.valueOf(maxSeqNo + 1));
                item.setOperate_type("新增");
            }
        boolean flag=this.save(item);

        if (flag){
            sqlService.updateOne("update epd_item_master_history t set file_path=(select max(file_path) from epd_item_master t2 where item_code ='" + item.getItem_code() +"' and inv_org='" + item.getInv_org() + "') where item_code ='" + item.getItem_code() +"' and inv_org='" + item.getInv_org() +"' and seq_no="+(maxSeqNo+1));
            return R.ok("添加成功");
        }else{
            return R.error("历史数据添加失败");
        }
    }

    @Override
    public R copyHistoryObj(EpdItemMasterHistory item) {
        Long maxSeqNo =     sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_item_master_history where item_code ='" + item.getItem_code() + "' and inv_org='" + item.getInv_org() + "'");
        item.setUu_id(null);
            item.setSeq_no(String.valueOf(maxSeqNo + 1)); // Changed from item.setSeq_no(maxSeqNo + 1); to item.setSeq_no(String.valueOf(maxSeqNo + 1));
            item.setOperate_type("复制");
        boolean flag=this.save(item);
        if (flag){
            sqlService.updateOne("update epd_item_master_history t set file_path=(select max(file_path) from epd_item_master t2 where item_code ='" + item.getItem_code() +"' and inv_org='" + item.getInv_org() + "') where item_code ='" + item.getItem_code() +"' and inv_org='" + item.getInv_org() +"' and seq_no="+(maxSeqNo+1));
            return R.ok("添加成功");
        }else{
            return R.error("历史数据添加失败");
        }
    }


}
