package com.jcloud.erp.ord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.ord.entity.OrdInvoiceHead;
import com.jcloud.system.utils.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface OrdInvoiceHeadService extends IService<OrdInvoiceHead> {

    R updateSubmit(List<List<Map<String, Object>>> list);

    R createBill(List<List<Map<String, Object>>> list,String isShipRet);

    R createBillDetail(List<List<Map<String, Object>>> list,String isShipRet,String ukShipNo);

    R getStatData(List<Map<String, Object>> queryList,String offset,String size);

    R getStatBackData(List<Map<String, Object>> queryList,String offset,String size);

    R delete(Map<String,Object> params);

    R deleteDetail(Map<String,Object> params);

    void getStatDataExcel(List<Map<String, Object>> queryList, String offset, String size, HttpServletResponse response);

    void getStatDataExcelB(List<Map<String, Object>> queryList, String offset, String size, HttpServletResponse response);

    R active(List<List<Map<String, Object>>> list,String isActive);

    R activeDetail(List<List<Map<String, Object>>> list,String isActive);
}