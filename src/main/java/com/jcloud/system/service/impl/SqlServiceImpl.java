package com.jcloud.system.service.impl;

import com.jcloud.system.config.JcloudConfig;
import com.jcloud.system.mapper.MyBatisSqlRepositoy;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.PageUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * 直接数据库操作
 * @Auther: yang
 * @Date: 2023/4/4 15:19
 * @Description:
 */
@Service
public class SqlServiceImpl implements SqlService {

    //    读取配置文件
    @Autowired
    private JcloudConfig jcloudConfig;

    @Autowired
    private MyBatisSqlRepositoy myBatisSqlRepositoy;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public String getUploadPath() {
        return jcloudConfig.getUploadPath();
    }

    @Override
    public String getDBtype() {
        return jcloudConfig.getDbtype();
    }

    @Override
    public String getDBname() {
        return jcloudConfig.getDbname();
    }

    @Override
    public List<Map<String, Object>> getAll(String randomSql) {
        List<Map<String, Object>> all = myBatisSqlRepositoy.getAll(randomSql);
        List<Map<String, Object>> maps = ObjectUtils.lowerList(all);
        return maps;
    }

    @Override
    public Map<String, Object> getOne(String randomSql) {
        Map<String, Object> one = myBatisSqlRepositoy.getOne(randomSql);
        if(one == null){
            return null;
        }
        Map<String, Object> stringObjectMap = ObjectUtils.lowerMap(one);
        return stringObjectMap;
    }

    @Override
    public Object getOnef(String randomSql) {
        Object onef = myBatisSqlRepositoy.getOnef(randomSql);
        return onef;
    }

    @Override
    public Map<String,Object> callFunction(String randomSql) {
        Map<String,Object> onef = myBatisSqlRepositoy.callFunction(randomSql);
        return onef;
    }

    @Override
    public Boolean updateOne(String randomSql) {
        Boolean onef = myBatisSqlRepositoy.updateOne(randomSql);
        return onef;
    }

    @Override
    public Boolean updateList(List<String> randomSqlList) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (String randomSql:
                    randomSqlList) {
                myBatisSqlRepositoy.updateOne(randomSql);
            }
            sqlSession.commit();
        }
        return true;
    }

    @Override
    public Boolean updateListTry(List<String> randomSqlList) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (String randomSql:
                    randomSqlList) {
                myBatisSqlRepositoy.updateOne(randomSql);
            }
            sqlSession.commit();
        }catch (Exception e) {
            System.err.println(e);
            return false;
        }

        return true;
    }

    @Override
    public Boolean insertOne(String randomSql) {
        Boolean onef = myBatisSqlRepositoy.insertOne(randomSql);
        return onef;
    }


    @Override
    public Boolean insertList(List<String> randomSqlList) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (String randomSql:
                    randomSqlList) {
                myBatisSqlRepositoy.insertOne(randomSql);
            }
            sqlSession.commit();
        }
        return true;
    }

    @Override
    public List<Long> getListId(String randomSql) {
        List<Long> listId = myBatisSqlRepositoy.getListId(randomSql);
        return listId;
    }

    @Override
    public List<String> getListString(String randomSql) {
        List<String> listString = myBatisSqlRepositoy.getListString(randomSql);
        return listString;
    }

    @Override
    public Long getOneInt(String randomSql) {
        Long oneInt = myBatisSqlRepositoy.getOneInt(randomSql);
        return oneInt;
    }

    @Override
    public Boolean deleteOne(String randomSql) {
        Boolean aBoolean = myBatisSqlRepositoy.deleteOne(randomSql);
        return aBoolean;
    }

    @Override
    public Boolean deleteList(List<String> randomSqlList) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (String randomSql:
                    randomSqlList) {
                myBatisSqlRepositoy.deleteOne(randomSql);
            }
            sqlSession.commit();
        }
        return true;
    }

    /**
     * 查询字典
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> getDict(String type) {
//        List<Map<String, Object>> dict = myBatisSqlRepositoy.getDict(type);
//        List<Map<String, Object>> maps = ObjectUtils.lowerList(dict);
        return null;
    }

    @Override
    public String getRoleValue(String value) {
//        String roleName = myBatisSqlRepositoy.getRoleValue(value);
        return null;
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        String sql  = "select role_id from sys_user_role where user_id = "+userId;
        List<Long> listId = myBatisSqlRepositoy.getListId(sql);
        return listId;
    }


    @Override
    public String getTopDept(String dept) {
        Map<String, Object> stringObjectMap = tpDate(dept);
        String grade = stringObjectMap.get("grade").toString();
        String parent_id = stringObjectMap.get("parent_id").toString();
        while(true){
            if(parent_id.equals("0")){
                break;
            }
            Map<String, Object> stringObjectMap1 = tpDate(parent_id);
            if(!stringObjectMap1.get("grade").toString().equals(grade)){
                break;
            }
            parent_id = stringObjectMap1.get("parent_id").toString();
        }
        return parent_id;
    }

    @Override
    public String getGrade(String dept) {
        String sql = "select  grade from sys_dept where  dept_id = "+dept;
        String grade = oGetS(getOnef(sql));
        return grade;
    }

    Map<String, Object> tpDate(String dept){
        String sql  = "select parent_id,grade from sys_dept where dept_id = "+dept;
        Map<String, Object> one = getOne(sql);
        return one;
    }

    /**
     * oracle
     * @param sql   主sql
     * @param where  过滤条件
     * @param offset  从好多条数据开始
     * @param limit    查询好多条数据
     * @return
     */
