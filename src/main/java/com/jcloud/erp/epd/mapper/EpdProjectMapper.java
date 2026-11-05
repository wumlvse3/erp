package com.jcloud.erp.epd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.erp.epd.entity.EpdProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EpdProjectMapper extends BaseMapper<EpdProject> {
    /*@Select("SELECT parameter_key FROM `sys_com_parameter` WHERE `unit_id` = '1165' and parameter_name=#{unitGroup} ")
    String getUnitGroup(String unitGroup);

    @Select("SELECT parameter_key FROM `sys_com_parameter` WHERE parameter_name=#{purchaseUnit}")
    String getPurchaseUnit(String purchaseUnit);

    @Select("select parameter_key from sys_com_parameter where unit_id=(select unit_id from sys_com_unit where unit_code='items_class') and parameter_name = #{itemsClass}")
    String getItemClass(String itemsClass);

    @Select("select parameter_key from sys_com_parameter where unit_id = (select unit_id from sys_com_unit where unit_code = 'fifo_flag') and parameter_name = #{fifoFlag}")
    String getIsValidity(String fifoFlag);

    @Select("select parameter_key from sys_com_parameter where unit_id=(select unit_id from sys_com_unit where unit_code='time_uom') and parameter_name = #{validityUom}")
    String getValidityUom(String validityUom);
     */
}