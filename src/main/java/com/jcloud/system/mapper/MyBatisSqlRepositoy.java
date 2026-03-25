package com.jcloud.system.mapper;


import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * sql使用
 * @Auther: yang
 * @Date: 2023/3/29 14:09
 * @Description:
 */
@Mapper
public interface MyBatisSqlRepositoy {

    /**
     * 自定义表单查询所有数据
     * @param randomSql  Sql语句
     * @return 返回List
     * yang
     */
    @Select("${randomSql}")
    public List<Map<String,Object>> getAll(@Param("randomSql") String randomSql);

    /**
     *自定义表单查询单独数据
     * @param randomSql
     * @return 返回Map
     * yang
     */
    @Select("${randomSql}")
    public Map<String,Object> getOne(@Param("randomSql") String randomSql);

    /**
     * 自定义表单查询一个字段
     * @param randomSql
     * @return
     * yang
     */
    @Select("${randomSql}")
    public Object getOnef(@Param("randomSql") String randomSql);

    /**
     * 自定义表单查询一个字段
     * @param randomSql
     * @return
     * yang
     */
    @Select(" CALL  ${randomSql}")
    public Map<String,Object> callFunction (@Param("randomSql") String randomSql);

    /**
     * 修改数据
     * @param randomSql
     */
    @Update("${randomSql}")
    public  Boolean  updateOne(@Param("randomSql") String randomSql);

    /**
     * 修改数据
     * @param randomSql
     */
    @Insert("${randomSql}")
    public  Boolean  insertOne(@Param("randomSql") String randomSql);
    /**
     * 查询一列id
     * @param randomSql
     * @return
     */
    @Select("${randomSql}")
    public List<Long> getListId(@Param("randomSql") String randomSql);

    /**
     * 查询一列string类型
     * @param randomSql
     * @return
     */
    @Select("${randomSql}")
    public List<String> getListString(@Param("randomSql") String randomSql);

    /**
     * 查询count
     * @param randomSql
     * @return
     */
    @Select("${randomSql}")
    public Long getOneInt(@Param("randomSql") String randomSql);

    /**
     * 删除是否成功
     * @param randomSql
     * @return
     */
    @Delete("${randomSql}")
    public Boolean deleteOne(@Param("randomSql") String randomSql);

}
