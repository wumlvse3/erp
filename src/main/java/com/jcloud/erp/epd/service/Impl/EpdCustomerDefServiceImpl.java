package com.jcloud.erp.epd.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.epd.entity.EpdCustomerDef;
import com.jcloud.erp.epd.entity.EpdVendorDef;
import com.jcloud.erp.epd.mapper.EpdCustomerDefMapper;
import com.jcloud.erp.epd.mapper.EpdVendorDefMapper;
import com.jcloud.erp.epd.service.EpdCustomerDefService;
import com.jcloud.erp.epd.service.EpdVendorDefService;
import com.jcloud.erp.ord.entity.*;
import com.jcloud.erp.ord.mapper.*;
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
 * 客户信息, 业务类
 *
 */
@Service
public class EpdCustomerDefServiceImpl extends ServiceImpl<EpdCustomerDefMapper, EpdCustomerDef> implements EpdCustomerDefService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SAVE_BATCH_SIZE = 500;

	@Autowired
	private EpdCustomerDefMapper epdCustomerDefMapper;
	@Autowired
	private EpdVendorDefMapper epdVendorDefMapper;
	@Autowired
	private ComUnitService comUnitService;
	@Resource
	private EpdCustomerDefService epdCustomerDefService;
	@Resource
	private EpdVendorDefService epdVendorDefService;

	@Autowired
	private OrdAgreementHeadMapper ordAgreementHeadMapper;
	@Autowired
	private OrdOrderHeadMapper ordOrderHeadMapper;
	@Autowired
	private OrdInvoiceHeadMapper ordInvoiceHeadMapper;

	@Autowired
	private OrdgroupItemDetailMapper ordgroupItemDetailMapper;
	@Autowired
	private OrdGroupvenDetailMapper ordGroupvenDetailMapper;


	public EpdCustomerDefServiceImpl(){}

	@Override
	@Transactional
	public R addOrgItems(List<List<Map<String, Object>>> list){
		try{
			UserEntity userEntity = ShiroUtils.getUserEntity();
			List<Map<String, Object>> orgData = list.get(0);
			List<Map<String, Object>> itemsData = list.get(1);
			List<EpdCustomerDef> insertBeans=new ArrayList<>();

			for(Map<String,Object> items:itemsData){
				EpdCustomerDef epdCustomerDef0 = baseMapper.selectOne(new QueryWrapper<EpdCustomerDef>().eq("uu_id",items.get("uu_id").toString()));
				for(Map<String,Object> orgMap:orgData){
					String customerCode = items.get("customer_code").toString();
					String orgCode= orgMap.get("org_code").toString();
					QueryWrapper<EpdCustomerDef> epdCustomerDefWrapper = new QueryWrapper();
					epdCustomerDefWrapper.eq("sales_org",orgCode);
					epdCustomerDefWrapper.eq("customer_code",customerCode);
					EpdCustomerDef epdCustomerDef = epdCustomerDefMapper.selectOne(epdCustomerDefWrapper);
					if(epdCustomerDef==null){
						epdCustomerDef = new EpdCustomerDef();
						BeanUtils.copyProperties(epdCustomerDef0,epdCustomerDef);
						epdCustomerDef.setUu_id(null);
						epdCustomerDef.setGroup_id(null);
						epdCustomerDef.setCreate_time(null);
						epdCustomerDef.setCreate_user_id(userEntity.getUsername());

						epdCustomerDef.setModify_time(null);
						epdCustomerDef.setModify_user_id(null);
						epdCustomerDef.setAssign_user_id(null);
						epdCustomerDef.setAssign_user_code(null);
						epdCustomerDef.setAssign_user_man(null);
						epdCustomerDef.setAssign_time(null);
						epdCustomerDef.setAssign_from_org(orgMap.get("org_code").toString());
						epdCustomerDef.setCustomer_code(items.get("customer_code").toString());
						epdCustomerDef.setCustomer_name(items.get("customer_name").toString());
						epdCustomerDef.setCustomer_abb(items.get("customer_abb")==null?null:items.get("customer_abb").toString());
						if(items.get("remark")==null){
							epdCustomerDef.setRemark(null);
						} else {
							epdCustomerDef.setRemark(items.get("remark").toString());
						}
						epdCustomerDef.setValid_flag("1");
						epdCustomerDef.setSales_org(orgMap.get("org_code").toString());

						epdCustomerDef.setFlow_id(items.get("flow_id")==null?null:items.get("flow_id").toString());
						epdCustomerDef.setIs_deleted(items.get("is_deleted")==null?null:(Boolean)items.get("is_deleted"));
						epdCustomerDef.setC_v_flag(items.get("c_v_flag")==null?null:items.get("c_v_flag").toString());
						epdCustomerDef.setInternal_flag(items.get("internal_flag")==null?null:items.get("internal_flag").toString());
						epdCustomerDef.setCountry_code(items.get("country_code")==null?null:items.get("country_code").toString());
						epdCustomerDef.setProvince_code(items.get("province_code")==null?null:items.get("province_code").toString());
						epdCustomerDef.setCity_code(items.get("city_code")==null?null:items.get("city_code").toString());
						epdCustomerDef.setCounty_code(items.get("county_code")==null?null:items.get("county_code").toString());
						epdCustomerDef.setIndustry_code(items.get("industry_code")==null?null:items.get("industry_code").toString());
						epdCustomerDef.setNature_code(items.get("nature_code")==null?null:items.get("nature_code").toString());
						epdCustomerDef.setLegal_man(items.get("legal_man")==null?null:items.get("legal_man").toString());
						epdCustomerDef.setBank(items.get("bank")==null?null:items.get("bank").toString());
						epdCustomerDef.setBank_account(items.get("bank_account")==null?null:items.get("bank_account").toString());
						epdCustomerDef.setPostal_code(items.get("postal_code")==null?null:items.get("postal_code").toString());
						epdCustomerDef.setPhone(items.get("phone")==null?null:items.get("phone").toString());
						epdCustomerDef.setFax(items.get("fax")==null?null:items.get("fax").toString());
						epdCustomerDef.setAddress(items.get("address")==null?null:items.get("address").toString());
						epdCustomerDef.setE_mail(items.get("e_mail")==null?null:items.get("e_mail").toString());
						epdCustomerDef.setWebsit(items.get("websit")==null?null:items.get("websit").toString());
						epdCustomerDef.setTax_registration_no(items.get("tax_registration_no")==null?null:items.get("tax_registration_no").toString());
						epdCustomerDef.setCurrency_code(items.get("currency_code")==null?null:items.get("currency_code").toString());
						epdCustomerDef.setTax_code(items.get("tax_code")==null?null:items.get("tax_code").toString());
						epdCustomerDef.setCreate_type(items.get("create_type")==null?null:items.get("create_type").toString());
						epdCustomerDef.setSelect_flag(items.get("select_flag")==null?null:items.get("select_flag").toString());
						epdCustomerDef.setTelephone(items.get("telephone")==null?null:items.get("telephone").toString());
						epdCustomerDef.setMaster_dept(items.get("master_dept")==null?null:items.get("master_dept").toString());
						epdCustomerDef.setTrust(items.get("trust")==null?null:Integer.parseInt(items.get("trust").toString()));
						epdCustomerDef.setCredit_day(items.get("credit_day")==null?null:Integer.parseInt(items.get("credit_day").toString()));
						epdCustomerDef.setCredit_limit(items.get("credit_limit")==null?null:Float.parseFloat(items.get("credit_limit").toString()));
						epdCustomerDef.setCredit_amt_adjust(items.get("credit_amt_adjust")==null?null:Float.parseFloat(items.get("credit_amt_adjust").toString()));
						epdCustomerDef.setCredit_day_adjust(items.get("credit_day_adjust")==null?null:Integer.parseInt(items.get("credit_day_adjust").toString()));
						epdCustomerDef.setAmt_control(items.get("amt_control")==null?null:items.get("amt_control").toString());
						epdCustomerDef.setDay_control(items.get("day_control")==null?null:items.get("day_control").toString());
						epdCustomerDef.setProfit(items.get("profit")==null?null:items.get("profit").toString());
						epdCustomerDef.setOutput_value(items.get("output_value")==null?null:items.get("output_value").toString());
						epdCustomerDef.setShip_address(items.get("ship_address")==null?null:items.get("ship_address").toString());
						epdCustomerDef.setClass_code(items.get("class_code")==null?null:items.get("class_code").toString());
						epdCustomerDef.setMne_code(items.get("mne_code")==null?null:items.get("mne_code").toString());
						epdCustomerDef.setInternal_acct_org(items.get("internal_acct_org")==null?null:items.get("internal_acct_org").toString());
						insertBeans.add(epdCustomerDef);
					}
				}
			}
			epdCustomerDefService.saveBatch(insertBeans);
			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

	@Override
	@Transactional
	public R update(Map<String,Object> params){
		try{
			String headInfo = (String) params.get("unitdatas");
			EpdCustomerDef bean = JSON.parseObject(headInfo, EpdCustomerDef.class);

			String mFlag = bean.getM();
			logger.info("mFlag===="+mFlag);
			String validFlag = bean.getValid_flag();
			QueryWrapper<EpdCustomerDef> wrapperN = new QueryWrapper<>();
			wrapperN.eq("uu_id", bean.getUu_id());
			EpdCustomerDef nowCustomer = epdCustomerDefMapper.selectOne(wrapperN);
			String validFlagN = nowCustomer.getValid_flag();
			logger.info("validFlag===="+validFlag);
			logger.info("validFlagN===="+validFlagN);
			if("e".equals(mFlag) && "0".equals(validFlag) && "1".equals(validFlagN)){
				//销售协议
				QueryWrapper<OrdAgreementHead> wrapperAgree = new QueryWrapper<>();
				wrapperAgree.eq("customer_code", bean.getCustomer_code());
				wrapperAgree.eq("is_active", "1");
				Long countAgree = ordAgreementHeadMapper.selectCount(wrapperAgree);
				if (countAgree > 0) {
					throw new RuntimeException("该客户编码数据在销售协议表中还在使用,有效标记不能修改为无效!");
				}
				//销售订单
				QueryWrapper<OrdOrderHead> wrapperOrder = new QueryWrapper<>();
				wrapperOrder.eq("customer_code", bean.getCustomer_code());
				wrapperOrder.eq("is_active", "1");
				Long countOrder = ordOrderHeadMapper.selectCount(wrapperOrder);
				if (countOrder > 0) {
					throw new RuntimeException("该客户编码数据在销售订单表中还在使用,有效标记不能修改为无效!");
				}
				//销售协议
				QueryWrapper<OrdInvoiceHead> wrapperInvoice = new QueryWrapper<>();
				wrapperInvoice.eq("customer_code", bean.getCustomer_code());
				wrapperInvoice.eq("is_active", "1");
				Long countInvoice = ordInvoiceHeadMapper.selectCount(wrapperInvoice);
				if (countInvoice > 0) {
					throw new RuntimeException("该客户编码数据在销售发货或销售退库单表中还在使用,有效标记不能修改为无效!");
				}

                //客户物料中客户无效
				UpdateWrapper<OrdgroupItemDetail> updateItemWrapper = new UpdateWrapper<>();
				updateItemWrapper.eq("customer_code", bean.getCustomer_code());
				updateItemWrapper.eq("sales_org", bean.getSales_org());
				updateItemWrapper.set("is_close", "1");
				ordgroupItemDetailMapper.update(null, updateItemWrapper);

				//小组客户权限中客户无效
				UpdateWrapper<OrdGroupvenDetail> updateVenWrapper = new UpdateWrapper<>();
				updateVenWrapper.eq("customer_code", bean.getCustomer_code());
				updateVenWrapper.eq("sales_org", bean.getSales_org());
				updateVenWrapper.set("is_close", "1");
				ordGroupvenDetailMapper.update(null, updateVenWrapper);
			}

			QueryWrapper<EpdCustomerDef> wrapper = new QueryWrapper<>();
			wrapper.eq("customer_code", bean.getCustomer_code());
			if (StringUtils.isNotEmpty(bean.getSales_org())) {
				wrapper.eq("sales_org", bean.getSales_org());
			}
			if (bean.getUu_id() != null) {
				wrapper.ne("uu_id", bean.getUu_id());
			}
			EpdCustomerDef existCustomer = epdCustomerDefMapper.selectOne(wrapper);
			if (existCustomer != null) {
				throw new RuntimeException("客户编码已存在,不能保存!");
			}

			QueryWrapper<EpdCustomerDef> wrapper2 = new QueryWrapper<>();
			wrapper2.eq("tax_registration_no", bean.getTax_registration_no());
			if (StringUtils.isNotEmpty(bean.getSales_org())) {
				wrapper2.eq("sales_org", bean.getSales_org());
			}
			if (bean.getUu_id() != null) {
				wrapper2.ne("uu_id", bean.getUu_id());
			}
			EpdCustomerDef existCustomer2 = epdCustomerDefMapper.selectOne(wrapper2);
			if (existCustomer2 != null) {
				throw new RuntimeException("统一社会信用代码已存在,不能保存!");
			}

			R res = comUnitService.SaveData(params, getEntityClass());
			String id = res.get("keyid").toString();
			EpdCustomerDef customerDef = epdCustomerDefMapper.selectById(id);
			
			if (StringUtils.isNotEmpty(customerDef.getTax_registration_no())) {
				QueryWrapper<EpdVendorDef> vendorWrapper = new QueryWrapper<>();
				vendorWrapper.eq("tax_registration_no", customerDef.getTax_registration_no());
				EpdVendorDef existVendor = epdVendorDefMapper.selectOne(vendorWrapper);
				
				if (existVendor == null) {
					EpdVendorDef vendorDef = new EpdVendorDef();
					BeanUtils.copyProperties(customerDef, vendorDef, "uu_id", "group_id", "create_time", "create_user_id", 
						"modify_time", "modify_user_id", "assign_user_id", "assign_user_code", "assign_user_man", 
						"assign_time", "assign_from_org", "customer_code", "customer_name", "customer_abb", "sales_org",
						"credit_day", "credit_limit", "credit_amt_adjust", "credit_day_adjust", "amt_control", "day_control",
						"profit", "output_value");
					vendorDef.setVendor_code(customerDef.getCustomer_code());
					vendorDef.setVendor_name(customerDef.getCustomer_name());
					vendorDef.setVendor_abb(customerDef.getCustomer_abb());
					//vendorDef.setPm_org("AA");
					vendorDef.setPm_org(customerDef.getSales_org());
					epdVendorDefService.save(vendorDef);
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
