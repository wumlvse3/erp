package com.jcloud.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.epd.entity.WoInfo;
import com.jcloud.system.utils.R;

/**
 * 工作令优先级 Service
 */
public interface WoInfoService extends IService<WoInfo> {

    /**
     * 新增工作令，并重算所有参与排序工作令的优先级序号
     */
    R addWo(WoInfo woInfo);

    /**
     * 修改工作令，并重算所有参与排序工作令的优先级序号
     */
    R updateWo(WoInfo woInfo);

    /**
     * 查询公司下所有工作令（按优先级序号排序）
     */
    R queryList(String companyCode);
}
