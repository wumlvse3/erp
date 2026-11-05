
package com.jcloud.erp.pur.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.pur.entity.PurAgreementHead;
import com.jcloud.system.utils.R;

import java.util.List;

public interface PurAgreementHeadService extends IService<PurAgreementHead> {

    public R addObj(List<PurAgreementHead> params);

    public R unaddObj(List<PurAgreementHead> params);
    public R detaillist(List<PurAgreementHead> params);

    public R orderCreate(List<PurAgreementHead> params);

    public R detailCreate(List<PurAgreementHead> params);

    public R zjdetailCreate(List<PurAgreementHead> params);

    public R enable(List<PurAgreementHead> params);

    public R disabl(List<PurAgreementHead> params);

    public R detailenable(List<PurAgreementHead> params);

    public R detaildisabl(List<PurAgreementHead> params);
}
