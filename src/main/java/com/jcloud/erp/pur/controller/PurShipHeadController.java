package com.jcloud.erp.pur.controller;

import com.jcloud.erp.pur.entity.PurShipHead;
import com.jcloud.erp.pur.service.PurShipHeadService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/shiphead")
public class PurShipHeadController {
    @Autowired
    private PurShipHeadService service;
    /**
     * 确定
     */
    @RequestMapping(value="/submit",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody List<PurShipHead> params) {
        return  service.passlist(params);
    }
    @RequestMapping(value="/passlist",method = RequestMethod.POST)
    public R passlist(@RequestBody List<PurShipHead> params) {
        return  service.addObj(params);
    }
    @RequestMapping(value="/detaillist",method = RequestMethod.POST)
    public R detaillist(@RequestBody List<PurShipHead> params) {
        return  service.detaillist(params);
    }
    @RequestMapping(value="/Drsubmit",method = RequestMethod.POST)
    public R dracceptTaskByCode(@RequestBody List<PurShipHead> params) {
        return  service.drpasslist(params);
    }
    @RequestMapping(value="/Drpasslist",method = RequestMethod.POST)
    public R drpasslist(@RequestBody List<PurShipHead> params) {
        return  service.draddObj(params);
    }
    /**
     * 头启用
     */
    @RequestMapping(value="/enable",method = RequestMethod.POST)
    public R enable(@RequestBody List<PurShipHead> params) {
        return  service.enable(params);
    }
    /**
     * 头关闭
     */
    @RequestMapping(value="/disabl",method = RequestMethod.POST)
    public R disabl(@RequestBody List<PurShipHead> params) {
        return  service.disabl(params);
    }
    /**
     * 明细启用
     */
    @RequestMapping(value="/detailenable",method = RequestMethod.POST)
    public R detailenable(@RequestBody List<PurShipHead> params) {
        return  service.detailenable(params);
    }
    /**
     * 明细关闭
     */
    @RequestMapping(value="/detaildisabl",method = RequestMethod.POST)
    public R detaildisabl(@RequestBody List<PurShipHead> params) {
        return  service.detaildisabl(params);
    }
}
