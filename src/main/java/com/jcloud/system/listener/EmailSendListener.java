package com.jcloud.system.listener;

import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.enums.EmailStatusEnum;
import com.jcloud.system.event.EmailSendEvent;
import com.jcloud.system.mapper.EmailRecordMapper;
import com.jcloud.system.service.SendEmailService;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class EmailSendListener implements ApplicationListener<EmailSendEvent> {
    public static final Logger LOGGER = LoggerFactory.getLogger(EmailSendListener.class);

    @Autowired
    private EmailRecordMapper emailRecordMapper;
    @Autowired
    Map<String, SendEmailService> emailMap;


    @Async
    @Override
    public void onApplicationEvent(final EmailSendEvent event) {
        int recordId = NumberUtils.toInt(event.getSource().toString());
        EmailRecord emailRecord = emailRecordMapper.findRecordById(recordId);
        try {
            SendEmailService sendEmailService = emailMap.getOrDefault(emailRecord.getSendMethod(), null);
            if(sendEmailService==null){
                emailRecord.setStatus(EmailStatusEnum.FAIL);
                LOGGER.error("未找到发送邮件的方式, record_id:" + recordId);
                throw new RuntimeException();
            }
            sendEmailService.sendEmail(emailRecord);
            emailRecord.setStatus(EmailStatusEnum.SUCCESS);
        } catch (Exception e) {
            emailRecord.setStatus(EmailStatusEnum.FAIL);
            LOGGER.error("email send error, record_id:" + recordId, e);
        } finally {
            emailRecord.setSendTime(new Date());
            emailRecordMapper.updateRecord(emailRecord);
        }
    }

}
