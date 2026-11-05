package com.jcloud.erp.pur.controller;
import com.jcloud.erp.pur.entity.PurAgreementHead;
import com.jcloud.erp.pur.service.PurAgreementHeadService;
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
@RequestMapping("/puragreementhead")
public class PurAgreementHeadController {
    @Autowired
    private PurAgreementHeadService service;
    /**
     * 提交
     */
    @RequestMapping(value="/submit",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody List<PurAgreementHead> params) {
        return  service.addObj(params);
    }
    @RequestMapping(value="/unsubmit",method = RequestMethod.POST)
    public R unacceptTaskByCode(@RequestBody List<PurAgreementHead> params) {
        return  service.unaddObj(params);
    }
    @RequestMapping(value="/detaillist",method = RequestMethod.POST)
    public R detaillist(@RequestBody List<PurAgreementHead> params) {
        return  service.detaillist(params);
    }
    @RequestMapping(value="/orderCreate",method = RequestMethod.POST)
    public R orderCreate(@RequestBody List<PurAgreementHead> params) {
        return  service.orderCreate(params);
    }
    @RequestMapping(value="/detailCreate",method = RequestMethod.POST)
    public R detailCreate(@RequestBody List<PurAgreementHead> params) {
        return  service.detailCreate(params);
    }
    @RequestMapping(value="/zjdetailCreate",method = RequestMethod.POST)
    public R zjdetailCreate(@RequestBody List<PurAgreementHead> params) {
        return  service.zjdetailCreate(params);
    }
    /**
     * 头启用
     */
    @RequestMapping(value="/enable",method = RequestMethod.POST)
    public R enable(@RequestBody List<PurAgreementHead> params) {
        return  service.enable(params);
    }
    /**
     * 头关闭
     */
    @RequestMapping(value="/disabl",method = RequestMethod.POST)
    public R disabl(@RequestBody List<PurAgreementHead> params) {
        return  service.disabl(params);
    }
    /**
     * 明细启用
     */
    @RequestMapping(value="/detailenable",method = RequestMethod.POST)
    public R detailenable(@RequestBody List<PurAgreementHead> params) {
        return  service.detailenable(params);
    }
    /**
     * 明细关闭
     */
    @RequestMapping(value="/detaildisabl",method = RequestMethod.POST)
    public R detaildisabl(@RequestBody List<PurAgreementHead> params) {
        return  service.detaildisabl(params);
    }

}
