package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.MtlPeriodTransactions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MtlPeriodTransactionMapper extends BaseMapper<MtlPeriodTransactions> {

    @Select("select * from mtl_period_transactions where period_month=#{month}")
    List<MtlPeriodTransactions> findLastMonthData(@Param("month") String month);

    MtlPeriodTransactions findByParam(@Param("period_id")String period, @Param("item_id")String item_id, @Param("vendor_id")String vendor_id,
                                        @Param("inventory_id")String inventory_id);
	
}