package com.jcloud.erp.epd.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.epd.entity.EpdCalendar;
import com.jcloud.erp.epd.mapper.EpdCalendarMapper;
import com.jcloud.erp.epd.service.EpdCalendarService;
import com.jcloud.erp.epd.utils.LunarCalendarFestivalUtils;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EpdCalendarServiceImpl extends ServiceImpl<EpdCalendarMapper, EpdCalendar> 
        implements EpdCalendarService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int WORK_STATUS_WORK = 1;
    private static final int WORK_STATUS_REST = 0;

    private static final int IS_FESTIVAL_YES = 1;
    private static final int IS_FESTIVAL_NO = 0;

    private static final int IS_CYCLE_YES = 1;
    private static final int IS_CYCLE_NO = 0;

    private static final int BATCH_SIZE = 1000;

    @Autowired
    private EpdCalendarMapper epdCalendarMapper;

    private static final Map<String, FestivalConfig> FESTIVAL_CONFIGS = new HashMap<>();

    // 只有这些节日才设为休息日
    private static final Set<String> REST_FESTIVALS = new HashSet<>(Arrays.asList(
            "春节", "端午节", "中秋节", "元旦", "劳动节", "国庆节", "清明节", "除夕"
    ));

    static {
        // 仅保留固定公历节日；春节、端午、中秋等农历节日由 LunarCalendarFestivalUtils 动态计算
        FESTIVAL_CONFIGS.put("元旦", new FestivalConfig(1, 1, IS_CYCLE_YES));
        FESTIVAL_CONFIGS.put("劳动节", new FestivalConfig(5, 1, IS_CYCLE_YES));
        FESTIVAL_CONFIGS.put("国庆节", new FestivalConfig(10, 1, IS_CYCLE_YES));
    }

    @Override
    @Transactional
    public R generateCalendar(String calendarId, String invOrg, Integer startYear, Integer endYear, 
                              List<Integer> restDays, Boolean coverDeptCalendar) {
        try {
            if (StringUtils.isEmpty(calendarId)) {
                return R.error("日历ID不能为空");
            }
            if (StringUtils.isEmpty(invOrg)) {
                return R.error("库存组织不能为空");
            }
            if (startYear == null || endYear == null || startYear > endYear) {
                return R.error("年份范围无效");
            }
            if (CollectionUtils.isEmpty(restDays)) {
                restDays = Arrays.asList(6, 7);
            }

            List<EpdCalendar> calendarList = new ArrayList<>();
            // 复用 LunarCalendarFestivalUtils 实例，提高性能
            LunarCalendarFestivalUtils lunarUtils = new LunarCalendarFestivalUtils();

            for (int year = startYear; year <= endYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month - 1, 1);
                    int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                    for (int day = 1; day <= daysInMonth; day++) {
                        Date date = getDate(year, month, day);
                        int weekDay = getWeekDay(date);

                        EpdCalendar epdCalendar = new EpdCalendar();
                        epdCalendar.setInv_org(invOrg);
                        epdCalendar.setCalendar_id(calendarId);
                        epdCalendar.setCalendar_date(date);
                        epdCalendar.setWeek_day(getWeekDayName(weekDay));
                        epdCalendar.setDay_of_month(day);
                        epdCalendar.setMonth_of_year(month);

                        // 使用 LunarCalendarFestivalUtils 计算农历信息
                        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
                        lunarUtils.initLunarCalendarInfo(dateStr);
                        
                        // 获取数字格式的农历信息
                        int lunarMonth = lunarUtils.getLunarMonthNum();
                        int lunarDay = lunarUtils.getLunarDayNum();
                        boolean isLeapMonth = lunarUtils.isLeapMonth();
                        
                        // 构建农历日期字符串（使用中文格式）
                        // 注意：getLunarMonth() 已经包含"闰"前缀（如"闰六月"），不需要再加
                        String lunarDateStr = lunarUtils.getLunarMonth() + "月" + 
                            lunarUtils.getLunarDay();
                        epdCalendar.setLunar_date(lunarDateStr);
                        epdCalendar.setLunar_month(lunarMonth);
                        epdCalendar.setLunar_day(lunarDay);

                        // 检查法定节假日（暂时保留原有逻辑，后续可优化）
                        boolean isLegalHoliday = false; // TODO: 需要实现法定节假日判断
                        epdCalendar.setIs_legal_holiday(isLegalHoliday ? 1 : 0);

                        // 检查是否为调休上班日（周末需要上班）
                        boolean isWorkOnWeekend = false; // TODO: 需要实现调休判断

                        // 判断休息日逻辑：法定节假日或普通休息日（排除调休）
                        boolean isRest = false;
                        if (isLegalHoliday) {
                            isRest = true;
                        } else if (isWorkOnWeekend) {
                            isRest = false; // 调休上班，即使是周末也要工作
                        } else {
                            isRest = restDays.contains(weekDay);
                        }

                        // 获取节日信息：优先使用 LunarCalendarFestivalUtils 获取的节日
                        String festivalName = "";
                        
                        // 1. 先获取农历节日
                        String lunarFestival = lunarUtils.getLunarFestival();
                        if (StringUtils.isNotEmpty(lunarFestival)) {
                            festivalName = lunarFestival;
                        } else {
                            // 2. 再获取公历节日
                            String solarFestival = lunarUtils.getSolarFestival();
                            if (StringUtils.isNotEmpty(solarFestival)) {
                                festivalName = solarFestival;
                            } else {
                                // 3. 最后检查固定公历节日
                                festivalName = checkFestival(year, month, day);
                            }
                        }
                        boolean isFestival = StringUtils.isNotEmpty(festivalName);

                        // 只有指定的法定节日才设为休息日，其他节日不设为休息日
                        boolean isRestFestival = isFestival && REST_FESTIVALS.contains(festivalName);
                        if (isRestFestival || isRest) {
                            epdCalendar.setWork_status(WORK_STATUS_REST);
                        } else {
                            epdCalendar.setWork_status(WORK_STATUS_WORK);
                        }

                        epdCalendar.setFestival_name(festivalName);
                        epdCalendar.setIs_festival(isFestival ? IS_FESTIVAL_YES : IS_FESTIVAL_NO);
                        epdCalendar.setIs_cycle(isFestival ? IS_CYCLE_YES : IS_CYCLE_NO);

                        calendarList.add(epdCalendar);
                    }
                }
            }

            // 只删除指定库存组织和年份范围内的数据
            Date startDate = getDate(startYear, 1, 1);
            Date endDate = getDate(endYear, 12, 31);
            epdCalendarMapper.deleteByDateRange(calendarId, invOrg, startDate, endDate);

            if (!CollectionUtils.isEmpty(calendarList)) {
                epdCalendarMapper.batchInsert(calendarList);
            }

            if (coverDeptCalendar != null && coverDeptCalendar) {
                pushToShopCalendar(calendarId);
            }

            return R.ok("日历生成成功，共生成 " + calendarList.size() + " 条记录");
        } catch (Exception e) {
            logger.error("生成日历失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("生成日历失败：" + e.getMessage());
        }
    }

    @Override
    public R queryCalendarList(Map<String, Object> params) {
        try {
            String calendarId = (String) params.get("calendarId");
            String startDate = (String) params.get("startDate");
            String endDate = (String) params.get("endDate");
            String invOrg = (String) params.get("invOrg");

            QueryWrapper<EpdCalendar> wrapper = new QueryWrapper<>();
            if (StringUtils.isNotEmpty(calendarId)) {
                wrapper.eq("calendar_id", calendarId);
            }
            if (StringUtils.isNotEmpty(invOrg)) {
                wrapper.eq("inv_org", invOrg);
            }
            if (StringUtils.isNotEmpty(startDate)) {
                wrapper.ge("calendar_date", startDate);
            }
            if (StringUtils.isNotEmpty(endDate)) {
                wrapper.le("calendar_date", endDate);
            }
            wrapper.orderByAsc("calendar_date");

            List<EpdCalendar> list = list(wrapper);
            return R.ok().setdata(list);
        } catch (Exception e) {
            logger.error("查询日历列表失败", e);
            return R.error("查询日历列表失败：" + e.getMessage());
        }
    }

    @Override
    public R queryCalendarByMonth(String calendarId, String invOrg, Integer year, Integer month) {
        try {
            if (StringUtils.isEmpty(calendarId) || year == null || month == null) {
                return R.error("参数不能为空");
            }

            QueryWrapper<EpdCalendar> wrapper = new QueryWrapper<>();
            wrapper.eq("calendar_id", calendarId);
            if (StringUtils.isNotEmpty(invOrg)) {
                wrapper.eq("inv_org", invOrg);
            }
            wrapper.apply("YEAR(calendar_date) = {0}", year);
            wrapper.apply("MONTH(calendar_date) = {0}", month);
            wrapper.orderByAsc("calendar_date");

            List<EpdCalendar> list = list(wrapper);
            return R.ok().setdata(list);
        } catch (Exception e) {
            logger.error("查询日历月视图失败", e);
            return R.error("查询日历月视图失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R updateCalendar(EpdCalendar calendar) {
        try {
            if (calendar.getUu_id() == null) {
                return R.error("ID不能为空");
            }
            updateById(calendar);
            return R.ok("更新成功");
        } catch (Exception e) {
            logger.error("更新日历失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("更新日历失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R batchUpdateCalendar(List<EpdCalendar> calendarList, String invOrg) {
        try {
            if (CollectionUtils.isEmpty(calendarList)) {
                return R.error("更新数据不能为空");
            }
            // 校验所有记录的 inv_org 是否与查询头一致
            if (StringUtils.isNotEmpty(invOrg)) {
                for (EpdCalendar item : calendarList) {
                    if (!invOrg.equals(item.getInv_org())) {
                        return R.error("只能修改当前库存组织下的数据");
                    }
                }
            }
            updateBatchById(calendarList, BATCH_SIZE);
            return R.ok("批量更新成功，共更新 " + calendarList.size() + " 条记录");
        } catch (Exception e) {
            logger.error("批量更新日历失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("批量更新日历失败：" + e.getMessage());
        }
    }

    @Override
    public R getCalendarById(Integer id) {
        try {
            if (id == null) {
                return R.error("ID不能为空");
            }
            EpdCalendar calendar = getById(id);
            if (calendar == null) {
                return R.error("记录不存在");
            }
            return R.ok().setdata(calendar);
        } catch (Exception e) {
            logger.error("查询日历详情失败", e);
            return R.error("查询日历详情失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R deleteCalendar(String calendarId, String invOrg) {
        try {
            if (StringUtils.isEmpty(calendarId)) {
                return R.error("日历ID不能为空");
            }
            if (StringUtils.isEmpty(invOrg)) {
                return R.error("库存组织不能为空");
            }
            QueryWrapper<EpdCalendar> wrapper = new QueryWrapper<>();
            wrapper.eq("calendar_id", calendarId);
            wrapper.eq("inv_org", invOrg);
            baseMapper.delete(wrapper);
            return R.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除日历失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("删除日历失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R deleteByYearRange(String calendarId, String invOrg, Integer startYear, Integer endYear) {
        try {
            if (StringUtils.isEmpty(calendarId)) {
                return R.error("日历ID不能为空");
            }
            if (StringUtils.isEmpty(invOrg)) {
                return R.error("库存组织不能为空");
            }
            if (startYear == null || endYear == null || startYear > endYear) {
                return R.error("年份范围无效");
            }

            Date startDate = getDate(startYear, 1, 1);
            Date endDate = getDate(endYear, 12, 31);

            epdCalendarMapper.deleteByDateRange(calendarId, invOrg, startDate, endDate);
            return R.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除日历失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("删除日历失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R pushToShopCalendar(String calendarId) {
        try {
            if (StringUtils.isEmpty(calendarId)) {
                return R.error("日历ID不能为空");
            }
            return R.ok("推送至车间日历功能待实现");
        } catch (Exception e) {
            logger.error("推送车间日历失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("推送车间日历失败：" + e.getMessage());
        }
    }

    private Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取星期几，1=星期一, 2=星期二, ..., 7=星期日
     */
    private int getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 1 ? 7 : dayOfWeek - 1;
    }

    private String getWeekDayName(int weekDay) {
        String[] weekDays = {"", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        return weekDays[weekDay];
    }

    private String checkFestival(int year, int month, int day) {
        for (Map.Entry<String, FestivalConfig> entry : FESTIVAL_CONFIGS.entrySet()) {
            FestivalConfig config = entry.getValue();
            if (config.getMonth() == month && config.getDay() == day) {
                return entry.getKey();
            }
        }
        return "";
    }

    private static class FestivalConfig {
        private int month;
        private int day;
        private int isCycle;

        public FestivalConfig(int month, int day, int isCycle) {
            this.month = month;
            this.day = day;
            this.isCycle = isCycle;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getIsCycle() {
            return isCycle;
        }
    }
}