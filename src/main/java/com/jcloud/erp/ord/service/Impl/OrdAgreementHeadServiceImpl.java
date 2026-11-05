package com.jcloud.erp.ord.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.ord.entity.OrdAgreementDetail;
import com.jcloud.erp.ord.entity.OrdAgreementHead;
import com.jcloud.erp.ord.mapper.OrdAgreementHeadMapper;
import com.jcloud.erp.ord.service.OrdAgreementDetailService;
import com.jcloud.erp.ord.service.OrdAgreementHeadService;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 协议单头信息, 业务层实现 */
@Service
public class OrdAgreementHeadServiceImpl extends ServiceImpl<OrdAgreementHeadMapper, OrdAgreementHead> implements OrdAgreementHeadService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrdAgreementHeadMapper ordAgreementHeadMapper;
	@Autowired
	private OrdAgreementDetailService ordAgreementDetailService;

	@Autowired
	private OrdAgreementHeadService ordAgreementHeadService;

	@Override
	@Transactional
	public R updateSubmit(List<List<Map<String, Object>>> list){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukAgreementId = (String) item.get("uk_agreement_id");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");
							if(ukAgreementId != null && !ukAgreementId.isEmpty()){
								String bill_status = (String) item.get("bill_status");
								
								if("0".equals(bill_status)) {
									QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
									detailQueryWrapper.eq("uk_agreement_uuid", uuId);
									detailQueryWrapper.eq("uk_agreement_id", ukAgreementId);
									List<OrdAgreementDetail> details = ordAgreementDetailService.list(detailQueryWrapper);
									
									//float totalBaseTaxMoney = 0;
									BigDecimal totalBaseTaxMoney = BigDecimal.ZERO;
									if(details != null && !details.isEmpty()){
										for(OrdAgreementDetail detail : details){
											if(detail.getBase_tax_money() != null){
												//totalBaseTaxMoney += detail.getBase_tax_money();
												totalBaseTaxMoney = totalBaseTaxMoney.add(detail.getBase_tax_money());
											}
										}
									}
									
									UpdateWrapper<OrdAgreementHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.eq("bill_status", bill_status);
									headUpdateWrapper.set("bill_status", "2");
									headUpdateWrapper.set("base_tax_money", totalBaseTaxMoney);
									ordAgreementHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdAgreementDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									detailUpdateWrapper.eq("uk_agreement_uuid", uuId);
									detailUpdateWrapper.eq("line_status", bill_status);
									detailUpdateWrapper.set("line_status", "2");
									ordAgreementDetailService.update(null, detailUpdateWrapper);
								} else {
									UpdateWrapper<OrdAgreementHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.eq("bill_status", bill_status);
									headUpdateWrapper.set("bill_status", "0");
									ordAgreementHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdAgreementDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									detailUpdateWrapper.eq("uk_agreement_uuid", uuId);
									detailUpdateWrapper.eq("line_status", bill_status);
									detailUpdateWrapper.set("line_status", "0");
									ordAgreementDetailService.update(null, detailUpdateWrapper);
								}
							}
						}
					}
				}
			}
			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

	@Override
	@Transactional
	public R active(List<List<Map<String, Object>>> list,String isActive){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukAgreementId = (String) item.get("uk_agreement_id");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");
							if(ukAgreementId != null && !ukAgreementId.isEmpty()){
								//String bill_status = (String) item.get("bill_status");
 								//关闭处理
								if("0".equals(isActive)) {
									UpdateWrapper<OrdAgreementHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.set("is_active", "0");

									ordAgreementHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdAgreementDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									detailUpdateWrapper.eq("uk_agreement_uuid", uuId);
									detailUpdateWrapper.set("is_active", "0");
									ordAgreementDetailService.update(null, detailUpdateWrapper);
								} else {
									UpdateWrapper<OrdAgreementHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.set("is_active", "1");

									ordAgreementHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdAgreementDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									detailUpdateWrapper.eq("uk_agreement_uuid", uuId);
									detailUpdateWrapper.set("is_active", "1");
									ordAgreementDetailService.update(null, detailUpdateWrapper);
								}
							}
						}
					}
				}
			}
			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

	@Override
	@Transactional
	public R activeDetail(List<List<Map<String, Object>>> list,String isActive){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukAgreementId = (String) item.get("uk_agreement_id");
							String ukAgreementUuid = (String) item.get("uk_agreement_uuid");
							String ukLineNo = (String) item.get("uk_line_no");
							// 关闭处理
							if("0".equals(isActive)) {
								// 更新明细行状态为已关
								UpdateWrapper<OrdAgreementDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
								detailUpdateWrapper.eq("uk_agreement_uuid", ukAgreementUuid);
								detailUpdateWrapper.eq("uk_line_no", ukLineNo);
								detailUpdateWrapper.set("is_active", "0");
								ordAgreementDetailService.update(null, detailUpdateWrapper);

								// 检查该协议的所有明细是否都已关
								QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
								detailQueryWrapper.eq("uk_agreement_id", ukAgreementId);
								detailQueryWrapper.eq("uk_agreement_uuid", ukAgreementUuid);
								detailQueryWrapper.eq("is_active", "1");
								List<OrdAgreementDetail> uncheckedDetails = ordAgreementDetailService.list(detailQueryWrapper);

								// 如果没有未关闭的明细，则更新协议头状态为已关
								if(uncheckedDetails == null || uncheckedDetails.isEmpty()) {
									UpdateWrapper<OrdAgreementHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									headUpdateWrapper.eq("uu_id", ukAgreementUuid);
									headUpdateWrapper.set("is_active", "0");
									ordAgreementHeadMapper.update(null, headUpdateWrapper);
								}
							}

							// 启用处理
							else {
								// 更新明细行状态为启用
								UpdateWrapper<OrdAgreementDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
								detailUpdateWrapper.eq("uk_agreement_uuid", ukAgreementUuid);
								detailUpdateWrapper.eq("uk_line_no", ukLineNo);
								detailUpdateWrapper.set("is_active", "1");
								ordAgreementDetailService.update(null, detailUpdateWrapper);

								// 处理协议头表
								QueryWrapper<OrdAgreementHead> headQueryWrapper = new QueryWrapper<>();
								headQueryWrapper.eq("uk_agreement_id", ukAgreementId);
								headQueryWrapper.eq("uu_id", ukAgreementUuid);
								headQueryWrapper.eq("is_active", "1");
								OrdAgreementHead orderHead = ordAgreementHeadService.getOne(headQueryWrapper);

								// 协议头状态改完启用
								if(orderHead == null) {
									UpdateWrapper<OrdAgreementHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
									headUpdateWrapper.eq("uu_id", ukAgreementUuid);
									headUpdateWrapper.set("is_active", "1");
									ordAgreementHeadMapper.update(null, headUpdateWrapper);
								}
							}
						}
					}
				}
			}
		  return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}
}