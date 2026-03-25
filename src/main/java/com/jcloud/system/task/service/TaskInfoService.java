package com.jcloud.system.task.service;

import cn.hutool.json.JSONArray;
import cn.hutool.system.oshi.OshiUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jcloud.system.dao.UserDao;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.entity.EmailRecordParam;
import com.jcloud.system.entity.EmailRecordReceiver;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.enums.EmailSendTypeEnum;
import com.jcloud.system.service.EmailService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.task.domain.TaskExecutionDetails;
import com.jcloud.system.task.domain.TaskInfo;
import com.jcloud.system.task.mapper.TaskInfoMapper;
import com.jcloud.system.task.vo.TaskVo;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
public class TaskInfoService {
    public static final Logger LOGGER = LoggerFactory.getLogger(TaskInfoService.class);

    @Resource
    private TaskInfoMapper taskInfoMapper;
    @Resource
    private TaskExecutionDetailsService taskExecutionDetailsService;
    @Resource
    private EmailService emailService;
    @Resource
    private UserDao userDao;
    @Resource
    private SqlService sqlService;

    public TaskInfo getObj(String taskInfoId) {
        TaskInfo taskInfo=taskInfoMapper.findOneById(taskInfoId);
        if (taskInfo==null) {
            throw new RuntimeException("未找到任务");
        }
        return  taskInfo;
    }

    public List<TaskInfo> getObjs(Wrapper<TaskInfo> wrapper) {
        return taskInfoMapper.selectList(wrapper);
    }

    public TaskInfo modifyObj(TaskInfo factory) {
        TaskInfo taskInfo=taskInfoMapper.findOneById(factory.getId());
        if (taskInfo==null){
            throw new RuntimeException("未找到任务");
        }
        BeanUtils.copyProperties(factory,taskInfo,"createBy","createTime","tenant");
        taskInfoMapper.updateById(factory);
        return factory;
    }

