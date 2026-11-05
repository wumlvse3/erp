package com.jcloud.erp.pur.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.pur.service.PurCountService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/purcount")
public class PurCountController {
    @Autowired
    private PurCountService service;
    @RequestMapping(value = "getApplyData", method = RequestMethod.GET)
    public R getApplyData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return service.getApplyData(queryList,offset, size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "getPurchaseData", method = RequestMethod.GET)
    public R getPurchaseData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return service.getPurchaseData(queryList,offset, size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "getShipData", method = RequestMethod.GET)
    public R getShipData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return service.getShipData(queryList,offset, size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "getShipReturnData", method = RequestMethod.GET)
    public R getShipReturnData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return service.getShipReturnData(queryList,offset, size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "getOrderData", method = RequestMethod.GET)
    public R getOrderData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return service.getOrderData(queryList,offset, size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }




}
