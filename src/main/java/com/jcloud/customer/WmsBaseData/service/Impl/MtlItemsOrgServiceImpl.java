package com.jcloud.customer.WmsBaseData.service.Impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.*;
import com.jcloud.customer.WmsBaseData.mapper.MtlItemsOrgMapper;
import com.jcloud.customer.WmsBaseData.service.MtlItemsOrgService;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * (MtlItemsOrg)表服务实现类
 *
 * @author Bianxs
 * @since 2023-12-07 12:24:11
 */
@Service("mtlItemsOrgService")
public class MtlItemsOrgServiceImpl extends ServiceImpl<MtlItemsOrgMapper, MtlItemsOrg> implements MtlItemsOrgService {
    @Autowired
    private ComUnitService comUnitService;

    @Transactional
    public R saveItem(Map<String,Object> params){
        try{
            String unitData = ObjectUtils.oGetS(params.get("unitdatas"));//列表数据
            MtlItemsOrg itemData = JSONUtil.toBean(unitData, MtlItemsOrg.class);
            MtlItemsOrg exist =null;
            if(!StringUtils.isEmpty(itemData.getItem_id())){
                exist=baseMapper.selectById(itemData.getItem_id());
            }
            R res = comUnitService.SaveData(params,getEntityClass());


            if (itemData.getIs_validity()==0){
                itemData.setValidity_day(null);
                itemData.setValidity_out(null);
            }else if (itemData.getIs_validity()==1){
                if (itemData.getValidity_day()==null||itemData.getValidity_out()==null){
                    throw new Exception("启用有效期时,过期天数和过期出库必填");
                }
            }
            if(!StringUtils.isEmpty(itemData.getItem_id())){
                if(exist.getVendor_flag()!=itemData.getVendor_flag()){
                    List<MtlOnHandDetails> checkOnhandLot = baseMapper.checkOnhandVendor(itemData.getItem_id());
                    if(!checkOnhandLot.isEmpty()){
                        throw new Exception("供应商管控与现有库存数据不匹配");
                    }
                }
                if(exist.getLocator_flag()!=itemData.getLocator_flag()){
                    List<MtlOnHandDetails> checkOnhandLot = baseMapper.checkOnhandLocator(itemData.getItem_id());
                    if(!checkOnhandLot.isEmpty()){
                        throw new Exception("货位管控与现有库存数据不匹配");
                    }
                }
                if(exist.getBox_flag()!=itemData.getBox_flag() || exist.getSerial_flag()!=itemData.getSerial_flag()){
                    List<MtlOnHandDetails> checkOnhandLot = baseMapper.checkOnhandSerial(itemData.getItem_id());
                    if(!checkOnhandLot.isEmpty()){
                        throw new Exception("条码管控与现有库存数据不匹配");
                    }
                }
            }
            return res;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error(e.getMessage());
        }
    }
}