    @Transactional
    public TaskInfo addObj(TaskInfo taskInfo) {
        //自动计算计划工时
        if(taskInfo.getStart_time()!=null&&taskInfo.getEnd_time()!=null){
            taskInfo.setTask_plan_hour(hoursBetween(taskInfo.getStart_time(),taskInfo.getEnd_time()));
        }
        if(StringUtils.isEmpty(taskInfo.getOrg_id())){
            taskInfo.setOrg_id(ShiroUtils.getOrgId());
        }
        if(StringUtils.isEmpty(taskInfo.getCreate_by())){
            taskInfo.setCreate_by(ShiroUtils.getUserEntity().getUsername());
        }
        taskInfo.setCreate_time(new Date());
        //新增任务
        taskInfoMapper.insert(taskInfo);
        TaskInfo newTaskInfo = taskInfoMapper.selectById(taskInfo.getId());
        if (StringUtils.isEmpty(newTaskInfo.getSheet_code())){
            throw new RuntimeException("新增失败，任务编码为空，请重试！");
        }
        //同步新增任务执行明细
        TaskExecutionDetails taskExecutionDetails = new TaskExecutionDetails();
        taskExecutionDetails.setOrg_id(taskInfo.getOrg_id());
        taskExecutionDetails.setCreate_by(taskInfo.getCreate_by());
        taskExecutionDetails.setCreate_time(new Date());
        //插入第一条任务历程记录
        taskExecutionDetails.setSheet_code(newTaskInfo.getSheet_code());
        //设置创建人
        taskExecutionDetails.setProcess_person_id(taskInfo.getCreate_by());
        taskExecutionDetails.setProcess_person_name(taskInfo.getCreate_by());
        taskExecutionDetails.setProcess_time(newTaskInfo.getCreate_time());
        taskExecutionDetails.setProcess_type("CREATED");
        taskExecutionDetails.setProcess_remark("任务已创建");
        taskExecutionDetailsService.addObj(taskExecutionDetails);
        if(taskInfo.getRemind_type().indexOf("REMIND_MAIL")>=0){
            //如果是邮件提醒
            UserEntity userEntity = userDao.selectById(taskInfo.getProcesser_id());
            if(userEntity==null){
                userEntity = userDao.selectOne(new QueryWrapper<UserEntity>().eq("username",taskInfo.getProcesser_id()));
            }
            String userName =userEntity.getUsername();
            String email = userEntity.getEmail();
            if(!StringUtils.isEmpty(email)){
                //发送邮件
                EmailRecord emailRecord = new EmailRecord();

                emailRecord.setSubject(newTaskInfo.getSheet_code()+"任务代办提醒");
                emailRecord.setSendMethod("noEnclosure");
                List<EmailRecordReceiver> receivers = new ArrayList<>();
                EmailRecordReceiver recordReceiver = new EmailRecordReceiver();
                recordReceiver.setEmail(email);
                recordReceiver.setName(userName);
                recordReceiver.setSendType(EmailSendTypeEnum.T);
                receivers.add(recordReceiver);
                emailRecord.setReceivers(receivers);
                emailRecord.setTemplateCode("TASK_TEMPLATE");
                emailRecord.setTemplateLanguage("zh");

                List<EmailRecordParam> list=new ArrayList<>();
                EmailRecordParam emailRecordParam=new EmailRecordParam();
                emailRecordParam.setParamKey("userName");
                emailRecordParam.setParamValue(userName);
                list.add(emailRecordParam);
                EmailRecordParam emailRecordParam1=new EmailRecordParam();
                emailRecordParam1.setParamKey("sender");
                emailRecordParam1.setParamValue(userName);
                list.add(emailRecordParam1);
                EmailRecordParam emailRecordParam2=new EmailRecordParam();
                emailRecordParam2.setParamKey("taskCode");
                emailRecordParam2.setParamValue(newTaskInfo.getSheet_code());
                list.add(emailRecordParam2);
                EmailRecordParam emailRecordParam3=new EmailRecordParam();
                emailRecordParam3.setParamKey("requiredDate");
                SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                emailRecordParam3.setParamValue(sp.format(newTaskInfo.getEnd_time()==null?new Date():newTaskInfo.getEnd_time()));
                list.add(emailRecordParam3);
                emailRecord.setParam(list);
                emailRecord.setCreateBy(taskInfo.getCreate_by());
                EmailRecord result = emailService.sendSms(emailRecord);
            }
        }
        return newTaskInfo;
    }

