
package com.jcloud.erp.pur.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.pur.entity.PurOrderHead;
import com.jcloud.system.utils.R;

import java.util.List;

public interface PurOrderHeadService extends IService<PurOrderHead> {

    public R addObj(List<PurOrderHead> params);

    public R auditlist(List<PurOrderHead> params);
    public R unaddObj(List<PurOrderHead> params);

    public R passlist(List<PurOrderHead> params);

    public R auditdetaillist(List<PurOrderHead> params);

    public R passdetaillist(List<PurOrderHead> params);
    public R detaillist(List<PurOrderHead> params);

    public R createHead(List<PurOrderHead> params);

    public R createDetail(List<PurOrderHead> params);

    public R createDrHead(List<PurOrderHead> params);

    public R createDrDetail(List<PurOrderHead> params);

    public R bhpasslist(List<PurOrderHead> params);

    public R zjDrDetail(List<PurOrderHead> params);

    public R zjThDetail(List<PurOrderHead> params);

    public R enable(List<PurOrderHead> params);

    public R disabl(List<PurOrderHead> params);

    public R detailenable(List<PurOrderHead> params);

    public R detaildisabl(List<PurOrderHead> params);
}
