package com.jcloud.epd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 获取阳历和阴历节日的节气生肖、干支年、农历年、农历月、农历日、公历节日、阴历节日、24节气等
 */
public class LunarCalendarFestivalUtils {
    // 生肖
    private String animal;
    // 干支年
    private String ganZhiYear;
    // 农历年
    private String lunarYear;
    // 农历月
    private String lunarMonth;
    // 农历日
    private String lunarDay;
    // 公历节日
    private String solarFestival;
    // 阴历节日
    private String lunarFestival;
    // 节气
    private String lunarTerm;
    // 数字格式的农历年
    private int lunarYearNum;
    // 数字格式的农历月
    private int lunarMonthNum;
    // 数字格式的农历日
    private int lunarDayNum;
    // 是否为闰月
    private boolean isLeapMonth;

    /**
     * 获取数字格式的农历年
     */
    public int getLunarYearNum() {
        return lunarYearNum;
    }

    /**
     * 获取数字格式的农历月
     */
    public int getLunarMonthNum() {
        return lunarMonthNum;
    }

    /**
     * 获取数字格式的农历日
     */
    public int getLunarDayNum() {
        return lunarDayNum;
    }

    /**
     * 获取是否为闰月
     */
    public boolean isLeapMonth() {
        return isLeapMonth;
    }

    /**
     * 获取查询日期的节气生肖
     */
    public String getAnimal() {
        return animal;
    }

    /**
     * 获取查询日期的干支年
     */
    public String getGanZhiYear() {
        return ganZhiYear;
    }

    /**
     * 获取查询日期的农历年
     */
    public String getLunarYear() {
        return lunarYear;
    }

    /**
     * 获取查询日期的农历月
     */
    public String getLunarMonth() {
        return lunarMonth;
    }

    /**
     * 获取查询日期的农历日
     */
    public String getLunarDay() {
        return lunarDay;
    }

    /**
     * 获取查询日期的公历节日（如果不是节日返回空）
     */
    public String getSolarFestival() {
        return solarFestival;
    }

    /**
     * 获取查询日期的阴历节日（如果不是节日返回空）
     */
    public String getLunarFestival() {
        return lunarFestival;
    }

    /**
     * 获取查询日期的节气内容（如果不是节气返回空）
     */
    public String getLunarTerm() {
        return lunarTerm;
    }

