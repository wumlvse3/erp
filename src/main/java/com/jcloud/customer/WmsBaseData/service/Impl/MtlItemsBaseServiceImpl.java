package com.jcloud.customer.WmsBaseData.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsBase;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsOrg;
import com.jcloud.customer.WmsBaseData.entity.SysOrg;
import com.jcloud.customer.WmsBaseData.mapper.MtlItemsBaseMapper;
import com.jcloud.customer.WmsBaseData.mapper.MtlItemsOrgMapper;
import com.jcloud.customer.WmsBaseData.mapper.Sys_orgMapper;
import com.jcloud.customer.WmsBaseData.service.MtlItemsBaseService;
import com.jcloud.customer.WmsBaseData.service.MtlItemsOrgService;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.utils.MessageUtils;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物料信息, 业务类
 *
 */
@Service
public class MtlItemsBaseServiceImpl extends ServiceImpl<MtlItemsBaseMapper, MtlItemsBase> implements MtlItemsBaseService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SAVE_BATCH_SIZE = 500;

	@Autowired
	private MtlItemsBaseMapper mtl_itemsMapper;
	@Autowired
	private MtlItemsOrgMapper mtlItemsOrgMapper;
	@Autowired
	private Sys_orgMapper sysOrgMapper;
	@Autowired
	private ComUnitService comUnitService;
	@Resource
	private MtlItemsOrgService mtlItemsOrgService;

	public MtlItemsBaseServiceImpl(){}

	@Transactional
	public R addAllOrgItems(List<List<Map<String, Object>>> list) {
		try{
			List<Map<String, Object>> orgData = list.get(0);
			if(CollectionUtils.isEmpty(orgData)){
				return R.error("分配组织不允许为空");
			}
			List<MtlItemsBase> allItemsList = super.list();

			for(Map<String,Object> orgMap:orgData){
				List<MtlItemsOrg> insertBeans=new ArrayList<>();
				List<MtlItemsOrg> updateBeans=new ArrayList<>();
				String orgId= orgMap.get("org_id").toString();
				SysOrg sys_org = sysOrgMapper.selectById(orgId);
				if(sys_org==null){
					throw new Exception(MessageUtils.get("purchase.valid","",orgId));
				}
				if(!sys_org.getOrg_type().equalsIgnoreCase("ORG")){
					continue;
				}
				/**查出当前组织所有的组织物料**/
				QueryWrapper<MtlItemsOrg> orgItemsWrapper = new QueryWrapper();
				orgItemsWrapper.eq("org_id",orgId);

				List<MtlItemsOrg> orgItemsList = mtlItemsOrgMapper.selectList(orgItemsWrapper);
				for(MtlItemsBase item:allItemsList){
					List<MtlItemsOrg> tempList = orgItemsList.stream().filter(a->(a.getItems_code().equalsIgnoreCase(item.getItems_code()))).collect(Collectors.toList());
					MtlItemsOrg mtlItemsOrg = new MtlItemsOrg();
					if(CollectionUtils.isEmpty(tempList)){
						mtlItemsOrg = new MtlItemsOrg();
						BeanUtils.copyProperties(item,mtlItemsOrg);
						mtlItemsOrg.setItem_id(null);
						mtlItemsOrg.setCreate_date(null);
						mtlItemsOrg.setCreate_by(null);
						mtlItemsOrg.setUpdate_date(null);
						mtlItemsOrg.setUpdate_by_name(null);
						mtlItemsOrg.setUdpate_by(null);
						mtlItemsOrg.setOrg_id(orgId);
						insertBeans.add(mtlItemsOrg);
//						mtlItemsOrgService.save(mtlItemsOrg);
					}else{
						mtlItemsOrg = tempList.get(0);
						BeanUtils.copyProperties(item,mtlItemsOrg,"item_id","create_date","create_by","org_id");
//						mtlItemsOrgService.updateById(mtlItemsOrg);
						updateBeans.add(mtlItemsOrg);
					}

				}
				if(!CollectionUtils.isEmpty(insertBeans)){
					mtlItemsOrgService.saveBatch(insertBeans,SAVE_BATCH_SIZE);
				}
				if(!CollectionUtils.isEmpty(updateBeans)){
					mtlItemsOrgService.updateBatchById(updateBeans,SAVE_BATCH_SIZE);
				}
			}

			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

	@Transactional
	public R addOrgItems(List<List<Map<String, Object>>> list){
		try{
			List<Map<String, Object>> orgData = list.get(0);

			List<Map<String, Object>> itemsData = list.get(1);
			List<MtlItemsOrg> insertBeans=new ArrayList<>();

			for(Map<String,Object> items:itemsData){
				MtlItemsBase mtlItemsBase = baseMapper.selectOne(new QueryWrapper<MtlItemsBase>().eq("items_code",items.get("items_code").toString()));
				for(Map<String,Object> orgMap:orgData){
					String itemsCode = items.get("items_code").toString();
					String orgId= orgMap.get("org_id").toString();
					SysOrg sys_org = sysOrgMapper.selectById(orgId);
					if(sys_org==null){
						throw new Exception(MessageUtils.get("purchase.valid","",orgId));
					}
					if(!sys_org.getOrg_type().equalsIgnoreCase("ORG")){
						throw new Exception(MessageUtils.get("E130",itemsCode,sys_org.getOrg_name()));
					}

					QueryWrapper<MtlItemsOrg> orgItemsWrapper = new QueryWrapper();
					orgItemsWrapper.eq("org_id",orgId);
					orgItemsWrapper.eq("items_code",itemsCode);
					MtlItemsOrg mtlItemsOrg = mtlItemsOrgMapper.selectOne(orgItemsWrapper);
					if(mtlItemsOrg==null){
						mtlItemsOrg = new MtlItemsOrg();
						BeanUtils.copyProperties(mtlItemsBase,mtlItemsOrg);
						mtlItemsOrg.setItem_id(null);
						mtlItemsOrg.setCreate_date(null);
						mtlItemsOrg.setCreate_by(null);
						mtlItemsOrg.setUpdate_date(null);
						mtlItemsOrg.setUpdate_by_name(null);
						mtlItemsOrg.setUdpate_by(null);
						mtlItemsOrg.setOrg_id(orgId);
						insertBeans.add(mtlItemsOrg);
					}
				}
			}
			mtlItemsOrgService.saveBatch(insertBeans);
			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}





	@Transactional
	public R update(Map<String,Object> params){
		try{
			String headInfo = (String) params.get("unitdatas");
			MtlItemsBase bean = JSON.parseObject(headInfo, MtlItemsBase.class);
			QueryWrapper wrapper = new QueryWrapper<MtlItemsBase>();
			wrapper.eq("items_code",bean.getItems_code());
			if(!StringUtils.isEmpty(bean.getItem_id())){
				wrapper.ne("item_id",bean.getItem_id());
			}
			MtlItemsBase mtl_itemsBase = baseMapper.selectOne(wrapper);
			if (mtl_itemsBase !=null){
				throw new RuntimeException("已存在对应物料编码");
			}
			R res = comUnitService.SaveData(params,getEntityClass());
			String id = res.get("keyid").toString();
			MtlItemsBase baseItem = baseMapper.selectById(id);

			wrapper = new QueryWrapper<MtlItemsOrg>();
			wrapper.eq("items_code",baseItem.getItems_code());

			List<MtlItemsOrg> mtlItemsOrgs = mtlItemsOrgMapper.selectList(wrapper);
			for(MtlItemsOrg mtlItemsOrg:mtlItemsOrgs){
				mtlItemsOrg.setItems_name(baseItem.getItems_name());
				mtlItemsOrg.setUnit_group(baseItem.getUnit_group());
				mtlItemsOrg.setItems_desc(baseItem.getItems_desc());
				mtlItemsOrg.setPurchase_unit(baseItem.getPurchase_unit());
				mtlItemsOrg.setItems_specification(baseItem.getItems_specification());
				mtlItemsOrg.setItems_files(StringUtils.isEmpty(baseItem.getItems_files())?mtlItemsOrg.getItems_files():baseItem.getItems_files());
				mtlItemsOrg.setItems_pictures(StringUtils.isEmpty(baseItem.getItems_pictures())?mtlItemsOrg.getItems_pictures():baseItem.getItems_pictures());
				mtlItemsOrg.setCheck_flag(baseItem.getCheck_flag());
				mtlItemsOrg.setItems_category_desc(baseItem.getItems_category_desc());
				mtlItemsOrgMapper.updateById(mtlItemsOrg);
			}
			return res;
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

}