package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.Mtl_items_org;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface Mtl_items_orgMapper extends BaseMapper<Mtl_items_org> {

    @Select("select * from mtl_items_org where items_code=#{items_code} and org_id=#{org_id}")
    Mtl_items_org getByItemsCodeAndOrgId(@Param("items_code")String items_code,@Param("org_id")String org_id);

}