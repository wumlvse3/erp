
package com.jcloud.erp.pur.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.pur.entity.PurApplyHead;
import com.jcloud.system.utils.R;

import java.util.List;

public interface PurApplyHeadService extends IService<PurApplyHead> {

    public R addObj(List<PurApplyHead> params);

    public R auditlist(List<PurApplyHead> params);

    public R passlist(List<PurApplyHead> params);

    public R auditdetaillist(List<PurApplyHead> params);

    public R passdetaillist(List<PurApplyHead> params);

    public R detaillist(List<PurApplyHead> params);

    public R unaddObj(List<PurApplyHead> params);

    public R bhpasslist(List<PurApplyHead> params);

    public R enable(List<PurApplyHead> params);

    public  R disabl(List<PurApplyHead> params);

    public R detailenable(List<PurApplyHead> params);

    public  R detaildisabl(List<PurApplyHead> params);
}
