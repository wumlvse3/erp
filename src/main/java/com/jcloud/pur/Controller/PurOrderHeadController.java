package com.jcloud.pur.Controller;

import com.jcloud.pur.entity.PurOrderHead;
import com.jcloud.pur.service.PurOrderHeadService;
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
@RequestMapping("/purorderhead")
public class PurOrderHeadController {
    @Autowired
    private PurOrderHeadService service;
    /**
     * 确定
     */
    @RequestMapping(value="/submit",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody List<PurOrderHead> params) {
        return  service.addObj(params);
    }
    @RequestMapping(value="/unsubmit",method = RequestMethod.POST)
    public R unacceptTaskByCode(@RequestBody List<PurOrderHead> params) {

        return  service.unaddObj(params);
    }
    @RequestMapping(value="/auditlist",method = RequestMethod.POST)
    public R auditlist(@RequestBody List<PurOrderHead> params) {
        return  service.auditlist(params);
    }
    @RequestMapping(value="/passlist",method = RequestMethod.POST)
    public R passlist(@RequestBody List<PurOrderHead> params) {
        return  service.passlist(params);
    }
    @RequestMapping(value="/bhpasslist",method = RequestMethod.POST)
    public R bhpasslist(@RequestBody List<PurOrderHead> params) {
        return  service.bhpasslist(params);
    }
    @RequestMapping(value="/auditdetaillist",method = RequestMethod.POST)
    public R auditdetaillist(@RequestBody List<PurOrderHead> params) {
        return  service.auditdetaillist(params);
    }
    @RequestMapping(value="/passdetaillist",method = RequestMethod.POST)
    public R passdetaillist(@RequestBody List<PurOrderHead> params) {
        return  service.passdetaillist(params);
    }

    @RequestMapping(value="/detaillist",method = RequestMethod.POST)
    public R detaillist(@RequestBody List<PurOrderHead> params) {

        return  service.detaillist(params);
    }
    @RequestMapping(value="/createHead",method = RequestMethod.POST)
    public R createHead(@RequestBody List<PurOrderHead> params) {

        return  service.createHead(params);
    }
    @RequestMapping(value="/zjDrDetail",method = RequestMethod.POST)
    public R zjDrDetail(@RequestBody List<PurOrderHead> params) {

        return  service.zjDrDetail(params);
    }

    @RequestMapping(value="/zjThDetail",method = RequestMethod.POST)
    public R zjThDetail(@RequestBody List<PurOrderHead> params) {

        return  service.zjThDetail(params);
    }



    @RequestMapping(value="/createDetail",method = RequestMethod.POST)
    public R createDetail(@RequestBody List<PurOrderHead> params) {

        return  service.createDetail(params);
    }

    @RequestMapping(value="/createDrHead",method = RequestMethod.POST)
    public R createDrHead(@RequestBody List<PurOrderHead> params) {

        return  service.createDrHead(params);
    }
    @RequestMapping(value="/createDrDetail",method = RequestMethod.POST)
    public R createDrDetail(@RequestBody List<PurOrderHead> params) {

        return  service.createDrDetail(params);
    }
    /**
     * 头启用
     */
    @RequestMapping(value="/enable",method = RequestMethod.POST)
    public R enable(@RequestBody List<PurOrderHead> params) {
        return  service.enable(params);
    }
    /**
     * 头关闭
     */
    @RequestMapping(value="/disabl",method = RequestMethod.POST)
    public R disabl(@RequestBody List<PurOrderHead> params) {
        return  service.disabl(params);
    }
    /**
     * 明细启用
     */
    @RequestMapping(value="/detailenable",method = RequestMethod.POST)
    public R detailenable(@RequestBody List<PurOrderHead> params) {
        return  service.detailenable(params);
    }
    /**
     * 明细关闭
     */
    @RequestMapping(value="/detaildisabl",method = RequestMethod.POST)
    public R detaildisabl(@RequestBody List<PurOrderHead> params) {
        return  service.detaildisabl(params);
    }

}
