package com.jcloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.system.entity.SysFile;
import com.jcloud.system.utils.R;

/**
 * (SysFile)表服务接口
 *
 * @author Bianxs
 * @since 2024-02-19 10:36:36
 */
public interface SysFileService extends IService<SysFile> {
    public R generalFileRecord(String path);
}

