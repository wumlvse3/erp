package com.jcloud.erp.epd.service.Impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcloud.erp.epd.entity.EpdRoutingDetailHistory;
import com.jcloud.erp.epd.mapper.EpdRoutingDetailMapperHistory;
import com.jcloud.erp.epd.service.AddroutingDetailHistoryService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddepdRoutingDetailServiceimpl extends ServiceImpl<EpdRoutingDetailMapperHistory, EpdRoutingDetailHistory> implements AddroutingDetailHistoryService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AddepdRoutingDetailServiceimpl.class);
    @Resource
    private SqlService sqlService;
    @Transactional
    public R delHistoryObj(List<List<Map<String,Object>>> params) {
        List<Map<String, Object>> itemsData = params.get(0);
        List<EpdRoutingDetailHistory> rulinsertBeans=new ArrayList<EpdRoutingDetailHistory>();
        List<EpdRoutingDetailHistory> insertBeans = itemsData.stream()
                .map(itemMap -> new ObjectMapper().convertValue(itemMap, EpdRoutingDetailHistory.class))
                .collect(Collectors.toList());
        List<String> errorLines = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for(EpdRoutingDetailHistory item:insertBeans){
            sqlService.deleteOne("delete from epd_routing_detail where uu_id='" + item.getUuId() + "'");
            int maxSeqNo = sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_routing_detail_history where routing_no='" + item.getRoutingNo()+ "' and op_no='" +item.getOpNo()+"' and inv_org='" + item.getInvOrg() + "'").intValue();
            item.setOperateType("删除");
            item.setUuId(null);
            item.setOperateUserId(user.getUserId().toString());
            item.setOperateUserCode(user.getUsername());
            item.setOperateUserName(user.getName());
            item.setOperateTime(new Date());
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
    public R insertHistoryObj(EpdRoutingDetailHistory item) {
        int maxSeqNo =     sqlService.getOneInt("select ifnull(max(seq_no),0) from epd_routing_detail_history where  routing_no='" + item.getRoutingNo()+ "' and op_no='" +item.getOpNo()+"'   and inv_org='" + item.getInvOrg() + "'").intValue();
            item.setUuId(null);
        UserEntity user = ShiroUtils.getUserEntity();
        item.setOperateUserId(user.getUserId().toString());
        item.setOperateUserCode(user.getUsername());
        item.setOperateUserName(user.getName());
        item.setOperateTime(new Date());
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



}
