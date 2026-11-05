package com.jcloud.epd.Controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.epd.entity.EpdRoutingHeadHistory;
import com.jcloud.epd.service.AddroutingHistoryService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/routingHistory")
public class EpdRoutingHeadHistoryController {
    @Autowired
    private AddroutingHistoryService addroutingHistoryService;
    /**
     * 历史数据插入
     */
    @RequestMapping(value="/deletehistory",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody Map<String, Object> params) {
        List<List<Map<String,Object>>> listData =JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return  addroutingHistoryService.delHistoryObj(listData);
    }
    @RequestMapping(value="/inserthistory",method = RequestMethod.POST)
    public R insertHistory(@RequestBody EpdRoutingHeadHistory params) {
        return  addroutingHistoryService.insertHistoryObj(params);
    }

}
