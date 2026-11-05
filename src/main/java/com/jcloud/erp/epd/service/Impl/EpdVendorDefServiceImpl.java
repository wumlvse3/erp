package com.jcloud.erp.epd.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.epd.entity.EpdCustomerDef;
import com.jcloud.erp.epd.entity.EpdVendorDef;
import com.jcloud.erp.epd.mapper.EpdCustomerDefMapper;
import com.jcloud.erp.epd.mapper.EpdVendorDefMapper;
import com.jcloud.erp.epd.service.EpdCustomerDefService;
import com.jcloud.erp.epd.service.EpdVendorDefService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.shiro.ShiroUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 供应商信�? 业务�? *
 */
@Service
public class EpdVendorDefServiceImpl extends ServiceImpl<EpdVendorDefMapper, EpdVendorDef> implements EpdVendorDefService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SAVE_BATCH_SIZE = 500;

	@Autowired
	private EpdVendorDefMapper epdVendorDefMapper;
	@Autowired
	private EpdCustomerDefMapper epdCustomerDefMapper;
	@Autowired
	private ComUnitService comUnitService;
	@Resource
	private EpdVendorDefService epdVendorDefService;
	@Resource
	private EpdCustomerDefService epdCustomerDefService;

	public EpdVendorDefServiceImpl(){}

	@Override
	@Transactional
	public R addOrgItems(List<List<Map<String, Object>>> list){
		try{
			UserEntity userEntity = ShiroUtils.getUserEntity();
			List<Map<String, Object>> orgData = list.get(0);
			List<Map<String, Object>> itemsData = list.get(1);
			List<EpdVendorDef> insertBeans=new ArrayList<>();

			for(Map<String,Object> items:itemsData){
				EpdVendorDef epdVendorDef0 = baseMapper.selectOne(new QueryWrapper<EpdVendorDef>().eq("uu_id",items.get("uu_id").toString()));
				for(Map<String,Object> orgMap:orgData){
					String vendorCode = items.get("vendor_code").toString();
					String orgCode= orgMap.get("org_code").toString();
					QueryWrapper<EpdVendorDef> epdVendorDefWrapper = new QueryWrapper();
					epdVendorDefWrapper.eq("pm_org",orgCode);
					epdVendorDefWrapper.eq("vendor_code",vendorCode);
					EpdVendorDef epdVendorDef = epdVendorDefMapper.selectOne(epdVendorDefWrapper);
					if(epdVendorDef==null){
						epdVendorDef = new EpdVendorDef();
						BeanUtils.copyProperties(epdVendorDef0,epdVendorDef);
						epdVendorDef.setUu_id(null);
						epdVendorDef.setGroup_id(null);
						epdVendorDef.setCreate_time(null);
						epdVendorDef.setCreate_user_id(userEntity.getUsername());
						epdVendorDef.setModify_time(null);
						epdVendorDef.setModify_user_id(null);
						epdVendorDef.setAssign_user_id(null);
						epdVendorDef.setAssign_user_code(null);
						epdVendorDef.setAssign_user_man(null);
						epdVendorDef.setAssign_time(null);
						epdVendorDef.setAssign_from_org(orgMap.get("org_code").toString());
						epdVendorDef.setVendor_code(items.get("vendor_code").toString());
						epdVendorDef.setVendor_name(items.get("vendor_name").toString());
						epdVendorDef.setVendor_abb(items.get("vendor_abb")==null?null:items.get("vendor_abb").toString());
						if(items.get("remark")==null){
							epdVendorDef.setRemark(null);
						} else {
							epdVendorDef.setRemark(items.get("remark").toString());
						}
						epdVendorDef.setValid_flag("1");
						epdVendorDef.setPm_org(orgMap.get("org_code").toString());

						epdVendorDef.setFlow_id(items.get("flow_id")==null?null:items.get("flow_id").toString());
						epdVendorDef.setIs_deleted(items.get("is_deleted")==null?null:(Boolean)items.get("is_deleted"));
						epdVendorDef.setC_v_flag(items.get("c_v_flag")==null?null:items.get("c_v_flag").toString());
						epdVendorDef.setInternal_flag(items.get("internal_flag")==null?null:items.get("internal_flag").toString());
						epdVendorDef.setCountry_code(items.get("country_code")==null?null:items.get("country_code").toString());
						epdVendorDef.setProvince_code(items.get("province_code")==null?null:items.get("province_code").toString());
						epdVendorDef.setCity_code(items.get("city_code")==null?null:items.get("city_code").toString());
						epdVendorDef.setCounty_code(items.get("county_code")==null?null:items.get("county_code").toString());
						epdVendorDef.setIndustry_code(items.get("industry_code")==null?null:items.get("industry_code").toString());
						epdVendorDef.setNature_code(items.get("nature_code")==null?null:items.get("nature_code").toString());
						epdVendorDef.setLegal_man(items.get("legal_man")==null?null:items.get("legal_man").toString());
						epdVendorDef.setBank(items.get("bank")==null?null:items.get("bank").toString());
						epdVendorDef.setBank_account(items.get("bank_account")==null?null:items.get("bank_account").toString());
						epdVendorDef.setPostal_code(items.get("postal_code")==null?null:items.get("postal_code").toString());
						epdVendorDef.setPhone(items.get("phone")==null?null:items.get("phone").toString());
						epdVendorDef.setFax(items.get("fax")==null?null:items.get("fax").toString());
						epdVendorDef.setAddress(items.get("address")==null?null:items.get("address").toString());
						epdVendorDef.setE_mail(items.get("e_mail")==null?null:items.get("e_mail").toString());
						epdVendorDef.setWebsit(items.get("websit")==null?null:items.get("websit").toString());
						epdVendorDef.setTax_registration_no(items.get("tax_registration_no")==null?null:items.get("tax_registration_no").toString());
						epdVendorDef.setCurrency_code(items.get("currency_code")==null?null:items.get("currency_code").toString());
						epdVendorDef.setTax_code(items.get("tax_code")==null?null:items.get("tax_code").toString());
						epdVendorDef.setCreate_type(items.get("create_type")==null?null:items.get("create_type").toString());
						epdVendorDef.setSelect_flag(items.get("select_flag")==null?null:items.get("select_flag").toString());
						epdVendorDef.setTelephone(items.get("telephone")==null?null:items.get("telephone").toString());
						epdVendorDef.setMaster_dept(items.get("master_dept")==null?null:items.get("master_dept").toString());
						epdVendorDef.setTrust(items.get("trust")==null?null:Integer.parseInt(items.get("trust").toString()));
						epdVendorDef.setClass_code(items.get("class_code")==null?null:items.get("class_code").toString());
						epdVendorDef.setMne_code(items.get("mne_code")==null?null:items.get("mne_code").toString());
						epdVendorDef.setShip_address(items.get("ship_address")==null?null:items.get("ship_address").toString());
						epdVendorDef.setInternal_acct_org(items.get("internal_acct_org")==null?null:items.get("internal_acct_org").toString());
						insertBeans.add(epdVendorDef);
					}
				}
			}
			epdVendorDefService.saveBatch(insertBeans);
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
			EpdVendorDef bean = JSON.parseObject(headInfo, EpdVendorDef.class);
			
			QueryWrapper<EpdVendorDef> wrapper = new QueryWrapper<>();
			wrapper.eq("vendor_code", bean.getVendor_code());
			if (StringUtils.isNotEmpty(bean.getPm_org())) {
				wrapper.eq("pm_org", bean.getPm_org());
			}
			if (bean.getUu_id() != null) {
				wrapper.ne("uu_id", bean.getUu_id());
			}
			EpdVendorDef existVendor = epdVendorDefMapper.selectOne(wrapper);
			if (existVendor != null) {
				throw new RuntimeException("供应商编码已存在,不能保存!");
			}

			QueryWrapper<EpdVendorDef> wrapper2 = new QueryWrapper<>();
			wrapper2.eq("tax_registration_no", bean.getTax_registration_no());
			if (StringUtils.isNotEmpty(bean.getPm_org())) {
				wrapper2.eq("pm_org", bean.getPm_org());
			}
			if (bean.getUu_id() != null) {
				wrapper2.ne("uu_id", bean.getUu_id());
			}
			EpdVendorDef existVendor2 = epdVendorDefMapper.selectOne(wrapper2);
			if (existVendor2 != null) {
				throw new RuntimeException("统一社会信用代码已存在,不能保存!");
			}
			
			R res = comUnitService.SaveData(params, getEntityClass());
			String id = res.get("keyid").toString();
			EpdVendorDef vendorDef = epdVendorDefMapper.selectById(id);
			
			if (StringUtils.isNotEmpty(vendorDef.getTax_registration_no())) {
				QueryWrapper<EpdCustomerDef> customerWrapper = new QueryWrapper<>();
				customerWrapper.eq("tax_registration_no", vendorDef.getTax_registration_no());
				EpdCustomerDef existCustomer = epdCustomerDefMapper.selectOne(customerWrapper);
				
				if (existCustomer == null) {
					EpdCustomerDef customerDef = new EpdCustomerDef();
					BeanUtils.copyProperties(vendorDef, customerDef, "uu_id", "group_id", "create_time", "create_user_id", 
						"modify_time", "modify_user_id", "assign_user_id", "assign_user_code", "assign_user_man", 
						"assign_time", "assign_from_org", "vendor_code", "vendor_name", "vendor_abb", "pm_org");
					customerDef.setCustomer_code(vendorDef.getVendor_code());
					customerDef.setCustomer_name(vendorDef.getVendor_name());
					customerDef.setCustomer_abb(vendorDef.getVendor_abb());
					//customerDef.setSales_org("AA");
					customerDef.setSales_org(vendorDef.getPm_org());
					epdCustomerDefService.save(customerDef);
				}
			}
			
			return res;
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

}
