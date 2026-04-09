package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.MtlOnHandDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MtlOnhandDetailsMapper extends BaseMapper<MtlOnHandDetails> {


    void deleteAll(@Param("org_id") String org_id);

    Double getOnhandQtyByParam(@Param("item_id")String item_id,@Param("inventory_id")String inventory_id,@Param("vendor_id")String vendor_id,
                               @Param("locator_id")String locator_id,@Param("lot_number")String lot_number,@Param("serial_number")String serial_number,
                               @Param("org_id")String org_id);

    Double getSumSheetReservedQtyByParamAndSerial(@Param("item_id")String item_id,@Param("inventory_id")String inventory_id,@Param("vendor_id")String vendor_id,
                               @Param("locator_id")String locator_id,@Param("lot_number")String lot_number,@Param("serial_number")String serial_number,
                               @Param("org_id")String org_id);

    Double getSumSheetReservedQtyByParamAndLine(@Param("item_id")String item_id,@Param("inventory_id")String inventory_id,@Param("vendor_id")String vendor_id,
                               @Param("locator_id")String locator_id,@Param("lot_number")String lot_number,@Param("org_id")String org_id);

    Double getSumPickReservedQtyByParamAndSerial(@Param("item_id")String item_id,@Param("inventory_id")String inventory_id,@Param("vendor_id")String vendor_id,
                               @Param("locator_id")String locator_id,@Param("lot_number")String lot_number,@Param("serial_number")String serial_number,
                               @Param("org_id")String org_id);

    Double getSumProjectReservedQty(@Param("item_id")String item_id,@Param("inventory_id")String inventory_id,@Param("vendor_id")String vendor_id,
                                                @Param("locator_id")String locator_id,@Param("lot_number")String lot_number,@Param("org_id")String org_id,@Param("project_id")String project_id);

}