package com.jcloud.pur.Controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.epd.service.AdditemService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/itemcode")
public class PurItemController {
    @Autowired
    private AdditemService service;
    @RequestMapping(value = "addOrgItems",method = RequestMethod.POST)
    public R addOrgItems(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return service.addOrgItems(list);
    }
}
