
package com.jcloud.pur.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.pur.entity.PurPlanHead;
import com.jcloud.system.utils.R;

import java.util.List;

public interface PurchasePlanHeadService extends IService<PurPlanHead> {

    public R addObj(List<PurPlanHead> params);

    public R auditlist(List<PurPlanHead> params);

    public R passlist(List<PurPlanHead> params);

    public R auditdetaillist(List<PurPlanHead> params);

    public R passdetaillist(List<PurPlanHead> params);

    public R unaddObj(List<PurPlanHead> params);

    public R detaillist(List<PurPlanHead> params);

    public R orderCreate(List<PurPlanHead> params);

    public R detailCreate(List<PurPlanHead> params);

    public  R planCreate();

    public  R bhpasslist(List<PurPlanHead> params);

    public R zjdetailCreate(List<PurPlanHead> params);

    public R enable(List<PurPlanHead> params);

    public R disabl(List<PurPlanHead> params);

    public R detailenable(List<PurPlanHead> params);

    public R detaildisabl(List<PurPlanHead> params);
}
