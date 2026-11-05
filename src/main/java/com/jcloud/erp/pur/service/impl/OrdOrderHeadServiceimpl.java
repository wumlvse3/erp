package com.jcloud.erp.pur.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.pur.entity.OrdOrderHead;
import com.jcloud.erp.pur.mapper.PurOrdOrderHeadMapper;
import com.jcloud.erp.pur.service.OrdOrderHeadService;
import com.jcloud.erp.pur.service.TransactionalService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrdOrderHeadServiceimpl extends ServiceImpl<PurOrdOrderHeadMapper, OrdOrderHead> implements OrdOrderHeadService {
    @Resource
    private SqlService sqlService;

    @Autowired
    private TransactionalService transactionalService;

    @Override
    public R handleCreate(List<OrdOrderHead> params) {
        try {
            transactionalService.dopurApply(params);
            return R.ok("自动生成成功");
        } catch (Exception e) {
            return R.error("自动生成失败！ " );
        }
    }

    @Override
    public R detailCreate(List<OrdOrderHead> params) {
        try {
            transactionalService.dodelpurApply(params);
            return R.ok("自动生成成功");
        } catch (Exception e) {
            return R.error("自动生成失败！ " );
        }
    }
}
