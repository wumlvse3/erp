package com.jcloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.system.event.ComUnitEntity;
import com.jcloud.system.utils.R;

import java.util.Map;

/**
 * @Auther: yang
 * @Date: 2023/04/06/13:59
 * @Description:
 */
public interface ComUnitService extends IService<ComUnitEntity> {

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
    R normsave(Long regionId, String unitCode, String listCode, String unitdatas, String listdatas, String listkey, String unitverify, String listverify,Class entityClass);

    public R SaveData(Map<String,Object> params);

    public R SaveData(Map<String,Object> params,Class entityClass);

    public Boolean checkMulti(String table,String column);

    public void passMySheetTask(String id);

    //  。。。。。。。。。。。。。。。。。执行方法。。。。。。。。。。。。。。。。。

    /**
     * 解析单元sql
     *
     * @param unitSql 解析sql
     * @param mapP    请求参数
     * @return 解析的sql
     */
    String getUnitSql(String unitSql, Map<String, Object> mapP,Boolean listsql);


    /**
     * 获取单元id
     *
     * @param region 域
     * @param unit   单元编号
     * @return
     */
    ComUnitEntity getUnitEntity(String region, String unit);

    void passAllSheetTask(String id);

    R normDelete(Map<String, Object> params);
}
