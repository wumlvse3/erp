package com.jcloud.erp.ord.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.ord.entity.OrdOrderDetail;
import com.jcloud.erp.ord.mapper.OrdOrderDetailMapper;
import com.jcloud.erp.ord.service.OrdOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单明细信息, 业务�? */
@Service
public class OrdOrderDetailServiceImpl extends ServiceImpl<OrdOrderDetailMapper, OrdOrderDetail> implements OrdOrderDetailService {
}