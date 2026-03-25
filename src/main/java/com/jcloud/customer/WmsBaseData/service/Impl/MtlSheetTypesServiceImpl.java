package com.jcloud.customer.WmsBaseData.service.Impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.Mtl_sheet_types;
import com.jcloud.customer.WmsBaseData.mapper.Mtl_sheet_typesMapper;
import com.jcloud.customer.WmsBaseData.service.MtlSheetTypesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Copyright 2016-2020 湖南湘北智造信息技术有限责任公司
 * 创 建: 超级管理员
 * 日 期: 2020-08-26
 * 描 述: 单据类型, 业务类
 *
 */
@Service
public class MtlSheetTypesServiceImpl extends ServiceImpl<Mtl_sheet_typesMapper, Mtl_sheet_types>
		implements MtlSheetTypesService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	/**
	 * @author 云之佳-开发部
	 * @date：2020-08-26
	 * @description：根据父id查询主实体
	 * @param idValue	id值
	 * @return
	 */
	public Mtl_sheet_types findById(String idValue) {
		return baseMapper.selectById(idValue);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String generateCodeById(String sheet_type_id){
		try{
			String code="";
			Integer currentSerial=0;
			Mtl_sheet_types todayExistData = baseMapper.getToDaysData(sheet_type_id);
			if(todayExistData==null){
				todayExistData = baseMapper.getById(sheet_type_id);
				currentSerial=1;

			}else{
				currentSerial =todayExistData.getCurrent_serial()+1;
			}

			String startNo= todayExistData.getStart_no()+currentSerial;
			startNo = startNo.substring(startNo.length()-todayExistData.getStart_no().length(),startNo.length());
			code = todayExistData.getPre_code()+todayExistData.getDate_str()+startNo;

			todayExistData.setCurrent_serial(currentSerial);
			baseMapper.updateById(todayExistData);
			return code;
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "";
		}
	}
	
}