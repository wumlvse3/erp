package com.jcloud.customer.WmsBaseData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.customer.WmsBaseData.entity.Mtl_items_org;

/**
 * (MtlItemsOrg)表服务接口
 *
 * @author Bianxs
 * @since 2023-12-07 12:24:11
 */
public interface MtlItemsOrgService extends IService<Mtl_items_org> {
    Mtl_items_org getObj(String orgId,String itemsCode);
}

