package com.jcloud.erp.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.epd.entity.EpdVendorDef;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface EpdVendorDefService extends IService<EpdVendorDef> {

    public R addOrgItems(List<List<Map<String, Object>>> list);

    R update(Map<String,Object> params);

}
