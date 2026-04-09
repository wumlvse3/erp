package com.jcloud.customer.WmsBaseData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsOrg;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

/**
 * (MtlItemsOrg)表服务接口
 *
 * @author Bianxs
 * @since 2023-12-07 12:24:11
 */
public interface MtlItemsOrgService extends IService<MtlItemsOrg> {

    public R saveItem(Map<String,Object> params);
}

