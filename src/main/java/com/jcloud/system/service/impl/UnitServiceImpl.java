package com.jcloud.system.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jcloud.customer.WmsBaseData.service.MtlSheetTypesService;
import com.jcloud.system.annotation.SheetCode;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.service.UnitService;
import com.jcloud.system.service.UnitSqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台标准方法
 *
 * @Auther: yang
 * @Date: 2023/04/23/14:25
 * @Description:
 */
@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private UnitSqlService unitSqlService;

    @Autowired
    ComUnitService comUnitService;


    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private MtlSheetTypesService mtlSheetTypesService;

    /**
     * 标准保存
     *
     * @param data     传入数据
     * @param regionId
     * @param unitCode
     * @return
     */



    @Override
    public R normSave(Map<String, Object> data, Long regionId, String unitCode,String unitverify,Class entityClass) throws Exception {
        Map<String, Object> unitMap = getrIdUnit(regionId, unitCode);//获取数据
        if (unitMap == null) {
            return R.error("未查询到对应单元数据");
        }
        String[] strings = verifySql(unitverify);
        if(strings != null ){
            if(verifydata(data, strings[0]))return  R.error(100,strings[1]);
        }
        String dataTable = ObjectUtils.oGetS(unitMap.get("data_table")); //表名
        String dataId = ObjectUtils.oGetS(unitMap.get("data_id"));   //数据源id
        UserEntity userEntity = ShiroUtils.getUserEntity();//用户
        Long id = getrIdUnitId(dataId, dataTable);  //获取数据区域的主键
        if (id == 0) return R.error("未查询到对应数据区域");
        StringBuilder sql2 = new StringBuilder();
        sql2.append("select parameter_key,parameter_keyup,parameter_keytype from sys_com_parameter where UNIT_ID = ").append(id)
                .append(" and PARAMETER_KEYZU = 1 and PARAMETER_STATUS = 1");
        Map<String, Object> one1 = sqlService.getOne(sql2.toString());
        if (one1 == null) R.error("数据源未维护主键或未启用主键保存状态");
        String key = ObjectUtils.oGetS(one1.get("parameter_key")); //主键名称
        String keydata = ObjectUtils.oGetS(data.get(key)); //主键值
        StringBuilder sql3 = new StringBuilder();
        sql3.append("select parameter_key,parameter_keytype,parameter_keyup from sys_com_parameter where UNIT_ID = ")
                .append(id).append(" and PARAMETER_KEYZU != 1 and PARAMETER_STATUS = 1");
        List<Map<String, Object>> all = sqlService.getAll(sql3.toString());
        StringBuilder sqlsum = new StringBuilder();
        if (keydata.equals("")) {
            keydata = getinsert(data, dataTable, userEntity, one1, key, keydata, all, sqlsum,entityClass);
        } else {
            StringBuilder sql4 = new StringBuilder();
            sql4.append(" from ").append(dataTable).append(" where ").append(key).append(" = ").append("'").append(keydata).append("'");
            Map<String, Object> one2 = sqlService.getOne("select " + key + sql4.toString());
            if (one2 == null) {
                StringBuilder fields = new StringBuilder();
                StringBuilder values = new StringBuilder();
                fields.append(key).append(",");
                if (ObjectUtils.oGetS(one1.get("parameter_key")).equals("NUMBER")) {
                    values.append(keydata).append(",");
                } else {
                    values.append("'" + keydata).append("',");
                }
                insertValue(data, userEntity, all, fields, values);
                sqlsum.append("insert into ").append(dataTable).append("(").append(fields.substring(0, fields.length() - 1)).append(")")
                        .append(" values(").append(values.substring(0, values.length() - 1)).append(")");
            } else {
                StringBuilder set = new StringBuilder();
                R r = updateValue(data, userEntity, all, set, sql4);
                if (r.get("code").toString().equals("500")) return r;
                sqlsum.append("update ").append(dataTable).append(" set ").append(set.substring(0, set.length() - 1)).
                        append(" where ").append(key).append(" = ").append("'").append(keydata).append("'");
                sqlService.updateOne(sqlsum.toString());
                return R.ok().put("keyid", keydata).put("keyname", key);
            }
        }
        if (Integer.valueOf(unitMap.get("unit_type").toString()) == 1 && keydata.equals("")) {
            Map<String, Object> insertKey = unitSqlService.returnInsertKey(sqlsum.toString(), key);
            if (insertKey.get("rt_code") == null) {
                keydata = insertKey.get("keyid").toString();
            } else {
                return R.error(ObjectUtils.oGetS(insertKey.get("er_buff")));
            }
        } else sqlService.insertOne(sqlsum.toString());
        return R.ok().put("keyid", keydata).put("keyname", key);
    }

    /**
     * 执行验证sql
     * @param data
     * @param sql
     * @return
     */
    private boolean verifydata(Map<String, Object> data, String sql) {
        Integer integer = Integer.valueOf(sqlService.getOnef(comUnitService.getUnitSql(sql, data, false)).toString());
        if(integer>0) return true;
        else return false;
    }

    /**
     * 获取验证提示与验证sql
     * @param verify  验证编号
     * @return
     */
    public String[]  verifySql(String verify){
        if(verify.equals("")||verify.equals("null"))    return null;
        String[] strings = new String[2];
        Map<String, Object> one = sqlService.getOne("select  unit_id,unit_content from sys_com_unit where unit_type = 9  and  unit_code = '" + verify + "'");
        if(one!=null) {
            String unitContent = ObjectUtils.oGetS(one.get("unit_content"));
            if (unitContent.equals("")) {
                return null;
            } else {
                strings[0] = unitContent;
                List<String> verifyhint = sqlService.getListString("select  parameter_name  from  sys_com_parameter where  unit_id = " + one.get("unit_id"));
                if(verifyhint.size()>0){
                    strings[1] = verifyhint.get(0);
                }else {
                    strings[1] = "验证失败";
                }
            }
        }else {
            return null;
        }
        return strings;
    }

    /**
     * 列表保存修改
     *
     * @param datass    数据
     * @param regionId  域id
     * @param unitCodes 单元名
     * @param listkey   关联主表字段名
     * @param listpid   关联主表值
     * @return
     */
    @Override
    public R normSaveList(List<List<Map<String, Object>>> datass, Long regionId, String unitCodes, String listkey, String listpid,String listverify) {
        String[] split = unitCodes.split(",");
        UserEntity userEntity = ShiroUtils.getUserEntity();//用户
        boolean listp = listkey.equals("") ? false : true;
        String[] split2 = listp ? listkey.split(",") : null;
        String[] verifys = listverify.split(",");
        boolean verifyb = verifys[0].equals("");
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (int y = 0; y < split.length; y++) {
                String unitCode = split[y];
                String[] strings = verifyb?null:verifySql(verifys[y]);//获取验证
                Map<String, Object> unitMap = getrIdUnit(regionId, unitCode);//获取数据
                if (unitMap == null) {
                    return R.error("未查询到对应单元数据");
                }
                String dataTable = ObjectUtils.oGetS(unitMap.get("data_table")); //表名
                String dataId = ObjectUtils.oGetS(unitMap.get("data_id"));   //数据源id
                String unitName = ObjectUtils.oGetS(unitMap.get("unit_name"));   //单元名字
                Long id = getrIdUnitId(dataId, dataTable);//获取主键
                if (id == 0) return R.error("未查询到对应数据域");
                StringBuilder sql2 = new StringBuilder();
                sql2.append("select parameter_key,parameter_keyup,parameter_keytype from sys_com_parameter where UNIT_ID = ").append(id)
                        .append(" and PARAMETER_KEYZU = 1 and PARAMETER_STATUS = 1");
                Map<String, Object> one1 = sqlService.getOne(sql2.toString());
                if (one1 == null) R.error("数据源未维护主键或未启用主键保存状态");
                String key = ObjectUtils.oGetS(one1.get("parameter_key"));
                StringBuilder sql3 = new StringBuilder();
                sql3.append("select parameter_key,parameter_keytype,parameter_keyup from sys_com_parameter where UNIT_ID = ")
                        .append(id).append(" and PARAMETER_KEYZU != 1 and PARAMETER_STATUS = 1");
                List<Map<String, Object>> all = sqlService.getAll(sql3.toString());
                List<Map<String, Object>> list = datass.get(y);
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> data = list.get(i);
                    if (listp) data.put(split2[y], listpid);//赋予父级id
                    if(strings != null ){
                        if(verifydata(data, strings[0])) return  R.error(100,unitName+"第"+(i+1)+"条数据："+strings[1]);
                    }
                    StringBuilder sqlsum = new StringBuilder();
                    String keydata = ObjectUtils.oGetS(data.get(key));
                    if (keydata.equals("")) {
                        getinsert(data, dataTable, userEntity, one1, key, keydata, all, sqlsum,null);
                        sqlService.insertOne(sqlsum.toString());
                    } else {
                        StringBuilder sql4 = new StringBuilder();
                        sql4.append(" from ").append(dataTable).append(" where ").append(key).append(" = ").append("'").append(keydata).append("'");
                        Map<String, Object> one2 = sqlService.getOne("select " + key + sql4.toString());
                        if (one2 == null) {
                            StringBuilder fields = new StringBuilder();
                            StringBuilder values = new StringBuilder();
                            fields.append(key).append(",");
                            if (ObjectUtils.oGetS(one1.get("parameter_key")).equals("NUMBER")) {
                                values.append(keydata).append(",");
                            } else {
                                values.append("'" + keydata).append("',");
                            }
                            insertValue(data, userEntity, all, fields, values);
                            sqlsum.append("insert into ").append(dataTable).append("(").append(fields.substring(0, fields.length() - 1)).append(")")
                                    .append(" values(").append(values.substring(0, values.length() - 1)).append(")");
                            sqlService.insertOne(sqlsum.toString());
                        } else {
                            StringBuilder set = new StringBuilder();
                            updateValue(data, userEntity, all, set, sql4);
                            sqlsum.append("update ").append(dataTable).append(" set ").append(set.substring(0, set.length() - 1)).
                                    append(" where ").append(key).append(" = ").append("'").append(keydata).append("'");
                            sqlService.updateOne(sqlsum.toString());
                        }
                    }
                }
            }
            sqlSession.commit();
        }catch (Exception e){
            return R.error(e.getMessage());
        }
        return R.ok();
    }


    /**
     * 通过域id和单元编号查询单元id
     *
     * @param regionId
     * @param unitCode
     * @return
     */
    public Map<String, Object> getrIdUnit(Long regionId, String unitCode) {
        String sqltable = "select unit_id,unit_type,data_id,data_table from sys_com_unit where  region_id = " + regionId + " and unit_code = '" + unitCode + "'";
        return sqlService.getOne(sqltable);
    }




    /**
     * 通过域id和单元编号查询单元id
     *
     * @param regionId
     * @param unitCode
     * @return
     */
    public Long getrIdUnitId(String regionId, String unitCode) {
        String sqltable = "select unit_id from sys_com_unit  where  region_id = " + regionId + " and unit_code = '" + unitCode + "'";
        String onetable = ObjectUtils.oGetS(sqlService.getOnef(sqltable));
        Long unitId = onetable.equals("") ? 0 : Long.valueOf(onetable);
        return unitId;
    }


    /**
     * 无主键insert语句组合（无主键新增数据）
     *
     * @param data       数据
     * @param data_table 表名
     * @param userEntity 用户     * @param one1       主键属性
     * @param key
     * @param keydata
     * @param all        非主键熟悉
     * @param sqlsum     sql
     * @return
     */
    private String getinsert(Map<String, Object> data, String data_table, UserEntity userEntity, Map<String, Object> one1, String key, String keydata, List<Map<String, Object>> all, StringBuilder sqlsum,Class entityClass) throws Exception {
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        if (ObjectUtils.oGetS(one1.get("parameter_keyup")).equals("1")) {
            fields.append(key).append(",");
            keydata = IdWorker.getIdStr();
            if (ObjectUtils.oGetS(one1.get("parameter_keytype")).equals("2")) {
                values.append(keydata);
            } else {
                values.append("'" + keydata + "',");
            }
        }

        if(entityClass!=null){
            Field[] entityFields = entityClass.getDeclaredFields();
            for(Field field:entityFields){
                if(field.isAnnotationPresent(SheetCode.class)){
                    String sheet_type_id = data.get("sheet_type_id").toString();
                    String sheet_code = mtlSheetTypesService.generateCodeById(sheet_type_id);
                    if(StringUtils.isEmpty(sheet_code)){
                        throw new Exception("单据号生成失败");
                    }
                    data.put(field.getName(),sheet_code);
                }
            }
        }
//        if(data.get("sheet_code")!=null&& StringUtils.isEmpty(data.get("sheet_code").toString())){
//            String sheet_type_id = data.get("sheet_type_id").toString();
//            String sheet_code = mtlSheetTypesService.generateCodeById(sheet_type_id);
//            if(StringUtils.isEmpty(sheet_code)){
//                throw new Exception("单据号生成失败");
//            }
//            data.put("sheet_code",sheet_code);
//        }

        insertValue(data, userEntity, all, fields, values);


        sqlsum.append("insert into ").append(data_table).append("(").append(fields.substring(0, fields.length() - 1)).append(")")
                .append(" values(").append(values.substring(0, values.length() - 1)).append(")");
        return keydata;
    }


    /**
     * 系统赋值判断
     *
     * @param data
     * @param userEntity 用户实体
     * @param all
     * @param fields
     * @param values
     */
    private void insertValue(Map<String, Object> data, UserEntity userEntity, List<Map<String, Object>> all, StringBuilder fields, StringBuilder values) {
        for (int i = 0; i < all.size(); i++) {
            Map<String, Object> map = all.get(i);
            String parameter_key = ObjectUtils.oGetS(map.get("parameter_key"));
            String parameter_keyup2 = ObjectUtils.oGetS(map.get("parameter_keyup"));
            if (parameter_keyup2.equals("2")) {
                fields.append(parameter_key).append(",");
                values.append(unitSqlService.sysdata() + ",");
                continue;
            } else if (parameter_keyup2.equals("4")) {
                fields.append(parameter_key).append(",");
                values.append("'" + userEntity.getUsername() + "',");
                continue;
            } else if (parameter_keyup2.equals("6")) {
                fields.append(parameter_key).append(",");
                values.append("'" + userEntity.getName() + "',");
                continue;
            }
            Object value = data.get(parameter_key);
            if (value == null) continue;
            String value2 = value.toString();
            fields.append(parameter_key).append(",");
            Integer parameterKeytype = Integer.valueOf(ObjectUtils.oGetS(map.get("parameter_keytype")));
            if (value2.equals("")) values.append("null,");
            else {
                if (parameterKeytype == 0)
                    values.append("'").append(value2).append("',");
                else if (parameterKeytype == 1)
                    if(value2.equals("null")) values.append("null,");
                    else values.append(unitSqlService.strTurnDate(value2) + ",");
                else if (parameterKeytype == 2) values.append(value2).append(",");
            }
        }
    }


    /**
     * 返回set的sql
     *
     * @param data       传入的数据
     * @param userEntity 用户实体
     * @param all
     * @param set
     */
    private R updateValue(Map<String, Object> data, UserEntity userEntity, List<Map<String, Object>> all, StringBuilder set, StringBuilder updatesql) {
        for (int i = 0; i < all.size(); i++) {
            Map<String, Object> map = all.get(i);
            String parameter_key = ObjectUtils.oGetS(map.get("parameter_key"));
            String parameter_keyup = ObjectUtils.oGetS(map.get("parameter_keyup"));
            if (parameter_keyup.equals("3")) {
                String update = ObjectUtils.oGetS(data.get(parameter_key));
                if (!update.equals("")) {
                    String update2 = ObjectUtils.oGetS(sqlService.getOnef("select " + parameter_key + updatesql));
                    DateUtil.parse(update);
                    int compare = DateUtil.compare(DateUtil.parse(update), DateUtil.parse(update2));
                    if (compare != 0) return R.error(510,"提交失败，数据已发生改变，已为你刷新数。");
                }
                set.append(parameter_key).append("=" + unitSqlService.sysdata() + ",");
                continue;
            } else if (parameter_keyup.equals("5")) {
                set.append(parameter_key).append("='" + userEntity.getUsername() + "',");
                continue;
            } else if (parameter_keyup.equals("7")) {
                set.append(parameter_key).append("='" + userEntity.getName() + "',");
                continue;
            }
            Object value = data.get(parameter_key);
            if (value == null) continue;
            String value2 = value.toString();
            Integer parameterKeytype = Integer.valueOf(ObjectUtils.oGetS(map.get("parameter_keytype")));
            if (value2.equals("")) set.append(parameter_key + " = null,");
            else if (parameterKeytype == 0) {
                set.append(parameter_key).append("=").append("'").append(value2).append("'").append(",");
            } else if (parameterKeytype == 1) {
                if(value2.equals("null")) set.append(parameter_key + " = null,");
                else {
                    value2 = dateSwitch(value2, "yyyy/MM/dd HH:mm:ss", 1);
                    unitSqlService.getDateSql(set, parameter_key, value2);
                    set.append(",");
                }
            } else {
                set.append(parameter_key).append("=").append(value2).append(",");
            }
        }
        return R.ok();
    }



    /**
     * 时间转换
     *
     * @param dates
     * @param datetype 转换类型  YYYY/MM/SS HH:mm:ss
     * @return
     */
    public String dateSwitch(String dates, String datetype, int type) {
        DateFormat format1 = new SimpleDateFormat(datetype);
        String[] split = dates.split(" - ");
        if (split.length == 1) {
            if (type == 1) dates = dates.replace('-', '/');
            int i = dates.indexOf(".0");
            if (i != -1) {
                dates = dates.substring(0, i);
            }
            try {
                Date parse = format1.parse(dates);
                dates = format1.format(parse);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return dates;
    }

    @Override
    @Transactional
    public R normDelete(Long unitId, List<Map<String, Object>> listdatas, String childtable, String childfield) {
        try {
            String sql = "select data_table,data_id from sys_com_unit where  unit_id = " + unitId;
            Map<String, Object> one = sqlService.getOne(sql);
            if (one == null) return R.error("未启用原数据");
            String data_table = ObjectUtils.oGetS(one.get("data_table"));
            String data_id = ObjectUtils.oGetS(one.get("data_id"));
            Map<String, Object> one1 = getTableKey(data_table, data_id);
            if (one1 == null) R.error("数据源未维护主键或未启用主键保存状态");
            String parameter_keyup = ObjectUtils.oGetS(one1.get("parameter_key"));
            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                if (childtable.equals("") || childtable.equals("")) {
                    for (Map<String, Object> map : listdatas) {
                        Object id = map.get(parameter_keyup);
                        if (id != null) {
                            String sqlinsert = "delete from  " + data_table + "  where " + parameter_keyup + " = '" + id + "'";
                            sqlService.deleteOne(sqlinsert);
                        }
                    }
                } else {
                    for (Map<String, Object> map : listdatas) {
                        Object id = map.get(parameter_keyup);
                        if (id != null) {
                            String sqlinsert = "delete from  " + data_table + "  where " + parameter_keyup + " = '" + id + "'";
                            sqlService.deleteOne(sqlinsert);
                            String sqlinsert2 = "delete from  " + childtable + "  where " + childfield + " = '" + id + "'";
                            sqlService.deleteOne(sqlinsert2);
                        }
                    }
                }
                sqlSession.commit();
            }
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return R.error(e.getCause().toString());
        }
    }

    public Map<String, Object> getTableKey(String date_table, String region_id) {
        String sqltable = "select unit_id from sys_com_unit  where unit_type = 4  and region_id = " + region_id + " and unit_code = '" + date_table + "'";
        Map<String, Object> onetable = sqlService.getOne(sqltable);
        if (onetable == null) return null;
        String id = ObjectUtils.oGetS(onetable.get("unit_id"));
        StringBuilder sql2 = new StringBuilder();
        sql2.append("select parameter_key,parameter_keyup from sys_com_parameter where UNIT_ID = ").append(id).append(" and PARAMETER_KEYZU = 1 and PARAMETER_STATUS = 1");
        Map<String, Object> one1 = sqlService.getOne(sql2.toString());
        if (one1 == null) return new HashMap<String, Object>();
        return one1;
    }
}
