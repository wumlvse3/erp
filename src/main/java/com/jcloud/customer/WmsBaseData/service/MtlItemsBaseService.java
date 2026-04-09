package com.jcloud.customer.WmsBaseData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsBase;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface MtlItemsBaseService extends IService<MtlItemsBase> {

    public R addOrgItems(List<List<Map<String, Object>>> list);

    R update(Map<String,Object> params);

    public R addAllOrgItems(List<List<Map<String, Object>>> list);
}
