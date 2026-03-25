package com.jcloud.system.service.impl;

import cn.hutool.json.JSONUtil;

import com.jcloud.system.event.ComUnitEntity;
import com.jcloud.system.service.ActivtiService;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivtiServiceImpl implements ActivtiService {


    @Autowired
    ComUnitService comUnitService;

    @Autowired
    SqlService sqlService;

    //  .............activti接口类.............
    @Autowired
    IdentityService identityService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;


    //  .............模型操作.............



    @Override
    public R taskSubmitUnit(String region, String unit, Map<String, Object> params, R normsave, Map<String, Object> variables) {
        ComUnitEntity unitEntity = comUnitService.getUnitEntity(region, unit);
        if(unitEntity!=null){
            params.put("unitId",unitEntity.getUnitId());
            return taskSubmit(params,normsave,variables);
        }else return R.error("未找到对应对象");

    }

    @Override
    public R taskSubmit(Map<String, Object> params, R normsave,Map<String, Object> variables){
        String unitCode = ObjectUtils.oGetS(params.get("unitCode"));
        String unitdatas = ObjectUtils.oGetS(params.get("unitdatas")); //表单数据
        Map<String, Object> taskData = JSONUtil.toBean(params.get("yang_task").toString(), Map.class);
        String taskId = ObjectUtils.oGetS(taskData.get("taskId"));
        String taskkey = ObjectUtils.oGetS(taskData.get("taskkey"));//流程
        String taskdate1 = ObjectUtils.oGetS(taskData.get("taskdate1"));//固定变量
        String taskdate2 = ObjectUtils.oGetS(taskData.get("taskdate2"));//界面变量
        String taskkeydata = ObjectUtils.oGetS(taskData.get("taskkeydata"));
        Map<String, Object> taskkeymap = taskkeydata.equals("")?null:JSONUtil.toBean(taskkeydata, Map.class);
        Map<String, Object> map = JSONUtil.toBean(unitdatas, Map.class);
        if (!taskId.equals("")) {
            Map<String, Object> vars = new HashMap<>();
            getvars(taskdate1, taskdate2, map, vars);
            String feedback = ObjectUtils.oGetS(map.get(unitCode + "_feedback"));//流程意见
            if(variables != null)for (String key : variables.keySet()) {
                vars.put(key,variables.get(key));
            }
            complete(taskId, vars, feedback, taskkeymap);
        } else if (!taskkey.equals("")) {
            if(normsave.get("keyname") !=null) map.put(ObjectUtils.oGetS(normsave.get("keyname")), normsave.get("keyid"));
            String unitId = ObjectUtils.oGetS(taskData.get("unitId"));
            Map<String, Object> vars = new HashMap<>(16);
            getvars(taskdate1, taskdate2, map, vars);
            if(variables != null)for (String key : variables.keySet()) {
                vars.put(key,variables.get(key));
            }
            String procInsId ;
            if(ObjectUtils.getFT(taskData.get("taskuser"))){
                String yangTaskUser = ObjectUtils.oGetS(taskData.get("yang_task_user"));
                if(yangTaskUser.equals("")){
                    return R.error("流程人员为空");
                }
                procInsId = startProcess(taskkey, taskkey, unitId, "", vars, taskkeymap);
                if(!insertUser(procInsId,yangTaskUser)){
                    return R.error("流程人员添加失败");
                }
            }else {
                procInsId = startProcess(taskkey, taskkey, unitId, "", vars, taskkeymap);
            }
            roleScreening(procInsId);
        }
        return normsave;
    }

    @Override
    @Transactional
    public R normsave(Map<String, Object> params) {
        try {
            Long regionId = Long.valueOf(params.get("regionId").toString());
            String unitCode = ObjectUtils.oGetS(params.get("unitCode"));
            String listCode = ObjectUtils.oGetS(params.get("listCode"));
            String unitdatas = ObjectUtils.oGetS(params.get("unitdatas")); //表单数据
            String listdatas = ObjectUtils.oGetS(params.get("listdatas"));//列表数据
            String listkey = ObjectUtils.oGetS(params.get("listkey"));
            String unitverify = ObjectUtils.oGetS(params.get("unitverify")); //表单验证
            String listverify = ObjectUtils.oGetS(params.get("listverify")); //列表数据
            R normsave = saveUnit(regionId, unitCode, listCode, unitdatas, listdatas, listkey, unitverify, listverify);
            String code = ObjectUtils.oGetS(normsave.get("code"));
            if (!code.equals("0")) return normsave;
//        流程判断
            R extracted = extracted(params, unitCode, unitdatas, normsave);
            if(ObjectUtils.oGetS(extracted.get("code")).equals("0")){
                return extracted;
            }else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return extracted;
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return R.error(ObjectUtils.oGetS(e.getMessage()));
        }
    }

    private R extracted(Map<String, Object> params, String unitCode, String unitdatas, R normsave) {
        Map<String, Object> taskData = JSONUtil.toBean(params.get("yang_task").toString(), Map.class);
        String taskId = ObjectUtils.oGetS(taskData.get("taskId"));
        String taskkey = ObjectUtils.oGetS(taskData.get("taskkey"));//流程
        String taskdate1 = ObjectUtils.oGetS(taskData.get("taskdate1"));//固定变量
        String taskdate2 = ObjectUtils.oGetS(taskData.get("taskdate2"));//界面变量
        String taskkeydata = ObjectUtils.oGetS(taskData.get("taskkeydata"));
        Map<String, Object> taskkeymap = taskkeydata.equals("") ? null : JSONUtil.toBean(taskkeydata, Map.class);
        Map<String, Object> map = JSONUtil.toBean(unitdatas, Map.class);
        if (!taskId.equals("")) {
            Map<String, Object> vars = new HashMap<>();
            getvars(taskdate1, taskdate2, map, vars);
            String feedback = ObjectUtils.oGetS(map.get(unitCode + "_feedback"));//流程意见
            complete(taskId, vars, feedback, taskkeymap);
        } else if (!taskkey.equals("")) {
            map.put(ObjectUtils.oGetS(normsave.get("keyname")), normsave.get("keyid"));
            String unitId = ObjectUtils.oGetS(taskData.get("unitId"));
            Map<String, Object> vars = new HashMap<>(16);
            getvars(taskdate1, taskdate2, map, vars);
            String procInsId ;
            if(ObjectUtils.getFT(taskData.get("taskuser"))){
                String yangTaskUser = ObjectUtils.oGetS(taskData.get("yang_task_user"));
                if(yangTaskUser.equals("")){
                    return R.error("流程人员为空");
                }
                procInsId = startProcess(taskkey, taskkey, unitId, "", vars, taskkeymap);
                if(!insertUser(procInsId,yangTaskUser)){
                    return R.error("流程人员添加失败");
                }
            }else {
                procInsId = startProcess(taskkey, taskkey, unitId, "", vars, taskkeymap);
            }
            roleScreening(procInsId);
        }
        return normsave;
    }

    public boolean insertUser(String procInsId,String taskusers){
        List<Map<String, Object>> list = JSONUtil.parseArray(taskusers).toBean(List.class);
        ArrayList<String> sqls = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            sqls.add("insert act_ru_user(p_id_,user_code_,act_task_,act_name_) values('"+procInsId
                    +"','"+map.get("act_personnel_")+"','"+map.get("act_name_")+"','"+map.get("act_task_")+"')");
        }
        Boolean aBoolean = sqlService.insertList(sqls);
        return aBoolean;
    }


    @Transactional
    public R saveUnit(Long regionId, String unitCode, String listCode, String unitdatas, String listdatas, String listkey,String unitverify, String listverify) {
        R normsave = comUnitService.normsave(regionId, unitCode, listCode, unitdatas, listdatas, listkey,unitverify,listverify,null);
        return normsave;
    }


    /**
     * 获取流程变量传入流程
     *
     * @param taskdate1 固定变量
     * @param taskdate2 界面变量
     * @param map       变量
     * @param vars      流程变量存入
     */
    @Override
    public void getvars(String taskdate1, String taskdate2, Map<String, Object> map, Map<String, Object> vars) {
        if (!ObjectUtils.oGetS(taskdate1).equals("")) {
            String[] split1 = taskdate1.split(",");
            for (String s :
                    split1) {
                String[] split = s.split("=");
                vars.put(split[0], split[1]);
            }
        }
        if (!ObjectUtils.oGetS(taskdate2).equals("")) {
            String[] split2 = ObjectUtils.oGetS(taskdate2).split(",");
            for (int i = 0; i < split2.length; i++) {
                String s = ObjectUtils.oGetS(map.get(split2[i]));
                vars.put(split2[i], s);
            }
        }
    }

    /**
     * 启动流程
     *
     * @param procDefKey    流程定义KEY
     * @param businessTable 业务表表名
     * @param businessId    业务表编号
     * @param title         流程标题，显示在待办任务标题
     * @param vars          流程变量
     * @return 流程实例ID
     */
    @Override
    @Transactional
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars, Map<String, Object> taskkeymap) {
        String userId = ShiroUtils.getUserEntity().getUsername();//ObjectUtils.toString(UserUtils.getUser().getId())
        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);
        // 设置流程变量
        if (vars == null) {
            vars = new HashMap<String, Object>();
        }
        // 设置流程标题
        if (StringUtils.isNotBlank(title)) {
            vars.put("title", title);
        }
        // ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
        //       .startProcessInstanceByKey(procDefKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
        // 启动流程
        ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessId, vars);
