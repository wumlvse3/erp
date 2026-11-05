package com.jcloud.erp.epd.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.epd.service.EpdCustomerDefService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/epdCustomerDef")
public class EpdCustomerDefController {
    @Autowired
    EpdCustomerDefService epdCustomerDefService;
    
    @RequestMapping(value = "addOrgItems",method = RequestMethod.POST)
    public R addOrgItems(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return epdCustomerDefService.addOrgItems(list);
    }
    
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public R update(@RequestBody Map<String,Object> params){
        return epdCustomerDefService.update(params);
    }

}
