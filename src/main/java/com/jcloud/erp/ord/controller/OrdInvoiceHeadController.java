package com.jcloud.erp.ord.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.ord.service.OrdInvoiceHeadService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import com.jcloud.system.utils.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordInvoiceHead")
public class OrdInvoiceHeadController {
    @Autowired
    OrdInvoiceHeadService ordInvoiceHeadService;
    
    @RequestMapping(value = "updateSubmit",method = RequestMethod.POST)
    public R updateSubmit(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return ordInvoiceHeadService.updateSubmit(list);
    }

    @RequestMapping(value = "createBill",method = RequestMethod.POST)
    public R createBill(@RequestBody Map<String,String> params){
        String isShipRet = params.get("isShipRet");
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return ordInvoiceHeadService.createBill(list,isShipRet);
    }

    @RequestMapping(value = "createBillDetail",method = RequestMethod.POST)
    public R createBillDetail(@RequestBody Map<String,String> params){
        String isShipRet = params.get("isShipRet");
        String ukShipNo = params.get("ukShipNo");
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return ordInvoiceHeadService.createBillDetail(list,isShipRet,ukShipNo);
    }

    @RequestMapping(value = "getStatData", method = RequestMethod.GET)
    public R getStatData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return ordInvoiceHeadService.getStatData(queryList,offset,size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "getStatBackData", method = RequestMethod.GET)
    public R getStatBackData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return ordInvoiceHeadService.getStatBackData(queryList,offset,size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public R delete(@RequestBody Map<String,Object> params){
        return ordInvoiceHeadService.delete(params);
    }

    @RequestMapping(value = "deleteDetail",method = RequestMethod.POST)
    public R deleteDetail(@RequestBody Map<String,Object> params){
        return ordInvoiceHeadService.deleteDetail(params);
    }

    @RequestMapping(value = "getStatDataExcel", method = RequestMethod.POST)
    public void getStatDataExcel(@RequestBody Map<String, Object> queryMap, HttpServletResponse response) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            ordInvoiceHeadService.getStatDataExcel(queryList,offset,size,response);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "getStatDataExcelB", method = RequestMethod.POST)
    public void getStatDataExcelB(@RequestBody Map<String, Object> queryMap, HttpServletResponse response) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            ordInvoiceHeadService.getStatDataExcelB(queryList,offset,size,response);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "active",method = RequestMethod.POST)
    public R active(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isActive=params.get("isActive");
        return ordInvoiceHeadService.active(list,isActive);
    }

    @RequestMapping(value = "activeDetail",method = RequestMethod.POST)
    public R activeDetail(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isActive=params.get("isActive");
        return ordInvoiceHeadService.activeDetail(list,isActive);
    }
}