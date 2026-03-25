package com.jcloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.system.entity.ShareInfo;
import com.jcloud.system.utils.R;

import java.util.List;

/**
 * (ShareInfo)表服务接口
 *
 * @author Bianxs
 * @since 2023-09-12 14:03:01
 */
public interface ShareInfoService extends IService<ShareInfo> {

    /**
    * 获取外部展示的公共下载
    *
     * @param null
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2023/9/12 14:06
    */
    List<ShareInfo> getPublicShareInfos();
}

