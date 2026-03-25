package com.jcloud.system.service.impl;

import com.jcloud.system.service.SqlService;
import com.jcloud.system.service.UnitSqlService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yang
 * @Date: 2023/04/11/16:43
 * @Description:
 */
@Service
public class UnitSqlServiceImpl implements UnitSqlService {


    @Autowired
    private SqlService sqlService;

    private String dbType;

    @PostConstruct
    private void getdbType() {
        dbType = sqlService.getDBtype();
    }


    @Override
    public List<Map<String, Object>> findTableField(String tableName) {
        StringBuilder sql = new StringBuilder();
        if (dbType.equals("oracle")) {
            tableName = tableName.toUpperCase();
            sql.append("select ut.TABLE_NAME,ut.COLUMN_NAME AS field_name, uc.COMMENTS AS field_zname,ut.DATA_TYPE AS field_type,ut.DATA_LENGTH AS field_extent ,decode(ut.NULLABLE,'N','0','Y','1',ut.NULLABLE) AS field_null,ut.COLUMN_ID AS field_rank ")
                    .append("from user_tab_columns  ut inner JOIN user_col_comments uc on ut.TABLE_NAME  = uc.table_name and ut.COLUMN_NAME = uc.column_name where ut.Table_Name='")
                    .append(tableName).append("' order by  field_rank ");
        } else if (dbType.equals("sqlserver")) {
            sql.append("SELECT\n" +
                    "     C.name as [field_name],T.name as [field_type]\n" +
                    "     ,CAST(convert(bit,C.IsNullable) as int)  as [field_null]\n" +
                    "     ,CAST(convert(bit,case when exists(SELECT 1 FROM sysobjects where xtype='PK' and parent_obj=c.id and name in (SELECT name FROM sysindexes WHERE indid in(SELECT indid FROM sysindexkeys WHERE id = c.id AND colid=c.colid))) then 1 else 0 end) as int) as [iskey]\n" +
                    "     ,case  when convert(bit,COLUMNPROPERTY(c.id,c.name,'IsIdentity'))=0 then 1 else 0 end as [isaddkey]\n" +
                    "     ,C.Length as [length] \n" +
                    "     ,COLUMNPROPERTY(C.id,C.name,'PRECISION') as [length2]\n" +
                    "     ,isnull(COLUMNPROPERTY(c.id,c.name,'Scale'),0) as [decimals]\n" +
                    "     ,ISNULL(CM.text,'') as [parameter_default]\n" +
                    "     ,CAST(isnull(ETP.value,'') as VARCHAR) AS [field_zname]\n" +
                    "     ,ROW_NUMBER() OVER (ORDER BY C.name) AS [Row]\n" +
                    "FROM syscolumns C\n" +
                    "INNER JOIN systypes T ON C.xusertype = T.xusertype \n" +
                    "left JOIN sys.extended_properties ETP   ON  ETP.major_id = c.id AND ETP.minor_id = C.colid AND ETP.name ='MS_Description' \n" +
                    "left join syscomments CM on C.cdefault=CM.id\n" +
                    "WHERE C.id = object_id('" + tableName + "')");
        } else if (dbType.equals("mysql")) {
            sql.append(" SELECT table_name,column_name as field_name,ORDINAL_POSITION as field_rank ,COLUMN_DEFAULT as field_default,IS_NULLABLE as field_null,DATA_TYPE as field_type ,CHARACTER_MAXIMUM_LENGTH as field_extent,COLUMN_TYPE,COLUMN_COMMENT as field_zname,COLUMN_KEY FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + sqlService.getDBname() + "' and TABLE_NAME='" + tableName + "' ORDER BY ORDINAL_POSITION");
        }
        List<Map<String, Object>> all = sqlService.getAll(sql.toString());
        if (all.size() == 0) {
            return null;
        }
        return all;
    }


    @Override
    public String sysdata() {
        if (dbType.equals("sqlserver")) {
            return "GETDATE()";
        } else if (dbType.equals("oracle")) {
            return "sysdate"; //oracle
        } else if (dbType.equals("mysql")) {
            return "now()"; //mysql
        }
        return "";
    }


    @Override
    public String strTurnDate(String value) {
        if (dbType.equals("mysql") || dbType.equals("sqlserver")) {
            return "CAST('" + value + "' as datetime)";
        } else if (dbType.equals("oracle")) {
            return "to_date('" + value + "','yyyy/MM/dd hh24:mi:ss')";
        }
        return "";
    }


    @Override
    public void getDateSql(StringBuilder sqlPage, String parameter_key, String date1) {
        if (dbType.equals("mysql") || dbType.equals("sqlserver")) {
            sqlPage.append(parameter_key + " = CAST('" + date1 + "' as datetime)");
        } else if (dbType.equals("oracle")) {
            sqlPage.append(parameter_key + " = to_date('" + date1 + "','yyyy/MM/dd hh24:mi:ss')");
        }
    }

    @Override
    public void getDateBetweenSql(StringBuilder sqlPage, String parameter_key, String date1, String date2) {
        if (dbType.equals("mysql") ||dbType.equals("sqlserver")) {
            sqlPage.append(parameter_key + " between  CAST('" + date1 + "' as datetime) and CAST('" + date2 + "' as datetime)");
        }else if (dbType.equals("oracle")) {
            sqlPage.append(parameter_key + " between  to_date('" + date1 + "','yyyy/MM/dd hh24:mi:ss') and to_date('" + date2 + "','yyyy/MM/dd hh24:mi:ss')");
        }
    }

    @Override
    public Map<String, Object> returnInsertKey(String insertdata, String key) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (dbType.equals("mysql")){
//            insertdata += ";SELECT LAST_INSERT_ID();";
            if(sqlService.insertOne(insertdata)){
                Object onef = sqlService.getOnef("select LAST_INSERT_ID()");
                map.put("keyid", ObjectUtils.oGetS(onef));
                map.put("rt_code", null);
            }
        } else if (dbType.equals("oracle")) { //占时不使用
//            StoredProcedureQuery call = storedProcedureRepository.getStoredProcedureQuery("YANG.YANG_INSERT");
//            call.registerStoredProcedureParameter(1, String.class, ParameterMode.IN).setParameter(1, insertdata);
//            call.registerStoredProcedureParameter(2, String.class, ParameterMode.IN).setParameter(2, key);
//            call.registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);
//            call.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT);
//            call.registerStoredProcedureParameter(5, String.class, ParameterMode.OUT);
//            call.execute();
//            map.put("keyid", call.getOutputParameterValue(3));
//            map.put("rt_code", call.getOutputParameterValue(4));
//            map.put("er_buff", call.getOutputParameterValue(5));
        } else if (dbType.equals("sqlserver")) {
            int values = insertdata.indexOf(" values(");
            insertdata = insertdata.substring(0, values) + " output inserted." + key + insertdata.substring(values);
            Object onef = sqlService.getOnef(insertdata);
            map.put("keyid", ObjectUtils.oGetS(onef));
            map.put("rt_code", null);
        }
        return map;
    }


    /**
     *
     * @param sql   主sql
     * @param offset  从好多条数据开始
     * @param limit    查询好多条数据
     * @param orderKey
     * @return
     */
    @Override
    public PageUtils getPage(String sql, int offset, int limit, String orderKey) {
        Long count = sqlService.getCount(sql);
        StringBuilder sqlPage = new StringBuilder();
        if(dbType.equals("mysql")){
            sqlPage.append(sql).append(" limit ").append(offset).append(",").append(limit);
        } else if (dbType.equals("oracle")) {
            int pagesum = offset + limit;
            sqlPage.append("select * from ").append("(select rownum  as romon,t.*  from (").append(sql).append(") t where rownum<=").append(pagesum).append(") tt ")
                    .append(" where  tt.romon >").append(offset);
        } else if (dbType.equals("sqlserver")) {
            int ob = sql.indexOf("order by ");
            if (ob == -1) {
                sql += " order by " + orderKey;
            }
            sqlPage.append(sql)
                    .append(" offset " + offset + " rows fetch next " + limit + " rows only");
        }
        PageUtils pageUtils = new PageUtils(sqlService.getAll(sqlPage.toString()), count);
        return pageUtils;
    }
}
