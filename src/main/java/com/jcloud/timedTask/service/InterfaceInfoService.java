package com.jcloud.timedTask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.timedTask.entity.InterfaceInfo;

import java.util.List;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    // 根据id获取一条接口信息
    InterfaceInfo getOne(String interfaceId);
    // 获取所有的接口信息
    List<InterfaceInfo> getAll();

    // 批量修改active字段
    Integer updateActiveByIds(List<String> ids,Integer active);
    // active 的字段查询
    List<InterfaceInfo> getList(Integer active);
    // 根据名称和组查询接口信息
    InterfaceInfo getInfoByNameAndGroup(String name,String group);

    public InterfaceInfo getByCode(String code);

    public InterfaceInfo getByCodeAndOrg(String code,String org_id);
}
