package com.jcloud.customer.WmsBaseData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.customer.WmsBaseData.entity.ItemsCategory;
import com.jcloud.customer.WmsBaseData.vo.MultiLayerVo;
import com.jcloud.system.utils.R;

import java.util.List;

/**
 *
 *
 * 创 建: 超级管理员
 * 日 期: 2021-01-07
 * 描 述: 物料大小类, 业务接口
 *
 */
public interface ItemsCategoryService extends IService<ItemsCategory> {
    /**
    * 获取第一层
    *
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2023/6/14 19:52
    */
    public List<ItemsCategory> getFistLevelData();

    /**
     * @Description:获取所有层级
     * @Param a
     * @retur: a
     * @Author: bianxs
     * @Date: 2023/6/14
     */
    public List<MultiLayerVo> getAllLevelData();

    /**
     * @Description: 获取下一层
     * @Param:
     * @return:
     * @Author: bianxs
     * @Date: 2023/6/14
     */
    public List<ItemsCategory> getNextLevelData(String id);


    R addObj(ItemsCategory items_category);

    R updateObj(ItemsCategory items_category);

    R getChildList(String parent_id, String category_code, String category_name);

    R findOne(String id);

    R deleteById(String id);


    ItemsCategory getValidObj(String code);
}