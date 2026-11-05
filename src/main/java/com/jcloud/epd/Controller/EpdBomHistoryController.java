package com.jcloud.epd.Controller;
import cn.hutool.json.JSONUtil;
import com.jcloud.epd.entity.EpdBomHistory;
import com.jcloud.epd.service.AddbomHistoryService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/bomHistory")
public class EpdBomHistoryController {
    @Autowired
    private AddbomHistoryService addbomHistoryService;
    /**
     * 历史数据插入
     */
    @RequestMapping(value="/deletehistory",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody Map<String, Object> params) {
        List<List<Map<String,Object>>> listData =JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return  addbomHistoryService.delHistoryObj(listData);
    }
    @RequestMapping(value="/inserthistory",method = RequestMethod.POST)
    public R insertHistory(@RequestBody EpdBomHistory params) {
        return  addbomHistoryService.insertHistoryObj(params);
    }
    @RequestMapping(value="/copythistory",method = RequestMethod.POST)
    public R copyHistory(@RequestBody EpdBomHistory params) {
        return  addbomHistoryService.copyHistoryObj(params);
    }

}
