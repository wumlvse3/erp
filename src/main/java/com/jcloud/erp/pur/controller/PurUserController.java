package com.jcloud.erp.pur.controller;

import com.alibaba.fastjson.JSON;
import com.jcloud.system.utils.R;
import com.jcloud.erp.pur.service.AdduserService;
import com.jcloud.erp.pur.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/sysuser")
public class PurUserController {
    @Autowired
    private AdduserService service;
    /**
     * 确定
     */
    @RequestMapping(value="/adduser",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody List<UserVo> params) {
        return  service.addObj(params);
    }
}
