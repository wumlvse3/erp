package com.jcloud.customer.WmsBaseData.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.Mtl_items_org;
import com.jcloud.customer.WmsBaseData.mapper.Mtl_items_orgMapper;
import com.jcloud.customer.WmsBaseData.service.MtlItemsOrgService;
import org.springframework.stereotype.Service;

/**
 * (MtlItemsOrg)表服务实现类
 *
 * @author Bianxs
 * @since 2023-12-07 12:24:11
 */
@Service("mtlItemsOrgService")
public class MtlItemsOrgServiceImpl extends ServiceImpl<Mtl_items_orgMapper, Mtl_items_org> implements MtlItemsOrgService {
    @Override
    public Mtl_items_org getObj(String orgId, String itemsCode) {
        try{
            Mtl_items_org bean = baseMapper.selectOne(new QueryWrapper<Mtl_items_org>().eq("org_id",orgId).eq("items_code",itemsCode));
            return bean;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

