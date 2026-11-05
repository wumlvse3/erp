package com.jcloud.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.epd.entity.EpdCalendar;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface EpdCalendarService extends IService<EpdCalendar> {

    R generateCalendar(String calendarId, String invOrg, Integer startYear, Integer endYear, 
                       List<Integer> restDays, Boolean coverDeptCalendar);

    R queryCalendarList(Map<String, Object> params);

    R queryCalendarByMonth(String calendarId, String invOrg, Integer year, Integer month);

    R updateCalendar(EpdCalendar calendar);

    R batchUpdateCalendar(List<EpdCalendar> calendarList, String invOrg);

    R deleteCalendar(String calendarId, String invOrg);

    R deleteByYearRange(String calendarId, String invOrg, Integer startYear, Integer endYear);

    R pushToShopCalendar(String calendarId);

    R getCalendarById(Integer id);
}