    public TaskInfo deleteObj(Long id) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo==null){
            throw new RuntimeException("未找到任务");
        }
        taskInfoMapper.deleteById(id);
        return taskInfo;
    }

    /**
    *根据任务代码获取任务详情
    */
    public TaskInfo getObjByCode(String taskCode){
        TaskInfo taskInfo=taskInfoMapper.findByOrgIdAndTaskCodeAndDeletedIsFalse(ShiroUtils.getOrgId(),taskCode);
        return taskInfo;
    }
    /**
    * 拒绝任务
    */
    public R rejectTaskByCode(TaskVo bean){
        TaskInfo taskInfo = getObjByCode(bean.getSheet_code());
        if(taskInfo==null){
            return R.error("未找到任务");
        }
        if(!"CREATED".equalsIgnoreCase(taskInfo.getTask_status())){
            return R.error("Current status does not allow updating");
        }

        taskInfo.setTask_status("REJECT");
        taskInfoMapper.updateById(taskInfo);
        //跟新任务记录表
        TaskExecutionDetails details = new TaskExecutionDetails();
        details.setProcess_person_id(ShiroUtils.getUserEntity().getUsername());
        details.setProcess_person_name(ShiroUtils.getUserEntity().getName());
        details.setProcess_remark(bean.getRemark());//拒绝原因
        details.setProcess_time(new Date());
        details.setProcess_type("REJECT");
        details.setSheet_code(bean.getSheet_code());
        taskExecutionDetailsService.addObj(details);
        return R.ok();
    }
    /**
     * 接受任务
     */
    public R acceptTaskByCode(TaskVo bean){
        TaskInfo taskInfo = getObjByCode(bean.getSheet_code());
        if(taskInfo==null){
            return R.error("未找到任务");
        }
        if(!"CREATED".equalsIgnoreCase(taskInfo.getTask_status())){
            return R.error("Current status does not allow updating");
        }

        taskInfo.setTask_status("PROCESSIONG");
        taskInfoMapper.updateById(taskInfo);
        //更新任务记录表
        TaskExecutionDetails details = new TaskExecutionDetails();
        details.setProcess_person_id(ShiroUtils.getUserEntity().getUsername());
        details.setProcess_person_name(ShiroUtils.getUserEntity().getName());
        details.setProcess_remark(bean.getRemark());//接受任务
        details.setProcess_time(new Date());
        details.setProcess_type("PROCESSIONG");
        details.setSheet_code(bean.getSheet_code());
        taskExecutionDetailsService.addObj(details);
        return R.ok();
    }
    /**
     * 暂停任务
     */
    public R pendingTaskByCode(TaskVo bean){
        TaskInfo taskInfo = getObjByCode(bean.getSheet_code());
        if(taskInfo==null){
            return R.error("未找到任务");
        }
        if("FINISHED".equalsIgnoreCase(taskInfo.getTask_status())){
            return R.error("Current status does not allow updating");
        }

        taskInfo.setTask_status("PENDING");
        taskInfoMapper.updateById(taskInfo);
        //更新任务记录表
        TaskExecutionDetails details = new TaskExecutionDetails();
        details.setProcess_person_id(ShiroUtils.getUserEntity().getUsername());
        details.setProcess_person_name(ShiroUtils.getUserEntity().getName());
        details.setProcess_remark(bean.getRemark());//暂停任务
        details.setProcess_time(new Date());
        details.setProcess_type("PENDING");
        details.setSheet_code(bean.getSheet_code());
        taskExecutionDetailsService.addObj(details);
        return R.ok();
    }
    /**
     * 取消任务
     */
    public R cancelTaskByCode(TaskVo bean){
        TaskInfo taskInfo = getObjByCode(bean.getSheet_code());
        if(taskInfo==null){
            return R.error("未找到任务");
        }
        if("FINISHED".equalsIgnoreCase(taskInfo.getTask_status())){
            return R.ok("任务已执行完毕");
        }

        taskInfo.setTask_status("CANCELED");
        taskInfoMapper.updateById(taskInfo);
        //更新任务记录表
        TaskExecutionDetails details = new TaskExecutionDetails();
        details.setProcess_person_id(ShiroUtils.getUserEntity().getUsername());
        details.setProcess_person_name(ShiroUtils.getUserEntity().getName());
        details.setProcess_remark(bean.getRemark());//取消任务
        details.setProcess_time(new Date());
        details.setProcess_type("CANCELED");
        details.setSheet_code(bean.getSheet_code());
        taskExecutionDetailsService.addObj(details);
        return R.ok();
    }
    /**
     * 完成任务
     */
    public R finishTaskByCode(TaskVo bean){
        TaskInfo taskInfo = getObjByCode(bean.getSheet_code());
        if(taskInfo==null){
            return R.error("未找到任务");
        }
        if("FINISHED".equalsIgnoreCase(taskInfo.getTask_status())){
            return R.ok("任务状态本身即为已完成，不作任何处理");
        }
        if(!"PROCESSIONG".equalsIgnoreCase(taskInfo.getTask_status())
                &&!"CREATED".equalsIgnoreCase(taskInfo.getTask_status())
                &&!"PENDING".equalsIgnoreCase(taskInfo.getTask_status())
                &&!"DERIVED".equalsIgnoreCase(taskInfo.getTask_status())
        ){
            return R.error("Current status does not allow updating");
        }


        taskInfo.setTask_status("FINISHED");
        //更新实际完成时间
        taskInfo.setFinish_time(new Date());
        taskInfo.setTask_real_hour(hoursBetween(taskInfo.getStart_time(),taskInfo.getFinish_time()));
        taskInfoMapper.updateById(taskInfo);
        //更新任务记录表
        TaskExecutionDetails details = new TaskExecutionDetails();
        details.setProcess_person_id(ShiroUtils.getUserEntity().getUsername());
        details.setProcess_person_name(ShiroUtils.getUserEntity().getName());
        details.setProcess_remark(bean.getRemark());//完成任务
        details.setProcess_time(new Date());
        details.setProcess_type("FINISHED");
        details.setSheet_code(bean.getSheet_code());
        taskExecutionDetailsService.addObj(details);
        return R.ok();
    }
    /**
     * 派生任务
     */
    @Transactional
    public R deriveTaskByCode(TaskVo bean){
        if(bean.getUsername()==null){
            return R.error("username is not allowed null");
        }
        TaskInfo taskInfo = getObjByCode(bean.getSheet_code());
        if(taskInfo==null){
            return R.error("未找到任务");
        }
        if(!"PROCESSIONG".equalsIgnoreCase(taskInfo.getTask_status())&&!"CREATED".equalsIgnoreCase(taskInfo.getTask_status())){
            return R.error("Current status does not allow updating");
        }

        taskInfo.setTask_status("DERIVED");
        taskInfoMapper.updateById(taskInfo);

        //生成一条新任务
        TaskInfo newTaskInfo = new TaskInfo();
        BeanUtils.copyProperties(taskInfo,newTaskInfo,"id","sheet_code");
        newTaskInfo.setProcesser_id(bean.getUsername());
        newTaskInfo.setProcesser_name(bean.getName());
        newTaskInfo.setParent_code(taskInfo.getSheet_code());
        newTaskInfo.setTask_status("CREATED");
        newTaskInfo.setTask_sort(taskInfo.getTask_sort()+1);
        newTaskInfo=this.addObj(newTaskInfo);

        //更新任务记录表
        TaskExecutionDetails details = new TaskExecutionDetails();
        details.setProcess_person_id(ShiroUtils.getUserEntity().getUsername());
        details.setProcess_person_name(ShiroUtils.getUserEntity().getName());
        details.setProcess_remark(bean.getRemark()+"派生任务【"+newTaskInfo.getSheet_code()+"】");//完成任务
        details.setProcess_time(new Date());
        details.setProcess_type("DERIVED");
        details.setSheet_code(bean.getSheet_code());
        taskExecutionDetailsService.addObj(details);
        return R.ok();
    }
    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    private  Double daysBetween(Date smdate,Date bdate)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return Double.parseDouble(String.valueOf(between_days));
    }
    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    private  Double hoursBetween(Date smdate,Date bdate)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600);
        return Double.parseDouble(String.valueOf(between_days));
    }

    public Map needToHandle(Integer offset,Integer size) {
        List<String> taskStatus=new ArrayList<>();
        taskStatus.add("CREATED");//创建
        taskStatus.add("DERIVED");//派生
        taskStatus.add("APPROVING");//审批中
        taskStatus.add("PROCESSIONG");//进行中
        List<TaskInfo> list;
        //如果当前登录用户为供应商，那么查询不分组织
        if(ShiroUtils.getSession().getAttribute("vendor_id")!=null && org.apache.commons.lang3.StringUtils.isNotEmpty(ShiroUtils.getSession().getAttribute("vendor_id").toString())){
            list = taskInfoMapper.selectList(new QueryWrapper<TaskInfo>()
                    .eq("processer_id", ShiroUtils.getUserEntity().getUsername())
                    .in("task_status", taskStatus)
                    .orderByDesc("create_time"));
        }else {
            list = taskInfoMapper.needToHandle(ShiroUtils.getOrgId(),ShiroUtils.getUserEntity().getUsername(),taskStatus);
        }

        /**获取流程审批待办**/
        String sql = "SELECT\n" +
                "\tyang.url as task_callback_url,\n" +
                "\tyang.create_time_ as create_time,\n" +
                "\tyang.task_name_ as task_title,\n" +
                "\t'l' keywords \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tCONCAT( '/sys/activiti/show/', art.PROC_DEF_ID_, '/', art.id_, '?m=e' ) AS url,\n" +
                "\t\tart.id_,\n" +
                "\t\tart.proc_def_id_,\n" +
                "\t\tconcat(arp.name_,'——',art.name_,'【',( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ),'】') AS task_name_,\n" +
                "\t\tart.task_def_key_,\n" +
                "\t\tart.create_time_,\n" +
                "\t\tah.START_USER_ID_,\n" +
                "\t\tarp.key_,\n" +
                "\t\tarp.name_,\n" +
                "\t\tarp.version_,\n" +
                "\t\t( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ) AS yang_flow_key \n" +
                "\tFROM\n" +
                "\t\tACT_RU_TASK art,\n" +
                "\t\tACT_HI_PROCINST ah,\n" +
                "\t\tact_re_procdef arp \n" +
                "\tWHERE\n" +
                "\t\tASSIGNEE_ = '"+ShiroUtils.getUserEntity().getUsername()+"' \n" +
                "\t\tAND art.PROC_INST_ID_ = ah.PROC_INST_ID_ \n" +
                "\t\tAND arp.id_ = art.proc_def_id_ UNION\n" +
                "\tSELECT\n" +
                "\t\tCONCAT( '/sys/activiti/show/', art.PROC_DEF_ID_, '/', art.id_, '?m=e' ) AS url,\n" +
                "\t\tart.id_,\n" +
                "\t\tart.proc_def_id_,\n" +
                "\t\tconcat(arp.name_,'——',art.name_,'【',( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ),'】') AS task_name_,\n" +
                "\t\tart.task_def_key_,\n" +
                "\t\tart.create_time_,\n" +
                "\t\tah.START_USER_ID_,\n" +
                "\t\tarp.key_,\n" +
                "\t\tarp.name_,\n" +
                "\t\tarp.version_,\n" +
                "\t\t( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ) AS yang_flow_key \n" +
                "\tFROM\n" +
                "\t\tACT_RU_TASK art,\n" +
                "\t\tact_ru_identitylink ari,\n" +
                "\t\tACT_HI_PROCINST ah,\n" +
                "\t\tact_re_procdef arp \n" +
                "\tWHERE\n" +
                "\t\tart.id_ = ari.task_id_ \n" +
                "\t\tAND art.PROC_INST_ID_ = ah.PROC_INST_ID_ \n" +
                "\t\tAND arp.id_ = art.proc_def_id_ \n" +
                "\t\tAND ari.type_ = 'candidate' \n" +
                "\t\tAND ari.user_id_ = '"+ShiroUtils.getUserEntity().getUsername()+"' UNION\n" +
                "\tSELECT\n" +
                "\t\tCONCAT( '/sys/activiti/show/', art.PROC_DEF_ID_, '/', art.id_, '?m=e' ) AS url,\n" +
                "\t\tart.id_,\n" +
                "\t\tart.proc_def_id_,\n" +
                "\t\tconcat(arp.name_,'——',art.name_,'【',( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ),'】') AS task_name_,\n" +
                "\t\tart.task_def_key_,\n" +
                "\t\tart.create_time_,\n" +
                "\t\tah.START_USER_ID_,\n" +
                "\t\tarp.key_,\n" +
                "\t\tarp.name_,\n" +
                "\t\tarp.version_,\n" +
                "\t\t( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ) AS yang_flow_key \n" +
                "\tFROM\n" +
                "\t\tACT_RU_TASK art,\n" +
                "\t\tACT_HI_PROCINST ah,\n" +
                "\t\tact_re_procdef arp \n" +
                "\tWHERE\n" +
                "\t\tASSIGNEE_ IN ( SELECT trustee_user FROM sys_act_trustee WHERE trustee_date > NOW( ) AND consignor_id = 1 ) \n" +
                "\t\tAND art.PROC_INST_ID_ = ah.PROC_INST_ID_ \n" +
                "\t\tAND arp.id_ = art.proc_def_id_ UNION\n" +
                "\tSELECT\n" +
                "\t\tCONCAT( '/sys/activiti/show/', art.PROC_DEF_ID_, '/', art.id_, '?m=e' ) AS url,\n" +
                "\t\tart.id_,\n" +
                "\t\tart.proc_def_id_,\n" +
                "\t\tconcat(arp.name_,'——',art.name_,'【',( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ),'】') AS task_name_,\n" +
                "\t\tart.task_def_key_,\n" +
                "\t\tart.create_time_,\n" +
                "\t\tah.START_USER_ID_,\n" +
                "\t\tarp.key_,\n" +
                "\t\tarp.name_,\n" +
                "\t\tarp.version_,\n" +
                "\t\t( SELECT text_ FROM act_ru_variable WHERE EXECUTION_ID_ = art.execution_id_ AND name_ = 'yang_flow_key' ) AS yang_flow_key \n" +
                "\tFROM\n" +
                "\t\tACT_RU_TASK art,\n" +
                "\t\tact_ru_identitylink ari,\n" +
                "\t\tACT_HI_PROCINST ah,\n" +
                "\t\tact_re_procdef arp \n" +
                "\tWHERE\n" +
                "\t\tart.id_ = ari.task_id_ \n" +
                "\t\tAND art.PROC_INST_ID_ = ah.PROC_INST_ID_ \n" +
                "\t\tAND arp.id_ = art.proc_def_id_ \n" +
                "\t\tAND ari.type_ = 'candidate' \n" +
                "\t\tAND ari.user_id_ IN ( SELECT trustee_user FROM sys_act_trustee WHERE trustee_date > NOW( ) AND consignor_id = 1 ) \n" +
                "\t) yang \n" +
                "WHERE\n" +
                "\tyang.id_ NOT IN ( SELECT task_id_ FROM ACT_HI_COMMENT WHERE task_id_ IN ( SELECT id_ FROM ACT_RU_TASK ) AND user_id_ = '"+ShiroUtils.getUserEntity().getUsername()+"' ) ";
        List<Map<String,Object>> result = sqlService.getAll(sql);
        if(result!=null){
            for(Map b:result){
                list.add(JSON.parseObject(JSON.toJSONString(b), TaskInfo.class));
            }
        }
        List<TaskInfo> returnData = new ArrayList<>();

        if(list!=null&&list.size()>0){
            list = list.stream().sorted(Comparator.comparing(TaskInfo::getCreate_time).reversed()).collect(Collectors.toList());
        }

        if(offset!=null&&list.size()>0){
            Integer startNum = offset;
            Integer endNum = offset+size;
            while(startNum<endNum&&startNum<list.size()){
                returnData.add(list.get(startNum));
                startNum++;
            }
        }else{
            returnData.addAll(list);
        }

        Integer total=returnData.size();
        Map<String,Object> map=new HashMap<>();
        map.put("msg","成功");
        map.put("code","0");
        map.put("total",total);
        map.put("data",returnData);
        return map;
    }

    public Map finished(Integer offset,Integer size) {
        String sql = "SELECT\n" +
                "\t* \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "CONCAT( '/sys/activiti/visithi/', ahp.PROC_DEF_ID_,'/',aht2.PROC_INST_ID_,'/',ahp.business_key_,'?m=s') AS task_callback_url,\n" +
                "\tconcat(arp.name_,'——',aht2.name_,'【',( SELECT text_ FROM act_hi_varinst WHERE proc_inst_id_ = ahp.proc_inst_id_ AND name_ = 'yang_flow_key' ),'】') as task_title,\n" +
                "\taht2.end_time_ as finish_time ,\n" +
                "\t'F' keywords,'task' as task_class \n" +
                "\tFROM\n" +
                "\t\tact_hi_procinst ahp\n" +
                "\t\tLEFT OUTER JOIN act_ru_task art ON ahp.proc_inst_id_ = art.proc_inst_id_,\n" +
                "\t\tact_re_procdef arp,\n" +
                "\t\tact_hi_comment ahc,\n" +
                "\t\tact_hi_taskinst aht,\n" +
                "\t\tact_hi_comment ahc2,\n" +
                "\t\tact_hi_taskinst aht2 \n" +
                "\tWHERE\n" +
                "\t\tahp.PROC_DEF_ID_ = arp.id_ \n" +
                "\t\tAND ahp.proc_inst_id_ = ahc2.proc_inst_id_ \n" +
                "\t\tAND ahc2.user_id_ = '"+ShiroUtils.getUserEntity().getUsername()+"' \n" +
                "\t\tAND ahc2.task_id_ = aht2.id_ \n" +
                "\t\tAND ahp.proc_inst_id_ = ahc.proc_inst_id_ \n" +
                "\t\tAND ahc.id_ IN ( SELECT max( id_ ) FROM act_hi_comment WHERE PROC_INST_ID_ = ahp.PROC_INST_ID_ GROUP BY proc_inst_id_ ) \n" +
                "\t\tAND ahc.task_id_ = aht.id_ \n" +
                "\t\tAND ahp.end_time_ IS NULL \n" +
                "\tORDER BY\n" +
                "\t\tart.create_time_ DESC \n" +
                "\t) yang1 UNION\n" +
                "SELECT\n" +
                "\t* \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                " \tCONCAT( '/sys/activiti/visithi/', ahp.PROC_DEF_ID_,'/',aht2.PROC_INST_ID_,'/',ahp.business_key_,'?m=s') AS task_callback_url,\n" +
                "\tconcat(arp.name_,'——',aht2.name_,'【',( SELECT text_ FROM act_hi_varinst WHERE proc_inst_id_ = ahp.proc_inst_id_ AND name_ = 'yang_flow_key'),'】') as task_title,\n" +
                "\taht2.end_time_ as end_time,\n" +
                "\t'F' keywords ,'task' as task_class  \n" +
                "\tFROM\n" +
                "\t\tact_hi_procinst ahp\n" +
                "\t\tLEFT OUTER JOIN act_ru_task art ON ahp.proc_inst_id_ = art.proc_inst_id_,\n" +
                "\t\tact_re_procdef arp,\n" +
                "\t\tact_hi_comment ahc,\n" +
                "\t\tact_hi_taskinst aht,\n" +
                "\t\tact_hi_comment ahc2,\n" +
                "\t\tact_hi_taskinst aht2\n" +
                "\tWHERE\n" +
                "\t\tahp.PROC_DEF_ID_ = arp.id_ \n" +
                "\t\tAND ahp.proc_inst_id_ = ahc2.proc_inst_id_ \n" +
                "\t\tAND ahc2.user_id_ = '"+ShiroUtils.getUserEntity().getUsername()+"' \n" +
                "\t\tAND ahc2.task_id_ = aht2.id_ \n" +
                "\t\tAND ahp.proc_inst_id_ = ahc.proc_inst_id_ \n" +
                "\t\tAND ahc.id_ IN ( SELECT max( id_ ) FROM act_hi_comment WHERE PROC_INST_ID_ = ahp.PROC_INST_ID_ GROUP BY proc_inst_id_ ) \n" +
                "\t\tAND ahc.task_id_ = aht.id_ \n" +
                "\t\tAND ahp.end_time_ IS NOT NULL \n" +
                "\tORDER BY\n" +
                "\t\tahp.start_time_ DESC \n" +
                "\t) yang2";
        List<String> taskStatus=new ArrayList<>();
        taskStatus.add("FINISHED");//完成
        taskStatus.add("REJECT");//拒绝
        taskStatus.add("CANCELED");//取消
        List<TaskInfo> list;
        if(ShiroUtils.getSession().getAttribute("vendor_id")!=null && org.apache.commons.lang3.StringUtils.isNotEmpty(ShiroUtils.getSession().getAttribute("vendor_id").toString())){
            list = taskInfoMapper.selectList(new QueryWrapper<TaskInfo>()
                    .eq("processer_id", ShiroUtils.getUserEntity().getUsername())
                    .in("task_status", taskStatus)
                    .orderByDesc("create_time"));
        }else {
            list = taskInfoMapper.needToHandle(ShiroUtils.getOrgId(),ShiroUtils.getUserEntity().getUsername(),taskStatus);
        }
        List<Map<String,Object>> result = sqlService.getAll(sql);
        if(result!=null){
            for(Map b:result){
                list.add(JSON.parseObject(JSON.toJSONString(b), TaskInfo.class));
            }
        }
        List<TaskInfo> returnData = new ArrayList<>();
        if(list!=null&&list.size()>0){
            list = list.stream().filter(b->(b.getFinish_time()!=null)).sorted(Comparator.comparing(TaskInfo::getFinish_time).reversed()).collect(Collectors.toList());
        }
        if(offset!=null&&list.size()>0){
            Integer startNum = offset;
            Integer endNum = offset+size;
            while(startNum<endNum&&startNum<list.size()){
                returnData.add(list.get(startNum));
                startNum++;
            }
        }else{
            returnData.addAll(list);
        }
        Map<String,Object> map=new HashMap<>();
        Integer total=returnData.size();
        map.put("msg","成功");
        map.put("code","0");
        map.put("total",total);
        map.put("data",returnData);
        return map;
    }

    public R finishBySheetId(String sheetId) {
        taskInfoMapper.finishBySheetId(sheetId);
        return R.ok();
    }
    public R deleteBySheetId(String sheetId){
        taskInfoMapper.delete(Wrappers.<TaskInfo>query().lambda().eq(TaskInfo::getSheet_id,sheetId));
        return R.ok();
    }
    /**
    * 根据messageId完成任务
    *
     * @param messageId
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2022/11/21 17:15
    */
