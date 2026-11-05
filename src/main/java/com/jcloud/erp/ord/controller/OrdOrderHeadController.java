package com.jcloud.erp.ord.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.ord.service.OrdOrderHeadService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/ordOrderHead")
public class OrdOrderHeadController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OrdOrderHeadService ordOrderHeadService;
    
    @RequestMapping(value = "updateSubmit",method = RequestMethod.POST)
    public R updateSubmit(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isSubmit = params.get("isSubmit");
        return ordOrderHeadService.updateSubmit(list,isSubmit);
    }

    @RequestMapping(value = "updateCheck",method = RequestMethod.POST)
    public R updateCheck(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String suggest = params.get("suggest");
        String isReturn = params.get("isReturn");
        return ordOrderHeadService.updateCheck(list,suggest,isReturn);
    }

    @RequestMapping(value = "updateCheckDetail",method = RequestMethod.POST)
    public R updateCheckDetail(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String suggest = params.get("suggest");
        return ordOrderHeadService.updateCheckDetail(list,suggest);
    }

    @RequestMapping(value = "createBill",method = RequestMethod.POST)
    public R createBill(@RequestBody Map<String,String> params){
        /*String salesOrg = params.get("sales_org");
        String ukAgreementId = params.get("uk_agreement_id");
        return ordOrderHeadService.createBill(salesOrg, ukAgreementId);
        */
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        return ordOrderHeadService.createBill(list);
    }

    @RequestMapping(value = "createBillDetail",method = RequestMethod.POST)
    public R createBillDetail(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String ukOrderNo = params.get("ukOrderNo");
        return ordOrderHeadService.createBillDetail(list,ukOrderNo);
    }
    @RequestMapping(value = "getStatData", method = RequestMethod.GET)
    public R getStatData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            return ordOrderHeadService.getStatData(queryList,offset,size);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public R delete(@RequestBody Map<String,Object> params){
        return ordOrderHeadService.delete(params);
    }

    @RequestMapping(value = "deleteDetail",method = RequestMethod.POST)
    public R deleteDetail(@RequestBody Map<String,Object> params){
        return ordOrderHeadService.deleteDetail(params);
    }

    @RequestMapping(value = "getStatDataExcel", method = RequestMethod.POST)
    public void getStatDataExcel(@RequestBody Map<String, Object> queryMap, HttpServletResponse response) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            String offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            logger.info("=== 收到 yang_query: {}", yangQuery);
            logger.info("=== 解析后的 queryList: {}", queryList);
            ordOrderHeadService.getStatDataExcel(queryList,offset,size,response);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "active",method = RequestMethod.POST)
    public R active(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isActive=params.get("isActive");
        return ordOrderHeadService.active(list,isActive);
    }

    @RequestMapping(value = "activeDetail",method = RequestMethod.POST)
    public R activeDetail(@RequestBody Map<String,String> params){
        List<List<Map<String,Object>>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        String isActive=params.get("isActive");
        return ordOrderHeadService.activeDetail(list,isActive);
    }
}