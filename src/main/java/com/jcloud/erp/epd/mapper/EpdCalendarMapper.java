package com.jcloud.erp.epd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.erp.epd.entity.EpdCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface EpdCalendarMapper extends BaseMapper<EpdCalendar> {

    List<EpdCalendar> selectByYearAndMonth(@Param("calendarId") String calendarId, 
                                           @Param("year") Integer year, 
                                           @Param("month") Integer month);

    int deleteByCalendarId(@Param("calendarId") String calendarId);

    int deleteByDateRange(@Param("calendarId") String calendarId,
                          @Param("invOrg") String invOrg,
                          @Param("startDate") Date startDate,
                          @Param("endDate") Date endDate);

    List<Map<String, Object>> selectCalendarByMonth(@Param("calendarId") String calendarId,
                                                     @Param("year") Integer year,
                                                     @Param("month") Integer month);

    int batchInsert(@Param("list") List<EpdCalendar> list);
}