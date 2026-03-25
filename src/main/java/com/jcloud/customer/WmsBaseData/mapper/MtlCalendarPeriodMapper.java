package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.MtlCalendarPeriod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MtlCalendarPeriodMapper extends BaseMapper<MtlCalendarPeriod> {
    @Select("select * from mtl_calendar_period where period_year=#{year} and period_month=#{month} and calendar_id=#{calendar_id}")
    MtlCalendarPeriod findByYearAndMonthAndCalendarId(@Param("year") String String, @Param("month") String month, @Param("calendar_id") String calendarId);


    @Select("select * from mtl_calendar_period where period_year=#{year} and period_month=#{month} and calendar_id=#{calendar_id}")
    MtlCalendarPeriod findByMonth(@Param("month") String month);
}