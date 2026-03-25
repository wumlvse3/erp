package com.jcloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.system.dao.ComUnitDao;
import com.jcloud.system.event.ComUnitEntity;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.service.UnitService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.PageUtils;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 单元逻辑
 *
 * @Auther: yang
 * @Date: 2023/04/06/13:59
 * @Description:
 */
@Service
public class ComUnitServiceImpl extends ServiceImpl<ComUnitDao, ComUnitEntity> implements ComUnitService {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private UnitService unitService;

//    @Autowired
//    private UnitSqlService unitSqlService;

//。。。。。。。。。。。。。。。。。。。。。。。数据操作。。。。。。。。。。。。。。。。。。。。。。。。。



    /**
     * 标准保存
     *
     * @param regionId  域id
     * @param unitCode  表单编号
     * @param listCode  列表编号
     * @param unitdatas 表单数据
     * @param listdatas 列表数据
     * @return
     */
//    @Override
//    public R normsave(Long regionId, String unitCode, String listCode, String unitdatas, String listdatas, String listkey, String unitverify, String listverify) {
//        try {
//            R ok = R.ok();
//            if (!unitCode.equals("")) {
//                Map<String, Object> map = JSONUtil.toBean(unitdatas, Map.class);
//                ok = unitService.normSave(map, regionId, unitCode,unitverify);
//                if (!ObjectUtils.oGetS(ok.get("code")).equals("0")) {
//                    return ok;
//                }
//            }
//            if (!listCode.equals("")) {
//                List<List<Map<String, Object>>> list = JSONUtil.parseArray(listdatas).toBean(List.class);
//                String keyid = ObjectUtils.oGetS(ok.get("keyid"));
//                R ok2 = unitService.normSaveList(list, regionId, listCode, listkey, keyid,listverify);
//                if (!ObjectUtils.oGetS(ok2.get("code")).equals("0")) {
//                    return ok2;
//                }
//            }
//            return ok;
//        } catch (Exception e) {
//TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            e.printStackTrace();
//            return R.error(ObjectUtils.oGetS(e.getCause().toString()));
//        }
//    }

    @Override
    public R normsave(Long regionId, String unitCode, String listCode, String unitdatas, String listdatas, String listkey, String unitverify, String listverify,Class entityClass) {
        try {
            R ok = R.ok();
            if (!unitCode.equals("")) {
                Map<String, Object> map = JSONUtil.toBean(unitdatas, Map.class);
                ok = unitService.normSave(map, regionId, unitCode,unitverify,entityClass);
                if (!ObjectUtils.oGetS(ok.get("code")).equals("0")) {
                    return ok;
                }
            }
            if (!listCode.equals("")) {
                List<List<Map<String, Object>>> list = JSONUtil.parseArray(listdatas).toBean(List.class);
                String keyid = ObjectUtils.oGetS(ok.get("keyid"));
                R ok2 = unitService.normSaveList(list, regionId, listCode, listkey, keyid,listverify);
                if (!ObjectUtils.oGetS(ok2.get("code")).equals("0")) {
                    return ok2;
                }
            }
            return ok;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return R.error(ObjectUtils.oGetS(e.getMessage()));
        }
    }

    public R SaveData(Map<String,Object> params){
        Long regionId = Long.valueOf(params.get("regionId").toString());
        String unitCode = ObjectUtils.oGetS(params.get("unitCode"));
        String listCode = ObjectUtils.oGetS(params.get("listCode"));
        String unitdatas = ObjectUtils.oGetS(params.get("unitdatas")); //表单数据
        String listdatas = ObjectUtils.oGetS(params.get("listdatas"));//列表数据
        String listkey = ObjectUtils.oGetS(params.get("listkey"));
        String unitverify = ObjectUtils.oGetS(params.get("unitverify")); //表单验证
        String listverify = ObjectUtils.oGetS(params.get("listverify")); //列表数据
        return normsave(regionId, unitCode, listCode, unitdatas, listdatas, listkey,unitverify,listverify,null);
    }


    public R SaveData(Map<String,Object> params,Class entityClass){
        Long regionId = Long.valueOf(params.get("regionId").toString());
        String unitCode = ObjectUtils.oGetS(params.get("unitCode"));
        String listCode = ObjectUtils.oGetS(params.get("listCode"));
        String unitdatas = ObjectUtils.oGetS(params.get("unitdatas")); //表单数据
        String listdatas = ObjectUtils.oGetS(params.get("listdatas"));//列表数据
        String listkey = ObjectUtils.oGetS(params.get("listkey"));
        String unitverify = ObjectUtils.oGetS(params.get("unitverify")); //表单验证
        String listverify = ObjectUtils.oGetS(params.get("listverify")); //列表数据
        return normsave(regionId, unitCode, listCode, unitdatas, listdatas, listkey,unitverify,listverify,entityClass);
    }

    public Boolean checkMulti(String table,String column){
        String sql = "select count(*),"+column+" from "+table+" group by "+column+" having count(*)>1";
        int count = sqlService.getCount(sql).intValue();
        if(count>0){
            return false;
        }

        return true;
    }


    public R normDelete(Map<String, Object> params) {
        Long unitId = Long.valueOf(params.get("unitId").toString());
        List<Map<String, Object>> listdatas = (List<Map<String, Object>>) params.get("listdatas");
        String childtable = ObjectUtils.oGetS(params.get("childtable"));//关联删除表
        String childfield = ObjectUtils.oGetS(params.get("childfield"));//管理删除字段
        return normDelete(unitId, listdatas, childtable, childfield);
    }

