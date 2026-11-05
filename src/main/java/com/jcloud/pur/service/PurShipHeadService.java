
package com.jcloud.pur.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.pur.entity.PurShipHead;
import com.jcloud.system.utils.R;

import java.util.List;

public interface PurShipHeadService extends IService<PurShipHead> {

    public R addObj(List<PurShipHead> params);

    public R passlist(List<PurShipHead> params);
    public R detaillist(List<PurShipHead> params);

    public R draddObj(List<PurShipHead> params);

    public R drpasslist(List<PurShipHead> params);

    public R enable(List<PurShipHead> params);

    public R disabl(List<PurShipHead> params);

    public R detailenable(List<PurShipHead> params);

    public R detaildisabl(List<PurShipHead> params);
}
