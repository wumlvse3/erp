package com.jcloud.erp.ord.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.ord.service.OrdAgreementHeadService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordAgreementHead")
public class OrdAgreementHeadController {
    @Autowired
    OrdAgreementHeadService ordAgreementHeadService;

    @RequestMapping(value = "updateSubmit",method = RequestMethod.POST)
    public R updateSubmit(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return ordAgreementHeadService.updateSubmit(list);
    }

    @RequestMapping(value = "active",method = RequestMethod.POST)
    public R active(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isActive=params.get("isActive");
        return ordAgreementHeadService.active(list,isActive);
    }

    @RequestMapping(value = "activeDetail",method = RequestMethod.POST)
    public R activeDetail(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isActive=params.get("isActive");
        return ordAgreementHeadService.activeDetail(list,isActive);
    }
}