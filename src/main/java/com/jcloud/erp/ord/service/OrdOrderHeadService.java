package com.jcloud.erp.ord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.ord.entity.OrdOrderHead;
import com.jcloud.system.utils.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface OrdOrderHeadService extends IService<OrdOrderHead> {

    R updateSubmit(List<List<Map<String, Object>>> list,String isSubmit);

    R updateCheck(List<List<Map<String, Object>>> list,String suggest,String isReturn);

    R updateCheckDetail(List<List<Map<String, Object>>> list,String suggest);

    /*R createBill(String salesOrg, String ukShipNo);*/
    R createBill(List<List<Map<String, Object>>> list);

    R createBillDetail(List<List<Map<String, Object>>> list,String ukOrderNo);

    R getStatData(List<Map<String, Object>> queryList,String offset,String size);

    R delete(Map<String,Object> params);

    R deleteDetail(Map<String,Object> params);

    void getStatDataExcel(List<Map<String, Object>> queryList, String offset, String size, HttpServletResponse response);

    R active(List<List<Map<String, Object>>> list,String isActive);

    R activeDetail(List<List<Map<String, Object>>> list,String isActive);
}