//    public Map finishTaskByMessageId(String messageId){
//        taskInfoMapper.findByOrgIdAndTaskCodeAndDeletedIsFalse(ShiroUtils.getOrgId(),messageId);
//        TaskInfo taskInfo =  repository.findByTenantAndMessageIdAndDeletedIsFalse(TenantHandler.getStrictTenant(),messageId);
//        if(taskInfo==null){
//            LOGGER.info("通过MessageId无法找到对应的任务对象");
//            return ImmutableBiMap.of("code","failed");
//        }
//        taskInfo.setTaskStatus("FINISHED");
//        //更新实际完成时间
//        taskInfo.setFinishTime(new Date());
//        taskInfo.setTaskRealHour(hoursBetween(taskInfo.getStartTime(),taskInfo.getFinishTime()));
//        repository.save(taskInfo);
//        try{
//            imMessageService.readMessage(MessageEnums.user_task_message.toString(),messageId);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return ImmutableBiMap.of("code","success");
//    }

    /**
    * 统计任务状态数量
    */
//    public Map countTaskVo(Wrapper<TaskInfo> queryWrapper){
//        List<TaskInfo> totalList =taskInfoMapper.selectList(queryWrapper);
//        Map<String, Long> statusCnt= totalList.stream().collect(groupingBy(TaskInfo::getTaskStatus, counting()));
//        Long personalCnt=totalList.stream().filter(item->(item.getProcesserId().equals(ShiroUtils.getId()))).count();
//
//        ListParam<TaskInfo> authListParam = new ListParam<>(TaskInfo.class);
//        userDataAuthDispatchService.checkAndDispatchUserDataPrivilege(authListParam, "processerId");
//        List<TaskInfo> subList = super.listAll(authListParam).getItems();
//        Map<String, Long> processerCnt= subList.stream().collect(groupingBy(TaskInfo::getProcesserInfo, counting()));
//        List<Map> subUserList = new ArrayList<>();
//        processerCnt.forEach((key,value)->{
//            if(!StringUtils.isEmpty(key)){
//                Map subMap = new HashMap();
//                String[]args = key.split("___");
//                subMap.put("user_name",args[1]);
//                subMap.put("user_id",args[0]);
//                subMap.put("sub_cnt",value);
//                subUserList.add(subMap);
//            }
//        });
//        Map result = new HashMap();
//        result.put("status_cnt",statusCnt);
//        result.put("personal_cnt",personalCnt);
//        result.put("sub_list",subUserList);
//        return result;
//    }

}
