package com.jcloud.system.service;

import com.jcloud.system.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * sql 识别
 * @Auther: yang
 * @Date: 2023/04/10/11:34
 * @Description:
 */
public interface UnitSqlService {

    /**
     * 查询数据库表的字段
     * @param tableName  表名
     * @return
     */
    public List<Map<String,Object>> findTableField(String tableName);

    /**
     *获取时间
     * @return
     */
    String sysdata();

    /**
     * 日期时间字符串转日期时间类型
     * @param value
     * @return
     */
    String strTurnDate(String value);

    /**
     * 返回时间查询
     * @param sqlPage
     * @param parameter_key
     * @param date1
     * @return
     */
    void getDateSql(StringBuilder sqlPage, String parameter_key, String date1);

    /**
     * 返回时间范围查询
     * @param sqlPage
     * @param parameter_key 参数字段
     * @param date1  时间1
     * @param date2  时间2
     * @return
     */
    void getDateBetweenSql(StringBuilder sqlPage, String parameter_key, String date1, String date2);

    /**
     * 获取主键
     * @param insertdata
     * @param key
     * @return
     */
    Map<String, Object> returnInsertKey(String insertdata, String key);

    /**
     * 分页查询(低效率版本)
     * @param sql   主sql
     * @param offset  从好多条数据开始
     * @param limit    查询好多条数据
     * @return
     */
    public PageUtils getPage(String sql, int offset, int limit, String orderKey);;
}
