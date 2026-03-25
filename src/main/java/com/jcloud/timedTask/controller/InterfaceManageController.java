package com.jcloud.timedTask.controller;

import com.jcloud.system.utils.R;
import com.jcloud.timedTask.service.InterfaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: brbai
 * @create: 2020-10-30 16:48:17
 * @description:
 */
@RequestMapping("/Interface_Manage")
@RestController
@Api(value = "InterfaceManageController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InterfaceManageController{

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    InterfaceInfoService service;

    /**
     * @author 云之佳-开发部
     * @date：2020-08-26
     * @url /Check_notice/close
     * @description：
     * 		关闭通知
     */
    @ApiOperation(value = "关闭通知", notes="关闭通知接口", httpMethod = "POST")
    @ApiImplicitParam(name = "keyValue", value = "质检通知id", required = true, dataType = "int", paramType = "query")
    @RequestMapping(value = "/closeOrOpen")
    public R closeOrOpen(@PathVariable("active")String active, @PathVariable("id")String id) {

        List<String> ids = new ArrayList<>();

        ids.add(id);
        service.updateActiveByIds(ids,Integer.parseInt(active) == 1 ? 0 : 1);
        return R.ok();
    }

    /**
     * @author 云之佳-开发部
     * @date：2020-08-26
     * @url /Check_notice/closeBatch
     * @description：
     * 		批量关闭通知
     */
    @RequestMapping(value = "/closeOrOpenBatch")
    public R closeBatch(@RequestParam("keyValues[]") String[] ids , @RequestParam("keyValue") String keyValue) {

        service.updateActiveByIds(Arrays.asList(ids),Integer.parseInt(keyValue) == 1 ? 0 : 1);
        return R.ok();
    }


}