    public R normDelete(Long unitId, List<Map<String, Object>> listdatas, String childtable, String childfield) {
        return unitService.normDelete(unitId, listdatas, childtable, childfield);
    }

    public void passMySheetTask(String id){
        sqlService.passMySheetTaskInfo(id,ShiroUtils.getUserEntity().getUsername(),ShiroUtils.getUserEntity().getUserId().toString());
    }

    /**
     * 解析单元sql
     *
     * @param unitSql   解析sql
     * @param mapP      请求参数
     * @param isListSql 是否为list sql
     * @return
     */
    @Override
    public String getUnitSql(String unitSql, Map<String, Object> mapP, Boolean isListSql) {
        if (unitSql == null) return "";
        if (mapP == null) mapP = new HashMap<String, Object>();
        String[] split = unitSql.split("\\$SQL\\[");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = getMaps1(split[0], 1);
        list.add(map);
        for (int i = 1; i < split.length; i++) {
            String[] split2 = split[i].split("\\]");
            String s1 = split2[0];
            Map<String, Object> maps1 = getMaps1(s1, 2);
            list.add(maps1);
            if (split2.length > 1) {
                String s = split2[1];
                Map<String, Object> maps2 = getMaps1(s, 1);
                list.add(maps2);
            }
        }
        StringBuilder sqls = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            ArrayList<String> sql = (ArrayList<String>) list.get(i).get("sql");
            ArrayList<String> datas = (ArrayList<String>) list.get(i).get("datas");
            String type = ObjectUtils.oGetS(list.get(i).get("type"));
            int size = datas.size();
            if (type.equals("1")) {//sql赋值判断
                for (int j = 0; j < sql.size(); j++) {
                    String s = sql.get(j);
                    sqls.append(s);
                    if (size > j) {
                        String s1 = datas.get(j);
                        Object s11 = mapP.get(s1);
                        if (s11 != null) {
                            sqls.append(s11);
                            if (isListSql) mapP.remove(s1);
                        } else sqls.append("''");
                    }
                }
            } else if (type.equals("2")) { //$sql[]中语句赋值
                boolean exist = false;
                int size1 = sql.size();
                for (int j = 0; j < size1; j++) {
                    if (size > j) {
                        String s1 = datas.get(j);
                        Object s11 = mapP.get(s1);
                        if (ObjectUtils.oGetS(s11).equals("")) {
                            break;
                        } else {
                            exist = true;
                            String s = sql.get(j);
                            sqls.append(s);
                            sqls.append(s11);
                            if (isListSql) mapP.remove(s1);
                        }
                    }
                }
                if (exist && size1 > size) sqls.append(sql.get(size1 - 1));
            }
        }
        return sqls.toString();
    }

    /**
     * 解析sql特殊符号
     *
     * @param s
     * @param type 状态 （1=正常sql，2=sql变量sql）
     * @return
     */
    public Map<String, Object> getMaps1(String s, int type) {
        StringBuilder zhi = new StringBuilder();
        String[] splitS1 = s.split("\\$S\\{");
        zhi.append(splitS1[0]);
        for (int i = 1; i < splitS1.length; i++) {
            String s1 = splitS1[i];
            int i1 = s1.indexOf("}");
            if (i1 != -1) {
                String substring = s1.substring(0, i1);
                Session session = ShiroUtils.getSession();
                zhi.append(ObjectUtils.oGetS(session.getAttribute(substring)) + s1.substring(i1 + 1));
            }
        }
        String[] splitUS1 = zhi.toString().split("\\$US\\{");//获取用户数据
        if (splitUS1.length > 1) {
            zhi = new StringBuilder();
            zhi.append(splitUS1[0]);
        }
        Map<String, Object> userMap = BeanUtil.beanToMap(ShiroUtils.getUserEntity());
        for (int i = 1; i < splitUS1.length; i++) {
            String s1 = splitUS1[i];
            int i1 = s1.indexOf("}");
            if (i1 != -1) {
                String substring = s1.substring(0, i1);
                zhi.append(ObjectUtils.oGetS(userMap.get(substring))).append(s1.substring(i1 + 1));
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String[] split1 = zhi.toString().split("\\$R\\{");
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        list1.add(split1[0]);
        for (int i = 1; i < split1.length; i++) {
            String s1 = split1[i];
            int i1 = s1.indexOf("}");
            if (i1 != -1) {
                String substring = s1.substring(0, i1);
                list2.add(substring);
                list1.add(s1.substring(i1 + 1));
            }
        }
        map.put("sql", list1);
        map.put("type", type);
        map.put("datas", list2);
        return map;
    }


    /**
     * 获取单元id
     *
     * @param region 域
     * @param unit   单元编号
     * @return
     */
    @Override
    public ComUnitEntity getUnitEntity(String region, String unit) {
        String sql = "select region_id from sys_com_region where  region_code = '" + region + "'";
        String region_id = ObjectUtils.oGetS(sqlService.getOnef(sql));
        return getrIdUnitId(region_id, unit);
    }

    @Override
    public void passAllSheetTask(String id) {
        sqlService.passAllSheetTaskInfo(id);
    }

    /**
     * 获取单元
     *
     * @param regionId 域
     * @param unitCode 单元编号
     * @return
     */
    public ComUnitEntity getrIdUnitId(String regionId, String unitCode) {
        QueryWrapper<ComUnitEntity> comUnitEntityQueryWrapper = new QueryWrapper<>();
        comUnitEntityQueryWrapper.eq("region_id", regionId);
        comUnitEntityQueryWrapper.eq("unit_code", unitCode);
        ComUnitEntity comUnitEntity = getOne(comUnitEntityQueryWrapper);
        return comUnitEntity;
    }



}