    // 数组表示农历每年的信息
    // 1-4位表示是否有闰月，有的话为新的月份的月份，没有的话为0
    // 5-16位为农历每年的月份的大小月分布，1为30天，0为29天。
    // 注意：从1月到12月对应的是第16位到第5位
    // 17-20位表示闰月是大月还是小月，如果有闰月的话。
    // 举例：
    // 1980年的数据是：0x095b0
    // 二进制：0000 1001 0101 1011 0000
    // 表示1980年没有闰月，从1月到12月的天数分别为：30、29、29、30、29、30、29、30、30、29、30、30
    // 1982年的数据是：0x0a974
    // 0000 1010 0 1001 0111 0100
    // 表示1982年4月为闰月，闰月的第二个4月，闰月是小月。
    // 从1月到13月的天数分别为：30、29、30、29、29(闰月)、30、29、29、30、29、30、30、30
    private static final long[] lunarInfo = new long[]{
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, //1900-1909
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0, //1950-1959
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x05ac0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, //2000-2009
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, //2020-2029
            0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
            0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0, //2050-2059
            0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4, //2060-2069
            0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0, //2070-2079
            0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160, //2080-2089
            0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a4d0, 0x0d150, 0x0f252, //2090-2099
            0x0d520 //2100
    };
    // 公历每月天数
    private static final int[] solarMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    // 生肖
    private static final String[] animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    // 天干
    private static final String[] tianGan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    // 地支
    private static final String[] diZhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    // 农历年
    private static final String[] lunarYears = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    // 农历月份
    private static final String[] lunarMonths = new String[]{"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    // 农历日期
    private static final String[] lunarNumber = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private static final String[] chineseTen = new String[]{"初", "十", "廿", "三"};

    // 二十四个节气
    private static final String[] solarTerms = new String[]{"小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
            "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};
    // 二十四个节气偏移量
    private static final double D = 0.2422;
    // 定义一个二维数组，第一维数组存储的是20世纪的节气C值，第二维数组存储的是21世纪的节气C值。0到23个分别代表：小寒、大寒、立春、雨水、惊蛰、春分、清明、谷雨...等24个节气C值
    private static final double[][] CENTURY_ARRAY = {
            {6.11, 20.84, 4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.888, 6.318, 21.86, 6.5, 22.2, 7.928, 23.65, 8.35, 23.95, 8.44, 23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6},
            {5.4055, 20.12, 3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678, 21.37, 7.108, 22.83, 7.5, 23.13, 7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94}
    };
    // 根据年份节气偏移量
    private static final Map<Integer, Integer[]> INCREASE_OFFSETMAP = new HashMap<>();//+1偏移
    private static final Map<Integer, Integer[]> DECREASE_OFFSETMAP = new HashMap<>();//-1偏移

    static {
        INCREASE_OFFSETMAP.put(0, new Integer[]{1982});//小寒
        DECREASE_OFFSETMAP.put(0, new Integer[]{2019});//小寒
        INCREASE_OFFSETMAP.put(1, new Integer[]{2000, 2082});//大寒
        DECREASE_OFFSETMAP.put(3, new Integer[]{2026});//雨水
        INCREASE_OFFSETMAP.put(5, new Integer[]{2084});//春分
        INCREASE_OFFSETMAP.put(8, new Integer[]{1911});//立夏
        INCREASE_OFFSETMAP.put(9, new Integer[]{2008});//小满
        INCREASE_OFFSETMAP.put(10, new Integer[]{1902});//芒种
        INCREASE_OFFSETMAP.put(11, new Integer[]{1928});//夏至
        INCREASE_OFFSETMAP.put(12, new Integer[]{1925, 2016});//小暑
        INCREASE_OFFSETMAP.put(13, new Integer[]{1922});//大暑
        INCREASE_OFFSETMAP.put(14, new Integer[]{2002});//立秋
        INCREASE_OFFSETMAP.put(16, new Integer[]{1927});//白露
        INCREASE_OFFSETMAP.put(17, new Integer[]{1942});//秋分
        INCREASE_OFFSETMAP.put(19, new Integer[]{2089});//霜降
        INCREASE_OFFSETMAP.put(20, new Integer[]{2089});//立冬
        INCREASE_OFFSETMAP.put(21, new Integer[]{1978});//小雪
        INCREASE_OFFSETMAP.put(22, new Integer[]{1954});//大雪
        DECREASE_OFFSETMAP.put(23, new Integer[]{1918, 2021});//冬至
    }

    // 阴历节日
    private static final String[] lunarHoliday = new String[]{"0101 春节", "0115 元宵节", "0202 龙抬头", "0303 上巳节", "0505 端午节", "0707 七夕节", "0715 中元节",
            "0815 中秋节", "0909 重阳节", "1001 寒衣节", "1015 下元节", "1208 腊八节", "1223 祭灶节", "1224 祭灶节"};
    // 公历节日
    private static final String[] solarHoliday = new String[]{
            "0101 元旦", "0110 中国人民警察节", "0214 情人节", "0305 学雷锋纪念日", "0308 妇女节", "0312 植树节", "0315 消费者权益日",
            "0422 地球日", "0423 世界读书日", "0501 劳动节", "0504 青年节", "0512 护士节", "0518 国际博物馆日", "0601 儿童节", "0605 环境日",
            "0701 建党节&香港回归纪念日", "0707 全民族抗战纪念日", "0801 建军节", "0819 中国医师节", "0903 中国人民抗日战争胜利纪念日", "0910 教师节", "0918 九一八事变纪念日", "0930 烈士纪念日",
            "1001 国庆节", "1024 联合国日", "1108 记者节", "1204 宪法日", "1213 南京大屠杀死难者国家公祭日", "1220 澳门回归纪念日", "1224 平安夜", "1225 圣诞节"
    };

    // 格式化日期（统一使用UTC时区，避免1900年LMT与现代CST的历史时区偏移导致天数计算误差）
    private static final SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    private static final SimpleDateFormat solarDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    static {
        chineseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        solarDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * 计算农历y年的总天数
     */
    private int lunarYearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            sum += ((lunarInfo[y - 1900] & i) != 0 ? 1 : 0);
        }
        return (sum + leapDays(y));
    }

    /**
     * 计算农历y年m月的天数
     */
    private int monthDays(int y, int m) {
        return ((lunarInfo[y - 1900] & (0x10000 >> m)) != 0 ? 30 : 29);
    }

    /**
     * 计算农历y年闰月的天数
     */
    private int leapDays(int y) {
        if (leapMonth(y) != 0) {
            return ((lunarInfo[y - 1900] & 0x10000) != 0 ? 30 : 29);
        } else
            return 0;
    }

    /**
     * 判断y年的农历闰月是那个月份，不闰月返回0
     */
    private int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    /**
     * 获取农历年
     */
    private String getLunarYearString(String year) {
        int y1 = Integer.parseInt(year.charAt(0) + "");
        int y2 = Integer.parseInt(year.charAt(1) + "");
        int y3 = Integer.parseInt(year.charAt(2) + "");
        int y4 = Integer.parseInt(year.charAt(3) + "");
        return lunarYears[y1] + lunarYears[y2] + lunarYears[y3] + lunarYears[y4];
    }

    /**
     * 获取农历日
     */
    private String getLunarDayString(int day) {
        if (day > 30)
            return "";

        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day == 10) {
            return "初十";
        } else if (day == 20) {
            return "二十";
        } else {
            return chineseTen[day / 10] + lunarNumber[n];
        }
    }

