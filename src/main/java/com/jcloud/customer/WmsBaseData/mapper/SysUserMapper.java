package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.Sys_dept;
import com.jcloud.customer.WmsBaseData.entity.Sys_user;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *供货品类
 */
@Mapper
public interface SysUserMapper extends BaseMapper<Sys_user> {

    @Select("select distinct u.email,u.`name`,u.username from sys_user u\n" +
            "INNER JOIN sys_user_role ur on ur.user_id=u.user_id\n" +
            "INNER JOIN sys_role r on r.role_id=ur.role_id and role_sign=#{role_sign}\n" +
            "INNER JOIN vendor_info_org v on v.vendor_org_id=u.org_id\n" +
            "where v.id=#{vendor_id}")
    List<Sys_user> selectVendorUserByRoleSign(String vendor_id,String role_sign);

}
