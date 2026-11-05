
package com.jcloud.erp.pur.service;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface PurCountService  {

    public  R getApplyData(List<Map<String, Object>> queryList, String offset, String size);

    public  R getPurchaseData(List<Map<String, Object>> queryList, String offset, String size);

    public  R getShipData(List<Map<String, Object>> queryList, String offset, String size);

    public R getShipReturnData(List<Map<String, Object>> queryList, String offset, String size);

    public R getOrderData(List<Map<String, Object>> queryList, String offset, String size);
}
