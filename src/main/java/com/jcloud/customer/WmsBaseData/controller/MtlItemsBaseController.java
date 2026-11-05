package com.jcloud.customer.WmsBaseData.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.customer.WmsBaseData.service.MtlItemsBaseService;
import com.jcloud.customer.WmsBaseData.service.MtlItemsOrgService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mtlItems")
public class MtlItemsBaseController {
    @Autowired
    MtlItemsBaseService mtlItemsBaseService;
    @Autowired
    MtlItemsOrgService mtlItemsOrgService;
    @RequestMapping(value = "addOrgItems",method = RequestMethod.POST)
    public R addOrgItems(@RequestBody String listdatasJson){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(listdatasJson).toBean(List.class);
        return mtlItemsBaseService.addOrgItems(list);
    }
    @RequestMapping(value = "addAllOrgItems",method = RequestMethod.POST)
    public R addAllOrgItems(@RequestBody String listdatasJson){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(listdatasJson).toBean(List.class);
        return mtlItemsBaseService.addAllOrgItems(list);
    }
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public R update(@RequestBody Map<String,Object> params){
        return mtlItemsBaseService.update(params);
    }


    @RequestMapping(value = "/saveItem",method = RequestMethod.POST)
    public R saveItem(@RequestBody Map<String,Object> params){
        return mtlItemsOrgService.saveItem(params);
    }

}
