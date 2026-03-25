package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.MtlTransactions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MtlTransactionsMapper extends BaseMapper<MtlTransactions> {

    @Select("select * from mtl_transactions where header_id=#{header_id} and status=0")
    List<MtlTransactions> findByHeaderId(@Param("header_id")String header_id);

}