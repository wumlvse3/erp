package com.jcloud.system.service;

import com.jcloud.system.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public interface SqlService {

//    /**
//     * 返回数据类型
//     * @return
//     */
//    public String getDBtype();
//
    /**
     * 文件路径
     * @return
     */
    public String getUploadPath();

    /**
     * 获取数据库类型
     * @return
     */
    public String getDBtype();


    public String getDBname();

    /**
     * 自定义表单查询所有数据
     * @param randomSql  Sql语句
     * @return 返回List
     * yang
     */
    public List<Map<String,Object>> getAll(String randomSql);

    /**
     *自定义表单查询单独数据
     * @param randomSql
     * @return 返回Map
     * yang
     */
    public Map<String,Object> getOne( String randomSql);

    /**
     * 自定义表单查询一个字段
     * @param randomSql
     * @return
     * yang
     */
    public Object getOnef( String randomSql);


    /**
     * 调用存储过程
     * @param randomSql
     * @return
     */
    public Map<String,Object> callFunction ( String randomSql);

    /**
     * 修改数据
     * @param randomSql
     */
    public  Boolean  updateOne(String randomSql);


    Boolean updateList(List<String> randomSqlList);


    Boolean updateListTry(List<String> randomSqlList);
    /**
     * 修改数据
     * @param randomSql
     */
    public  Boolean  insertOne(String randomSql);

    /**
     * 多次插入一次修改
     * @param randomSqlList
     * @return
     */
    Boolean insertList(List<String> randomSqlList);


    /**
     * 查询一列id
     * @param randomSql
     * @return
     */
    public List<Long> getListId(String randomSql);

    /**
     * 查询一列string类型
     * @param randomSql
     * @return
     */
    public List<String> getListString(String randomSql);

    /**
     * 查询count
     * @param randomSql
     * @return
     */
    public Long getOneInt(String randomSql);

    /**
     * 删除是否成功
     * @param randomSql
     * @return
     */
    public Boolean deleteOne(String randomSql);


    /**
     * 删除是否成功
     * @param randomSqlList
     * @return
     */
    public Boolean deleteList(List<String> randomSqlList);

    /**
     * 查询字典表数据 升序排序
     * @param type
     * @return
     * yang
     */
    public List<Map<String,Object>> getDict(String type);

    /**
     * 查询字典表角色id
     * @param name 角色名字
     * @return
     * yang
     */
    public String getRoleValue(String name);

    /**
     * 获取用户角色id
     * @param userId 用户id
     * @return
     */
    public List<Long> getRoleIds(Long userId);

    /**
     * 获取顶级部门
     * @return
     */
    public String getTopDept(String dept);

    /**
     * 获取部门等级
     * @return
     */
    public String getGrade(String dept);

    /**
     * 分页查询
     * @param sql   主sql
     * @param where  过滤条件
     * @param offset  从好多条数据开始
     * @param limit    查询好多条数据
     * @return
     */
    public PageUtils getPage(String sql, String where, String offset, String limit);

    /**
     * 分页查询
     * @param sql 主sql
     * @param where 过滤条件
     * @param offset 从好多条数据开始
     * @param limit 查询好多条数据
     * @param Order 排序语句
     * @return
     */
    public PageUtils getPageOrder(String sql,String where,String offset,String limit,String Order);

    /**
     * 查询countSql总数
     * @param sql
     * @param where
     * @return
     */
    public Long getCount(String sql,String where);

    /**
     * 分页查询(低效率版本)
     * @param sql   主sql
     * @param offset  从好多条数据开始
     * @param limit    查询好多条数据
     * @return
     */
    public PageUtils getPage(String sql,String offset,String limit);

    /**
     * 查询countSql总数(低效率版本)
     * @param sql
     * @return
     */
    public Long getCount(String sql);


    /**
     * 处理代办任务通知
     * @param id 业务id
     * @param username 用户名
     */
    public void passMySheetTaskInfo(String id,String username,String userId);


    void passAllSheetTaskInfo(String id);
}
