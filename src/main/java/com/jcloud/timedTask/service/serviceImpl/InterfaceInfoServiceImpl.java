package com.jcloud.timedTask.service.serviceImpl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.timedTask.entity.InterfaceInfo;
import com.jcloud.timedTask.mapper.InterfaceInfoMapper;
import com.jcloud.timedTask.service.InterfaceInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {



    @Override
    public InterfaceInfo getOne(String interfaceId) {
        return baseMapper.selectById(interfaceId);
    }

    @Override
    public List<InterfaceInfo> getAll() {
        return baseMapper.selectList(new QueryWrapper<>(null));
    }



    @Override
    public Integer updateActiveByIds(List<String> ids, Integer active) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setActive(active);
        QueryWrapper<InterfaceInfo> entityWrapper = new QueryWrapper<>();
        entityWrapper.in("id",ids);
        Integer update = baseMapper.update(interfaceInfo, entityWrapper);
        return update;
    }

    @Override
    public List<InterfaceInfo> getList(Integer active) {
        return baseMapper.selectList(new QueryWrapper<InterfaceInfo>().eq("active",0));
    }

    @Override
    public InterfaceInfo getInfoByNameAndGroup(String name, String group) {
        return baseMapper.selectOne(new QueryWrapper<InterfaceInfo>().eq("name",name).eq("group_name",group));
    }

    public InterfaceInfo getByCode(String code){
        return baseMapper.selectOne(new QueryWrapper<InterfaceInfo>().eq("code",code));
    }

    public InterfaceInfo getByCodeAndOrg(String code,String org_id){
        return baseMapper.selectOne(new QueryWrapper<InterfaceInfo>().eq("code",code).eq("org_id", org_id));
    }
}
