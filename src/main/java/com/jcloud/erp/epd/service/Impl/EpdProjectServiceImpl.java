package com.jcloud.erp.epd.service.Impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsBase;
import com.jcloud.customer.WmsBaseData.entity.MtlItemsOrg;
import com.jcloud.customer.WmsBaseData.entity.SysOrg;
import com.jcloud.customer.WmsBaseData.service.MtlItemsOrgService;
import com.jcloud.erp.epd.entity.EpdProject;
import com.jcloud.erp.epd.entity.EpdProjectHistory;
import com.jcloud.erp.ord.entity.OrdAgreementHead;
import com.jcloud.erp.ord.entity.OrdOrderHead;
import com.jcloud.erp.ord.mapper.OrdAgreementHeadMapper;
import com.jcloud.erp.ord.mapper.OrdOrderHeadMapper;
import com.jcloud.erp.epd.mapper.EpdProjectHistoryMapper;
import com.jcloud.erp.epd.mapper.EpdProjectMapper;
import com.jcloud.erp.epd.service.EpdProjectService;
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
 * 项目信息, 业务类
 *
 */
@Service
public class EpdProjectServiceImpl extends ServiceImpl<EpdProjectMapper, EpdProject> implements EpdProjectService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final int SAVE_BATCH_SIZE = 500;

	@Autowired
	private EpdProjectMapper epdProjectMapper;
	@Autowired
	private EpdProjectHistoryMapper epdProjectHistoryMapper;
	@Autowired
	private ComUnitService comUnitService;
	@Resource
	private EpdProjectService epdProjectService;
	@Autowired
	private OrdAgreementHeadMapper ordAgreementHeadMapper;
	@Autowired
	private OrdOrderHeadMapper ordOrderHeadMapper;

	public EpdProjectServiceImpl(){}

	@Transactional
	public R addOrgItems(List<List<Map<String, Object>>> list){
		try{
			List<Map<String, Object>> orgData = list.get(0);
			List<Map<String, Object>> itemsData = list.get(1);
			List<EpdProject> insertBeans=new ArrayList<>();

			for(Map<String,Object> items:itemsData){
				EpdProject epdProject0 = baseMapper.selectOne(new QueryWrapper<EpdProject>().eq("uu_id",items.get("uu_id").toString()));
				for(Map<String,Object> orgMap:orgData){
					String projectNo = items.get("project_no").toString();
					String orgCode= orgMap.get("org_code").toString();
					/*SysOrg sys_org = sysOrgMapper.selectById(orgId);
					if(sys_org==null){
						throw new Exception(MessageUtils.get("purchase.valid","",orgId));
					}
					if(!sys_org.getOrg_type().equalsIgnoreCase("ORG")){
						throw new Exception(MessageUtils.get("E130",itemsCode,sys_org.getOrg_name()));
					}*/
					QueryWrapper<EpdProject> epdProjectWrapper = new QueryWrapper();
					epdProjectWrapper.eq("inv_org",orgCode);
					epdProjectWrapper.eq("project_No",projectNo);
					EpdProject epdProject = epdProjectMapper.selectOne(epdProjectWrapper);
					if(epdProject==null){
						epdProject = new EpdProject();
						BeanUtils.copyProperties(epdProject0,epdProject);
						epdProject.setUu_id(null);
						epdProject.setGroup_id(null);
						epdProject.setCreate_time(null);
						epdProject.setCreate_user_id(null);
						epdProject.setModify_time(null);
						epdProject.setModify_user_id(null);
						epdProject.setAssign_user_id(null);
						epdProject.setAssign_user_code(null);
						epdProject.setAssign_user_man(null);
						epdProject.setAssign_time(null);
						epdProject.setAssign_from_org(orgMap.get("userEntityorg_code").toString());
						epdProject.setProject_no(items.get("project_no").toString());
						epdProject.setProject_name(items.get("project_name").toString());
						epdProject.setProject_type(items.get("project_type").toString());
						epdProject.setItem_code(items.get("item_code").toString());
						epdProject.setItem_name(items.get("item_name").toString());
						if(items.get("remark")==null){
							epdProject.setRemark(null);
						} else {
							epdProject.setRemark(items.get("remark").toString());
						}
						epdProject.setValid_flag("1");
						epdProject.setInv_org(orgMap.get("org_code").toString());
						insertBeans.add(epdProject);
					}
				}
			}
			epdProjectService.saveBatch(insertBeans);
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
		String epdProjectInfo = (String) params.get("unitdatas");
		String inNewFlag = (String) params.get("inNewFlag");

		EpdProject epdProject = JSON.parseObject(epdProjectInfo, EpdProject.class);
		try{
			String projectNo = (String) epdProject.getProject_no();
			String invOrg = (String) epdProject.getInv_org();
			String groupId = (String) epdProject.getGroup_id();

			EpdProject existingProject = null;
			if (!"a".equals(inNewFlag)) {
				QueryWrapper<EpdProject> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("project_no", projectNo);
				queryWrapper.eq("inv_org", invOrg);
				if (groupId != null && !groupId.isEmpty()) {
					queryWrapper.eq("group_id", groupId);
				}
				existingProject = epdProjectMapper.selectOne(queryWrapper);

				if (existingProject != null) {
					EpdProjectHistory history = new EpdProjectHistory();
					BeanUtils.copyProperties(existingProject, history);
					history.setUu_id(null);

					QueryWrapper<EpdProjectHistory> historyQueryWrapper = new QueryWrapper<>();
					historyQueryWrapper.eq("group_id", existingProject.getGroup_id());
					historyQueryWrapper.eq("inv_org", existingProject.getInv_org());
					historyQueryWrapper.orderByDesc("seq_no");
					List<Map<String, Object>> maxSeqNoResult = epdProjectHistoryMapper.selectMaps(historyQueryWrapper.last("LIMIT 1"));

					int newSeqNo = 1;
					if (maxSeqNoResult != null && !maxSeqNoResult.isEmpty()) {
						Object maxSeqObj = maxSeqNoResult.get(0).get("seq_no");
						if (maxSeqObj != null) {
							newSeqNo = Integer.parseInt(maxSeqObj.toString()) + 1;
						}
					}
					history.setSeq_no(newSeqNo);
					history.setOperate_type("U");

					epdProjectHistoryMapper.insert(history);
				}
			} else {
				QueryWrapper<EpdProject> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("project_no", projectNo);
				queryWrapper.eq("inv_org", invOrg);
				if (groupId != null && !groupId.isEmpty()) {
					queryWrapper.eq("group_id", groupId);
				}
				existingProject = epdProjectMapper.selectOne(queryWrapper);
				if (existingProject != null) {
					return R.error("该项目号数据已经存在，无法新增!");
				}

				EpdProjectHistory history = new EpdProjectHistory();
				BeanUtils.copyProperties(epdProject, history);
				history.setUu_id(null);
				QueryWrapper<EpdProjectHistory> historyQueryWrapper = new QueryWrapper<>();
				if (groupId != null && !groupId.isEmpty()) {
					historyQueryWrapper.eq("group_id", groupId);
				}
				historyQueryWrapper.eq("inv_org", invOrg);
				historyQueryWrapper.orderByDesc("seq_no");
				List<Map<String, Object>> maxSeqNoResult = epdProjectHistoryMapper.selectMaps(historyQueryWrapper.last("LIMIT 1"));

				int newSeqNo = 1;
				if (maxSeqNoResult != null && !maxSeqNoResult.isEmpty()) {
					Object maxSeqObj = maxSeqNoResult.get(0).get("seq_no");
					if (maxSeqObj != null) {
						newSeqNo = Integer.parseInt(maxSeqObj.toString()) + 1;
					}
				}
				history.setSeq_no(newSeqNo);
				history.setOperate_type("N");

				epdProjectHistoryMapper.insert(history);

			}
			R res = comUnitService.SaveData(params, getEntityClass());
			if (!res.get("code").equals(200)) {
				return res;
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
			for (Map<String, Object> item : list) {
				String projectNo = (String) item.get("project_no");
				String invOrg = (String) item.get("inv_org");
				String groupId = (String) item.get("group_id");

				// 查询销售协议是否使用
				/*QueryWrapper<OrdAgreementHead> agreementQueryWrapper = new QueryWrapper<>();
				agreementQueryWrapper.eq("project_no", projectNo);
				if (invOrg != null && !invOrg.isEmpty()) {
					agreementQueryWrapper.eq("inv_org", invOrg);
				}
				List<OrdAgreementHead> agreementHeads = ordAgreementHeadMapper.selectList(agreementQueryWrapper);
				if (agreementHeads != null && !agreementHeads.isEmpty()) {
					return R.error("销售协议中已使用，不能删除!");
				}*/

				// 查询销售订单是否使用
				/*QueryWrapper<OrdOrderHead> orderQueryWrapper = new QueryWrapper<>();
				orderQueryWrapper.eq("project_no", projectNo);
				if (invOrg != null && !invOrg.isEmpty()) {
					orderQueryWrapper.eq("inv_org", invOrg);
				}
				List<OrdOrderHead> orderHeads = ordOrderHeadMapper.selectList(orderQueryWrapper);
				if (orderHeads != null && !orderHeads.isEmpty()) {
					return R.error("销售订单中已使用，不能删除！");
				}*/

				// 查询要删除的项目数据
				QueryWrapper<EpdProject> projectQueryWrapper = new QueryWrapper<>();
				projectQueryWrapper.eq("project_no", projectNo);
				projectQueryWrapper.eq("inv_org", invOrg);
				if (groupId != null && !groupId.isEmpty()) {
					projectQueryWrapper.eq("group_id", groupId);
				}
				EpdProject project = epdProjectMapper.selectOne(projectQueryWrapper);

				// 如果存在，保存到历史表
				if (project != null) {
					EpdProjectHistory history = new EpdProjectHistory();
					BeanUtils.copyProperties(project, history);
					history.setUu_id(null);

					// 查询最大 seq_no
					QueryWrapper<EpdProjectHistory> historyQueryWrapper = new QueryWrapper<>();
					if (groupId != null && !groupId.isEmpty()) {
						historyQueryWrapper.eq("group_id", groupId);
					}
					historyQueryWrapper.eq("inv_org", invOrg);
					historyQueryWrapper.orderByDesc("seq_no");
					List<Map<String, Object>> maxSeqNoResult = epdProjectHistoryMapper.selectMaps(historyQueryWrapper.last("LIMIT 1"));

					int newSeqNo = 1;
					if (maxSeqNoResult != null && !maxSeqNoResult.isEmpty()) {
						Object maxSeqObj = maxSeqNoResult.get(0).get("seq_no");
						if (maxSeqObj != null) {
							newSeqNo = Integer.parseInt(maxSeqObj.toString()) + 1;
						}
					}
					history.setSeq_no(newSeqNo);
					history.setOperate_type("D"); // D表示删除

					epdProjectHistoryMapper.insert(history);
				}

				// 执行删除
				//Map<String, Object> params = new HashMap<>();
				//params.put("listdatas", JSON.toJSONString(list));
				R res = comUnitService.normDelete(params);
				if (!res.get("code").equals(200)) {
					return res;
				}
			}

			return R.ok();
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error(e.getMessage());
		}
	}
}
