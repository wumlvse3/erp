package com.jcloud.erp.ord.service.Impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.ord.entity.OrdAgreementDetail;
import com.jcloud.erp.ord.entity.OrdAgreementHead;
import com.jcloud.erp.ord.entity.OrdInvoiceDetail;
import com.jcloud.erp.ord.entity.OrdInvoiceHead;
import com.jcloud.erp.ord.entity.OrdOrderDetail;
import com.jcloud.erp.ord.entity.OrdOrderHead;
import com.jcloud.erp.ord.mapper.OrdOrderHeadMapper;
import com.jcloud.erp.ord.service.OrdAgreementDetailService;
import com.jcloud.erp.ord.service.OrdAgreementHeadService;
import com.jcloud.erp.ord.service.OrdInvoiceDetailService;
import com.jcloud.erp.ord.service.OrdInvoiceHeadService;
import com.jcloud.erp.ord.service.OrdOrderDetailService;
import com.jcloud.erp.ord.service.OrdOrderHeadService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.alibaba.excel.EasyExcel;
/**
 * 销售订单信息, 业务层实现 *
 * @author wxc
 */
@Service
public class OrdOrderHeadServiceImpl extends ServiceImpl<OrdOrderHeadMapper, OrdOrderHead> implements OrdOrderHeadService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SAVE_BATCH_SIZE = 500;
	@Autowired
	SqlService sqlService;
	@Autowired
	private OrdOrderHeadMapper ordOrderHeadMapper;
	@Autowired
	private OrdOrderDetailService ordOrderDetailService;
	@Autowired
	private OrdInvoiceHeadService ordInvoiceHeadService;
	@Autowired
	private OrdInvoiceDetailService ordInvoiceDetailService;
	@Autowired
	private OrdAgreementHeadService ordAgreementHeadService;
	@Autowired
	private OrdAgreementDetailService ordAgreementDetailService;
	@Resource
	private OrdOrderHeadService ordOrderHeadService;
	@Autowired
	private ComUnitService comUnitService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	public OrdOrderHeadServiceImpl(){}

	@Override
	@Transactional
	public R updateSubmit(List<List<Map<String, Object>>> list,String isSubmit){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukOrderNo = (String) item.get("uk_order_no");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");

							String ordCheckFlag="";
							String ordCheckSql = "SELECT ord_check FROM ord_run WHERE sales_org='"+salesOrg+"'";
							Object ordCheck = sqlService.getOnef(ordCheckSql);
							if (ordCheck != null) {
								ordCheckFlag = ordCheck.toString();
							}

							if(ukOrderNo != null && !ukOrderNo.isEmpty()){
								UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
								headUpdateWrapper.eq("uk_order_no", ukOrderNo);
								headUpdateWrapper.eq("sales_org", salesOrg);
								String bill_status = (String) item.get("bill_status");
								//if("0".equals(bill_status)) {
								if("1".equals(isSubmit)) {
									String billStatus="1";
									if ("0".equals(ordCheckFlag)) {
										billStatus="2";
									}
									QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
									detailQueryWrapper.eq("uk_order_uuid", uuId);
									detailQueryWrapper.eq("uk_order_no", ukOrderNo);
									List<OrdOrderDetail> details = ordOrderDetailService.list(detailQueryWrapper);

									//float totalBaseTaxMoney = 0;
									BigDecimal totalBaseTaxMoney = BigDecimal.ZERO;
									if(details != null && !details.isEmpty()){
										for(OrdOrderDetail detail : details){
											if(detail.getBase_tax_money() != null){
												//totalBaseTaxMoney += detail.getBase_tax_money();
												totalBaseTaxMoney = totalBaseTaxMoney.add(detail.getBase_tax_money());
											}
										}
									}
									headUpdateWrapper.eq("bill_status", bill_status);
									headUpdateWrapper.set("bill_status", billStatus);
									headUpdateWrapper.set("base_tax_money", totalBaseTaxMoney);
								} else{
									headUpdateWrapper.eq("bill_status", bill_status);
									headUpdateWrapper.set("bill_status", "0");
								}
								ordOrderHeadMapper.update(null, headUpdateWrapper);

								UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
								detailUpdateWrapper.eq("uk_order_uuid", uuId);
								if("0".equals(bill_status)) {
									String lineStatus="1";
									if ("0".equals(ordCheckFlag)) {
										lineStatus="2";
									}
									detailUpdateWrapper.eq("line_status", bill_status);
									detailUpdateWrapper.set("line_status", lineStatus);
								} else {
									detailUpdateWrapper.eq("line_status", bill_status);
									detailUpdateWrapper.set("line_status", "0");
								}
								ordOrderDetailService.update(null, detailUpdateWrapper);
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
	public R updateCheck(List<List<Map<String, Object>>> list,String suggest,String isReturn){
		try{
			UserEntity userEntity = ShiroUtils.getUserEntity();
			SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukOrderNo = (String) item.get("uk_order_no");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");
							String ordCheckFlag="";
							String ordCheckSql = "SELECT ord_check FROM ord_run WHERE sales_org='"+salesOrg+"'";
							Object ordCheck = sqlService.getOnef(ordCheckSql);
							if (ordCheck != null) {
								ordCheckFlag = ordCheck.toString();
							}

							if(ukOrderNo != null && !ukOrderNo.isEmpty()){
								UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
								headUpdateWrapper.eq("uk_order_no", ukOrderNo);
								headUpdateWrapper.eq("sales_org", salesOrg);
								String bill_status = (String) item.get("bill_status");
								if("1".equals(bill_status)) {
									if("0".equals(isReturn)) {
										headUpdateWrapper.eq("bill_status", bill_status);
										headUpdateWrapper.set("bill_status", "2");
										headUpdateWrapper.set("approver_opinion", suggest);
										headUpdateWrapper.set("approver_user_code", userEntity.getUsername());
										headUpdateWrapper.set("approver_user_man", userEntity.getName());
										headUpdateWrapper.set("approver_time", sp.format(new Date()));
									} else{
										headUpdateWrapper.eq("bill_status", bill_status);
										headUpdateWrapper.set("bill_status", "3");
										headUpdateWrapper.set("approver_opinion", suggest);
										headUpdateWrapper.set("approver_user_code", userEntity.getUsername());
										headUpdateWrapper.set("approver_user_man", userEntity.getName());
										headUpdateWrapper.set("approver_time", sp.format(new Date()));
									}
								} else{
									String billStatus="1";
									if ("0".equals(ordCheckFlag)) {
										billStatus="0";
									}

									headUpdateWrapper.eq("bill_status", bill_status);
									headUpdateWrapper.set("bill_status", billStatus);
									headUpdateWrapper.set("approver_opinion", null);
									headUpdateWrapper.set("approver_user_code", null);
									headUpdateWrapper.set("approver_user_man", null);
									headUpdateWrapper.set("approver_time",null);
								}
								ordOrderHeadMapper.update(null, headUpdateWrapper);

								UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
								detailUpdateWrapper.eq("uk_order_uuid", uuId);
								if("1".equals(bill_status)) {
									if("0".equals(isReturn)) {
										detailUpdateWrapper.eq("line_status", bill_status);
										detailUpdateWrapper.set("line_status", "2");
										detailUpdateWrapper.set("line_opinion", suggest);
									} else{
										detailUpdateWrapper.eq("line_status", bill_status);
										detailUpdateWrapper.set("line_status", "3");
										detailUpdateWrapper.set("line_opinion", suggest);
									}
								} else{
									String lineStatus="1";
									if ("0".equals(ordCheckFlag)) {
										lineStatus="0";
									}

									detailUpdateWrapper.eq("line_status", bill_status);
									detailUpdateWrapper.set("line_status", lineStatus);
									detailUpdateWrapper.set("line_opinion", "");
								}
								ordOrderDetailService.update(null, detailUpdateWrapper);
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
	public R updateCheckDetail(List<List<Map<String, Object>>> list,String suggest){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukOrderNo = (String) item.get("uk_order_no");
							String ukOrderUuid = (String) item.get("uk_order_uuid");
							String ukLineNo = (String) item.get("uk_line_no");
							String lineStatus = (String) item.get("line_status");
							if(ukOrderNo != null && !ukOrderNo.isEmpty() && lineStatus != null){
								// 审核处理：line_status=1 -> 2
								if("1".equals(lineStatus)) {
									// 更新明细行状态为已审
									UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
									detailUpdateWrapper.eq("uk_order_uuid", ukOrderUuid);
									detailUpdateWrapper.eq("uk_line_no", ukLineNo);
									detailUpdateWrapper.set("line_status", "2");
									detailUpdateWrapper.set("line_opinion", suggest);
									ordOrderDetailService.update(null, detailUpdateWrapper);

									// 检查该订单的所有明细是否都已审
									QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
									detailQueryWrapper.eq("uk_order_no", ukOrderNo);
									detailQueryWrapper.eq("uk_order_uuid", ukOrderUuid);
									detailQueryWrapper.ne("line_status", "2");
									List<OrdOrderDetail> uncheckedDetails = ordOrderDetailService.list(detailQueryWrapper);

									// 如果没有未审核的明细，则更新订单头状态为已审
									if(uncheckedDetails == null || uncheckedDetails.isEmpty()) {
										UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
										headUpdateWrapper.eq("uk_order_no", ukOrderNo);
										headUpdateWrapper.eq("uu_id", ukOrderUuid);
										headUpdateWrapper.set("bill_status", "2");
										headUpdateWrapper.set("approver_opinion", suggest);
										ordOrderHeadMapper.update(null, headUpdateWrapper);
									}
								}
								// 取消审核处理：line_status=2 -> 1
								else if("2".equals(lineStatus)) {
									// 更新明细行状态为未审
									UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
									detailUpdateWrapper.eq("uk_order_uuid", ukOrderUuid);
									detailUpdateWrapper.eq("uk_line_no", ukLineNo);
									detailUpdateWrapper.set("line_status", "1");
									detailUpdateWrapper.set("line_opinion", "");
									ordOrderDetailService.update(null, detailUpdateWrapper);

									// 检查该订单的所有明细是否都已取消审
									QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
									detailQueryWrapper.eq("uk_order_no", ukOrderNo);
									detailQueryWrapper.eq("uk_order_uuid", ukOrderUuid);
									detailQueryWrapper.ne("line_status", "1");
									List<OrdOrderDetail> checkedDetails = ordOrderDetailService.list(detailQueryWrapper);

									// 如果没有已审核的明细，则检查订单头状态是否为已审
									if(checkedDetails == null || checkedDetails.isEmpty()) {
										QueryWrapper<OrdOrderHead> headQueryWrapper = new QueryWrapper<>();
										headQueryWrapper.eq("uk_order_no", ukOrderNo);
										headQueryWrapper.eq("uu_id", ukOrderUuid);
										OrdOrderHead orderHead = ordOrderHeadService.getOne(headQueryWrapper);

										// 如果订单头状态为已审核，则更新为已提
										if(orderHead != null && "2".equals(orderHead.getBill_status())) {
											UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
											headUpdateWrapper.eq("uk_order_no", ukOrderNo);
											headUpdateWrapper.eq("uu_id", ukOrderUuid);
											headUpdateWrapper.set("bill_status", "1");
											headUpdateWrapper.set("approver_opinion", "");
											ordOrderHeadMapper.update(null, headUpdateWrapper);
										}
									}
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
	public R createBill(List<List<Map<String, Object>>> list) {
		try {
			UserEntity userEntity = ShiroUtils.getUserEntity();
			List<Map<String, Object>> agreeData = list.get(0);
			for(Map<String,Object> agreeMap:agreeData){
				String salesOrg= agreeMap.get("sales_org").toString();
				String ukAgreementId= agreeMap.get("uk_agreement_id").toString();
				String uuId= agreeMap.get("uu_id").toString();

				QueryWrapper<OrdAgreementHead> agreementHeadQueryWrapper = new QueryWrapper<>();
				agreementHeadQueryWrapper.eq("sales_org", salesOrg);
				agreementHeadQueryWrapper.eq("uk_agreement_id", ukAgreementId);
				List<OrdAgreementHead> agreementHeads = ordAgreementHeadService.list(agreementHeadQueryWrapper);

				if (agreementHeads == null || agreementHeads.isEmpty()) {
					return R.error("未找到符合条件的协议单头数据");
				}

				/*QueryWrapper<OrdOrderHead> headQueryWrapper2 = new QueryWrapper<>();
				headQueryWrapper2.eq("sales_org", salesOrg);
				headQueryWrapper2.eq("up_bill", ukAgreementId);
				List<OrdOrderHead> orderHeads = ordOrderHeadService.list(headQueryWrapper2);
				if (!(orderHeads == null || orderHeads.isEmpty())) {
					return R.error("销售协议:"+ukAgreementId+"已经生成过订单，不能重复生成!");
				}*/

				String maxOrderNoSql = "SELECT CONCAT(DATE_FORMAT(CURDATE(),'%Y%m%d'),LPAD(IFNULL((SELECT MAX(CAST(SUBSTRING(uk_order_no, 9, 4) AS SIGNED)) FROM ord_order_head WHERE SUBSTRING(uk_order_no, 1, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')), 0) + 1,4, '0')) AS uk_order_no";
				Object maxOrderNoObj = sqlService.getOnef(maxOrderNoSql);
				String newUkOrderNo = "1";
				if (maxOrderNoObj != null) {
					newUkOrderNo = maxOrderNoObj.toString();
				}

				for (OrdAgreementHead agreementHead : agreementHeads) {
					OrdOrderHead orderHead = new OrdOrderHead();
					orderHead.setS_uu_id(agreementHead.getUu_id().toString());
					orderHead.setUk_order_no(newUkOrderNo);
					orderHead.setUp_bill(agreementHead.getUk_agreement_id());
					orderHead.setBill_status("0");
					orderHead.setType_code("1");
					orderHead.setGeneration_type("1");

					orderHead.setGroup_id(agreementHead.getGroup_id());
					orderHead.setSales_org(agreementHead.getSales_org());
					orderHead.setCustomer_id(agreementHead.getCustomer_id() != null ? agreementHead.getCustomer_id().toString() : null);
					orderHead.setCustomer_code(agreementHead.getCustomer_code());
					orderHead.setCustomer_name(agreementHead.getCustomer_name());
					orderHead.setRole_id(agreementHead.getRole_id() != null ? agreementHead.getRole_id().toString() : null);
					orderHead.setRole_code(agreementHead.getRole_code());
					orderHead.setRole_name(agreementHead.getRole_name());
					orderHead.setDept_id(agreementHead.getDept_id() != null ? agreementHead.getDept_id().toString() : null);
					orderHead.setDept_code(agreementHead.getDept_code());
					orderHead.setDept_name(agreementHead.getDept_name());
					orderHead.setSales_person_id(agreementHead.getSales_person_id() != null ? agreementHead.getSales_person_id().toString() : null);
					orderHead.setSales_person_code(agreementHead.getSales_person_code());
					orderHead.setSales_person_man(agreementHead.getSales_person_man());
					orderHead.setBill_date(agreementHead.getBill_date());
					orderHead.setTransport_type(agreementHead.getTransport_type());
					orderHead.setBill_discount(agreementHead.getBill_discount());
					orderHead.setBase_tax_money(agreementHead.getBase_tax_money());
					orderHead.setBase_currency_id(agreementHead.getCurrency_id() != null ? agreementHead.getCurrency_id().toString() : null);
					orderHead.setBase_currency_code(agreementHead.getCurrency_code());
					orderHead.setBase_currency_desc(agreementHead.getCurrency_desc());
					orderHead.setAddress(agreementHead.getAddress());
					orderHead.setSettlement(agreementHead.getSettlement());
					orderHead.setPay_way(agreementHead.getPay_way());
					orderHead.setApprover_user_id(agreementHead.getApprover_user_id() != null ? agreementHead.getApprover_user_id().toString() : null);
					orderHead.setApprover_user_code(agreementHead.getApprover_user_code());
					orderHead.setApprover_user_man(agreementHead.getApprover_user_man());
					orderHead.setApprover_time(agreementHead.getApprover_time());
					orderHead.setApprover_opinion(agreementHead.getApprover_opinion());
					orderHead.setNotes(agreementHead.getNotes());
					orderHead.setProject_id(agreementHead.getProject_id() != null ? agreementHead.getProject_id().toString() : null);
					orderHead.setProject_no(agreementHead.getProject_no());
					orderHead.setProject_name(agreementHead.getProject_name());
					orderHead.setWork_order(agreementHead.getWork_order());
					orderHead.setContract_no(agreementHead.getContract_no());
					orderHead.setContract_name(agreementHead.getContract_name());
					orderHead.setSign_date(agreementHead.getSign_date());
					orderHead.setSign_person_id(agreementHead.getSign_person_id() != null ? agreementHead.getSign_person_id().toString() : null);
					orderHead.setSign_person_code(agreementHead.getSign_person_code());
					orderHead.setSign_person_man(agreementHead.getSign_person_man());
					orderHead.setInvoice_customer_id(agreementHead.getInvoice_customer_id() != null ? agreementHead.getInvoice_customer_id().toString() : null);
					orderHead.setInvoice_customer_code(agreementHead.getInvoice_customer_code());
					orderHead.setInvoice_customer_name(agreementHead.getInvoice_customer_name());
					orderHead.setPay_term(agreementHead.getPay_term());
					orderHead.setRemarks(agreementHead.getRemarks());
					orderHead.setIs_collect(agreementHead.getIs_collect());
					orderHead.setCreate_user_id(userEntity.getUsername());
					//orderHead.setCreate_user_id(agreementHead.getCreate_user_id());
					//orderHead.setModify_user_id(agreementHead.getModify_user_id());
					//orderHead.setFlow_id(agreementHead.getFlow_id());
					//orderHead.setIs_deleted(agreementHead.getIs_deleted());

					ordOrderHeadService.save(orderHead);
                    String agreementType= agreementHead.getAgreement_type();
					QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
					detailQueryWrapper.eq("uk_agreement_id", ukAgreementId);
					detailQueryWrapper.eq("uk_agreement_uuid", uuId);
					List<OrdAgreementDetail> agreementDetails = ordAgreementDetailService.list(detailQueryWrapper);

					if (agreementDetails != null && !agreementDetails.isEmpty()) {
						int lineNo = 1;
						for (OrdAgreementDetail agreementDetail : agreementDetails) {
							//根据货币取单价和金额小数位数
							String currencyCode= agreementDetail.getCurrency_code();
							int deciPrice=2;
							int deciAmt=2;
							String deciSql = "SELECT decimal_digits_price,decimal_digits_amt FROM epd_currency WHERE currency_code='"+currencyCode+"'";
							Map<String,Object> deciArray = sqlService.getOne(deciSql);
							if (deciArray != null) {
								deciPrice = Integer.parseInt(deciArray.get("decimal_digits_price").toString());
								deciAmt = Integer.parseInt(deciArray.get("decimal_digits_amt").toString());
							}

							//根据单位和主单位取数量小数位数
							/*String ordUnitId= agreementDetail.getOrd_unit_id();
							int deciS=2;
							String deciSql1 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+ordUnitId+"'";
							Map<String,Object> deciArray1 = sqlService.getOne(deciSql1);
							if (deciArray1 != null) {
								deciS = Integer.parseInt(deciArray1.get("decimal_digits").toString());
							}*/
							String mainUnitId= agreementDetail.getMain_unit_id();
							int deciM=2;
							String deciSql2 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+mainUnitId+"'";
							Map<String,Object> deciArray2 = sqlService.getOne(deciSql2);
							if (deciArray2 != null) {
								deciM = Integer.parseInt(deciArray2.get("decimal_digits").toString());
							}
							/*float agreementQty = agreementDetail.getAgreement_qty() != null ? agreementDetail.getAgreement_qty() : 0;
							float ordNum = agreementDetail.getOrd_num() != null ? agreementDetail.getOrd_num() : 0;
							float orderQtyCan = 0;
							orderQtyCan = agreementQty - ordNum;
							if(orderQtyCan==0) {
								continue;
							}*/
							// 协议数量
							BigDecimal agreementQty = agreementDetail.getAgreement_qty() != null? new BigDecimal(agreementDetail.getAgreement_qty().toString()): BigDecimal.ZERO;
							// 已订单数量
							BigDecimal ordNum = agreementDetail.getOrd_num() != null? new BigDecimal(agreementDetail.getOrd_num().toString()): BigDecimal.ZERO;
							// 可订单数量 = 协议数量 - 已订单数量
							BigDecimal orderQtyCan = agreementQty.subtract(ordNum);
							// 如果可订单数量为 0，跳过
							if (orderQtyCan.compareTo(BigDecimal.ZERO) == 0) {
								continue;
							}
							//float unitPrice=Float.parseFloat(agreementDetail.getUnit_price().toString());
							//float noTaxPrice =Float.parseFloat(agreementDetail.getNo_tax_price().toString());
							//float taxRatio=(float) (Math.round(((unitPrice - noTaxPrice) / noTaxPrice * 100 )* 100)) / 100;
							//float exchangeRate=Float.parseFloat(agreementDetail.getExchange_rate().toString());
							//float changeRate=Float.parseFloat(agreementDetail.getChange_rate().toString());

							// 含税单价
							BigDecimal unitPrice = roundToDecimal(new BigDecimal(agreementDetail.getUnit_price().toString()),deciPrice );
							// 不含税单价
							BigDecimal noTaxPrice = roundToDecimal(new BigDecimal(agreementDetail.getNo_tax_price().toString()),deciPrice );
							// 税率 = (含税单价 - 不含税单价) / 不含税单价 × 100
							BigDecimal taxRatio = roundToDecimal(unitPrice.subtract(noTaxPrice).divide(noTaxPrice, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100")),         deciPrice );
							// 汇率
							BigDecimal exchangeRate = new BigDecimal(agreementDetail.getExchange_rate().toString());
							// 换算率
							BigDecimal changeRate = new BigDecimal(agreementDetail.getChange_rate().toString());
							/*float noTaxAmount = (float) (Math.round((noTaxPrice * orderQtyCan) * 100)) / 100;
							float taxAmount  = (float) (Math.round((noTaxAmount * (taxRatio / 100)) * 100)) / 100;
							float amt  = (float) (Math.round((unitPrice * orderQtyCan) * 100)) / 100;
							float baseTaxPrice  = (float) (Math.round((unitPrice * exchangeRate) * 100)) / 100;
							float baseTaxMoney  = (float) (Math.round((baseTaxPrice * orderQtyCan) * 100)) / 100;
							//float mainQty  =orderQtyCan*changeRate;
							*/

							// 不含税金额
							BigDecimal noTaxAmount = roundToDecimal(noTaxPrice.multiply(orderQtyCan),deciAmt );
							// 税额 = 不含税金额 × (税率 / 100)
							BigDecimal taxAmount = roundToDecimal(noTaxAmount.multiply(taxRatio.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)),deciAmt );
							// 价税合计 = 含税单价 × 数量
							BigDecimal amt = roundToDecimal(unitPrice.multiply(orderQtyCan),deciAmt );
							// 本位币含税单价 = 含税单价 × 汇率
							BigDecimal baseTaxPrice = roundToDecimal(unitPrice.multiply(exchangeRate),deciPrice );
							// 本位币含税金额 = 本位币含税单价 × 数量
							BigDecimal baseTaxMoney = roundToDecimal(baseTaxPrice.multiply(orderQtyCan),deciAmt );
							// 主数量 = 数量 × 换算率
							BigDecimal mainQty = roundToDecimal(orderQtyCan.multiply(changeRate),deciM );

							OrdOrderDetail orderDetail = new OrdOrderDetail();
							orderDetail.setS_uu_id(agreementDetail.getUu_id().toString());
							orderDetail.setUk_order_uuid(orderHead.getUu_id().toString());
							orderDetail.setUk_order_no(newUkOrderNo);
							orderDetail.setUk_line_no(String.valueOf(lineNo++));
							orderDetail.setUp_bill(agreementHead.getUk_agreement_id());
							orderDetail.setUp_bill_line(agreementDetail.getUk_line_no());
							orderDetail.setPur_qty(orderQtyCan);
							orderDetail.setLine_status("0");
							orderDetail.setIs_close("0");

							orderDetail.setGroup_id(agreementDetail.getGroup_id());
							orderDetail.setItem_id(agreementDetail.getItem_id());
							orderDetail.setItem_code(agreementDetail.getItem_code());
							orderDetail.setItem_name(agreementDetail.getItem_name());
							orderDetail.setItem_spec(agreementDetail.getItem_spec());
							orderDetail.setItem_model(agreementDetail.getItem_model());
							orderDetail.setSend_org(agreementDetail.getSend_org());
							orderDetail.setShip_date(agreementDetail.getShip_date());
							orderDetail.setOrd_unit_id(agreementDetail.getOrd_unit_id());
							orderDetail.setOrd_unit_name(agreementDetail.getOrd_unit_name());
							orderDetail.setChange_rate(agreementDetail.getChange_rate());
							orderDetail.setMain_qty(mainQty);
							orderDetail.setMain_unit_id(agreementDetail.getMain_unit_id());
							orderDetail.setMain_unit_name(agreementDetail.getMain_unit_name());
							orderDetail.setTax_code(agreementDetail.getTax_code());
							orderDetail.setTax_name(agreementDetail.getTax_name());
							orderDetail.setExchange_rate(agreementDetail.getExchange_rate());
							orderDetail.setCurrency_id(agreementDetail.getCurrency_id());
							orderDetail.setCurrency_code(agreementDetail.getCurrency_code());
							orderDetail.setCurrency_desc(agreementDetail.getCurrency_desc());
							orderDetail.setUnit_price(agreementDetail.getUnit_price());
							orderDetail.setAmt(amt);
							orderDetail.setTax_amount(taxAmount);
							orderDetail.setNo_tax_price(noTaxPrice);
							orderDetail.setNo_tax_amount(noTaxAmount);
							orderDetail.setDiscount(agreementDetail.getDiscount());
							orderDetail.setBase_currency_id(agreementDetail.getBase_currency_id());
							orderDetail.setBase_currency_code(agreementDetail.getBase_currency_code());
							orderDetail.setBase_currency_desc(agreementDetail.getBase_currency_desc());
							orderDetail.setBase_tax_price(baseTaxPrice);
							orderDetail.setBase_tax_money(baseTaxMoney);
							orderDetail.setGift(agreementDetail.getGift());
							orderDetail.setProject_id(agreementDetail.getProject_id());
							orderDetail.setProject_no(agreementDetail.getProject_no());
							orderDetail.setProject_name(agreementDetail.getProject_name());
							orderDetail.setWork_order(agreementDetail.getWork_order());
							orderDetail.setNotes(agreementDetail.getNotes());
							orderDetail.setManufacturer(agreementDetail.getManufacturer());
							orderDetail.setProd_date(agreementDetail.getProd_date());
							orderDetail.setCreate_user_id(userEntity.getUsername());
							//orderDetail.setCreate_user_id(agreementDetail.getCreate_user_id());
							//orderDetail.setModify_user_id(agreementDetail.getModify_user_id());
							//orderDetail.setFlow_id(agreementDetail.getFlow_id());
							//orderDetail.setIs_deleted(agreementDetail.getIs_deleted());

							ordOrderDetailService.save(orderDetail);

							//float currentOrdnumQty = agreementDetail.getOrd_num() != null ? agreementDetail.getOrd_num() : 0;
							//float totalQty = currentOrdnumQty + orderQtyCan;

							// 当前已订单数量
							BigDecimal currentOrdnumQty = agreementDetail.getOrd_num() != null? new BigDecimal(agreementDetail.getOrd_num().toString()): BigDecimal.ZERO;
							// 累计数量 = 当前已订单数量 + 可订单数量
							BigDecimal totalQty = currentOrdnumQty.add(orderQtyCan);

							String isActive="1";
							if("0".equals(agreementType) || "2".equals(agreementType)) {
								if(totalQty.compareTo(agreementQty)==0){
									isActive = "0";
								}
							}
							UpdateWrapper<OrdAgreementDetail> agreementDetailUpdateWrapper = new UpdateWrapper<>();
							agreementDetailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
							agreementDetailUpdateWrapper.eq("uk_agreement_uuid", uuId);
							agreementDetailUpdateWrapper.eq("uk_line_no", agreementDetail.getUk_line_no());
							agreementDetailUpdateWrapper.set("is_ord", "1");
							agreementDetailUpdateWrapper.set("ord_num", totalQty);
							agreementDetailUpdateWrapper.set("is_active", isActive);
							ordAgreementDetailService.update(null, agreementDetailUpdateWrapper);
						}
					}
					QueryWrapper<OrdAgreementDetail> detailQueryWrapper2 = new QueryWrapper<>();
					detailQueryWrapper2.eq("uk_agreement_id", ukAgreementId);
					detailQueryWrapper2.eq("uk_agreement_uuid", uuId);
					detailQueryWrapper2.eq("is_active", "0");
					Long agreementDetailsSize2 = ordAgreementDetailService.count(detailQueryWrapper2);
					Long agreementDetailsSize =  Long.valueOf(agreementDetails.size());
					if(agreementDetailsSize.compareTo(agreementDetailsSize2)==0){
						UpdateWrapper<OrdAgreementHead> orderAgreementHeadUpdateWrapper = new UpdateWrapper<>();
						orderAgreementHeadUpdateWrapper.eq("sales_org", salesOrg);
						orderAgreementHeadUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
						orderAgreementHeadUpdateWrapper.set("is_active", "0");
						ordAgreementHeadService.update(null, orderAgreementHeadUpdateWrapper);
					}
				}
			}

			return R.ok();
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}
	@Override
	@Transactional
	public R createBillDetail(List<List<Map<String, Object>>> list,String ukOrderNo) {
		try {
			UserEntity userEntity = ShiroUtils.getUserEntity();
			List<Map<String, Object>> agreementData = list.get(0);
			Map<String,Object> agreementMap0=agreementData.get(0);
			String salesOrg = agreementMap0.get("sales_org").toString();
			String ukAgreementId = agreementMap0.get("uk_agreement_id").toString();
			String uuId = agreementMap0.get("uk_agreement_uuid").toString();
			//订单头生成
			QueryWrapper<OrdAgreementHead> headQueryWrapper = new QueryWrapper<>();
			headQueryWrapper.eq("sales_org", salesOrg);
			headQueryWrapper.eq("uk_agreement_id", ukAgreementId);
			List<OrdAgreementHead> agreementHeads = ordAgreementHeadService.list(headQueryWrapper);

			if (agreementHeads == null || agreementHeads.isEmpty()) {
				return R.error("未找到符合条件的协议单头数据!");
			}

			QueryWrapper<OrdOrderHead> headQueryWrapperOrder = new QueryWrapper<>();
			headQueryWrapperOrder.eq("sales_org", salesOrg);
			headQueryWrapperOrder.eq("up_bill", ukAgreementId);
			headQueryWrapperOrder.eq("uk_order_no", ukOrderNo);
			List<OrdOrderHead> orderHeads = ordOrderHeadService.list(headQueryWrapperOrder);

			OrdOrderHead orderHead = new OrdOrderHead();
			OrdAgreementHead agreementHead = agreementHeads.get(0);
			String newUkOrderNo = "";
            if("".equals(ukOrderNo)) {
				String maxOrderNoSql = "SELECT CONCAT(DATE_FORMAT(CURDATE(),'%Y%m%d'),LPAD(IFNULL((SELECT MAX(CAST(SUBSTRING(uk_order_no, 9, 4) AS SIGNED)) FROM ord_order_head WHERE SUBSTRING(uk_order_no, 1, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')), 0) + 1,4, '0')) AS uk_order_no";
				Object maxOrderNoObj = sqlService.getOnef(maxOrderNoSql);
				newUkOrderNo = "1";
				if (maxOrderNoObj != null) {
					newUkOrderNo = maxOrderNoObj.toString();
				}

				orderHead.setS_uu_id(agreementHead.getUu_id().toString());
				orderHead.setUk_order_no(newUkOrderNo);
				orderHead.setUp_bill(agreementHead.getUk_agreement_id());
				orderHead.setBill_status("0");
				orderHead.setType_code("1");
				orderHead.setGeneration_type("1");

				orderHead.setGroup_id(agreementHead.getGroup_id());
				orderHead.setSales_org(agreementHead.getSales_org());
				orderHead.setCustomer_id(agreementHead.getCustomer_id() != null ? agreementHead.getCustomer_id().toString() : null);
				orderHead.setCustomer_code(agreementHead.getCustomer_code());
				orderHead.setCustomer_name(agreementHead.getCustomer_name());
				orderHead.setRole_id(agreementHead.getRole_id() != null ? agreementHead.getRole_id().toString() : null);
				orderHead.setRole_code(agreementHead.getRole_code());
				orderHead.setRole_name(agreementHead.getRole_name());
				orderHead.setDept_id(agreementHead.getDept_id() != null ? agreementHead.getDept_id().toString() : null);
				orderHead.setDept_code(agreementHead.getDept_code());
				orderHead.setDept_name(agreementHead.getDept_name());
				orderHead.setSales_person_id(agreementHead.getSales_person_id() != null ? agreementHead.getSales_person_id().toString() : null);
				orderHead.setSales_person_code(agreementHead.getSales_person_code());
				orderHead.setSales_person_man(agreementHead.getSales_person_man());
				orderHead.setBill_date(agreementHead.getBill_date());
				orderHead.setTransport_type(agreementHead.getTransport_type());
				orderHead.setBill_discount(agreementHead.getBill_discount());
				orderHead.setBase_tax_money(agreementHead.getBase_tax_money());
				orderHead.setBase_currency_id(agreementHead.getCurrency_id() != null ? agreementHead.getCurrency_id().toString() : null);
				orderHead.setBase_currency_code(agreementHead.getCurrency_code());
				orderHead.setBase_currency_desc(agreementHead.getCurrency_desc());
				orderHead.setAddress(agreementHead.getAddress());
				orderHead.setSettlement(agreementHead.getSettlement());
				orderHead.setPay_way(agreementHead.getPay_way());
				orderHead.setApprover_user_id(agreementHead.getApprover_user_id() != null ? agreementHead.getApprover_user_id().toString() : null);
				orderHead.setApprover_user_code(agreementHead.getApprover_user_code());
				orderHead.setApprover_user_man(agreementHead.getApprover_user_man());
				orderHead.setApprover_time(agreementHead.getApprover_time());
				orderHead.setApprover_opinion(agreementHead.getApprover_opinion());
				orderHead.setNotes(agreementHead.getNotes());
				orderHead.setProject_id(agreementHead.getProject_id() != null ? agreementHead.getProject_id().toString() : null);
				orderHead.setProject_no(agreementHead.getProject_no());
				orderHead.setProject_name(agreementHead.getProject_name());
				orderHead.setWork_order(agreementHead.getWork_order());
				orderHead.setContract_no(agreementHead.getContract_no());
				orderHead.setContract_name(agreementHead.getContract_name());
				orderHead.setSign_date(agreementHead.getSign_date());
				orderHead.setSign_person_id(agreementHead.getSign_person_id() != null ? agreementHead.getSign_person_id().toString() : null);
				orderHead.setSign_person_code(agreementHead.getSign_person_code());
				orderHead.setSign_person_man(agreementHead.getSign_person_man());
				orderHead.setInvoice_customer_id(agreementHead.getInvoice_customer_id() != null ? agreementHead.getInvoice_customer_id().toString() : null);
				orderHead.setInvoice_customer_code(agreementHead.getInvoice_customer_code());
				orderHead.setInvoice_customer_name(agreementHead.getInvoice_customer_name());
				orderHead.setPay_term(agreementHead.getPay_term());
				orderHead.setRemarks(agreementHead.getRemarks());
				orderHead.setIs_collect(agreementHead.getIs_collect());
				orderHead.setCreate_user_id(userEntity.getUsername());
				//orderHead.setCreate_user_id(agreementHead.getCreate_user_id());
				//orderHead.setModify_user_id(agreementHead.getModify_user_id());
				//orderHead.setFlow_id(agreementHead.getFlow_id());
				//orderHead.setIs_deleted(agreementHead.getIs_deleted())
				ordOrderHeadService.save(orderHead);
			}
			int lineNo = 1;
			// 2. 计算新行号
			if(!"".equals(ukOrderNo)) {
				QueryWrapper<OrdOrderDetail> headQueryWrapperOrderDetail = new QueryWrapper<>();
				headQueryWrapperOrderDetail.eq("up_bill", ukAgreementId);
				headQueryWrapperOrderDetail.eq("uk_order_no", ukOrderNo);
				headQueryWrapperOrderDetail.eq("uk_order_uuid", orderHeads.get(0).getUu_id());
				headQueryWrapperOrderDetail.select("IFNULL(MAX(uk_line_no), 0) AS uk_line_no");
				List<OrdOrderDetail> orderDetails = ordOrderDetailService.list(headQueryWrapperOrderDetail);
				if (!orderDetails.isEmpty() && orderDetails.get(0).getUk_line_no() != null) {
					lineNo = Integer.valueOf(orderDetails.get(0).getUk_line_no());
				}
				 lineNo = lineNo + 1;
			}
			for(Map<String,Object> agreeMap:agreementData) {
				//根据货币取单价和金额小数位数
				String currencyCode= agreeMap.get("currency_code").toString();
				int deciPrice=2;
				int deciAmt=2;
				String deciSql = "SELECT decimal_digits_price,decimal_digits_amt FROM epd_currency WHERE currency_code='"+currencyCode+"'";
				Map<String,Object> deciArray = sqlService.getOne(deciSql);
				if (deciArray != null) {
					deciPrice = Integer.parseInt(deciArray.get("decimal_digits_price").toString());
					deciAmt = Integer.parseInt(deciArray.get("decimal_digits_amt").toString());
				}
				//根据单位和主单位取数量小数位数
				/*String ordUnitId= agreeMap.get("ord_unit_id").toString();
				int deciS=2;
				String deciSql1 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+ordUnitId+"'";
				Map<String,Object> deciArray1 = sqlService.getOne(deciSql1);
				if (deciArray1 != null) {
					deciS = Integer.parseInt(deciArray1.get("decimal_digits").toString());
				}*/
				String mainUnitId= agreeMap.get("main_unit_id").toString();;
				int deciM=2;
				String deciSql2 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+mainUnitId+"'";
				Map<String,Object> deciArray2 = sqlService.getOne(deciSql2);
				if (deciArray2 != null) {
					deciM = Integer.parseInt(deciArray2.get("decimal_digits").toString());
				}

				//float orderQtyCur=Float.parseFloat(agreeMap.get("order_qty_cur").toString());
				BigDecimal orderQtyCur = new BigDecimal(agreeMap.get("order_qty_cur").toString());

				String agreementType=agreeMap.get("agreement_type").toString();
				String taxCode=agreeMap.get("tax_code").toString();
				String taxName=agreeMap.get("tax_name").toString();
				//float taxRatio=Float.parseFloat(agreeMap.get("tax_ratio").toString());
				//float unitPrice=Float.parseFloat(agreeMap.get("unit_price").toString());;
				//float noTaxPrice = (float) (Math.round((unitPrice / (1 + taxRatio/100)) * 100)) / 100;

				BigDecimal taxRatio = roundToDecimal(new BigDecimal(agreeMap.get("tax_ratio").toString()), deciPrice);
				BigDecimal unitPrice = roundToDecimal(new BigDecimal(agreeMap.get("unit_price").toString()),deciPrice);
				BigDecimal divisor = BigDecimal.ONE.add(taxRatio.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
				BigDecimal noTaxPrice = roundToDecimal(unitPrice.divide(divisor, 10, RoundingMode.HALF_UP),deciPrice);

				QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
				detailQueryWrapper.eq("uk_agreement_uuid", uuId);
				detailQueryWrapper.eq("uk_agreement_id", ukAgreementId);
				detailQueryWrapper.eq("uk_line_no", agreeMap.get("uk_line_no"));
				List<OrdAgreementDetail> AgreementDetails = ordAgreementDetailService.list(detailQueryWrapper);
				OrdAgreementDetail agreementDetail =AgreementDetails.get(0);

				OrdOrderDetail orderDetail = new OrdOrderDetail();
				orderDetail.setS_uu_id(agreementDetail.getUu_id().toString());
				if("".equals(ukOrderNo)) {
					orderDetail.setUk_order_uuid(orderHead.getUu_id().toString());
					orderDetail.setUk_order_no(newUkOrderNo);
					orderDetail.setUp_bill(agreementHead.getUk_agreement_id());
				} else{
					orderDetail.setUk_order_uuid(orderHeads.get(0).getUu_id().toString());
					orderDetail.setUk_order_no(ukOrderNo);
					orderDetail.setUp_bill(orderHeads.get(0).getUp_bill());
				}
				orderDetail.setUk_line_no(String.valueOf(lineNo++));
				orderDetail.setUp_bill_line(agreementDetail.getUk_line_no());
				orderDetail.setPur_qty(orderQtyCur);
				orderDetail.setLine_status("0");
				orderDetail.setIs_close("0");

				//float unitPrice=Float.parseFloat(agreementDetail.getUnit_price().toString());
				//float noTaxPrice =Float.parseFloat(agreementDetail.getNo_tax_price().toString());
				//float taxRatio=(float) (Math.round((unitPrice - noTaxPrice) / noTaxPrice * 100 )* 100) / 100;

				//float exchangeRate=Float.parseFloat(agreementDetail.getExchange_rate().toString());
				//float changeRate=Float.parseFloat(agreementDetail.getChange_rate().toString());
				BigDecimal exchangeRate = roundToDecimal(new BigDecimal(agreementDetail.getExchange_rate().toString()), deciPrice);
				BigDecimal changeRate = roundToDecimal(new BigDecimal(agreementDetail.getChange_rate().toString()), deciPrice);

				/*float noTaxAmount = (float) (Math.round((noTaxPrice * orderQtyCur) * 100)) / 100;
				float taxAmount  = (float) (Math.round((noTaxAmount * (taxRatio / 100)) * 100)) / 100;
				float amt  = (float) (Math.round((unitPrice * orderQtyCur) * 100)) / 100;
				float baseTaxPrice  = (float) (Math.round((unitPrice * exchangeRate) * 100)) / 100;
				float baseTaxMoney  = (float) (Math.round((baseTaxPrice * orderQtyCur) * 100)) / 100;
				float mainQty  =orderQtyCur*changeRate;*/

				// 不含税金额
				BigDecimal noTaxAmount = roundToDecimal(noTaxPrice.multiply(orderQtyCur),deciAmt );
				// 税额 = 不含税金额 × (税率 / 100)
				BigDecimal taxAmount = roundToDecimal(noTaxAmount.multiply(taxRatio.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)),deciAmt );
				// 价税合计 = 含税单价 × 数量
				BigDecimal amt = roundToDecimal(unitPrice.multiply(orderQtyCur),deciAmt );
				// 本位币含税单价 = 含税单价 × 汇率
				BigDecimal baseTaxPrice = roundToDecimal(unitPrice.multiply(exchangeRate),deciPrice );
				// 本位币含税金额 = 本位币含税单价 × 数量
				BigDecimal baseTaxMoney = roundToDecimal(baseTaxPrice.multiply(orderQtyCur),deciAmt );
				// 主数量 = 数量 × 换算率
				BigDecimal mainQty = roundToDecimal(orderQtyCur.multiply(changeRate),deciM );

				orderDetail.setGroup_id(agreementDetail.getGroup_id());
				orderDetail.setItem_id(agreementDetail.getItem_id());
				orderDetail.setItem_code(agreementDetail.getItem_code());
				orderDetail.setItem_name(agreementDetail.getItem_name());
				orderDetail.setItem_spec(agreementDetail.getItem_spec());
				orderDetail.setItem_model(agreementDetail.getItem_model());
				orderDetail.setSend_org(agreementDetail.getSend_org());
				orderDetail.setShip_date(agreementDetail.getShip_date());
				orderDetail.setOrd_unit_id(agreementDetail.getOrd_unit_id());
				orderDetail.setOrd_unit_name(agreementDetail.getOrd_unit_name());
				orderDetail.setChange_rate(agreementDetail.getChange_rate());
				orderDetail.setMain_qty(mainQty);
				orderDetail.setMain_unit_id(agreementDetail.getMain_unit_id());
				orderDetail.setMain_unit_name(agreementDetail.getMain_unit_name());
				orderDetail.setTax_code(taxCode);
				orderDetail.setTax_name(taxName);
				orderDetail.setExchange_rate(agreementDetail.getExchange_rate());
				orderDetail.setCurrency_id(agreementDetail.getCurrency_id());
				orderDetail.setCurrency_code(agreementDetail.getCurrency_code());
				orderDetail.setCurrency_desc(agreementDetail.getCurrency_desc());
				//orderDetail.setUnit_price(agreementDetail.getUnit_price());
				orderDetail.setUnit_price(unitPrice);
				orderDetail.setAmt(amt);
				orderDetail.setTax_amount(taxAmount);
				orderDetail.setNo_tax_price(noTaxPrice);
				orderDetail.setNo_tax_amount(noTaxAmount);
				orderDetail.setDiscount(agreementDetail.getDiscount());
				orderDetail.setBase_currency_id(agreementDetail.getBase_currency_id());
				orderDetail.setBase_currency_code(agreementDetail.getBase_currency_code());
				orderDetail.setBase_currency_desc(agreementDetail.getBase_currency_desc());
				orderDetail.setBase_tax_price(baseTaxPrice);
				orderDetail.setBase_tax_money(baseTaxMoney);
				orderDetail.setGift(agreementDetail.getGift());
				orderDetail.setProject_id(agreementDetail.getProject_id());
				orderDetail.setProject_no(agreementDetail.getProject_no());
				orderDetail.setProject_name(agreementDetail.getProject_name());
				orderDetail.setWork_order(agreementDetail.getWork_order());
				orderDetail.setNotes(agreementDetail.getNotes());
				orderDetail.setManufacturer(agreementDetail.getManufacturer());
				orderDetail.setProd_date(agreementDetail.getProd_date());
				orderDetail.setCreate_user_id(userEntity.getUsername());

				ordOrderDetailService.save(orderDetail);

				UpdateWrapper<OrdAgreementDetail> agreementDetailUpdateWrapper = new UpdateWrapper<>();
				agreementDetailUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
				agreementDetailUpdateWrapper.eq("uk_agreement_uuid", uuId);
				agreementDetailUpdateWrapper.eq("uk_line_no", agreementDetail.getUk_line_no());

				//float currentOrdnumQty = agreementDetail.getOrd_num() != null ? agreementDetail.getOrd_num() : 0;
				//float totalQty = currentOrdnumQty + orderQtyCur;
				// 当前订单数量（判空 + 转换）
				BigDecimal currentOrdnumQty = agreementDetail.getOrd_num() != null
						? new BigDecimal(agreementDetail.getOrd_num().toString())
						: BigDecimal.ZERO;

				// 累计数量 = 当前数量 + 本次数量
				BigDecimal totalQty = currentOrdnumQty.add(orderQtyCur);
				BigDecimal agreementQty=new BigDecimal(agreementDetail.getAgreement_qty().toString());
                String isActive="1";
				if("0".equals(agreementType) || "2".equals(agreementType)) {
                	if(totalQty.compareTo(agreementQty)==0){
						isActive = "0";
					}
				}
				agreementDetailUpdateWrapper.set("is_ord", "1");
				agreementDetailUpdateWrapper.set("ord_num", totalQty);
				agreementDetailUpdateWrapper.set("is_active", isActive);

				ordAgreementDetailService.update(null, agreementDetailUpdateWrapper);
			}
			QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
			detailQueryWrapper.eq("uk_agreement_id", ukAgreementId);
			detailQueryWrapper.eq("uk_agreement_uuid", uuId);
			Long agreementDetailsSize = ordAgreementDetailService.count(detailQueryWrapper);

			QueryWrapper<OrdAgreementDetail> detailQueryWrapper2 = new QueryWrapper<>();
			detailQueryWrapper2.eq("uk_agreement_id", ukAgreementId);
			detailQueryWrapper2.eq("uk_agreement_uuid", uuId);
			detailQueryWrapper2.eq("is_active", "0");
			Long agreementDetailsSize2 = ordAgreementDetailService.count(detailQueryWrapper2);
			if(agreementDetailsSize.compareTo(agreementDetailsSize2)==0){
				UpdateWrapper<OrdAgreementHead> orderAgreementHeadUpdateWrapper = new UpdateWrapper<>();
				orderAgreementHeadUpdateWrapper.eq("sales_org", salesOrg);
				orderAgreementHeadUpdateWrapper.eq("uk_agreement_id", ukAgreementId);
				orderAgreementHeadUpdateWrapper.set("is_active", "0");
				ordAgreementHeadService.update(null, orderAgreementHeadUpdateWrapper);
			}

			return R.ok();
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}
	/**
	 * 保留指定小数位数（四舍五入）
	 * @param value 原数值
	 * @param decimalPlaces 小数位数
	 * @return 处理后的 BigDecimal
	 */
	public static BigDecimal roundToDecimal(BigDecimal value, int decimalPlaces) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.setScale(decimalPlaces, RoundingMode.HALF_UP);
	}

	@Override
	public R getStatData(List<Map<String, Object>> queryList,String offset,String size) {
		try {
			StringBuffer condStrAll = new StringBuffer();
			if (queryList != null && !queryList.isEmpty()) {
				for (Map<String, Object> queryKey : queryList) {
					if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()) {
						String condStr ="";
						String andStr =" and ";
						if(queryKey.get("field").toString().equals("uk_order_no") || queryKey.get("field").toString().equals("create_user_id") || queryKey.get("field").toString().equals("create_time")) {
							andStr =" and t.";
						}
						logger.info("operator=="+queryKey.get("operator"));
						String operator = queryKey.get("operator").toString();
						if("=".equals(operator) || "<>".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
						}
						if("like".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
						}
						if("notLike".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
						}
						if("in".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
						}
						if("notIn".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
						}
						if("isNull".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " is null ";
						}
						if("notIsNull".equals(operator)) {
							condStr = andStr + queryKey.get("field") + "  is not null ";
						}
						condStrAll.append(condStr);
					}
				}
			}
			logger.info(condStrAll.toString());
		 	String selAll = "select uk_order_no,(select org_name from sys_org where org_code=sales_org ) as sales_org,customer_code,customer_name,role_code,role_name,sales_person_code,sales_person_man,bill_date,bill_status,bill_status_show,create_user_id,create_time,  item_code,item_name,item_spec,item_model,(select org_name from sys_org where org_code=send_org ) as send_org,ord_unit_name,ROUND(pur_qty, 2) AS pur_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(store_qty, 2) AS store_qty,ROUND(return_stock_qty, 2) AS return_stock_qty,ROUND(cost_qty, 2) AS cost_qty,work_order,project_no,project_name, sort_flag  from  (  select t.uk_order_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,CASE t.bill_status WHEN '0' THEN '未提交' WHEN '1' THEN '已提交' WHEN '2' THEN '审批通过' WHEN '3' THEN '驳回' WHEN '4' THEN '关闭' END AS bill_status_show,t.create_user_id,t.create_time,  t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,  sum(t2.pur_qty) as pur_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.return_stock_qty) as return_stock_qty,sum(t2.cost_qty) as cost_qty,t2.work_order,t2.project_no,t2.project_name, 1 as sort_flag  from ord_order_head t,ord_order_detail t2  where t.uu_id=t2.uk_order_uuid and t.uk_order_no=t2.uk_order_no " + condStrAll + "  group BY t.uk_order_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,t.create_user_id,t.create_time,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,t2.work_order,t2.project_no,t2.project_name  union  select '小计','','','','','','','','','','','','',  t2.item_code,'','','','','',  sum(t2.pur_qty) as pur_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.return_stock_qty) as return_stock_qty,sum(t2.cost_qty) as cost_qty,'','','', 1 as sort_flag  from ord_order_head t,ord_order_detail t2  where t.uu_id=t2.uk_order_uuid and t.uk_order_no=t2.uk_order_no " + condStrAll + "  group BY t.sales_org,t2.item_code  union  select '合计','','','','','','','','','','','','',  '','','','','','',  sum(t2.pur_qty) as pur_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.return_stock_qty) as return_stock_qty,sum(t2.cost_qty) as cost_qty,'','','', 2 as sort_flag  from ord_order_head t,ord_order_detail t2  where t.uu_id=t2.uk_order_uuid and t.uk_order_no=t2.uk_order_no " + condStrAll + " GROUP BY t.sales_org ) t2  order by sort_flag, t2.item_code, uk_order_no ";
			logger.info(selAll);
			logger.info("offset=="+offset);
			logger.info("size=="+size);
			List<Map<String, Object>> allStat = sqlService.getAll(selAll);
			int total = allStat != null ? allStat.size() : 0;

			int offsetInt = Integer.parseInt(offset);
			int sizeInt = Integer.parseInt(size);
			if (allStat != null && offsetInt < allStat.size()) {
				int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
				allStat = allStat.subList(offsetInt, endIndex);
			} else {
				allStat = new ArrayList<>();
			}

			Map<String, Object> result = new HashMap<>();
			result.put("rows", allStat);
			result.put("total", total);

			return R.ok().setdata(result);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

	@Override
	public void getStatDataExcel(List<Map<String, Object>> queryList, String offset, String size, HttpServletResponse response) {
		try {
			logger.info("=== Service 收到 queryList: {}", queryList);
			StringBuffer condStrAll = new StringBuffer();
			if (queryList != null && !queryList.isEmpty()) {
				for (Map<String, Object> queryKey : queryList) {
					if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()) {
						String condStr ="";
						String andStr =" and ";
						if(queryKey.get("field").toString().equals("uk_order_no") || queryKey.get("field").toString().equals("create_user_id") || queryKey.get("field").toString().equals("create_time")) {
							andStr =" and t.";
						}
						logger.info("operator=="+queryKey.get("operator"));
						String operator = queryKey.get("operator").toString();
						if("=".equals(operator) || "<>".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " " + queryKey.get("operator") + " '" + queryKey.get("value") + "' ";
						}
						if("like".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " like '" + queryKey.get("value") + "%' ";
						}
						if("notLike".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " not like '" + queryKey.get("value") + "%' ";
						}
						if("in".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " in ('" + queryKey.get("value") + "') ";
						}
						if("notIn".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " not in ('" + queryKey.get("value") + "') ";
						}
						if("isNull".equals(operator)) {
							condStr = andStr + queryKey.get("field") + " is null ";
						}
						if("notIsNull".equals(operator)) {
							condStr = andStr + queryKey.get("field") + "  is not null ";
						}
						condStrAll.append(condStr);
					}
				}
			}
			logger.info(condStrAll.toString());
			String selAll = "select uk_order_no,(select org_name from sys_org where org_code=sales_org ) as sales_org,customer_code,customer_name,role_code,role_name,sales_person_code,sales_person_man,bill_date,bill_status,bill_status_show,create_user_id,create_time,  item_code,item_name,item_spec,item_model,(select org_name from sys_org where org_code=send_org ) as send_org,ord_unit_name,ROUND(pur_qty, 2) AS pur_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(store_qty, 2) AS store_qty,ROUND(return_stock_qty, 2) AS return_stock_qty,ROUND(cost_qty, 2) AS cost_qty,work_order,project_no,project_name, sort_flag  from  (  select t.uk_order_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,CASE t.bill_status WHEN '0' THEN '未提交' WHEN '1' THEN '已提交' WHEN '2' THEN '审批通过' WHEN '3' THEN '驳回' WHEN '4' THEN '关闭' END AS bill_status_show,t.create_user_id,t.create_time,  t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,  sum(t2.pur_qty) as pur_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.return_stock_qty) as return_stock_qty,sum(t2.cost_qty) as cost_qty,t2.work_order,t2.project_no,t2.project_name, 1 as sort_flag  from ord_order_head t,ord_order_detail t2  where t.uu_id=t2.uk_order_uuid and t.uk_order_no=t2.uk_order_no " + condStrAll + "  group BY t.uk_order_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,t.create_user_id,t.create_time,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,t2.work_order,t2.project_no,t2.project_name  union  select '小计','','','','','','','','','','','','',  t2.item_code,'','','','','',  sum(t2.pur_qty) as pur_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.return_stock_qty) as return_stock_qty,sum(t2.cost_qty) as cost_qty,'','','', 1 as sort_flag  from ord_order_head t,ord_order_detail t2  where t.uu_id=t2.uk_order_uuid and t.uk_order_no=t2.uk_order_no " + condStrAll + "  group BY t.sales_org,t2.item_code  union  select '合计','','','','','','','','','','','','',  '','','','','','',  sum(t2.pur_qty) as pur_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.return_stock_qty) as return_stock_qty,sum(t2.cost_qty) as cost_qty,'','','', 2 as sort_flag  from ord_order_head t,ord_order_detail t2  where t.uu_id=t2.uk_order_uuid and t.uk_order_no=t2.uk_order_no " + condStrAll + " GROUP BY t.sales_org ) t2  order by sort_flag, t2.item_code, uk_order_no ";
			logger.info("getStatDataExcel=="+selAll);
			List<Map<String, Object>> allStat = sqlService.getAll(selAll);
			//int total = allStat != null ? allStat.size() : 0
			// 2. 设置响应头
			String fileName = "订单统计报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

			logger.info("查询结果条数: {}", allStat != null ? allStat.size() : 0);
			// 将 Map 数据按表头列顺序转换为 List<List<Object>>，
			// 避免 EasyExcel 用中文表头名作为 Map key 查找导致内容为空
			List<List<Object>> dataList = new ArrayList<>();
			if (allStat != null) {
				for (Map<String, Object> row : allStat) {
					List<Object> rowData = new ArrayList<>();
					rowData.add(row.get("uk_order_no"));
					rowData.add(row.get("sales_org"));
					rowData.add(row.get("customer_name"));
					rowData.add(row.get("role_name"));
					rowData.add(row.get("sales_person_man"));
					rowData.add(row.get("send_org"));
					rowData.add(row.get("bill_status_show"));
					rowData.add(row.get("item_code"));
					rowData.add(row.get("item_name"));
					rowData.add(row.get("item_spec"));
					rowData.add(row.get("item_model"));
					rowData.add(row.get("pur_qty"));
					rowData.add(row.get("ord_unit_name"));
					rowData.add(row.get("ship_qty"));
					rowData.add(row.get("store_qty"));
					rowData.add(row.get("return_stock_qty"));
					rowData.add(row.get("cost_qty"));
					rowData.add(row.get("bill_date"));
					rowData.add(row.get("work_order"));
					rowData.add(row.get("project_name"));
					dataList.add(rowData);
				}
			}
			EasyExcel.write(response.getOutputStream())
					.head(getExcelHead())
					.registerWriteHandler(new SimpleColumnWidthStyleStrategy(20))  // 统一宽度 20
					.sheet("Sheet1")
					.doWrite(dataList);

			logger.info("导出Excel成功: {}", fileName);
			// 返回文件下载链接
			//return R.ok().setdata(fileName);

		} catch (Exception e) {
			e.printStackTrace();
			//return R.error("导出失败：" + e.getMessage());
		}
	}

	/**
	 * 定义 Excel 表头
	 */
	private List<List<String>> getExcelHead() {
		List<List<String>> head = new ArrayList<>();
		head.add(Arrays.asList("单据号"));
		head.add(Arrays.asList("销售组织"));
		head.add(Arrays.asList("客户"));
		head.add(Arrays.asList("销售组"));
		head.add(Arrays.asList("销售员"));
		head.add(Arrays.asList("发货组织"));
		head.add(Arrays.asList("单据状态"));
		head.add(Arrays.asList("物料编码"));
		head.add(Arrays.asList("物料名称"));
		head.add(Arrays.asList("物料规格"));
		head.add(Arrays.asList("物料型号"));
		head.add(Arrays.asList("订货数量"));
		head.add(Arrays.asList("单位名称"));
		head.add(Arrays.asList("发货数量"));
		head.add(Arrays.asList("出库数量"));
		head.add(Arrays.asList("退货数量"));
		head.add(Arrays.asList("费用数量"));
		head.add(Arrays.asList("订单日期"));
		head.add(Arrays.asList("工作令号"));
		head.add(Arrays.asList("项目名称"));
		return head;
	}

	@Override
	@Transactional
	public R delete(Map<String,Object> params) {
		List<Map<String,Object>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
		try {
			/*for (Map<String, Object> item : list) {
				String s_uu_id = (String) item.get("s_uu_id");
				String up_bill = (String) item.get("up_bill");

				// 查询 ord_agreement_detail 中 is_ord=1 的记录
				QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
				detailQueryWrapper.eq("uk_agreement_uuid", s_uu_id);
				detailQueryWrapper.eq("uk_agreement_id", up_bill);
				detailQueryWrapper.eq("is_ord", "1");
				List<OrdAgreementDetail> agreementDetails = ordAgreementDetailService.list(detailQueryWrapper);

				// 如果存在符合条件的记录，更新 is_ord=0 和 ord_num 为空
				if (agreementDetails != null && !agreementDetails.isEmpty()) {
					UpdateWrapper<OrdAgreementDetail> updateWrapper = new UpdateWrapper<>();
					updateWrapper.eq("uk_agreement_uuid", s_uu_id);
					updateWrapper.eq("uk_agreement_id", up_bill);
					updateWrapper.eq("is_ord", "1");
					updateWrapper.set("is_ord", "0");
					updateWrapper.set("ord_num", null);
					ordAgreementDetailService.update(null, updateWrapper);
				}
			}*/
			R res = comUnitService.normDelete(params);
			if (!res.get("code").equals(200)) {
				return res;
			}
			return R.ok();
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}

	@Override
	@Transactional
	public R deleteDetail(Map<String,Object> params) {
		List<Map<String,Object>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
		try {
			for (Map<String, Object> item : list) {
				//String ukOrderUuid = (String) item.get("uk_order_uuid");
				//String ukOrderNo = (String) item.get("uk_order_no");
				String s_uu_id = (String) item.get("s_uu_id");
				String up_bill = (String) item.get("up_bill");
				String up_bill_line = (String) item.get("up_bill_line");

				logger.info("s_uu_id="+s_uu_id);
				logger.info("up_bill="+up_bill);
				logger.info("up_bill_line="+up_bill_line);

				// 查询 ord_agreement_detail 中的记录
				QueryWrapper<OrdAgreementDetail> detailQueryWrapper = new QueryWrapper<>();
				detailQueryWrapper.eq("uu_id", s_uu_id);
				detailQueryWrapper.eq("uk_agreement_id", up_bill);
				detailQueryWrapper.eq("uk_line_no", up_bill_line);

				List<OrdAgreementDetail> agreementDetails = ordAgreementDetailService.list(detailQueryWrapper);

				// 如果存在符合条件的记录
				if (agreementDetails != null && !agreementDetails.isEmpty()) {
					OrdAgreementDetail agreementDetail = agreementDetails.get(0);
					UpdateWrapper<OrdAgreementDetail> updateWrapper = new UpdateWrapper<>();

					float currentOrdnumQty = agreementDetail.getOrd_num() != null ? Float.parseFloat(agreementDetail.getOrd_num().toString()) : 0;
					float purQty = item.get("pur_qty") != null ? Float.parseFloat(item.get("pur_qty").toString()) : 0;
					float totalQty = currentOrdnumQty - purQty;
					logger.info("totalQty=" + totalQty);
					updateWrapper.eq("uu_id", s_uu_id);
					updateWrapper.eq("uk_agreement_id", up_bill);
					updateWrapper.eq("uk_line_no", up_bill_line);
					//updateWrapper.eq("is_ord", "1");
					if (totalQty == 0) {
						updateWrapper.set("is_ord", "0");
					}
					updateWrapper.set("ord_num", totalQty);
					updateWrapper.set("is_active", "1");
					ordAgreementDetailService.update(null, updateWrapper);

					UpdateWrapper<OrdAgreementHead> orderAgreementHeadUpdateWrapper = new UpdateWrapper<>();
					orderAgreementHeadUpdateWrapper.eq("uu_id", agreementDetail.getUk_agreement_uuid());
					orderAgreementHeadUpdateWrapper.eq("uk_agreement_id", up_bill);
					orderAgreementHeadUpdateWrapper.set("is_active", "1");
					ordAgreementHeadService.update(null, orderAgreementHeadUpdateWrapper);
					logger.info("end end ");
				}
			}

			R res = comUnitService.normDelete(params);
			if (!res.get("code").equals(200)) {
				return res;
			}
			return R.ok();
		} catch (Exception e) {
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
							String ukOrderNo = (String) item.get("uk_order_no");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");
							if(ukOrderNo != null && !ukOrderNo.isEmpty()){
								//String bill_status = (String) item.get("bill_status");
								//关闭处理
								if("0".equals(isActive)) {
									UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_order_no", ukOrderNo);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.set("is_active", "0");

									ordOrderHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
									detailUpdateWrapper.eq("uk_order_uuid", uuId);
									detailUpdateWrapper.set("is_active", "0");
									ordOrderDetailService.update(null, detailUpdateWrapper);
								} else {
									UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_order_no", ukOrderNo);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.set("is_active", "1");

									ordOrderHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
									detailUpdateWrapper.eq("uk_order_uuid", uuId);
									detailUpdateWrapper.set("is_active", "1");
									ordOrderDetailService.update(null, detailUpdateWrapper);
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
							String ukOrderNo = (String) item.get("uk_order_no");
							String ukOrderUuid = (String) item.get("uk_order_uuid");
							String ukLineNo = (String) item.get("uk_line_no");
							// 关闭处理
							if("0".equals(isActive)) {
								// 更新明细行状态为已关
								UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
								detailUpdateWrapper.eq("uk_order_uuid", ukOrderUuid);
								detailUpdateWrapper.eq("uk_line_no", ukLineNo);
								detailUpdateWrapper.set("is_active", "0");
								ordOrderDetailService.update(null, detailUpdateWrapper);

								// 检查该协议的所有明细是否都已关
								QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
								detailQueryWrapper.eq("uk_order_no", ukOrderNo);
								detailQueryWrapper.eq("uk_order_uuid", ukOrderUuid);
								detailQueryWrapper.eq("is_active", "1");
								List<OrdOrderDetail> uncheckedDetails = ordOrderDetailService.list(detailQueryWrapper);

								// 如果没有未关闭的明细，则更新协议头状态为已关
								if(uncheckedDetails == null || uncheckedDetails.isEmpty()) {
									UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_order_no", ukOrderNo);
									headUpdateWrapper.eq("uu_id", ukOrderUuid);
									headUpdateWrapper.set("is_active", "0");
									ordOrderHeadMapper.update(null, headUpdateWrapper);
								}
							}

							// 启用处理
							else {
								// 更新明细行状态为启用
								UpdateWrapper<OrdOrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_order_no", ukOrderNo);
								detailUpdateWrapper.eq("uk_order_uuid", ukOrderUuid);
								detailUpdateWrapper.eq("uk_line_no", ukLineNo);
								detailUpdateWrapper.set("is_active", "1");
								ordOrderDetailService.update(null, detailUpdateWrapper);

								// 处理协议头表
								QueryWrapper<OrdOrderHead> headQueryWrapper = new QueryWrapper<>();
								headQueryWrapper.eq("uk_order_no", ukOrderNo);
								headQueryWrapper.eq("uu_id", ukOrderUuid);
								headQueryWrapper.eq("is_active", "1");
								OrdOrderHead orderHead = ordOrderHeadService.getOne(headQueryWrapper);

								// 协议头状态改完启用
								if(orderHead == null) {
									UpdateWrapper<OrdOrderHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_order_no", ukOrderNo);
									headUpdateWrapper.eq("uu_id", ukOrderUuid);
									headUpdateWrapper.set("is_active", "1");
									ordOrderHeadMapper.update(null, headUpdateWrapper);
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