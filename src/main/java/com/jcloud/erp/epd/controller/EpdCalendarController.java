package com.jcloud.erp.epd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcloud.customer.WmsBaseData.entity.SysOrg;
import com.jcloud.customer.WmsBaseData.mapper.Sys_orgMapper;
import com.jcloud.erp.epd.entity.EpdCalendar;
import com.jcloud.erp.epd.service.EpdCalendarService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/factoryCalendar")
public class EpdCalendarController {

    @Autowired
    private EpdCalendarService epdCalendarService;

    @Autowired
    private Sys_orgMapper sysOrgMapper;

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public R generateCalendar(@RequestBody Map<String, Object> params) {
        String calendarId = (String) params.get("calendarId");
        String invOrg = (String) params.get("invOrg");
        Integer startYear = params.get("startYear") != null ? Integer.parseInt(params.get("startYear").toString()) : null;
        Integer endYear = params.get("endYear") != null ? Integer.parseInt(params.get("endYear").toString()) : null;
        
        @SuppressWarnings("unchecked")
        List<Integer> restDays = (List<Integer>) params.get("restDays");
        Boolean coverDeptCalendar = params.get("coverDeptCalendar") != null 
                ? Boolean.parseBoolean(params.get("coverDeptCalendar").toString()) : false;

        return epdCalendarService.generateCalendar(calendarId, invOrg, startYear, endYear, restDays, coverDeptCalendar);
    }

    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public R queryCalendarList(@RequestBody Map<String, Object> params) {
        return epdCalendarService.queryCalendarList(params);
    }

    @RequestMapping(value = "/queryByMonth", method = RequestMethod.POST)
    public R queryCalendarByMonth(@RequestBody Map<String, Object> params) {
        String calendarId = (String) params.get("calendarId");
        String invOrg = (String) params.get("invOrg");
        Integer year = params.get("year") != null ? Integer.parseInt(params.get("year").toString()) : null;
        Integer month = params.get("month") != null ? Integer.parseInt(params.get("month").toString()) : null;

        return epdCalendarService.queryCalendarByMonth(calendarId, invOrg, year, month);
    }

    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public R getCalendarById(@RequestParam("id") Integer id) {
        return epdCalendarService.getCalendarById(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R updateCalendar(@RequestBody EpdCalendar calendar) {
        return epdCalendarService.updateCalendar(calendar);
    }

    @RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
    public R batchUpdateCalendar(@RequestBody Map<String, Object> params) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("list");
        
        List<EpdCalendar> calendarList = list.stream()
            .map(map -> {
                // 过滤掉 Vue 的特殊字段（以 $ 开头的字段）
                map.remove("$updated");
                map.remove("$index");
                map.remove("$key");
                return objectMapper.convertValue(map, EpdCalendar.class);
            })
            .collect(java.util.stream.Collectors.toList());
        
        String invOrg = (String) params.get("invOrg");
        return epdCalendarService.batchUpdateCalendar(calendarList, invOrg);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R deleteCalendar(@RequestBody Map<String, Object> params) {
        String calendarId = (String) params.get("calendarId");
        String invOrg = (String) params.get("invOrg");
        return epdCalendarService.deleteCalendar(calendarId, invOrg);
    }

    @RequestMapping(value = "/deleteByYearRange", method = RequestMethod.POST)
    public R deleteByYearRange(@RequestBody Map<String, Object> params) {
        String calendarId = (String) params.get("calendarId");
        String invOrg = (String) params.get("invOrg");
        Integer startYear = params.get("startYear") != null ? Integer.parseInt(params.get("startYear").toString()) : null;
        Integer endYear = params.get("endYear") != null ? Integer.parseInt(params.get("endYear").toString()) : null;

        return epdCalendarService.deleteByYearRange(calendarId, invOrg, startYear, endYear);
    }

    @RequestMapping(value = "/pushToShop", method = RequestMethod.POST)
    public R pushToShopCalendar(@RequestBody Map<String, Object> params) {
        String calendarId = (String) params.get("calendarId");
        return epdCalendarService.pushToShopCalendar(calendarId);
    }

    @RequestMapping(value = "/getOrgList", method = RequestMethod.GET)
    public R getOrgList() {
        List<SysOrg> orgList = sysOrgMapper.getOrgList();
        return R.ok().setdata(orgList);
    }

    @RequestMapping(value = "/getDefaultOrg", method = RequestMethod.GET)
    public R getDefaultOrg() {
        UserEntity user = ShiroUtils.getUserEntity();
        if (user == null) {
            return R.error("用户未登录");
        }
        String defaultOrg = sysOrgMapper.getDefaultOrgCode(user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("defaultOrg", defaultOrg);
        return R.ok().setdata(result);
    }
}