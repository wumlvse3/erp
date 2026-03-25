package com.jcloud.timedTask;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jcloud.timedTask.entity.InterfaceInfo;
import com.jcloud.timedTask.entity.TaskLog;
import com.jcloud.timedTask.http.HttpUtil;
import com.jcloud.timedTask.http.ResultBean;
import com.jcloud.timedTask.service.InterfaceInfoService;
import com.jcloud.timedTask.service.TaskLogService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author banana
 * @date 2020/9/9
 * @time 11:57
 * 所有定时任务的执行类须继承此类重写方法
 */
public abstract class TimedTask implements Job {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public final void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
        InterfaceInfoService interfaceInfoService = (InterfaceInfoService)map.get("interfaceInfoService");
        TaskLogService taskLogService = (TaskLogService) map.get("taskLogService");
        TaskLog taskLog = new TaskLog();
        // 记录任务执行时间
        taskLog.setExecuteTime(new Date());
        try {
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            InterfaceInfo interfaceInfo = interfaceInfoService.getOne(jobDataMap.getString("interfaceInfoId"));
            if (interfaceInfo != null) {
                logger.info("任务开始执行---> {}", interfaceInfo);
                // 日志记录
                taskLog.setInterfaceName(interfaceInfo.getName());
                taskLog.setInterfaceId(interfaceInfo.getId());
                taskLog.setParam(interfaceInfo.getPreParams());

                String preToke = this.preToken(interfaceInfo);
                Map<String, String> params = JSONObject.parseObject(interfaceInfo.getPreParams(), new TypeReference<Map<String, String>>() {
                });
                Map<String, String> header = new HashMap<>();
                header.put("token", preToke);
                // 发送请求
                ResultBean res = HttpUtil.sendRequest(interfaceInfo.getPreUrl(), interfaceInfo.getMethod(), params, header);
                // 判断请求是否成功
                if (!(res.getStatusLine().getStatusCode() == 200 || res.getStatusLine().getReasonPhrase().equalsIgnoreCase("ok"))) {
                    // 日志错误信息记录
                    // 日志状态记录
                    taskLog.setExecuteStatus("error");
                    taskLog.setExecute_result(res.getHttpEntity());
                } else {

                    // 日志状态记录
                    taskLog.setExecuteStatus("success");
                    // 请求结果
                    JSONObject jsonObject = JSONObject.parseObject(res.getHttpEntity());
                    // 记录执行结果
                    String s1 = jsonObject.toJSONString();
                    if (s1.length() > 2500) {
                        logger.info("定时任务执行结果(前置请求): {},接口信息: {}",s1,interfaceInfo);
                    } else {
                        taskLog.setExecute_result(s1);
                    }
                    //将请求结果传入回调
                    JSONObject result = this.result(jsonObject);
                    String replayUrl = interfaceInfo.getReplyUrl();
                    // 需要执行回执的情况
                    if (null != replayUrl && !replayUrl.equals("") && null != result) {
                        String replyToken = this.replyToken(interfaceInfo);
                        Map<String, String> replyParams = JSONObject.parseObject(result.toJSONString(), new TypeReference<Map<String, String>>() {
                        });
                        Map<String, String> replyHeader = new HashMap<>();
                        replyHeader.put("token", replyToken);
                        // 发送回执
                        ResultBean replyRes = HttpUtil.sendRequest(interfaceInfo.getReplyUrl(), interfaceInfo.getReplyMethod(), replyParams, replyHeader);
                        if (!(replyRes.getStatusLine().getStatusCode() == 200 || replyRes.getStatusLine().getReasonPhrase().equalsIgnoreCase("ok"))) {
                            // 日志错误信息记录
                            taskLog.setReplyExecuteStatus("error");
                            taskLog.setReply_execute_result(replyRes.getHttpEntity());
                        } else {
                            JSONObject jsonObject1 = JSONObject.parseObject(replyRes.getHttpEntity());

                            taskLog.setReplyExecuteStatus("success");
                            // 记录回执请求执行结果 超过两千字符写到日志
                            String s = jsonObject1.toJSONString();
                            if (s.length() > 2500) {
                                logger.info("定时任务执行结果(回执): {},接口信息: {}",s,interfaceInfo);
                            } else {
                                taskLog.setReply_execute_result(s);
                            }
                            // 将请求结果传入回调函数
                            this.replyResult(jsonObject1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("---");
            taskLog.setExtra(e.getMessage());
        } finally {
            taskLogService.add(taskLog);

            // 对于立即执行的任务，删除定时器和job
            String temp = map.getString("temp");
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
        }

    }

    /**
     * @param interfaceInfo 接口信息
     * @return 前置请求是所用的token
     */
    protected abstract String preToken(InterfaceInfo interfaceInfo);

    /**
     * @param interfaceInfo 接口信息
     * @return 回执请求所用的token
     */
    protected abstract String replyToken(InterfaceInfo interfaceInfo);

    /**
     * @param preResponse 前置请求返回的结果
     * @return 返回回执请求所需请求参数，若没有回执可返回 null
     */
    protected abstract JSONObject result(JSONObject preResponse);

    /**
     * @param replyResponse 回执请求返回的结果
     */
    protected abstract void replyResult(JSONObject replyResponse);

}
