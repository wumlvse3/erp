package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.vo.MtlOnhandDetailsV;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MtlOnhandVDetailsMapper extends BaseMapper<MtlOnhandDetailsV> {


	List<Map<String, Object>> getExportList(@Param("itemscode") String itemscode, @Param("itemsname") String itemsname,
                                            @Param("vendorcode") String vendorcode, @Param("vendorname") String vendorname,
                                            @Param("inventorycode") String inventorycode, @Param("inventoryname") String inventoryname,
                                            @Param("lotnumber") String lotnumber, @Param("serialnumber") String serialnumber,
                                            @Param("customerCode") String customerCode, @Param("customerName") String customerName,
                                            @Param("item_spec") String item_spec,
                                            @Param("param") Map<String, Object> param,
                                            @Param("inventoryid") String inventoryid);

	MtlOnhandDetailsV getBySerialV(@Param("serial_number") String serial);


	Double getOnhandSumQtyByParam(@Param("inventoryId") String inventoryId, @Param("locatorId") String locatorId,
                                  @Param("vendorId") String vendorId, @Param("lotNumber") String lotNumber, @Param("serialNUmber") String serialNumber,
                                  @Param("orgId") String orgId, @Param("itemId") String itemId, @Param("businessUnit") String businessUnit);

	Double getReservedSumQtyByParam(@Param("inventoryId") String inventoryId, @Param("locatorId") String locatorId,
                                    @Param("vendorId") String vendorId, @Param("lotNumber") String lotNumber, @Param("serialNUmber") String serialNumber,
                                    @Param("orgId") String orgId, @Param("itemId") String itemId, @Param("lineId") String lineId, @Param("businessUnit") String businessUnit);

	Double getReservedSumQtyByParamPick(@Param("inventoryId") String inventoryId, @Param("locatorId") String locatorId,
                                        @Param("vendorId") String vendorId, @Param("lotNumber") String lotNumber, @Param("serialNUmber") String serialNumber,
                                        @Param("orgId") String orgId, @Param("itemId") String itemId, @Param("lineId") String lineId, @Param("businessUnit") String businessUnit);
}