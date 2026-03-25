package com.jcloud.system.service;

import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

/**
 * 平台标准方法
 * @Auther: yang
 * @Date: 2023/04/23/14:24
 * @Description:
 */
public interface UnitService {

    /**
     * 标准保存
     * @param data 传入数据
     * @return
     */

    R normSave(Map<String, Object> data, Long regionId, String unitCode,String unitverify,Class entityClass) throws Exception;


    /**
     * 标准保存(列表)
     * @param datas 数据
     * @param regionId 域id
     * @param unitCode 单元名
     * @param listkey 关联主表字段名
     * @param listpid 关联主表值
     * @return
     */
    R normSaveList( List<List<Map<String, Object>>> datas, Long regionId, String unitCode, String listkey, String listpid,String listverify);


    R normDelete(Long unitId, List<Map<String, Object>> listdatas, String childtable, String childfield);
}
