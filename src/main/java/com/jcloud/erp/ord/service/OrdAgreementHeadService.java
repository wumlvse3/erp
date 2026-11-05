package com.jcloud.erp.ord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.ord.entity.OrdAgreementHead;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface OrdAgreementHeadService extends IService<OrdAgreementHead> {

    R updateSubmit(List<List<Map<String, Object>>> list);

    R active(List<List<Map<String, Object>>> list,String isActive);

    R activeDetail(List<List<Map<String, Object>>> list,String isActive);
}