//       判断流程选择
        String procInsId
                = procIns.getId();
        return procInsId;
    }


    /**
     * 提交任务
     *
     * @param taskId     流程实例id
     * @param vars       变量
     * @param feedback   意见
     * @param taskkeymap 流程节点属性变量
     */
    @Override
    public void complete(String taskId, Map<String, Object> vars, String feedback, Map<String, Object> taskkeymap) {
        // 2.1根据人物ID查询流程实力ID
        Task task = taskService.createTaskQuery()
                .taskId(taskId).singleResult();
        // 获取流程实例ID
        String processInstance = task.getProcessInstanceId();
        // 2.2根据流程实例ID，人物ID，意见
        String username = ShiroUtils.getUserEntity().getUsername();
//		会签重复提交审批意见（占时不适用）
//		Object onef = sqlService.getOnef("select id_ from ACT_HI_COMMENT where user_id_ = '" + username + "' and task_id_ = " + taskId);
//		if(onef != null){
//			sqlService.updateOne("update ACT_HI_COMMENT set time_ =sysdate,message_ = '"+feedback+"' where  user_id_ = '"+username+"' and task_id_ = "+taskId);
//		}else {
        identityService.setAuthenticatedUserId(username);
        Comment comment = taskService.addComment(taskId, processInstance, feedback);
//        流程结果
        String taskType = ObjectUtils.oGetS(vars.get("yang_task_type"));
        if (!taskType.equals("")) {
            sqlService.updateOne(" update act_hi_comment set  TASK_TYPE_  = '"+taskType+"' where ID_ = '"+comment.getId()+"'");
            vars.remove("yang_task_type");
        }
//		}
//      通过判断条件
        String formKey = ObjectUtils.oGetS(task.getFormKey());
        if (!formKey.equals("")) {
            String[] split = formKey.split("==");
            if (split.length == 2) {
                if (!ObjectUtils.oGetS(vars.get(split[0])).equals(split[1])) {
                    return;
                }
            }
        }

//      节点变量判断
//      是否会签判断
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processDefinitionId = task.getProcessDefinitionId();
        String sql = "select state_key_,state_value_ from act_ge_statekey  where type_ = 1 and  ACT_TASK_ = '" + taskDefinitionKey + "' " +
                "and key_id_ in (select DEPLOYMENT_ID_ from act_re_procdef where id_ = '" + processDefinitionId + "') ";
        List<Map<String, Object>> all = sqlService.getAll(sql);
        Map<String, Object> statemap = new HashMap<String, Object>();
        for (int i = 0; i < all.size(); i++) {
            Map<String, Object> map = all.get(i);
            statemap.put(ObjectUtils.oGetS(map.get("state_key_")), ObjectUtils.oGetS(map.get("state_value_")));
        }
        if (ObjectUtils.oGetS(statemap.get("flowtype")).equals("1") && !ObjectUtils.oGetS(vars.get("yang_task_pass")).equals("1")) {//流程审批  yang_task_pass 审批通过
            String sql2 = "select  count(1) from (select assignee_ from  act_ru_task WHERE ID_ = " + taskId + " and assignee_ is NOT null " +
                    "UNION select user_id_ as assignee_ from  act_ru_identitylink WHERE TASK_ID_ = " + taskId + " and user_id_ is NOT null) a";
            String sql3 = "select count(1) from ACT_HI_COMMENT where task_id_ = " + taskId;
            int integer = Integer.valueOf(sqlService.getOnef(sql2).toString());
            int integer2 = Integer.valueOf(sqlService.getOnef(sql3).toString());
            if (integer != integer2) {//如果人数不对等，流程不通过。
                return;
            }
        }
        taskService.complete(taskId, vars);
        if (!taskType.equals(""))
            sqlService.updateOne("update act_hi_actinst set task_type_ = '" + taskType + "'  where   task_id_ = " + taskId);
        roleScreening(processInstance);
    }




    /**
     * 角色解析为备用人员
     *
     * @param procInsId 实体id
     */
    private void roleScreening(String procInsId) {
        Map<String, Object> one = sqlService.getOne("select id_,assignee_,task_def_key_ from act_ru_task where proc_inst_id_ = " + procInsId);
        if (one == null) return;
        String assignee = ObjectUtils.oGetS(one.get("assignee_"));
        int id_ = Integer.valueOf(one.get("id_").toString());
        if (assignee.equals("@{initiatorup}")) {
            String initiator = ObjectUtils.oGetS(sqlService.getOnef("select start_user_id_ from  ACT_HI_PROCINST  where  PROC_INST_ID_ =  155005"));
            String username = ObjectUtils.oGetS(sqlService.getOnef("select  username from  sys_user where  user_id in (select leader_id from sys_user where  username = '" + initiator + "' )"));
            String update = "update  act_ru_task SET  ASSIGNEE_= '" + username + "'  where  id_ = " + id_;
            sqlService.updateOne(update);
        } else if (assignee.equals("@{nodeup}")) {
            Long id = ShiroUtils.getId();
            String username = ObjectUtils.oGetS(sqlService.getOnef("select  username from  sys_user where  user_id in (select leader_id from sys_user where  user_id = '" + id + "' )"));
            String update = "update  act_ru_task SET  ASSIGNEE_= '" + username + "'  where  id_ = " + id_;
            sqlService.updateOne(update);
        }
        int nextId = (id_ + 1000);
        String sql = "SELECT su.username FROM sys_user su JOIN sys_user_role sur ON su.user_id = sur.user_id" +
                "  where   sur.role_id in (select  SUBSTRING_INDEX(group_id_, '/', 1) as id from  act_ru_identitylink WHERE TASK_ID_ = " + id_ + ") " +
                "AND su.username not IN (select  assignee_ as suername  from  act_ru_task where ID_ = " + id_ + " and assignee_ is NOT null" +
                " UNION select  USER_ID_ from  act_ru_identitylink WHERE TASK_ID_ = " + id_ + " and USER_ID_ is NOT null)";
        List<String> username = sqlService.getListString(sql);
        List<String> usernamesql = new ArrayList<>();
        if (username.size() > 0) {
            for (int i = 0; i < username.size(); i++) {
                usernamesql.add("insert into act_ru_identitylink(id_,rev_,type_,user_id_,task_id_) " +
                        "values(" + (++nextId) + ",1,'candidate','" + username.get(i) + "'," + id_ + ")");
            }
        }
        String userCode = ObjectUtils.oGetS(sqlService.getOnef("select user_code_ from act_ru_user  where  p_id_ = '" + procInsId + "' and  act_name_ = '" + one.get("task_def_key_") + "' LIMIT 1"));
        if(!userCode.equals("")){
            String[] split = userCode.split(",");
            for (int i = 0; i < split.length; i++) {
                usernamesql.add("insert into act_ru_identitylink(id_,rev_,type_,user_id_,task_id_) " +
                        "values(" + (++nextId) + ",1,'candidate','" + split[i] + "'," + id_ + ")");
            }
        }
        sqlService.insertList(usernamesql);
    }

}
