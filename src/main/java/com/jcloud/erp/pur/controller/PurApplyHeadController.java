package com.jcloud.erp.pur.controller;
import com.jcloud.erp.pur.entity.OrdOrderHead;
import com.jcloud.erp.pur.entity.PurApplyHead;
import com.jcloud.erp.pur.service.OrdOrderHeadService;
import com.jcloud.erp.pur.service.PurApplyHeadService;
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
@RequestMapping("/applyhead")
public class PurApplyHeadController {
    @Autowired
    private PurApplyHeadService service;

    @Autowired
    private OrdOrderHeadService Orservice;
    /**
     * 确定
     */
    @RequestMapping(value="/submit",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody List<PurApplyHead> params) {
        return  service.addObj(params);
    }
    @RequestMapping(value="/unsubmit",method = RequestMethod.POST)
    public R unacceptTaskByCode(@RequestBody List<PurApplyHead> params) {
        return  service.unaddObj(params);
    }
    @RequestMapping(value="/auditlist",method = RequestMethod.POST)
    public R auditlist(@RequestBody List<PurApplyHead> params) {
        return  service.auditlist(params);
    }
    @RequestMapping(value="/passlist",method = RequestMethod.POST)
    public R passlist(@RequestBody List<PurApplyHead> params) {
        return  service.passlist(params);
    }
    @RequestMapping(value="/bhpasslist",method = RequestMethod.POST)
    public R bhpasslist(@RequestBody List<PurApplyHead> params) {
        return  service.bhpasslist(params);
    }
    @RequestMapping(value="/auditdetaillist",method = RequestMethod.POST)
    public R auditdetaillist(@RequestBody List<PurApplyHead> params) {
        return  service.auditdetaillist(params);
    }
    @RequestMapping(value="/passdetaillist",method = RequestMethod.POST)
    public R passdetaillist(@RequestBody List<PurApplyHead> params) {
        return  service.passdetaillist(params);
    }
    @RequestMapping(value="/detaillist",method = RequestMethod.POST)
    public R detaillist(@RequestBody List<PurApplyHead> params) {
        return  service.detaillist(params);
    }
    @RequestMapping(value="/handleCreate",method = RequestMethod.POST)
    public R handleCreate(@RequestBody List<OrdOrderHead> params) {
        return  Orservice.handleCreate(params);
    }
    @RequestMapping(value="/detailCreate",method = RequestMethod.POST)
    public R detailCreate(@RequestBody List<OrdOrderHead> params) {
        return  Orservice.detailCreate(params);
    }
    @RequestMapping(value="/enable",method = RequestMethod.POST)
    public R enable(@RequestBody List<PurApplyHead> params) {
        return  service.enable(params);
    }
    @RequestMapping(value="/disabl",method = RequestMethod.POST)
    public R disabl(@RequestBody List<PurApplyHead> params) {
        return  service.disabl(params);
    }
    @RequestMapping(value="/detailenable",method = RequestMethod.POST)
    public R detailenable(@RequestBody List<PurApplyHead> params) {
        return  service.detailenable(params);
    }
    @RequestMapping(value="/detaildisabl",method = RequestMethod.POST)
    public R detaildisabl(@RequestBody List<PurApplyHead> params) {
        return  service.detaildisabl(params);
    }
}
