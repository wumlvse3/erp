package com.jcloud.erp.epd.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.epd.service.EpdProjectService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/epdProject")
public class EpdProjectController {
    @Autowired
    EpdProjectService epdProjectService;
    
    @RequestMapping(value = "addOrgItems",method = RequestMethod.POST)
    public R addOrgItems(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return epdProjectService.addOrgItems(list);
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public R update(@RequestBody Map<String,Object> params){
        return epdProjectService.update(params);
    }

    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public R delete(@RequestBody Map<String,Object> params){
        return epdProjectService.delete(params);
    }

}