    /**
     * 根据特殊年份的节气偏移量，在公式计算的基础上加上偏移，使得节气日期更准确
     *
     * @param year 年份
     * @param n    节气索引
     * @return 节气偏移量
     */
    private int specialYearOffset(int year, int n) {
        int offset = 0;
        offset += getOffset(DECREASE_OFFSETMAP, year, n, -1);
        offset += getOffset(INCREASE_OFFSETMAP, year, n, 1);
        return offset;
    }

    /**
     * 获取偏移量
     */
    private int getOffset(Map<Integer, Integer[]> map, int year, int n, int offset) {
        int off = 0;
        Integer[] years = map.get(n);
        if (null != years) {
            for (int i : years) {
                if (i == year) {
                    off = offset;
                    break;
                }
            }
        }
        return off;
    }

    /**
     * 获取某年的第n个节气为几日(从0小寒起算)
     */
    private int sTerm(int year, int n) {
        int centuryIndex;
        if (year >= 1901 && year < 2000) {//20世纪
            centuryIndex = 0;
        } else if (year == 2000) {//20和21世纪
            if (n < 4) {
                centuryIndex = 0;
            } else {
                centuryIndex = 1;
            }
        } else if (year >= 2001 && year < 2100) {//21世纪
            centuryIndex = 1;
        } else {
            // throw new RuntimeException("不支持此年份：" + year + "，目前只支持1901年~2100年的时间范围");
            return -1;
        }
        double centuryValue = CENTURY_ARRAY[centuryIndex][n];// 节气世纪值，每个节气每个世纪的值都不同

        int Y = year % 100;// 步骤1：取年份的后两位
        int L = Y / 4;// 步骤2：计算闰年
        if (n < 4) {
            //L和L=Y/4只适用于小寒、大寒、立春、雨水，L=(Y-1)/4
            L = (Y - 1) / 4;
        }
        int dateNum = (int) (Y * D + centuryValue) - L;// 步骤3：计算通用公式，公式=[Y*D+C]-L
        dateNum += specialYearOffset(year, n);// 步骤4：加上特殊年份的节气偏移量
        return dateNum;
    }

    /**
     * 母亲节和父亲节
     */
    private String getMotherOrFatherDay(int year, int month, int day) {
        if (month != 5 && month != 6) return null;
        if ((month == 5 && (day < 8 || day > 14)) || (month == 6 && (day < 15 || day > 21))) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int weekDate = calendar.get(Calendar.DAY_OF_WEEK);
        weekDate = (weekDate == 1) ? 7 : weekDate - 1;
        switch (month) {
            case 5:
                if (day == 15 - weekDate) {
                    return "母亲节";
                }
                break;
            case 6:
                if (day == 22 - weekDate) {
                    return "父亲节";
                }
                break;
        }
        return null;
    }

    /**
     * 感恩节
     */
    private String thanksgiving(int year, int month, int day) {
        if (month != 11) return null;
        if ((month == 11 && (day < 22 || day > 28))) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int weekDate = calendar.get(Calendar.DAY_OF_WEEK);
        weekDate = (weekDate == 1) ? 7 : weekDate - 1;
        if (weekDate - 4 <= 0) {
            weekDate += 7;
        }
        switch (month) {
            case 11:
                if (day == 29 - weekDate + 4) {
                    return "感恩节";
                }
                break;
        }
        return null;
    }

