package com.jcloud.customer.WmsBaseData.vo;

import lombok.Data;

import java.util.List;

/**
 * 品类多级对象
 *
 * @ClassName: ItemsCategoryVo
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2023-06-14 19:35
 **/
@Data
public class MultiLayerVo {
    /**
     * key
     * **/
    private String id;
    /**
     * parentKey
     * **/
    private String parentId;

    /**
     * 值
     * **/
    private String value;
    /**
     * 名称
     * **/
    private String label;
    /**
     *
     */
    private String code;
    /**
     * 子对象
     * **/
    private List<MultiLayerVo> children;
    /**
     * 父级代码
     * **/
    private String parentCode;
    /**
     * 父级名称
     * **/
    private String parentName;


    @Override
    public String toString() {
        return "ItemsCategoryVo{" +
                "value='" + value + '\'' +
                '}';
    }
}
