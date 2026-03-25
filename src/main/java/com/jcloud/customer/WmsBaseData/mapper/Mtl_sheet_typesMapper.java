package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcloud.customer.WmsBaseData.entity.Mtl_sheet_types;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface Mtl_sheet_typesMapper extends BaseMapper<Mtl_sheet_types> {

    @Select("select *,DATE_FORMAT(sysdate(),date_type) as date_str from mtl_sheet_types where " +
            "sheet_type_id=#{sheet_type_id} and DATE_FORMAT(update_date,date_type) = DATE_FORMAT(sysdate(),date_type)")
    Mtl_sheet_types getToDaysData(@Param("sheet_type_id")String sheet_type_id);


    @Select("select *,DATE_FORMAT(sysdate(),date_type) as date_str from mtl_sheet_types where " +
            "sheet_type_id=#{sheet_type_id} ")
    Mtl_sheet_types getById(@Param("sheet_type_id")String sheet_type_id);
}