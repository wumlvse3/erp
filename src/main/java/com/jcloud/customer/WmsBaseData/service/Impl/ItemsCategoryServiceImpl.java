package com.jcloud.customer.WmsBaseData.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.ImportData.ImportBeanValidUtils;
import com.jcloud.customer.WmsBaseData.entity.ItemsCategory;
import com.jcloud.customer.WmsBaseData.mapper.ItemsCategoryMapper;
import com.jcloud.customer.WmsBaseData.mapper.SysUserMapper;
import com.jcloud.customer.WmsBaseData.mapper.Sys_orgMapper;
import com.jcloud.customer.WmsBaseData.service.ItemsCategoryService;
import com.jcloud.customer.WmsBaseData.vo.MultiLayerVo;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.MessageUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * Copyright 2016-2020 湖南湘北智造信息技术有限责任公司
 * 创 建: 超级管理员
 * 日 期: 2020-08-24
 * 描 述: 物料信息, 业务类
 *
 */
@Service
public class ItemsCategoryServiceImpl extends ServiceImpl<ItemsCategoryMapper, ItemsCategory> implements ItemsCategoryService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SqlService sqlService;
	@Resource
	ImportBeanValidUtils importBeanValidUtils;
    @Autowired
    private SysUserMapper sysUserMapper;
	@Resource
	private DataSourceTransactionManager transactionManager;
    @Autowired
    private Sys_orgMapper sys_orgMapper;

	@Override
	public List<ItemsCategory> getFistLevelData() {
		QueryWrapper<ItemsCategory> wrapper =new QueryWrapper<>();
		wrapper.eq("parent_id","");
//		wrapper.or().isNull("parent_id");

		wrapper.eq("status",1);
		wrapper.eq("category_level",0);
		wrapper.orderByAsc("category_code");
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<ItemsCategory> getNextLevelData(String id) {
		QueryWrapper<ItemsCategory> wrapper =new QueryWrapper<>();
		wrapper.eq("parent_id",id);
		wrapper.eq("status",1);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<MultiLayerVo> getAllLevelData() {
		List<ItemsCategory> allData = super.list();
		List<ItemsCategory> firstLevelData = allData.stream().filter(b->StringUtils.isEmpty(b.getParent_code())&&b.getStatus().equalsIgnoreCase("1")).collect(Collectors.toList());
		List<MultiLayerVo> voData = new ArrayList<>();
		if(firstLevelData!=null && !firstLevelData.isEmpty()){

			firstLevelData.forEach(t->{
				MultiLayerVo bean = new MultiLayerVo();
				bean.setLabel(t.getCategory_name());
				bean.setCode(t.getCategory_code());
				bean.setValue(t.getId());
				bean.setId(t.getId());
				bean.setParentId(t.getParent_id());
				getChild(bean,allData);
				voData.add(bean);
			});

		}
		return voData;
	}
	private MultiLayerVo getChild(MultiLayerVo bean,List<ItemsCategory> allData){
		List<ItemsCategory> firstLevelData =allData.stream().filter(b->b.getParent_id().equalsIgnoreCase(bean.getId())&&b.getStatus().equalsIgnoreCase("1")).collect(Collectors.toList());
		List<MultiLayerVo> voData = new ArrayList<>();
		if(firstLevelData!=null && !firstLevelData.isEmpty()){
			for(ItemsCategory a:firstLevelData){
				MultiLayerVo tempBean = new MultiLayerVo();
				tempBean.setLabel(a.getCategory_name());
				tempBean.setCode(a.getCategory_code());
				tempBean.setValue(a.getId());
				tempBean.setId(a.getId());
				tempBean.setParentId(a.getParent_id());
				tempBean.setParentCode(bean.getCode());
				tempBean.setParentName(bean.getLabel());
				voData.add(tempBean);
				getChild(tempBean,allData);
			}

			bean.setChildren(voData);

			return bean;
		}else{
			bean.setChildren(Collections.emptyList());
			return bean;
		}
	}





	@Override
	@Transactional
	public R addObj(ItemsCategory items_category) {
		try{
			ItemsCategory old = baseMapper.selectOne(new QueryWrapper<ItemsCategory>().
					eq("category_code",items_category.getCategory_code()));
			if(old!=null){
				return R.error("品类重复");
			}
			if(StringUtils.isEmpty(items_category.getParent_id())){
				items_category.setCategory_level("0");
				items_category.setStatus("1");//默认为有效
				baseMapper.insert(items_category);
			}else {
				ItemsCategory parent = baseMapper.selectById(items_category.getParent_id());
				if(parent==null){
					throw new Exception(MessageUtils.get("wmsBaseData.itemCategory.e1","获取父级对象为空"));
				}
				if("0".equals(parent.getCategory_level())){
					items_category.setCategory_level("1");
				}else {
					int i = Integer.parseInt(parent.getCategory_level());
					i++;
					items_category.setCategory_level(String.valueOf(i));
				}
				items_category.setStatus("1");//默认为有效
				items_category.setParent_code(parent.getCategory_code());
				items_category.setParent_name(parent.getCategory_name());
				baseMapper.insert(items_category);
			}
			return R.ok();
		}catch (Exception e){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return R.error(e.getMessage());
		}


	}




	public void delItemsChild(Long id,Boolean flag){
		List<ItemsCategory> childItemsCategory = baseMapper.selectList(Wrappers.<ItemsCategory>query().lambda().eq(ItemsCategory::getParent_id, id));
		if (!childItemsCategory.isEmpty()){
			childItemsCategory.forEach(child -> {
				child.setStatus("0");
				baseMapper.updateById(child);
				delItemsChild(Long.valueOf(child.getId()),false);
			});
		}else {
			flag = true;
		}
		if (flag){
			return;
		}
	}

	@Override
	public R updateObj(ItemsCategory items_category) {
		baseMapper.updateById(items_category);
		return R.ok();
	}

	@Override
	public R getChildList(String parent_id, String category_code, String category_name) {
		QueryWrapper<ItemsCategory> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id",parent_id);
//		queryWrapper.or().isNull("parent_id");
		if(StringUtils.isNotEmpty(category_code)){
			queryWrapper.eq("category_code",category_code);
		}
		if(StringUtils.isNotEmpty(category_name)){
			queryWrapper.eq("category_name",category_name);
		}
		List<ItemsCategory> items_categories = baseMapper.selectList(queryWrapper);
		return R.ok().setdata(items_categories);
	}

	@Override
	public R findOne(String id) {
		ItemsCategory items_category = baseMapper.selectById(id);
		return R.ok().setdata(items_category);
	}

	@Override
	public R deleteById(String id) {
		List<ItemsCategory> dataList = getNextLevelData(id);
		if (dataList.size()!=0){
			return R.error("不能删除还有下级品类的物料品类");
		}
		List<String> ids =dataList.stream().map(ItemsCategory::getId).collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(ids)){
			int nDelete = baseMapper.deleteBatchIds(ids);
		}

		baseMapper.deleteById(id);
		return R.ok();
	}

	@Override
	public ItemsCategory getValidObj(String code) {
		return baseMapper.selectOne(Wrappers.<ItemsCategory>query().lambda().eq(ItemsCategory::getCategory_code,code).eq(ItemsCategory::getDeleted,"0"));
	}
}