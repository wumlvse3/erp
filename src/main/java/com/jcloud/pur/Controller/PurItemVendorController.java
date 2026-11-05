package com.jcloud.pur.Controller;
import com.jcloud.pur.service.PurItemVendorService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/puritemvendor")
public class PurItemVendorController {
    @Autowired
    private PurItemVendorService service;
    /**
     * 确定
     */
    @RequestMapping(value="/createitem",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody Map<String, String> params) {
        return  service.addObj(params.get("pur_org"));
    }
}
