package com.jcloud.system.aspect;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jcloud.system.annotation.Log;
import com.jcloud.system.dao.LogDao;
import com.jcloud.system.entity.LogEntity;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.HttpContextUtils;
import com.jcloud.system.utils.IPUtils;
import com.jcloud.timedTask.entity.InterfaceInfo;
import com.jcloud.timedTask.entity.TaskLog;
import com.jcloud.timedTask.service.InterfaceInfoService;
import com.jcloud.timedTask.service.TaskLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
/**
 * 日志Aspect
 * @Auther: yang
 * @Date: 2023/3/29 11:37
 * @Description:
 */
@Aspect
@Component
public class LogAspect {

    @Resource
    LogDao logDao;
    @Resource
    InterfaceInfoService interfaceInfoService;
    @Resource
    TaskLogService taskLogService;


    @Pointcut("@annotation(com.jcloud.system.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Date startTime = new Date();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //异步保存日志
        try{
            saveLog(point, time,result,startTime);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    void saveLog(ProceedingJoinPoint joinPoint, long time,Object result,Date beginTime) throws InterruptedException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogEntity sysLog = new LogEntity();
        Log syslog = method.getAnnotation(Log.class);
        // 请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSONUtil.toJsonStr(args[0]);
            sysLog.setParams(params);
        } catch (Exception e) {
            StringBuilder params = new StringBuilder();
            for (Object a:args) {
                if(a!=null) params.append(" parameter:"+a.toString());
            }
            sysLog.setParams(params.toString());
        }
        if (syslog != null) {
            // 注解上的描述
            sysLog.setOperation(syslog.value());
            if(StringUtils.isNotEmpty(syslog.interfaceCode())){
                //记录接口日志
                InterfaceInfo interfaceInfo = interfaceInfoService.getOne(Wrappers.<InterfaceInfo>query().lambda().eq(InterfaceInfo::getCode,syslog.interfaceCode()));
                if(interfaceInfo!=null){
                    TaskLog taskLog = new TaskLog();
                    taskLog.setExecute_end_time(new Date());//接口结束时间
                    taskLog.setExecute_result(JSON.toJSONString(result));
                    taskLog.setExecuteTime(beginTime);
                    taskLog.setParam(sysLog.getParams());
                    taskLog.setInterfaceId(interfaceInfo.getId());
                    taskLog.setInterfaceName(interfaceInfo.getName());
                    taskLog.setInterfaceCode(interfaceInfo.getCode());
                    taskLog.setInterfaceTimeSpan(time);
                    taskLogService.add(taskLog);
                }
            }
        }
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));
        // 用户名
        UserEntity userEntity = ShiroUtils.getUserEntity();
        if (null == userEntity) {
//            if (null != sysLog.getParams()) {
//                sysLog.setUserId(-1L);
//                sysLog.setUsername(sysLog.getParams());
//            } else {
            sysLog.setUser_id(-1L);
            sysLog.setUsername("获取用户信息为空");
//            }
        } else {
            sysLog.setUser_id(userEntity.getUserId());
            sysLog.setUsername(userEntity.getUsername());
        }
        sysLog.setTime((int) time);
        // 系统当前时间
        Date date = new Date();
        sysLog.setGmt_create(date);
        // 保存系统日志
        logDao.insert(sysLog);
    }
}
