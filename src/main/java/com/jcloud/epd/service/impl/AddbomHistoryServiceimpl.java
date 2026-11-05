package com.jcloud.epd.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcloud.epd.entity.EpdBomHistory;
import com.jcloud.epd.mapper.EpdbomMapperHistory;
import com.jcloud.epd.service.AddbomHistoryService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddbomHistoryServiceimpl extends ServiceImpl<EpdbomMapperHistory, EpdBomHistory> implements AddbomHistoryService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AddbomHistoryServiceimpl.class);
    @Resource
    private SqlService sqlService;
    @Transactional
    public R delHistoryObj(List<List<Map<String,Object>>> params) {
        List<Map<String, Object>> itemsData = params.get(0);
        List<EpdBomHistory> rulinsertBeans=new ArrayList<EpdBomHistory>();
        List<EpdBomHistory> insertBeans = itemsData.stream()
                .map(itemMap -> new ObjectMapper().convertValue(itemMap, EpdBomHistory.class))
                .collect(Collectors.toList());
        List<String> errorLines = new ArrayList<>();
        for(EpdBomHistory item:insertBeans){
            Long thecount=sqlService.getCount("SELECT parent_code from epd_bom where  parent_code='" + item.getComponentCode() + "' and inv_org='" + item.getInvOrg() + "'");
            if(thecount>0){
                errorLines.add("子项编码:"+item.getComponentCode() +"存在子项，请先删除其下面子项！");
                continue;
            }
            sqlService.deleteOne("delete from epd_bom where uu_id='" + item.getUu_id() + "'");
            int maxSeqNo = sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_bom_history where parent_code ='" + item.getParentCode() + "' and component_code='" + item.getComponentCode() + "' and inv_org='" + item.getInvOrg() + "'").intValue();
            item.setOperateType("删除");
            item.setUu_id(null);
            item.setSeqNo(maxSeqNo + 1);
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
    public R insertHistoryObj(EpdBomHistory item) {
        int maxSeqNo =     sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_bom_history where parent_code ='" + item.getParentCode() + "' and component_code='" + item.getComponentCode() + "' and inv_org='" + item.getInvOrg() + "'").intValue();
            item.setUu_id(null);
            if(maxSeqNo>0){
                item.setSeqNo(maxSeqNo + 1);
                item.setOperateType("修改");
            }else{
                item.setSeqNo(maxSeqNo + 1);
                item.setOperateType("新增");
            }
        boolean flag=this.save(item);
        if (flag){
            return R.ok("添加成功");
        }else{
            return R.error("历史数据添加失败");
        }
    }

    @Override
    public R copyHistoryObj(EpdBomHistory item) {
        int maxSeqNo =     sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_bom_history where parent_code ='" + item.getParentCode() + "' and component_code='" + item.getComponentCode() + "' and inv_org='" + item.getInvOrg() + "'").intValue();
        item.setUu_id(null);
            item.setSeqNo(maxSeqNo + 1);
            item.setOperateType ("复制");
        boolean flag=this.save(item);
        if (flag){
            return R.ok("添加成功");
        }else{
            return R.error("历史数据添加失败");
        }
    }


}
