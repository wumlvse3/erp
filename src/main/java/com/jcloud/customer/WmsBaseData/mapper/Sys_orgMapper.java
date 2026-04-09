package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.jcloud.customer.WmsBaseData.entity.SysOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Sys_orgMapper extends BaseMapper<SysOrg> {
    @Select("select * from sys_org  where  (org_type='ORG' or org_type='VIRTUAL')\n" +
            "and org_id<>'0' and org_code<>'vendor_user_temp_org'")
    List<SysOrg> getOrgList();
}