package com.jcloud.system.controller;
/**
 * 邮件发送
 */

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.entity.EmailRecordReceiver;
import com.jcloud.system.event.EmailSendEvent;
import com.jcloud.system.service.EmailService;
import com.jcloud.system.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationContext applicationContext;

    //发送邮件
    @RequestMapping(value = "/send",method = RequestMethod.POST)
    public EmailRecord sendSms(@RequestBody Map<String,Object> params){
        String emailRecordStr = (String) params.get("unitdatas");
        EmailRecord emailRecord = JSON.parseObject(emailRecordStr, EmailRecord.class);
        List<JSONArray> list = JSONUtil.parseArray(params.get("listdatas")).toBean(List.class);
        JSONArray maps = list.get(0);
        List<EmailRecordReceiver> emailRecordReceivers = JSONObject.parseArray(maps.toString(), EmailRecordReceiver.class);
        emailRecord.setReceivers(emailRecordReceivers);
        emailService.send(emailRecord);
        applicationContext.publishEvent(new EmailSendEvent(emailRecord.getId().toString()));
        return emailRecord;
    }

    //发送邮件
    @RequestMapping(value = "/apiSend",method = RequestMethod.POST)
    public EmailRecord sendSms(@RequestBody EmailRecord emailRecord){
        emailService.send(emailRecord);
        applicationContext.publishEvent(new EmailSendEvent(emailRecord.getId().toString()));
        return emailRecord;
    }
}
