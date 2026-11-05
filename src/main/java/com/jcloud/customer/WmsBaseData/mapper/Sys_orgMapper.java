package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.jcloud.customer.WmsBaseData.entity.SysOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Sys_orgMapper extends BaseMapper<SysOrg> {
    @Select("SELECT * from sys_org  where  (org_type='ORG' or org_type='VIRTUAL') and org_id<>'0' and inv_flag='Y' ")
    List<SysOrg> getOrgList();

    @Select("SELECT MIN(o.org_code) AS inv_org FROM ( SELECT org_power FROM sys_user WHERE username = #{username}) u " +
            "JOIN sys_org o ON FIND_IN_SET(o.org_id, u.org_power) > 0 " +
            "GROUP BY u.org_power ORDER BY u.org_power")
    String getDefaultOrgCode(@Param("username") String username);
}
