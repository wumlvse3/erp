package com.jcloud.erp.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.epd.entity.EpdCustomerDef;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface EpdCustomerDefService extends IService<EpdCustomerDef> {

    public R addOrgItems(List<List<Map<String, Object>>> list);

    R update(Map<String,Object> params);

}
