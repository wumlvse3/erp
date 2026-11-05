package com.jcloud.erp.ord.service.Impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.ord.entity.*;
import com.jcloud.erp.ord.mapper.OrdInvoiceHeadMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 发货单头信息, 业务层实现  *
 */
@Service
public class OrdInvoiceHeadServiceImpl extends ServiceImpl<OrdInvoiceHeadMapper, OrdInvoiceHead> implements OrdInvoiceHeadService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SAVE_BATCH_SIZE = 500;

	@Autowired
	private OrdInvoiceHeadMapper ordInvoiceHeadMapper;
	@Autowired
	private OrdInvoiceDetailService ordInvoiceDetailService;
	@Autowired
	private OrdOrderHeadService ordOrderHeadService;
	@Autowired
	private OrdOrderDetailService ordOrderDetailService;
	@Autowired
	ComUnitService comUnitService;
	@Resource
	private OrdInvoiceHeadService ordInvoiceHeadService;
	@Resource
	private SqlService sqlService;

	public OrdInvoiceHeadServiceImpl(){}

	@Override
	@Transactional
	public R updateSubmit(List<List<Map<String, Object>>> list){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukShipNo = (String) item.get("uk_ship_no");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");
							if(ukShipNo != null && !ukShipNo.isEmpty()){
								UpdateWrapper<OrdInvoiceHead> headUpdateWrapper = new UpdateWrapper<>();
								headUpdateWrapper.eq("uk_ship_no", ukShipNo);
								headUpdateWrapper.eq("sales_org", salesOrg);
								headUpdateWrapper.eq("uu_id", uuId);
								String bill_status = (String) item.get("bill_status");
								if("0".equals(bill_status)) {
									QueryWrapper<OrdInvoiceDetail> detailQueryWrapper = new QueryWrapper<>();
									detailQueryWrapper.eq("uk_ship_uuid", uuId);
									detailQueryWrapper.eq("uk_ship_no", ukShipNo);
									List<OrdInvoiceDetail> details = ordInvoiceDetailService.list(detailQueryWrapper);

									//float totalBaseTaxMoney = 0;
									BigDecimal totalBaseTaxMoney = BigDecimal.ZERO;
									if(details != null && !details.isEmpty()){
										for(OrdInvoiceDetail detail : details){
											if(detail.getBase_tax_money() != null){
												//totalBaseTaxMoney += detail.getBase_tax_money();
												totalBaseTaxMoney = totalBaseTaxMoney.add(detail.getBase_tax_money());
											}
										}
									}
									headUpdateWrapper.set("bill_status", "2");
									headUpdateWrapper.set("base_tax_money", totalBaseTaxMoney);
								} else{
									headUpdateWrapper.set("bill_status", "0");
								}
								ordInvoiceHeadMapper.update(null, headUpdateWrapper);
								
								UpdateWrapper<OrdInvoiceDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_ship_no", ukShipNo);
								detailUpdateWrapper.eq("uk_ship_uuid", uuId);
								if("0".equals(bill_status)) {
									detailUpdateWrapper.set("line_status", "2");
								} else {
									detailUpdateWrapper.set("line_status", "0");
								}
								ordInvoiceDetailService.update(null, detailUpdateWrapper);
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
	public R delete(Map<String,Object> params) {
		List<Map<String,Object>> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
		try {
			/*for (Map<String, Object> item : list) {
				String s_uu_id = (String) item.get("s_uu_id");
				String up_bill = (String) item.get("up_bill");
				String isShipRet=(String) item.get("is_ship_ret");
				logger.info("isShipRet=" + isShipRet);

				// 查询 ord_order_detail 中 is_ship=1 的记录
				if("0".equals(isShipRet)) {
					QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
					detailQueryWrapper.eq("uk_order_uuid", s_uu_id);
					detailQueryWrapper.eq("uk_order_no", up_bill);
					detailQueryWrapper.eq("is_ship", "1");
					List<OrdOrderDetail> orderDetails = ordOrderDetailService.list(detailQueryWrapper);

					// 如果存在符合条件的记录，更新 is_ship=0 和 ship_qty 为空
					if (orderDetails != null && !orderDetails.isEmpty()) {
						UpdateWrapper<OrdOrderDetail> updateWrapper = new UpdateWrapper<>();
						updateWrapper.eq("uk_order_uuid", s_uu_id);
						updateWrapper.eq("uk_order_no", up_bill);
						updateWrapper.eq("is_ship", "1");
						updateWrapper.set("is_ship", "0");
						updateWrapper.set("ship_qty", null);
						ordOrderDetailService.update(null, updateWrapper);
					}
				} else {
					//退货单场合
					QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
					detailQueryWrapper.eq("uk_order_uuid", s_uu_id);
					detailQueryWrapper.eq("uk_order_no", up_bill);
					detailQueryWrapper.eq("is_stock_return", "1");
					List<OrdOrderDetail> orderDetails = ordOrderDetailService.list(detailQueryWrapper);

					// 如果存在符合条件的记录，更新 is_ship=0 和 ship_qty 为空
					if (orderDetails != null && !orderDetails.isEmpty()) {
						UpdateWrapper<OrdOrderDetail> updateWrapper = new UpdateWrapper<>();
						updateWrapper.eq("uk_order_uuid", s_uu_id);
						updateWrapper.eq("uk_order_no", up_bill);
						updateWrapper.eq("is_stock_return", "1");
						updateWrapper.set("is_stock_return", "0");
						updateWrapper.set("return_stock_qty", null);
						ordOrderDetailService.update(null, updateWrapper);
					}
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
				String ukShipUuid = (String) item.get("uk_ship_uuid");
				String ukShipNo = (String) item.get("uk_ship_no");

				QueryWrapper<OrdInvoiceHead> headQueryWrapper = new QueryWrapper<>();
				headQueryWrapper.eq("uu_id", ukShipUuid);
				headQueryWrapper.eq("uk_ship_no", ukShipNo);
				List<OrdInvoiceHead> invoiceHeads = ordInvoiceHeadService.list(headQueryWrapper);
				String isShipRet=invoiceHeads.get(0).getIs_ship_ret();
				logger.info("isShipRet=" + isShipRet);

				String s_uu_id = (String) item.get("s_uu_id");
				String up_bill = (String) item.get("up_bill");
				String up_bill_line = (String) item.get("up_bill_line");
				logger.info("s_uu_id="+s_uu_id);
				logger.info("up_bill="+up_bill);
				logger.info("up_bill_line="+up_bill_line);
				// 查询 ord_order_detail 中的记录
				QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
				detailQueryWrapper.eq("uu_id", s_uu_id);
				detailQueryWrapper.eq("uk_order_no", up_bill);
				detailQueryWrapper.eq("uk_line_no", up_bill_line);
				//detailQueryWrapper.eq("is_ship", "1");
				List<OrdOrderDetail> orderDetails = ordOrderDetailService.list(detailQueryWrapper);

				// 如果存在符合条件的记录，更新 is_ship=0 和 ship_qty 为计算后值
				if (orderDetails != null && !orderDetails.isEmpty()) {
					OrdOrderDetail orderDetail = orderDetails.get(0);
					UpdateWrapper<OrdOrderDetail> updateWrapper = new UpdateWrapper<>();
					//发货单场合
					if("0".equals(isShipRet)) {
						float currentShipQty = orderDetail.getShip_qty() != null ? Float.parseFloat(orderDetail.getShip_qty().toString()) : 0;
						float invoiceShipQty = item.get("ship_qty") != null ? Float.parseFloat(item.get("ship_qty").toString()) : 0;
						float totalQty = currentShipQty - invoiceShipQty;
						logger.info("totalQty=" + totalQty);
						updateWrapper.eq("uu_id", s_uu_id);
						updateWrapper.eq("uk_order_no", up_bill);
						updateWrapper.eq("uk_line_no", up_bill_line);
						//updateWrapper.eq("is_ship", "1");
						if (totalQty == 0) {
							updateWrapper.set("is_ship", "0");
						}
						updateWrapper.set("ship_qty", totalQty);

					} else{
						//退货单场合
						float returnStockQty = orderDetail.getReturn_stock_qty() != null ? Float.parseFloat(orderDetail.getReturn_stock_qty().toString()) : 0;
						float invoiceShipQty = item.get("ship_qty") != null ? Float.parseFloat(item.get("ship_qty").toString()) : 0;
						float totalQty = returnStockQty + invoiceShipQty;  //ship_qty页面传来是负数，加了就减少
						logger.info("totalQty=" + totalQty);

						updateWrapper.eq("uu_id", s_uu_id);
						updateWrapper.eq("uk_order_no", up_bill);
						updateWrapper.eq("uk_line_no", up_bill_line);

						if (totalQty == 0) {
							updateWrapper.set("is_stock_return", "0");
						}
						updateWrapper.set("return_stock_qty", totalQty);
					}
					ordOrderDetailService.update(null, updateWrapper);
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
	public R createBill(List<List<Map<String, Object>>> list,String isShipRet) {
		try {
			UserEntity userEntity = ShiroUtils.getUserEntity();
			List<Map<String, Object>> orderData = list.get(0);
			for(Map<String,Object> orderMap:orderData) {
				String salesOrg = orderMap.get("sales_org").toString();
				String ukOrderNo = orderMap.get("uk_order_no").toString();
				String uuId = orderMap.get("uu_id").toString();

				QueryWrapper<OrdOrderHead> headQueryWrapper = new QueryWrapper<>();
				headQueryWrapper.eq("sales_org", salesOrg);
				headQueryWrapper.eq("uk_order_no", ukOrderNo);
				List<OrdOrderHead> orderHeads = ordOrderHeadService.list(headQueryWrapper);

				if (orderHeads == null || orderHeads.isEmpty()) {
					return R.error("未找到符合条件的订单头数据!");
				}

				/*QueryWrapper<OrdInvoiceHead> headQueryWrapper2 = new QueryWrapper<>();
				headQueryWrapper2.eq("sales_org", salesOrg);
				headQueryWrapper2.eq("up_bill", ukOrderNo);
				List<OrdInvoiceHead> invoiceHeads = ordInvoiceHeadService.list(headQueryWrapper2);
				if (!(invoiceHeads == null || invoiceHeads.isEmpty())) {
					return R.error("销售订单:"+ukOrderNo+"已经生成过发货单，不能重复生成!");
				}*/

				String maxOrderNoSql = "SELECT CONCAT(DATE_FORMAT(CURDATE(),'%Y%m%d'),LPAD(IFNULL((SELECT MAX(CAST(SUBSTRING(uk_ship_no, 9, 4) AS SIGNED)) FROM ord_invoice_head WHERE IFNULL(is_ship_ret,0)='"+isShipRet+"' and SUBSTRING(uk_ship_no, 1, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')), 0) + 1,4, '0')) AS uk_ship_no";
				Object maxOrderNoObj = sqlService.getOnef(maxOrderNoSql);
				String newUkShipNo = "1";
				if (maxOrderNoObj != null) {
					newUkShipNo = maxOrderNoObj.toString();
				}

				for (OrdOrderHead orderHead : orderHeads) {
					OrdInvoiceHead invoiceHead = new OrdInvoiceHead();
					invoiceHead.setS_uu_id(orderHead.getUu_id().toString());
					invoiceHead.setUk_ship_no(newUkShipNo);
					invoiceHead.setUp_bill(orderHead.getUk_order_no());
					invoiceHead.setBill_status("0");
					invoiceHead.setIs_ship_ret(isShipRet);

					invoiceHead.setGroup_id(orderHead.getGroup_id());
					invoiceHead.setSales_org(orderHead.getSales_org());
					invoiceHead.setCustomer_id(orderHead.getCustomer_id());
					invoiceHead.setCustomer_code(orderHead.getCustomer_code());
					invoiceHead.setCustomer_name(orderHead.getCustomer_name());
					invoiceHead.setRole_id(orderHead.getRole_id());
					invoiceHead.setRole_code(orderHead.getRole_code());
					invoiceHead.setRole_name(orderHead.getRole_name());
					invoiceHead.setDept_id(orderHead.getDept_id());
					invoiceHead.setDept_code(orderHead.getDept_code());
					invoiceHead.setDept_name(orderHead.getDept_name());
					invoiceHead.setSales_person_id(orderHead.getSales_person_id());
					invoiceHead.setSales_person_code(orderHead.getSales_person_code());
					invoiceHead.setSales_person_man(orderHead.getSales_person_man());
					invoiceHead.setBase_currency_id(orderHead.getBase_currency_id());
					invoiceHead.setBase_currency_code(orderHead.getBase_currency_code());
					invoiceHead.setBase_currency_desc(orderHead.getBase_currency_desc());
					invoiceHead.setBase_tax_money(orderHead.getBase_tax_money());
					invoiceHead.setType_code(orderHead.getType_code());
					invoiceHead.setTransport_type(orderHead.getTransport_type());
					invoiceHead.setAddress(orderHead.getAddress());
					invoiceHead.setPay_way(orderHead.getPay_way());
					invoiceHead.setSettlement(orderHead.getSettlement());
					invoiceHead.setApprover_user_id(orderHead.getApprover_user_id());
					invoiceHead.setApprover_user_code(orderHead.getApprover_user_code());
					invoiceHead.setApprover_user_man(orderHead.getApprover_user_man());
					invoiceHead.setApprover_time(orderHead.getApprover_time());
					invoiceHead.setApprover_opinion(orderHead.getApprover_opinion());
					invoiceHead.setBill_date(orderHead.getBill_date());
					invoiceHead.setNotes(orderHead.getNotes());
					invoiceHead.setCreate_user_id(userEntity.getUsername());
					//invoiceHead.setCreate_user_id(orderHead.getCreate_user_id());
					//invoiceHead.setModify_user_id(orderHead.getModify_user_id());
					//invoiceHead.setFlow_id(orderHead.getFlow_id());
					//invoiceHead.setIs_deleted(orderHead.getIs_deleted());

					ordInvoiceHeadService.save(invoiceHead);

					QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
					//detailQueryWrapper.eq("sales_org", salesOrg);
					detailQueryWrapper.eq("uk_order_uuid", orderHead.getUu_id());
					detailQueryWrapper.eq("uk_order_no", ukOrderNo);
					List<OrdOrderDetail> orderDetails = ordOrderDetailService.list(detailQueryWrapper);

					if (orderDetails != null && !orderDetails.isEmpty()) {
						int lineNo = 1;
						for (OrdOrderDetail orderDetail : orderDetails) {
							String currencyCode= orderDetail.getCurrency_code();
							int deciPrice=2;
							int deciAmt=2;
							String deciSql = "SELECT decimal_digits_price,decimal_digits_amt FROM epd_currency WHERE currency_code='"+currencyCode+"'";
							Map<String,Object> deciArray = sqlService.getOne(deciSql);
							if (deciArray != null) {
								deciPrice = Integer.parseInt(deciArray.get("decimal_digits_price").toString());
								deciAmt = Integer.parseInt(deciArray.get("decimal_digits_amt").toString());
							}
							//根据单位和主单位取数量小数位数
							/*String ordUnitId= orderDetail.getOrd_unit_id();
							int deciS=2;
							String deciSql1 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+ordUnitId+"'";
							Map<String,Object> deciArray1 = sqlService.getOne(deciSql1);
							if (deciArray1 != null) {
								deciS = Integer.parseInt(deciArray1.get("decimal_digits").toString());
							}*/
							String mainUnitId= orderDetail.getMain_unit_id();
							int deciM=2;
							String deciSql2 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+mainUnitId+"'";
							Map<String,Object> deciArray2 = sqlService.getOne(deciSql2);
							if (deciArray2 != null) {
								deciM = Integer.parseInt(deciArray2.get("decimal_digits").toString());
							}
							//可发数量等于上游单据的订单数量-累计发货数量+累计退库数量
							/*float purQty = orderDetail.getPur_qty() != null ? orderDetail.getPur_qty() : 0;
							float shipQty = orderDetail.getShip_qty() != null ? orderDetail.getShip_qty() : 0;
							float returnStockQty = orderDetail.getReturn_stock_qty() != null ? orderDetail.getReturn_stock_qty() : 0;
							float storyQty = orderDetail.getStore_qty() != null ? orderDetail.getStore_qty() : 0;
							float shipQtyCan = 0;*/

							// 采购数量
							BigDecimal purQty = orderDetail.getPur_qty() != null? new BigDecimal(orderDetail.getPur_qty().toString()) : BigDecimal.ZERO;
							// 发货数量
							BigDecimal shipQty = orderDetail.getShip_qty() != null? new BigDecimal(orderDetail.getShip_qty().toString()): BigDecimal.ZERO;
							// 退货入库数量
							BigDecimal returnStockQty = orderDetail.getReturn_stock_qty() != null ? new BigDecimal(orderDetail.getReturn_stock_qty().toString()): BigDecimal.ZERO;
							// 入库数量
							BigDecimal storyQty = orderDetail.getStore_qty() != null ? new BigDecimal(orderDetail.getStore_qty().toString()): BigDecimal.ZERO;
							// 可发货数量（初始化为 0）
							BigDecimal shipQtyCan = BigDecimal.ZERO;

							if ("0".equals(isShipRet)) {
								/*shipQtyCan = purQty - shipQty + returnStockQty;
								if(shipQtyCan==0) {
									continue;
								}*/
								// 可发货数量 = 采购数量 - 发货数量 + 退货入库数量
								shipQtyCan = purQty.subtract(shipQty).add(returnStockQty);

								// 判断是否为 0
								if (shipQtyCan.compareTo(BigDecimal.ZERO) == 0) {
									continue;
								}
							}
							if ("1".equals(isShipRet)) {
								/*shipQtyCan = storyQty - returnStockQty;
								if(shipQtyCan==0) {
									continue;
								}
								shipQtyCan = -shipQtyCan;*/
								// 可发货数量 = 入库数量 - 退货入库数量
								shipQtyCan = storyQty.subtract(returnStockQty);
								// 判断是否为 0
								if (shipQtyCan.compareTo(BigDecimal.ZERO) == 0) {
									continue;
								}
								// 取负
								shipQtyCan = shipQtyCan.negate();
							}
							/*float unitPrice=Float.parseFloat(orderDetail.getUnit_price().toString());
							float noTaxPrice =Float.parseFloat(orderDetail.getNo_tax_price().toString());
							float taxRatio=(float) (Math.round(((unitPrice - noTaxPrice) / noTaxPrice * 100 )* 100)) / 100;
							float exchangeRate=Float.parseFloat(orderDetail.getExchange_rate().toString());
							float changeRate=Float.parseFloat(orderDetail.getChange_rate().toString());*/

							// 含税单价
							BigDecimal unitPrice = roundToDecimal(new BigDecimal(orderDetail.getUnit_price().toString()),deciPrice );
							// 不含税单价
							BigDecimal noTaxPrice = roundToDecimal(new BigDecimal(orderDetail.getNo_tax_price().toString()),deciPrice );
							// 税率 = (含税单价 - 不含税单价) / 不含税单价 × 100
							BigDecimal taxRatio = roundToDecimal(unitPrice.subtract(noTaxPrice).divide(noTaxPrice, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100")),deciPrice );
							// 汇率
							BigDecimal exchangeRate = new BigDecimal(orderDetail.getExchange_rate().toString());
							// 换算率
							BigDecimal changeRate = new BigDecimal(orderDetail.getChange_rate().toString());

							/*float noTaxAmount = (float) (Math.round((noTaxPrice * shipQtyCan) * 100)) / 100;
							float taxAmount  = (float) (Math.round((noTaxAmount * (taxRatio / 100)) * 100)) / 100;
							float amt  = (float) (Math.round((unitPrice * shipQtyCan) * 100)) / 100;
							float baseTaxPrice  = (float) (Math.round((unitPrice * exchangeRate) * 100)) / 100;
							float baseTaxMoney  = (float) (Math.round((baseTaxPrice * shipQtyCan) * 100)) / 100;
							float mainQty  =shipQtyCan*changeRate;*/

							// 不含税金额
							BigDecimal noTaxAmount = roundToDecimal(noTaxPrice.multiply(shipQtyCan),deciAmt );
							// 税额 = 不含税金额 × (税率 / 100)
							BigDecimal taxAmount = roundToDecimal(noTaxAmount.multiply(taxRatio.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)),deciAmt );
							// 价税合计 = 含税单价 × 数量
							BigDecimal amt = roundToDecimal(unitPrice.multiply(shipQtyCan),deciAmt );
							// 本位币含税单价 = 含税单价 × 汇率
							BigDecimal baseTaxPrice = roundToDecimal(unitPrice.multiply(exchangeRate),deciPrice );
							// 本位币含税金额 = 本位币含税单价 × 数量
							BigDecimal baseTaxMoney = roundToDecimal(baseTaxPrice.multiply(shipQtyCan),deciAmt );
							// 主数量 = 数量 × 换算率
							BigDecimal mainQty = roundToDecimal(shipQtyCan.multiply(changeRate),deciM );

							OrdInvoiceDetail invoiceDetail = new OrdInvoiceDetail();
							invoiceDetail.setS_uu_id(orderDetail.getUu_id().toString());
							invoiceDetail.setUk_ship_uuid(invoiceHead.getUu_id().toString());
							invoiceDetail.setUk_ship_no(newUkShipNo);
							invoiceDetail.setUk_line_no(String.valueOf(lineNo++));
							invoiceDetail.setUp_bill(orderDetail.getUk_order_no());
							invoiceDetail.setUp_bill_line(orderDetail.getUk_line_no());

							invoiceDetail.setShip_qty(shipQtyCan);
							invoiceDetail.setArrive_qty(shipQtyCan);

							invoiceDetail.setLine_status("0");
							invoiceDetail.setGroup_id(orderDetail.getGroup_id());
							invoiceDetail.setItem_id(orderDetail.getItem_id());
							invoiceDetail.setItem_code(orderDetail.getItem_code());
							invoiceDetail.setItem_name(orderDetail.getItem_name());
							invoiceDetail.setItem_spec(orderDetail.getItem_spec());
							invoiceDetail.setItem_model(orderDetail.getItem_model());
							invoiceDetail.setShip_date(orderDetail.getShip_date());
							invoiceDetail.setOrd_unit_id(orderDetail.getOrd_unit_id());
							invoiceDetail.setOrd_unit_name(orderDetail.getOrd_unit_name());
							invoiceDetail.setAmt(amt);
							invoiceDetail.setSend_org(orderDetail.getSend_org());
							invoiceDetail.setWarehouse_id(orderDetail.getWarehouse_id());
							invoiceDetail.setWarehouse_code(orderDetail.getWarehouse_code());
							invoiceDetail.setWarehouse_name(orderDetail.getWarehouse_name());
							invoiceDetail.setChange_rate(orderDetail.getChange_rate());
							invoiceDetail.setMain_qty(mainQty);
							invoiceDetail.setMain_unit_id(orderDetail.getMain_unit_id());
							invoiceDetail.setMain_unit_name(orderDetail.getMain_unit_name());
							invoiceDetail.setTax_code(orderDetail.getTax_code());
							invoiceDetail.setTax_name(orderDetail.getTax_name());
							invoiceDetail.setExchange_rate(orderDetail.getExchange_rate());
							invoiceDetail.setCurrency_id(orderDetail.getCurrency_id());
							invoiceDetail.setCurrency_code(orderDetail.getCurrency_code());
							invoiceDetail.setCurrency_desc(orderDetail.getCurrency_desc());
							invoiceDetail.setUnit_price(orderDetail.getUnit_price());
							invoiceDetail.setTax_amount(taxAmount);
							invoiceDetail.setNo_tax_price(noTaxPrice);
							invoiceDetail.setNo_tax_amount(noTaxAmount);
							invoiceDetail.setBase_currency_id(orderDetail.getBase_currency_id());
							invoiceDetail.setBase_currency_code(orderDetail.getBase_currency_code());
							invoiceDetail.setBase_currency_desc(orderDetail.getBase_currency_desc());
							invoiceDetail.setBase_tax_price(baseTaxPrice);
							invoiceDetail.setBase_tax_money(baseTaxMoney);
							invoiceDetail.setGift(orderDetail.getGift());
							invoiceDetail.setProject_id(orderDetail.getProject_id());
							invoiceDetail.setProject_no(orderDetail.getProject_no());
							invoiceDetail.setProject_name(orderDetail.getProject_name());
							invoiceDetail.setWork_order(orderDetail.getWork_order());
							invoiceDetail.setBatch_no(orderDetail.getBatch_no());
							invoiceDetail.setNotes(orderDetail.getNotes());
							invoiceDetail.setManufacturer(orderDetail.getManufacturer());
							invoiceDetail.setProd_date(orderDetail.getProd_date());
							invoiceDetail.setCreate_user_id(userEntity.getUsername());
							//invoiceDetail.setCreate_user_id(orderDetail.getCreate_user_id());
							//invoiceDetail.setModify_user_id(orderDetail.getModify_user_id());
							//invoiceDetail.setFlow_id(orderDetail.getFlow_id());
							//invoiceDetail.setIs_deleted(orderDetail.getIs_deleted());

							ordInvoiceDetailService.save(invoiceDetail);

							UpdateWrapper<OrdOrderDetail> orderDetailUpdateWrapper = new UpdateWrapper<>();
							orderDetailUpdateWrapper.eq("uk_order_no", ukOrderNo);
							orderDetailUpdateWrapper.eq("uk_order_uuid", uuId);
							orderDetailUpdateWrapper.eq("uk_line_no", orderDetail.getUk_line_no());

							if ("0".equals(isShipRet)) {
								//float currentShipQty = orderDetail.getShip_qty() != null ? orderDetail.getShip_qty() : 0;
								//float totalQty = currentShipQty + shipQtyCan;
								// 当前已发货数量
								BigDecimal currentShipQty = orderDetail.getShip_qty() != null? new BigDecimal(orderDetail.getShip_qty().toString()): BigDecimal.ZERO;
								// 累计发货数量 = 当前已发货数量 + 可发货数量
								BigDecimal totalQty = currentShipQty.add(shipQtyCan);

								orderDetailUpdateWrapper.set("is_ship", "1");
								orderDetailUpdateWrapper.set("ship_qty", totalQty);
							} else if ("1".equals(isShipRet)) {
								//float currentShipQty = orderDetail.getReturn_stock_qty() != null ? orderDetail.getReturn_stock_qty() : 0;
								//float totalQty = currentShipQty + Math.abs(shipQtyCan);
								// 当前退货入库数量
								BigDecimal currentShipQty = orderDetail.getReturn_stock_qty() != null? new BigDecimal(orderDetail.getReturn_stock_qty().toString()): BigDecimal.ZERO;
								// 累计数量 = 当前退货入库数量 + |可发货数量|
								BigDecimal totalQty = currentShipQty.add(shipQtyCan.abs());
								orderDetailUpdateWrapper.set("is_stock_return", "1");
								orderDetailUpdateWrapper.set("return_stock_qty", totalQty);
							}
							ordOrderDetailService.update(null, orderDetailUpdateWrapper);
						}
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
	public R createBillDetail(List<List<Map<String, Object>>> list,String isShipRet,String ukShipNo) {
		try {
			UserEntity userEntity = ShiroUtils.getUserEntity();
			List<Map<String, Object>> orderData = list.get(0);
			Map<String,Object> orderMap0=orderData.get(0);
			String salesOrg = orderMap0.get("sales_org").toString();
			String ukOrderNo = orderMap0.get("uk_order_no").toString();
			String uuId = orderMap0.get("uk_order_uuid").toString();
			//发货单头生成
			QueryWrapper<OrdOrderHead> headQueryWrapper = new QueryWrapper<>();
			headQueryWrapper.eq("sales_org", salesOrg);
			headQueryWrapper.eq("uk_order_no", ukOrderNo);
			List<OrdOrderHead> orderHeads = ordOrderHeadService.list(headQueryWrapper);

			if (orderHeads == null || orderHeads.isEmpty()) {
				return R.error("未找到符合条件的订单头数据!");
			}
			QueryWrapper<OrdInvoiceHead> headQueryWrapperShip = new QueryWrapper<>();
			headQueryWrapperShip.eq("sales_org", salesOrg);
			headQueryWrapperShip.eq("up_bill", ukOrderNo);
			headQueryWrapperShip.eq("uk_ship_no", ukShipNo);
			headQueryWrapperShip.eq("is_ship_ret", isShipRet);
			List<OrdInvoiceHead> invoiceHeads = ordInvoiceHeadService.list(headQueryWrapperShip);

			OrdOrderHead orderHead = orderHeads.get(0);
			OrdInvoiceHead invoiceHead = new OrdInvoiceHead();
			String newUkShipNo = "";
			if("".equals(ukShipNo)) {
				String maxOrderNoSql = "SELECT CONCAT(DATE_FORMAT(CURDATE(),'%Y%m%d'),LPAD(IFNULL((SELECT MAX(CAST(SUBSTRING(uk_ship_no, 9, 4) AS SIGNED)) FROM ord_invoice_head WHERE IFNULL(is_ship_ret,0)='" + isShipRet + "' and SUBSTRING(uk_ship_no, 1, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')), 0) + 1,4, '0')) AS uk_ship_no";
				Object maxOrderNoObj = sqlService.getOnef(maxOrderNoSql);
				newUkShipNo = "1";
				if (maxOrderNoObj != null) {
					newUkShipNo = maxOrderNoObj.toString();
				}

				invoiceHead.setS_uu_id(orderHead.getUu_id().toString());
				invoiceHead.setUk_ship_no(newUkShipNo);
				invoiceHead.setUp_bill(orderHead.getUk_order_no());
				invoiceHead.setBill_status("0");
				invoiceHead.setIs_ship_ret(isShipRet);

				invoiceHead.setGroup_id(orderHead.getGroup_id());
				invoiceHead.setSales_org(orderHead.getSales_org());
				invoiceHead.setCustomer_id(orderHead.getCustomer_id());
				invoiceHead.setCustomer_code(orderHead.getCustomer_code());
				invoiceHead.setCustomer_name(orderHead.getCustomer_name());
				invoiceHead.setRole_id(orderHead.getRole_id());
				invoiceHead.setRole_code(orderHead.getRole_code());
				invoiceHead.setRole_name(orderHead.getRole_name());
				invoiceHead.setDept_id(orderHead.getDept_id());
				invoiceHead.setDept_code(orderHead.getDept_code());
				invoiceHead.setDept_name(orderHead.getDept_name());
				invoiceHead.setSales_person_id(orderHead.getSales_person_id());
				invoiceHead.setSales_person_code(orderHead.getSales_person_code());
				invoiceHead.setSales_person_man(orderHead.getSales_person_man());
				invoiceHead.setBase_currency_id(orderHead.getBase_currency_id());
				invoiceHead.setBase_currency_code(orderHead.getBase_currency_code());
				invoiceHead.setBase_currency_desc(orderHead.getBase_currency_desc());
				invoiceHead.setBase_tax_money(orderHead.getBase_tax_money());
				invoiceHead.setType_code(orderHead.getType_code());
				invoiceHead.setTransport_type(orderHead.getTransport_type());
				invoiceHead.setAddress(orderHead.getAddress());
				invoiceHead.setPay_way(orderHead.getPay_way());
				invoiceHead.setSettlement(orderHead.getSettlement());
				invoiceHead.setApprover_user_id(orderHead.getApprover_user_id());
				invoiceHead.setApprover_user_code(orderHead.getApprover_user_code());
				invoiceHead.setApprover_user_man(orderHead.getApprover_user_man());
				invoiceHead.setApprover_time(orderHead.getApprover_time());
				invoiceHead.setApprover_opinion(orderHead.getApprover_opinion());
				invoiceHead.setBill_date(orderHead.getBill_date());
				invoiceHead.setNotes(orderHead.getNotes());
				invoiceHead.setCreate_user_id(userEntity.getUsername());
			/*invoiceHead.setCreate_user_id(orderHead.getCreate_user_id());
			invoiceHead.setModify_user_id(orderHead.getModify_user_id());
			invoiceHead.setFlow_id(orderHead.getFlow_id());
			invoiceHead.setIs_deleted(orderHead.getIs_deleted());
			*/
				ordInvoiceHeadService.save(invoiceHead);
			}
			int lineNo = 1;
			// 2. 计算新行号
			if(!"".equals(ukShipNo)) {
				QueryWrapper<OrdInvoiceDetail> headQueryWrapperShipDetail = new QueryWrapper<>();
				headQueryWrapperShipDetail.eq("up_bill", ukOrderNo);
				headQueryWrapperShipDetail.eq("uk_ship_no", ukShipNo);
				headQueryWrapperShipDetail.eq("uk_ship_uuid", invoiceHeads.get(0).getUu_id());
				headQueryWrapperShipDetail.select("IFNULL(MAX(uk_line_no), 0) AS uk_line_no");
				List<OrdInvoiceDetail> shipDetails = ordInvoiceDetailService.list(headQueryWrapperShipDetail);
				if (!shipDetails.isEmpty() && shipDetails.get(0).getUk_line_no() != null) {
					lineNo = Integer.valueOf(shipDetails.get(0).getUk_line_no());
				}
				lineNo = lineNo + 1;
			}
			for(Map<String,Object> orderMap:orderData) {
				//根据货币取单价和金额小数位数
				String currencyCode= orderMap.get("currency_code").toString();
				int deciPrice=2;
				int deciAmt=2;
				String deciSql = "SELECT decimal_digits_price,decimal_digits_amt FROM epd_currency WHERE currency_code='"+currencyCode+"'";
				Map<String,Object> deciArray = sqlService.getOne(deciSql);
				if (deciArray != null) {
					deciPrice = Integer.parseInt(deciArray.get("decimal_digits_price").toString());
					deciAmt = Integer.parseInt(deciArray.get("decimal_digits_amt").toString());
				}
				//根据单位和主单位取数量小数位数
				/*String ordUnitId= orderMap.get("ord_unit_id").toString();
				int deciS=2;
				String deciSql1 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+ordUnitId+"'";
				Map<String,Object> deciArray1 = sqlService.getOne(deciSql1);
				if (deciArray1 != null) {
					deciS = Integer.parseInt(deciArray1.get("decimal_digits").toString());
				}*/
				String mainUnitId= orderMap.get("main_unit_id").toString();;
				int deciM=2;
				String deciSql2 = "SELECT decimal_digits FROM epd_unit_measure WHERE unit_id='"+mainUnitId+"'";
				Map<String,Object> deciArray2 = sqlService.getOne(deciSql2);
				if (deciArray2 != null) {
					deciM = Integer.parseInt(deciArray2.get("decimal_digits").toString());
				}
				//float shipQtyCur=Float.parseFloat(orderMap.get("ship_qty_cur").toString());
				// 当前发货数量
				BigDecimal shipQtyCur = new BigDecimal(orderMap.get("ship_qty_cur").toString());

				QueryWrapper<OrdOrderDetail> detailQueryWrapper = new QueryWrapper<>();
				detailQueryWrapper.eq("uk_order_uuid", uuId);
				detailQueryWrapper.eq("uk_order_no", ukOrderNo);
				detailQueryWrapper.eq("uk_line_no", orderMap.get("uk_line_no"));
				List<OrdOrderDetail> orderDetails = ordOrderDetailService.list(detailQueryWrapper);
				OrdOrderDetail orderDetail =orderDetails.get(0);

				/*float unitPrice=Float.parseFloat(orderDetail.getUnit_price().toString());
				float noTaxPrice =Float.parseFloat(orderDetail.getNo_tax_price().toString());
				float taxRatio=(float) (Math.round(((unitPrice - noTaxPrice) / noTaxPrice * 100 )* 100)) / 100;
				float exchangeRate=Float.parseFloat(orderDetail.getExchange_rate().toString());
				float changeRate=Float.parseFloat(orderDetail.getChange_rate().toString());*/
				// 含税单价
				BigDecimal unitPrice = roundToDecimal(new BigDecimal(orderDetail.getUnit_price().toString()),deciPrice );
				// 不含税单价
				BigDecimal noTaxPrice = roundToDecimal(new BigDecimal(orderDetail.getNo_tax_price().toString()),deciPrice );
				// 税率 = (含税单价 - 不含税单价) / 不含税单价 × 100
				BigDecimal taxRatio = roundToDecimal(unitPrice.subtract(noTaxPrice).divide(noTaxPrice, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100")),         deciPrice );
				// 汇率
				BigDecimal exchangeRate = new BigDecimal(orderDetail.getExchange_rate().toString());
				// 换算率
				BigDecimal changeRate = new BigDecimal(orderDetail.getChange_rate().toString());

				/*float noTaxAmount = (float) (Math.round((noTaxPrice * shipQtyCur) * 100)) / 100;
				float taxAmount  = (float) (Math.round((noTaxAmount * (taxRatio / 100)) * 100)) / 100;
				float amt  = (float) (Math.round((unitPrice * shipQtyCur) * 100)) / 100;
				float baseTaxPrice  = (float) (Math.round((unitPrice * exchangeRate) * 100)) / 100;
				float baseTaxMoney  = (float) (Math.round((baseTaxPrice * shipQtyCur) * 100)) / 100;
				float mainQty  =shipQtyCur*changeRate;*/

				// 不含税金额
				BigDecimal noTaxAmount = roundToDecimal(noTaxPrice.multiply(shipQtyCur),deciAmt );
				// 税额 = 不含税金额 × (税率 / 100)
				BigDecimal taxAmount = roundToDecimal(noTaxAmount.multiply(taxRatio.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)),deciAmt );
				// 价税合计 = 含税单价 × 数量
				BigDecimal amt = roundToDecimal(unitPrice.multiply(shipQtyCur),deciAmt );
				// 本位币含税单价 = 含税单价 × 汇率
				BigDecimal baseTaxPrice = roundToDecimal(unitPrice.multiply(exchangeRate),deciPrice );
				// 本位币含税金额 = 本位币含税单价 × 数量
				BigDecimal baseTaxMoney = roundToDecimal(baseTaxPrice.multiply(shipQtyCur),deciAmt );
				// 主数量 = 数量 × 换算率
				BigDecimal mainQty = roundToDecimal(shipQtyCur.multiply(changeRate),deciM );

				OrdInvoiceDetail invoiceDetail = new OrdInvoiceDetail();
				invoiceDetail.setS_uu_id(orderDetail.getUu_id().toString());
				if("".equals(ukShipNo)) {
					invoiceDetail.setUk_ship_uuid(invoiceHead.getUu_id().toString());
					invoiceDetail.setUk_ship_no(newUkShipNo);
					invoiceDetail.setUp_bill(orderDetail.getUk_order_no());
				} else {
					invoiceDetail.setUk_ship_uuid(invoiceHeads.get(0).getUu_id().toString());
					invoiceDetail.setUk_ship_no(ukShipNo);
					invoiceDetail.setUp_bill(invoiceHeads.get(0).getUp_bill());
				}

				invoiceDetail.setUk_line_no(String.valueOf(lineNo++));
				invoiceDetail.setUp_bill_line(orderDetail.getUk_line_no());

				invoiceDetail.setShip_qty(shipQtyCur);
				invoiceDetail.setArrive_qty(shipQtyCur);

				invoiceDetail.setLine_status("0");
				invoiceDetail.setGroup_id(orderDetail.getGroup_id());
				invoiceDetail.setItem_id(orderDetail.getItem_id());
				invoiceDetail.setItem_code(orderDetail.getItem_code());
				invoiceDetail.setItem_name(orderDetail.getItem_name());
				invoiceDetail.setItem_spec(orderDetail.getItem_spec());
				invoiceDetail.setItem_model(orderDetail.getItem_model());
				invoiceDetail.setShip_date(orderDetail.getShip_date());
				invoiceDetail.setOrd_unit_id(orderDetail.getOrd_unit_id());
				invoiceDetail.setOrd_unit_name(orderDetail.getOrd_unit_name());
				invoiceDetail.setAmt(amt);
				invoiceDetail.setSend_org(orderDetail.getSend_org());
				invoiceDetail.setWarehouse_id(orderDetail.getWarehouse_id());
				invoiceDetail.setWarehouse_code(orderDetail.getWarehouse_code());
				invoiceDetail.setWarehouse_name(orderDetail.getWarehouse_name());
				invoiceDetail.setChange_rate(orderDetail.getChange_rate());
				invoiceDetail.setMain_qty(mainQty);
				invoiceDetail.setMain_unit_id(orderDetail.getMain_unit_id());
				invoiceDetail.setMain_unit_name(orderDetail.getMain_unit_name());
				invoiceDetail.setTax_code(orderDetail.getTax_code());
				invoiceDetail.setTax_name(orderDetail.getTax_name());
				invoiceDetail.setExchange_rate(orderDetail.getExchange_rate());
				invoiceDetail.setCurrency_id(orderDetail.getCurrency_id());
				invoiceDetail.setCurrency_code(orderDetail.getCurrency_code());
				invoiceDetail.setCurrency_desc(orderDetail.getCurrency_desc());

				invoiceDetail.setUnit_price(orderDetail.getUnit_price());
				invoiceDetail.setTax_amount(taxAmount);
				invoiceDetail.setNo_tax_price(noTaxPrice);
				invoiceDetail.setNo_tax_amount(noTaxAmount);
				invoiceDetail.setBase_currency_id(orderDetail.getBase_currency_id());
				invoiceDetail.setBase_currency_code(orderDetail.getBase_currency_code());
				invoiceDetail.setBase_currency_desc(orderDetail.getBase_currency_desc());
				invoiceDetail.setBase_tax_price(baseTaxPrice);
				invoiceDetail.setBase_tax_money(baseTaxMoney);

				invoiceDetail.setGift(orderDetail.getGift());
				invoiceDetail.setProject_id(orderDetail.getProject_id());
				invoiceDetail.setProject_no(orderDetail.getProject_no());
				invoiceDetail.setProject_name(orderDetail.getProject_name());
				invoiceDetail.setWork_order(orderDetail.getWork_order());
				invoiceDetail.setBatch_no(orderDetail.getBatch_no());
				invoiceDetail.setNotes(orderDetail.getNotes());
				invoiceDetail.setManufacturer(orderDetail.getManufacturer());
				invoiceDetail.setProd_date(orderDetail.getProd_date());
				invoiceDetail.setCreate_user_id(userEntity.getUsername());
				/*invoiceDetail.setCreate_user_id(orderDetail.getCreate_user_id());
				invoiceDetail.setModify_user_id(orderDetail.getModify_user_id());
				invoiceDetail.setFlow_id(orderDetail.getFlow_id());
				invoiceDetail.setIs_deleted(orderDetail.getIs_deleted());*/

				ordInvoiceDetailService.save(invoiceDetail);

				UpdateWrapper<OrdOrderDetail> orderDetailUpdateWrapper = new UpdateWrapper<>();
				orderDetailUpdateWrapper.eq("uk_order_no", ukOrderNo);
				orderDetailUpdateWrapper.eq("uk_order_uuid", uuId);
				orderDetailUpdateWrapper.eq("uk_line_no", orderDetail.getUk_line_no());

				if ("0".equals(isShipRet)) {
					//float currentShipQty = orderDetail.getShip_qty() != null ? orderDetail.getShip_qty() : 0;
					//float totalQty = currentShipQty + shipQtyCur;
					// 当前已发货数量
					BigDecimal currentShipQty = orderDetail.getShip_qty() != null? new BigDecimal(orderDetail.getShip_qty().toString()): BigDecimal.ZERO;
					// 累计发货数量 = 当前已发货数量 + 本次发货数量
					BigDecimal totalQty = currentShipQty.add(shipQtyCur);
					orderDetailUpdateWrapper.set("is_ship", "1");
					orderDetailUpdateWrapper.set("ship_qty", totalQty);
				} else if ("1".equals(isShipRet)) {
					//float currentShipQty = orderDetail.getReturn_stock_qty() != null ? orderDetail.getReturn_stock_qty() : 0;
					//float totalQty = currentShipQty + Math.abs(shipQtyCur);
					// 当前退货入库数量
					BigDecimal currentShipQty = orderDetail.getReturn_stock_qty() != null? new BigDecimal(orderDetail.getReturn_stock_qty().toString()): BigDecimal.ZERO;
					// 累计数量 = 当前退货入库数量 + |本次发货数量|
					BigDecimal totalQty = currentShipQty.add(shipQtyCur.abs());
					orderDetailUpdateWrapper.set("is_stock_return", "1");
					orderDetailUpdateWrapper.set("return_stock_qty", totalQty);
				}
				ordOrderDetailService.update(null, orderDetailUpdateWrapper);
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
						if(queryKey.get("field").toString().equals("uk_ship_no") || queryKey.get("field").toString().equals("create_user_id") || queryKey.get("field").toString().equals("create_time")) {
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
			String selAll = "select uk_ship_no,(select org_name from sys_org where org_code=sales_org ) as sales_org,customer_code,customer_name,role_code,role_name,sales_person_code,sales_person_man,bill_date,bill_status,bill_status_show,create_user_id,create_time,  item_code,item_name,item_spec,item_model,(select org_name from sys_org where org_code=send_org ) as send_org,ord_unit_name,ROUND(arrive_qty, 2) AS arrive_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(store_qty, 2) AS store_qty,ROUND(quality_qty, 2) AS quality_qty,ROUND(inspection_qty, 2) AS inspection_qty,work_order,project_no,project_name, sort_flag  from  (  select t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,CASE t.bill_status WHEN '0' THEN '未提交' WHEN '1' THEN '已提交' WHEN '2' THEN '审批通过' WHEN '3' THEN '驳回' WHEN '4' THEN '关闭' END AS bill_status_show,t.create_user_id,t.create_time,  t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,t2.work_order,t2.project_no,t2.project_name, 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='0' " + condStrAll + "  group BY t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,t.create_user_id,t.create_time,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,t2.work_order,t2.project_no,t2.project_name  union  select '小计','','','','','','','','','','','','',  t2.item_code,'','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='0' " + condStrAll + "  group BY t.sales_org,t2.item_code  union  select '合计','','','','','','','','','','','','',  '','','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 2 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='0' " + condStrAll + "  GROUP BY t.sales_org ) t2  order by sort_flag, t2.item_code, uk_ship_no ";
			logger.info(selAll);
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
	public R getStatBackData(List<Map<String, Object>> queryList,String offset,String size) {
		try {
			StringBuffer condStrAll = new StringBuffer();
			if (queryList != null && !queryList.isEmpty()) {
				for (Map<String, Object> queryKey : queryList) {
					if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()) {
						String condStr ="";
						String andStr =" and ";
						if(queryKey.get("field").toString().equals("uk_ship_no") || queryKey.get("field").toString().equals("create_user_id") || queryKey.get("field").toString().equals("create_time")) {
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
			String selAll = "select uk_ship_no,(select org_name from sys_org where org_code=sales_org ) as sales_org,customer_code,customer_name,role_code,role_name,sales_person_code,sales_person_man,bill_date,bill_status,bill_status_show,create_user_id,create_time,  item_code,item_name,item_spec,item_model,(select org_name from sys_org where org_code=send_org ) as send_org,ord_unit_name,ROUND(arrive_qty, 2) AS arrive_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(quality_qty, 2) AS quality_qty,ROUND(quality_qty, 2) AS quality_qty,work_order,project_no,project_name, sort_flag  from  (  select t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,CASE t.bill_status WHEN '0' THEN '未提交' WHEN '1' THEN '已提交' WHEN '2' THEN '审批通过' WHEN '3' THEN '驳回' WHEN '4' THEN '关闭' END AS bill_status_show,t.create_user_id,t.create_time,  t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,t2.work_order,t2.project_no,t2.project_name, 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='1' " + condStrAll + "  group BY t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,t.create_user_id,t.create_time,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,t2.work_order,t2.project_no,t2.project_name  union  select '小计','','','','','','','','','','','','',  t2.item_code,'','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='1' " + condStrAll + "  group BY t.sales_org,t2.item_code  union  select '合计','','','','','','','','','','','','',  '','','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 2 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='1' " + condStrAll + "  GROUP BY t.sales_org ) t2  order by sort_flag, t2.item_code, uk_ship_no ";
			logger.info(selAll);
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
			StringBuffer condStrAll = new StringBuffer();
			if (queryList != null && !queryList.isEmpty()) {
				for (Map<String, Object> queryKey : queryList) {
					if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()) {
						String condStr ="";
						String andStr =" and ";
						if(queryKey.get("field").toString().equals("uk_ship_no") || queryKey.get("field").toString().equals("create_user_id") || queryKey.get("field").toString().equals("create_time")) {
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
			String selAll = "select uk_ship_no,(select org_name from sys_org where org_code=sales_org ) as sales_org,customer_code,customer_name,role_code,role_name,sales_person_code,sales_person_man,bill_date,bill_status,bill_status_show,create_user_id,create_time,  item_code,item_name,item_spec,item_model,(select org_name from sys_org where org_code=send_org ) as send_org,ord_unit_name,ROUND(arrive_qty, 2) AS arrive_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(store_qty, 2) AS store_qty,ROUND(quality_qty, 2) AS quality_qty,ROUND(inspection_qty, 2) AS inspection_qty,work_order,project_no,project_name, sort_flag  from  (  select t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,CASE t.bill_status WHEN '0' THEN '未提交' WHEN '1' THEN '已提交' WHEN '2' THEN '审批通过' WHEN '3' THEN '驳回' WHEN '4' THEN '关闭' END AS bill_status_show,t.create_user_id,t.create_time,  t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,t2.work_order,t2.project_no,t2.project_name, 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='0' " + condStrAll + "  group BY t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,t.create_user_id,t.create_time,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,t2.work_order,t2.project_no,t2.project_name  union  select '小计','','','','','','','','','','','','',  t2.item_code,'','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='0' " + condStrAll + "  group BY t.sales_org,t2.item_code  union  select '合计','','','','','','','','','','','','',  '','','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 2 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='0' " + condStrAll + "  GROUP BY t.sales_org ) t2  order by sort_flag, t2.item_code, uk_ship_no ";
			logger.info(selAll);
			List<Map<String, Object>> allStat = sqlService.getAll(selAll);
			//int total = allStat != null ? allStat.size() : 0;
			// 2. 设置响应头
			String fileName = "发货单统计报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
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
					rowData.add(row.get("uk_ship_no"));
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
					rowData.add(row.get("arrive_qty"));
					rowData.add(row.get("ord_unit_name"));
					rowData.add(row.get("ship_qty"));
					rowData.add(row.get("store_qty"));
					rowData.add(row.get("ship_date"));
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
		head.add(Arrays.asList("可发数量"));
		head.add(Arrays.asList("单位"));
		head.add(Arrays.asList("发货数量"));
		head.add(Arrays.asList("出库数量"));
		head.add(Arrays.asList("发货日期"));
		head.add(Arrays.asList("工作令号"));
		head.add(Arrays.asList("项目名称"));
		return head;
	}
	@Override
	public void getStatDataExcelB(List<Map<String, Object>> queryList, String offset, String size, HttpServletResponse response) {
		try {
			StringBuffer condStrAll = new StringBuffer();
			if (queryList != null && !queryList.isEmpty()) {
				for (Map<String, Object> queryKey : queryList) {
					if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()) {
						String condStr ="";
						String andStr =" and ";
						if(queryKey.get("field").toString().equals("uk_ship_no") || queryKey.get("field").toString().equals("create_user_id") || queryKey.get("field").toString().equals("create_time")) {
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
			String selAll = "select uk_ship_no,(select org_name from sys_org where org_code=sales_org ) as sales_org,customer_code,customer_name,role_code,role_name,sales_person_code,sales_person_man,bill_date,bill_status,bill_status_show,create_user_id,create_time,  item_code,item_name,item_spec,item_model,(select org_name from sys_org where org_code=send_org ) as send_org,ord_unit_name,ROUND(arrive_qty, 2) AS arrive_qty,ROUND(ship_qty, 2) AS ship_qty,ROUND(store_qty, 2) AS store_qty,ROUND(quality_qty, 2) AS quality_qty,ROUND(inspection_qty, 2) AS inspection_qty,work_order,project_no,project_name, sort_flag  from  (  select t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,CASE t.bill_status WHEN '0' THEN '未提交' WHEN '1' THEN '已提交' WHEN '2' THEN '审批通过' WHEN '3' THEN '驳回' WHEN '4' THEN '关闭' END AS bill_status_show,t.create_user_id,t.create_time,  t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,t2.work_order,t2.project_no,t2.project_name, 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='1' " + condStrAll + "  group BY t.uk_ship_no,t.sales_org,t.customer_code,t.customer_name,t.role_code,t.role_name,t.sales_person_code,t.sales_person_man,t.bill_date,t.bill_status,t.create_user_id,t.create_time,t2.item_code,t2.item_name,t2.item_spec,t2.item_model,t2.send_org,t2.ord_unit_name,t2.work_order,t2.project_no,t2.project_name  union  select '小计','','','','','','','','','','','','',  t2.item_code,'','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 1 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='1' " + condStrAll + "  group BY t.sales_org,t2.item_code  union  select '合计','','','','','','','','','','','','',  '','','','','','',  sum(t2.arrive_qty) as arrive_qty,sum(t2.ship_qty) as ship_qty,sum(t2.store_qty) as store_qty,sum(t2.quality_qty) as quality_qty,sum(t2.inspection_qty) as inspection_qty,'','','', 2 as sort_flag  from ord_invoice_head t,ord_invoice_detail t2  where t.uu_id=t2.uk_ship_uuid and t.uk_ship_no=t2.uk_ship_no and IFNULL(t.is_ship_ret,0)='1' " + condStrAll + "  GROUP BY t.sales_org ) t2  order by sort_flag, t2.item_code, uk_ship_no ";
			logger.info(selAll);
			List<Map<String, Object>> allStat = sqlService.getAll(selAll);
			int total = allStat != null ? allStat.size() : 0;
			// 2. 设置响应头
			String fileName = "退库单统计报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
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
					rowData.add(row.get("uk_ship_no"));
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
					rowData.add(row.get("arrive_qty"));
					rowData.add(row.get("ord_unit_name"));
					rowData.add(row.get("ship_qty"));
					rowData.add(row.get("store_qty"));
					rowData.add(row.get("ship_date"));
					rowData.add(row.get("work_order"));
					rowData.add(row.get("project_name"));
					dataList.add(rowData);
				}
			}
			EasyExcel.write(response.getOutputStream())
					.head(getExcelHeadB())
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
	private List<List<String>> getExcelHeadB() {
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
		head.add(Arrays.asList("可退数量"));
		head.add(Arrays.asList("单位名称"));
		head.add(Arrays.asList("退库数量"));
		head.add(Arrays.asList("出库数量"));
		head.add(Arrays.asList("订单日期"));
		head.add(Arrays.asList("工作令号"));
		head.add(Arrays.asList("项目名称"));
		return head;
	}
	@Override
	@Transactional
	public R active(List<List<Map<String, Object>>> list,String isActive){
		try{
			if(list != null && !list.isEmpty()){
				for(List<Map<String, Object>> innerList : list){
					if(innerList != null && !innerList.isEmpty()){
						for(Map<String, Object> item : innerList){
							String ukShipNo = (String) item.get("uk_ship_no");
							String uuId = (String) item.get("uu_id");
							String salesOrg = (String) item.get("sales_org");
							if(ukShipNo != null && !ukShipNo.isEmpty()){
								//String bill_status = (String) item.get("bill_status");
								//关闭处理
								if("0".equals(isActive)) {
									UpdateWrapper<OrdInvoiceHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_ship_no", ukShipNo);
									headUpdateWrapper.eq("uu_id", uuId);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.set("is_active", "0");

									ordInvoiceHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdInvoiceDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_ship_no", ukShipNo);
									detailUpdateWrapper.eq("uk_ship_uuid", uuId);
									detailUpdateWrapper.set("is_active", "0");
									ordInvoiceDetailService.update(null, detailUpdateWrapper);
								} else {
									UpdateWrapper<OrdInvoiceHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_ship_no", ukShipNo);
									headUpdateWrapper.eq("uu_id", uuId);
									headUpdateWrapper.eq("sales_org", salesOrg);
									headUpdateWrapper.set("is_active", "1");

									ordInvoiceHeadMapper.update(null, headUpdateWrapper);

									UpdateWrapper<OrdInvoiceDetail> detailUpdateWrapper = new UpdateWrapper<>();
									detailUpdateWrapper.eq("uk_ship_no", ukShipNo);
									detailUpdateWrapper.eq("uk_ship_uuid", uuId);
									detailUpdateWrapper.set("is_active", "1");
									ordInvoiceDetailService.update(null, detailUpdateWrapper);
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
							String ukShipNo = (String) item.get("uk_ship_no");
							String ukOrderUuid = (String) item.get("uk_ship_uuid");
							String ukLineNo = (String) item.get("uk_line_no");
							// 关闭处理
							if("0".equals(isActive)) {
								// 更新明细行状态为已关
								UpdateWrapper<OrdInvoiceDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_ship_no", ukShipNo);
								detailUpdateWrapper.eq("uk_ship_uuid", ukOrderUuid);
								detailUpdateWrapper.eq("uk_line_no", ukLineNo);
								detailUpdateWrapper.set("is_active", "0");
								ordInvoiceDetailService.update(null, detailUpdateWrapper);

								// 检查该协议的所有明细是否都已关
								QueryWrapper<OrdInvoiceDetail> detailQueryWrapper = new QueryWrapper<>();
								detailQueryWrapper.eq("uk_ship_no", ukShipNo);
								detailQueryWrapper.eq("uk_ship_uuid", ukOrderUuid);
								detailQueryWrapper.eq("is_active", "1");
								List<OrdInvoiceDetail> uncheckedDetails = ordInvoiceDetailService.list(detailQueryWrapper);

								// 如果没有未关闭的明细，则更新协议头状态为已关
								if(uncheckedDetails == null || uncheckedDetails.isEmpty()) {
									UpdateWrapper<OrdInvoiceHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_ship_no", ukShipNo);
									headUpdateWrapper.eq("uu_id", ukOrderUuid);
									headUpdateWrapper.set("is_active", "0");
									ordInvoiceHeadMapper.update(null, headUpdateWrapper);
								}
							}

							// 启用处理
							else {
								// 更新明细行状态为启用
								UpdateWrapper<OrdInvoiceDetail> detailUpdateWrapper = new UpdateWrapper<>();
								detailUpdateWrapper.eq("uk_ship_no", ukShipNo);
								detailUpdateWrapper.eq("uk_ship_uuid", ukOrderUuid);
								detailUpdateWrapper.eq("uk_line_no", ukLineNo);
								detailUpdateWrapper.set("is_active", "1");
								ordInvoiceDetailService.update(null, detailUpdateWrapper);

								// 处理协议头表
								QueryWrapper<OrdInvoiceHead> headQueryWrapper = new QueryWrapper<>();
								headQueryWrapper.eq("uk_ship_no", ukShipNo);
								headQueryWrapper.eq("uu_id", ukOrderUuid);
								headQueryWrapper.eq("is_active", "1");
								OrdInvoiceHead shipHead = ordInvoiceHeadService.getOne(headQueryWrapper);

								// 协议头状态改完启用
								if(shipHead == null) {
									UpdateWrapper<OrdInvoiceHead> headUpdateWrapper = new UpdateWrapper<>();
									headUpdateWrapper.eq("uk_ship_no", ukShipNo);
									headUpdateWrapper.eq("uu_id", ukOrderUuid);
									headUpdateWrapper.set("is_active", "1");
									ordInvoiceHeadMapper.update(null, headUpdateWrapper);
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