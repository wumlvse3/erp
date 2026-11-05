
package com.jcloud.pur.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.pur.entity.OrdOrderHead;
import com.jcloud.system.utils.R;

import java.util.List;

public interface OrdOrderHeadService extends IService<OrdOrderHead> {


    public R handleCreate(List<OrdOrderHead> params);

    public R detailCreate(List<OrdOrderHead> params);
}
