package com.jcloud.customer.WmsBaseData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.customer.WmsBaseData.entity.Mtl_sheet_types;

import java.util.List;
import java.util.Map;

/**
 *
 * Copyright 2016-2020 湖南湘北智造信息技术有限责任公司
 * 创 建: 超级管理员
 * 日 期: 2020-08-26
 * 描 述: 单据类型, 业务接口
 *
 */
public interface MtlSheetTypesService extends IService<Mtl_sheet_types> {
	public Mtl_sheet_types findById(String idValue);

	public String generateCodeById(String sheet_type_id);
}