    /**
     * 传入阳历和阴历日期初始化当前的生肖、干支、农历年、农历月、农历日、公历节日、阴历节日、24节气
     * 传入日期的格式为(YYYY-MM-DD)
     */
    public void initLunarCalendarInfo(String currentDate) {
        // 基准日期
        Date baseDate = null;
        // 当前日期
        Date nowDay = null;
        try {
            baseDate = solarDateFormat.parse("1900-01-31");// 基准：1900年1月31日是农历1900年正月初一
            nowDay = solarDateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 获取当前日期到1900年1月31日之间的天数
        int offset = (int) ((nowDay.getTime() - baseDate.getTime()) / 86400000L);

        // 从offset中去除每年农历年的总天数，算出当年的农历剩余几天，iYear为最后得到的农历年份
        int iYear;
        int daysOfYear = 0;
        for (iYear = 1900; iYear < 10000 && offset > 0; iYear++) {
            daysOfYear = lunarYearDays(iYear);
            offset -= daysOfYear;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
        }
        // 得到生肖
        this.animal = animals[(iYear - 4) % 12];
        // 得到干支年
        int num = iYear - 1900 + 36;
        this.ganZhiYear = (tianGan[num % 10] + diZhi[num % 12]);
        // 得到农历年数
        this.lunarYear = getLunarYearString(iYear + "");
        // 保存数字格式的农历年
        this.lunarYearNum = iYear;

        boolean leap = false;
        int leapMonth = leapMonth(iYear); // 哪个月是闰月，1-12
        // 用得到的offset依次减去每个月，农历月份就是得到的新月份的日期
        int iMonth;
        int daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(iYear);
            } else {
                daysOfMonth = monthDays(iYear, iMonth);
            }

            offset -= daysOfMonth;
            // 解除闰月
            if (leap && iMonth == (leapMonth + 1)) {
                leap = false;
            }
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
        }
        // 设置对应的农历月份
        this.lunarMonth = lunarMonths[iMonth - 1];
        if (leap) {
            this.lunarMonth = "闰" + this.lunarMonth;
        }
        // 保存数字格式的农历月
        this.lunarMonthNum = iMonth;
        // 保存是否为闰月
        this.isLeapMonth = leap;

        // 得到农历日
        int iDay = offset + 1;
        this.lunarDay = getLunarDayString(iDay);
        // 保存数字格式的农历日
        this.lunarDayNum = iDay;

        String[] splitDate = currentDate.split("-");
        // 阳历日期
        int year = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]);
        int day = Integer.parseInt(splitDate[2]);
        // 设置节气
        if (day == sTerm(year, (month - 1) * 2)) {
            this.lunarTerm = solarTerms[(month - 1) * 2];
        } else if (day == sTerm(year, (month - 1) * 2 + 1)) {
            this.lunarTerm = solarTerms[(month - 1) * 2 + 1];
        } else {
            this.lunarTerm = "";
        }

        // 设置公历节日
        String solarFestival = "";
        for (int i = 0; i < solarHoliday.length; i++) {
            // 返回公历节日节日日期
            String sd = solarHoliday[i].split(" ")[0]; // 节日的日期
            String sdv = solarHoliday[i].split(" ")[1]; // 节日的名称
            String currentSM = month + "";
            if (month < 10) {
                currentSM = "0" + month;
            }
            String currentSD = day + "";
            if (day < 10) {
                currentSD = "0" + day;
            }
            String currentSMD = currentSM + currentSD;
            if (sd.trim().equals(currentSMD.trim())) {
                solarFestival = sdv;
                break;
            }
        }
        // 判断节日是否是父亲节或母亲节
        String motherOrFatherDay = getMotherOrFatherDay(year, month, day);
        if (motherOrFatherDay != null) {
            solarFestival = motherOrFatherDay;
        }
        // 判断节日是否是感恩节
        String thanksgiving = thanksgiving(year, month, day);
        if (thanksgiving != null) {
            solarFestival = thanksgiving;
        }
        this.solarFestival = solarFestival;

        // 设置阴历节日
        String lunarFestival = "";
        for (int i = 0; i < lunarHoliday.length; i++) {
            // 阴历闰月节日
            if (leap) {
                break;
            }
            // 返回阴历节日节日日期
            String ld = lunarHoliday[i].split(" ")[0]; // 节日的日期
            String ldv = lunarHoliday[i].split(" ")[1]; // 节日的名称
            String currentLM = iMonth + "";
            if (iMonth < 10) {
                currentLM = "0" + iMonth;
            }
            String currentLD = iDay + "";
            if (iDay < 10) {
                currentLD = "0" + iDay;
            }
            String currentLMD = currentLM + currentLD;
            if (ld.trim().equals(currentLMD.trim())) {
                lunarFestival = ldv;
                break;
            }
            if ("12".equals(currentLM) && iDay == daysOfMonth) { // 除夕夜需要特殊处理
                lunarFestival = "除夕";
                break;
            }
        }
        if ("清明".equals(this.lunarTerm)) {
            lunarFestival = "清明节";
        }
        this.lunarFestival = lunarFestival;
    }

}
