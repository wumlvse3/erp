package com.jcloud.customer.WmsBaseData.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.MtlProjectLine;
import com.jcloud.customer.WmsBaseData.entity.MtlTransactions;
import com.jcloud.customer.WmsBaseData.mapper.MtlProjectLineMapper;
import com.jcloud.customer.WmsBaseData.mapper.MtlTransactionsMapper;
import com.jcloud.customer.WmsBaseData.service.MtlProjectLineService;
import com.jcloud.customer.WmsBaseData.service.MtlTransactionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 *
 * 创 建: 超级管理员
 * 日 期: 2021-01-08
 * 描 述: 期间交易汇总, 业务类
 *
 */
@Service
public class MtlProjectLineServiceImpl extends ServiceImpl<MtlProjectLineMapper, MtlProjectLine>
		implements MtlProjectLineService {

}