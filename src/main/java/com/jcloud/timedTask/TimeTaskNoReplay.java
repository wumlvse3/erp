package com.jcloud.timedTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jcloud.timedTask.entity.InterfaceInfo;
import com.jcloud.timedTask.entity.TaskLog;
import com.jcloud.timedTask.exception.TaskException;
import com.jcloud.timedTask.service.InterfaceInfoService;
import com.jcloud.timedTask.service.TaskLogService;
import org.quartz.*;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author banana
 * @date 2020/11/20
 * @time 11:05 定时任务，没有回执请求
 */
public abstract class TimeTaskNoReplay implements Job {

    private static Set<String> executeSet = new CopyOnWriteArraySet<>();

    @Override
    public final void  execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        TaskLogService taskLogService = (TaskLogService)jobDataMap.get("taskLogService");
        JSONObject jsonObject = JSON.parseObject(jobDataMap.getString("param") == null ? "{}" : jobDataMap.getString("param"));
        String interfaceInfoId = jobDataMap.getString("interfaceInfoId");
        InterfaceInfoService interfaceInfoService = (InterfaceInfoService)jobDataMap.get("interfaceInfoService");
        InterfaceInfo interfaceInfo = interfaceInfoService.getOne(interfaceInfoId);

        // 放入interfaceInfoId
        jsonObject.put("interfaceInfo",interfaceInfo);

        TaskLog taskLog = new TaskLog();
        // 记录执行开始时间
        taskLog.setExecuteTime(new Date());
        // 接口名称
        taskLog.setInterfaceName(interfaceInfo.getName());
        // 接口组名称
        taskLog.setInterfaceGroup(interfaceInfo.getGroupName());
        // 接口数据库id
        taskLog.setInterfaceId(interfaceInfo.getId());
        // 执行时参数
        taskLog.setParam(interfaceInfo.getPreParams());
        // 接口代码
        taskLog.setInterfaceCode(interfaceInfo.getCode());
        try {
            if (executeSet.contains(interfaceInfoId)) {
                throw new TaskException("相同的定时任务正在执行");
            } else {
                executeSet.add(interfaceInfoId);
            }
            executeTask(taskLog,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            taskLog.setExtra(e.getMessage());
            taskLog.setExecuteStatus("error");
        } finally {
            // 记录执行结束时间
            taskLog.setExecute_end_time(new Date());

            // 任务执行成功，记录最后执行时间,取任务开始执行的时间
            if ("success".equals(taskLog.getExecuteStatus())) {
                interfaceInfo.setLast_execute_time(taskLog.getExecuteTime());
                interfaceInfo.setEnd_time(null);
                interfaceInfoService.updateById(interfaceInfo);
            }
            // 保存日志记录
            taskLogService.add(taskLog);
            String temp = jobDataMap.getString("temp");
            // 对于立即执行的任务，删除定时器和job
            if ("onlyOne".equalsIgnoreCase(temp)) {
                Scheduler scheduler = jobExecutionContext.getScheduler();
                JobDetail jobDetail = jobExecutionContext.getJobDetail();
                Trigger trigger = jobExecutionContext.getTrigger();
                try {
                    scheduler.pauseTrigger(trigger.getKey());
                    scheduler.unscheduleJob(trigger.getKey());
                    scheduler.deleteJob(jobDetail.getKey());
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
            if (!"相同的定时任务正在执行".equals(taskLog.getExtra())) {
                executeSet.remove(interfaceInfoId);
            }

        }

    }
    // 执行任务
    protected abstract void executeTask (TaskLog taskLog, JSONObject param) throws Exception;
}
