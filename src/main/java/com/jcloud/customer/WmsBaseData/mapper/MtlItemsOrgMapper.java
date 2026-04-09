package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsOrg;
import com.jcloud.customer.WmsBaseData.entity.MtlOnHandDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MtlItemsOrgMapper extends BaseMapper<MtlItemsOrg> {

    @Select("select * from mtl_items_org where items_code=#{items_code} and org_id=#{org_id}")
    MtlItemsOrg getByItemsCodeAndOrgId(@Param("items_code")String items_code, @Param("org_id")String org_id);

    @Select("select md.* from \n" +
            "mtl_onhand_details md,mtl_inventories miv,mtl_items_org mi\n" +
            "where (md.item_id=mi.item_id and md.inventory_id =miv.inventory_id)\n" +
            "and ((md.pro_lot is null and miv.lot_flag=1 and mi.lot_flag=1) or (md.pro_lot is not null and (miv.lot_flag=0 or mi.lot_flag=0)))\n" +
            "and md.item_qty>0 and md.item_id=#{item_id}")
    List<MtlOnHandDetails> checkOnhandLot(@Param("item_id")String item_id);

    @Select("select md.* from \n" +
            "mtl_onhand_details md,mtl_inventories miv,mtl_items_org mi\n" +
            "where (md.item_id=mi.item_id and md.inventory_id =miv.inventory_id)\n" +
            "and ((md.vendor_id is null and miv.vendor_flag=1 and mi.vendor_flag=1) or (md.vendor_id is not null and (miv.vendor_flag=0 or mi.vendor_flag=0)))\n" +
            "and md.item_qty>0 and md.item_id=#{item_id}")
    List<MtlOnHandDetails> checkOnhandVendor(@Param("item_id")String item_id);

    @Select("select md.* from \n" +
            "mtl_onhand_details md,mtl_inventories miv,mtl_items_org mi\n" +
            "where (md.item_id=mi.item_id and md.inventory_id =miv.inventory_id)\n" +
            "and ((md.locator_id is null and miv.locator_flag=1 and mi.locator_flag=1) or (md.locator_id is not null and (miv.locator_flag=0 or mi.locator_flag=0)))\n" +
            "and md.item_qty>0 and md.item_id=#{item_id}")
    List<MtlOnHandDetails> checkOnhandLocator(@Param("item_id")String item_id);

    @Select("select md.* from \n" +
            "mtl_onhand_details md,mtl_inventories miv,mtl_items_org mi\n" +
            "where (md.item_id=mi.item_id and md.inventory_id =miv.inventory_id)\n" +
            "and ((md.serial_number is null and miv.box_flag=1 and (mi.box_flag=1 or mi.serial_flag=1)) or (md.serial_number is not null and (miv.box_flag=0 or (mi.box_flag=0 and mi.serial_flag=0))))\n" +
            "and md.item_qty>0 and md.item_id=#{item_id}")
    List<MtlOnHandDetails> checkOnhandSerial(@Param("item_id")String item_id);

}