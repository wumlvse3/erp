package com.jcloud.pur.Controller;
import com.jcloud.pur.entity.PurApplyHead;
import com.jcloud.pur.entity.PurPlanHead;
import com.jcloud.pur.service.PurchasePlanHeadService;
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
@RequestMapping("/purchasehead")
public class PurchasePlanHeadController {
    @Autowired
    private PurchasePlanHeadService service;
    /**
     * 确定
     */
    @RequestMapping(value="/submit",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody List<PurPlanHead> params) {
        return  service.addObj(params);
    }
    @RequestMapping(value="/unsubmit",method = RequestMethod.POST)
    public R unacceptTaskByCode(@RequestBody List<PurPlanHead> params) {
        return  service.unaddObj(params);
    }
    @RequestMapping(value="/auditlist",method = RequestMethod.POST)
    public R auditlist(@RequestBody List<PurPlanHead> params) {
        return  service.auditlist(params);
    }
    @RequestMapping(value="/passlist",method = RequestMethod.POST)
    public R passlist(@RequestBody List<PurPlanHead> params) {
        return  service.passlist(params);
    }
    @RequestMapping(value="/auditdetaillist",method = RequestMethod.POST)
    public R auditdetaillist(@RequestBody List<PurPlanHead> params) {
        return  service.auditdetaillist(params);
    }
    @RequestMapping(value="/passdetaillist",method = RequestMethod.POST)
    public R passdetaillist(@RequestBody List<PurPlanHead> params) {
        return  service.passdetaillist(params);
    }
    @RequestMapping(value="/detaillist",method = RequestMethod.POST)
    public R detaillist(@RequestBody List<PurPlanHead> params) {
        return  service.detaillist(params);
    }
    @RequestMapping(value="/orderCreate",method = RequestMethod.POST)
    public R orderCreate(@RequestBody List<PurPlanHead> params) {
        return  service.orderCreate(params);
    }
    @RequestMapping(value="/detailCreate",method = RequestMethod.POST)
    public R detailCreate(@RequestBody List<PurPlanHead> params) {
        return  service.detailCreate(params);
    }
    @RequestMapping(value="/zjdetailCreate",method = RequestMethod.POST)
    public R zjdetailCreate(@RequestBody List<PurPlanHead> params) {
        return  service.zjdetailCreate(params);
    }
    @RequestMapping(value="/planCreate",method = RequestMethod.POST)
    public R planCreate() {
        return  service.planCreate();
    }
    @RequestMapping(value="/bhpasslist",method = RequestMethod.POST)
    public R bhpasslist(@RequestBody List<PurPlanHead> params) {
        return  service.bhpasslist(params);
    }
    @RequestMapping(value="/enable",method = RequestMethod.POST)
    public R enable(@RequestBody List<PurPlanHead> params) {
        return  service.enable(params);
    }
    @RequestMapping(value="/disabl",method = RequestMethod.POST)
    public R disabl(@RequestBody List<PurPlanHead> params) {
        return  service.disabl(params);
    }
    @RequestMapping(value="/detailenable",method = RequestMethod.POST)
    public R detailenable(@RequestBody List<PurPlanHead> params) {
        return  service.detailenable(params);
    }
    @RequestMapping(value="/detaildisabl",method = RequestMethod.POST)
    public R detaildisabl(@RequestBody List<PurPlanHead> params) {
        return  service.detaildisabl(params);
    }

}