//    @Override
//    public PageUtils getPage(String sql, String where, String offset, String limit) {
//        String select = sql.substring(sql.indexOf("select") + 6);
//        if(sql.indexOf("* ") != -1){
//        select = select.replace("*","t.*");
//            select = select + " t";
//        }
//        String sql2 = "SELECT ROWNUM AS rowno,"+select ;
//        Long count  = getCount(sql,where);
//        int pagesum = Integer.valueOf(offset)+Integer.valueOf(limit);
//        where = where == null?"":where;
//        if(where.equals("")){
//            sql = "SELECT * FROM ("+sql2 + " WHERE  ROWNUM <= "+pagesum+") table_alias WHERE table_alias.rowno > "+offset ;
//        }else{
//            sql = "SELECT * FROM ("+sql2 + " WHERE "+where+" ROWNUM <= "+pagesum+") table_alias WHERE table_alias.rowno > "+offset ;
//        }
//        List<Map<String, Object>> all = myBatisSqlRepositoy.getAll(sql);
//        List<Map<String, Object>> maps = ObjectUtils.lowerList(all);
//        PageUtils pageUtils =  new PageUtils(maps,count);
//        return pageUtils;
//    }

    //使用与mysql
    public PageUtils getPage(String sql, String where, String offset, String limit) {
        String table = sql.substring(sql.indexOf("from") + 4);
        Long count  = getCount(sql,where);
        where = where == null?"":where;
        if(where.equals("")){
            sql = sql + " LIMIT "+offset+","+ limit ;
        }else{
            sql = sql + " where 1=1 and "+where+ " LIMIT "+offset+","+ limit ;
        }
        List<Map<String, Object>> all = myBatisSqlRepositoy.getAll(sql);
        List<Map<String, Object>> maps = ObjectUtils.lowerList(all);
        PageUtils pageUtils =  new PageUtils(maps,count);
        return pageUtils;
    }

    @Override
    public PageUtils getPageOrder(String sql, String where, String offset, String limit, String order) {
        Long count  = getCount(sql,where);
        int pagesum = Integer.valueOf(offset)+Integer.valueOf(limit);
        where = where == null?"":where;
        if(where.equals("")){
            String sql2 =sql + order;
            sql = " SELECT * FROM (SELECT  ROWNUM AS rowno,tt.* FROM ( "+sql2 + ") tt WHERE  ROWNUM <= "+pagesum+") table_alias WHERE table_alias.rowno > "+offset ;
        }else{
            String sql2 =sql +" WHERE 1=1 and "+where+" "+ order;
            sql = " SELECT * FROM (SELECT  ROWNUM AS rowno,tt.* FROM ( "+sql2 + ") tt WHERE  ROWNUM <= "+pagesum+") table_alias WHERE table_alias.rowno > "+offset ;
        }
        List<Map<String, Object>> all = myBatisSqlRepositoy.getAll(sql);
        List<Map<String, Object>> maps = ObjectUtils.lowerList(all);
        PageUtils pageUtils =  new PageUtils(maps,count);
        return pageUtils;
    }

    @Override
    public PageUtils getPage(String sql, String offset, String limit) {
//        StringBuilder sqlPage =  new StringBuilder();
//        int pagesum = Integer.valueOf(offset)+Integer.valueOf(limit);
//        sqlPage.append("select * from ").append("(select rownum  as romon,t.*  from (").append(sql).append(") t where rownum<=").append(pagesum).append(") tt ")
//        .append(" where  tt.romon >").append(offset);
        Long count  = getCount(sql);
        StringBuilder sqlPage = new StringBuilder();
//        if (dbType.equals("oracle")) {
//            int pagesum = Integer.valueOf(offset) + Integer.valueOf(limit);
//            sqlPage.append("select * from ").append("(select rownum  as romon,t.*  from (").append(sql).append(") t where rownum<=").append(pagesum).append(") tt ")
//                    .append(" where  tt.romon >").append(offset);
//        } else if (dbType.equals("sqlserver")) {
//            sqlPage.append(sql)
//                    .append(" offset " + offset + " rows fetch next " + limit + " rows only");
//        }
        PageUtils pageUtils = new PageUtils(getAll(sqlPage.toString()), count);
        return pageUtils;
    }

    @Override
    public Long getCount(String sql) {
        String sum = "";
        if (getDBtype().equals("sqlserver")) {
            int select_ = sql.indexOf("select ");
            sql = sql.substring(0, select_ + 7) + "top 99999999 " + sql.substring(select_ + 7);
        }
        sum = "select count(1) from (" + sql + ") ycount";
        Long count = getOneInt(sum);
        return count;
    }


    @Override
    public Long getCount(String sql, String where) {
        where = where == null?"":where;
        String table = sql.substring(sql.indexOf("from") + 4);
        String sum  = "select count(1) from "+table;
        if(!where.equals("")) {
            if (sql.indexOf(" where ") == -1)
                sum = sum +" where 1=1 and "+ where;
            else
                sum = sum +"  and "+ where;
        }
        Long count = myBatisSqlRepositoy.getOneInt(sum);
        return count;
    }

    public String oGetS(Object a) {
        String b  = a == null?"":a.toString();
        return b;
    }

    public void passMySheetTaskInfo(String id,String username,String userId){
        String sql="update task_info set task_status='FINISHED',finish_time=sysdate() where (processer_id='"+username+"' or processer_id='"+userId+"') " +
                "and sheet_id='"+id+"'";
        myBatisSqlRepositoy.updateOne(sql);
    }

    @Override
    public void passAllSheetTaskInfo(String id) {
        String sql="update task_info set task_status='FINISHED',finish_time=sysdate() where sheet_id='"+id+"'";
        myBatisSqlRepositoy.updateOne(sql);
    }
}
