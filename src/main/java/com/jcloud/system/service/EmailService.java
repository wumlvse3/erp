package com.jcloud.system.service;

import cn.hutool.json.JSONUtil;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.entity.EmailRecordEnclosure;
import com.jcloud.system.entity.EmailRecordParam;
import com.jcloud.system.enums.EmailSendTypeEnum;
import com.jcloud.system.enums.EmailStatusEnum;
import com.jcloud.system.mapper.EmailRecordMapper;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailService{
    public static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private EmailModuleService emailModuleService;
    @Autowired
    private EmailRecordMapper emailRecordMapper;
    @Autowired
    Map<String, SendEmailService> emailMap;
    @Autowired
    SqlService sqlService;

    @Transactional
    public EmailRecord send(EmailRecord emailRecord) {
        emailRecord.setOrgId(ShiroUtils.getOrgId());
        if(ShiroUtils.getUserEntity()!=null){
            emailRecord.setCreateBy(ShiroUtils.getUserEntity().getUsername());
        }
        emailRecord.setCreateTime(new Date());
        emailRecord.setStatus(EmailStatusEnum.WAIT);//等待发送
        if(StringUtils.isEmpty(emailRecord.getContent()) && StringUtils.isNotEmpty(emailRecord.getTemplateCode()) && !CollectionUtils.isEmpty(emailRecord.getParam())){
            Map<String, String> maps = emailRecord.getParam().stream().collect(Collectors.toMap(EmailRecordParam::getParamKey,EmailRecordParam :: getParamValue));
            emailRecord = emailModuleService.getEmailContentByParam(emailRecord, maps);
        }
        emailRecordMapper.insertObj(emailRecord);
        Integer recordId=emailRecord.getId();

        emailRecord.getReceivers().forEach(emailRecordReceiver -> {
            emailRecordReceiver.setOrgId(ShiroUtils.getOrgId());
            if(ShiroUtils.getUserEntity()!=null){
                emailRecordReceiver.setCreateBy(ShiroUtils.getUserEntity().getUsername());
            }

            emailRecordReceiver.setRecordId(recordId);
            emailRecordReceiver.setCreateTime(new Date());
            if (emailRecordReceiver.getSendType() == null) {
                emailRecordReceiver.setSendType(EmailSendTypeEnum.T);//主送
            }
            if (StringUtils.isEmpty(emailRecordReceiver.getName())) {
                emailRecordReceiver.setName(emailRecordReceiver.getEmail());
            }
            emailRecordMapper.insertReceiver(emailRecordReceiver);
        });
        if(emailRecord.getParam()!=null && emailRecord.getParam().size()>0){
            emailRecord.getParam().forEach(emailRecordParam -> {
                emailRecordParam.setRecordId(recordId);
                emailRecordMapper.insertParam(emailRecordParam);
            });
        }
        if(emailRecord.getEmailRecordEnclosures()!=null && emailRecord.getEmailRecordEnclosures().size()>0){
            emailRecord.getEmailRecordEnclosures().forEach(emailRecordEnclosure -> {
                emailRecordEnclosure.setOrgId(ShiroUtils.getOrgId());
                if(ShiroUtils.getUserEntity()!=null){
                    emailRecordEnclosure.setCreateBy(ShiroUtils.getUserEntity().getUsername());
                }
                emailRecordEnclosure.setRecordId(recordId);
                emailRecordEnclosure.setCreateTime(new Date());
                emailRecordMapper.insertEnclosure(emailRecordEnclosure);
            });
        }
        return emailRecord;
    }

    /**
     * 带文件发送（自带文件）
     * @param emailRecord
     * @return
     */
    public EmailRecord sendSms(EmailRecord emailRecord){
        String templateCode = emailRecord.getTemplateCode();
        String sql = "select enclosures_status,enclosures_file from  email_module where module_code = '"+templateCode+"'";
        Map<String, Object> one = sqlService.getOne(sql);
        if(one!= null){
            if(ObjectUtils.oGetS(one.get("enclosures_status")).equals("1")){
                String enclosuresFile = ObjectUtils.oGetS(one.get("enclosures_file"));
                if(!enclosuresFile.equals("") && !enclosuresFile.equals("[]")){
                    List< Map<String, Object>> list = JSONUtil.parseArray(enclosuresFile).toBean(List.class);
                    Map<String, Object> map = list.get(0);
                    List<EmailRecordEnclosure> emailRecordEnclosures = emailRecord.getEmailRecordEnclosures();
                    EmailRecordEnclosure emailRecordEnclosure =  new EmailRecordEnclosure();
                    emailRecordEnclosure.setEnclosureName(ObjectUtils.oGetS(map.get("name")));
                    emailRecordEnclosure.setEnclosurePath(
                            ObjectUtils.oGetS(
                                    sqlService.getOnef(
                                            "select url from sys_file where  id = '"+map.get("id")+"'")));
                    if(emailRecordEnclosures == null){
                        emailRecordEnclosures =  new ArrayList<EmailRecordEnclosure>();
                    }
                    emailRecordEnclosures.add(emailRecordEnclosure);
                    emailRecord.setEmailRecordEnclosures(emailRecordEnclosures);
                }
            }
        }
        EmailRecord send = this.send(emailRecord);
        Integer recordId=send.getId();
        try {
            SendEmailService sendEmailService = emailMap.getOrDefault(send.getSendMethod(), null);
            if(sendEmailService==null){
                send.setStatus(EmailStatusEnum.FAIL);
                LOGGER.error("未找到发送邮件的方式, record_id:" + recordId);
                throw new RuntimeException();
            }
            //发送邮件
            sendEmailService.sendEmail(send);
            send.setStatus(EmailStatusEnum.SUCCESS);
        } catch (Exception e) {
            send.setStatus(EmailStatusEnum.FAIL);
        } finally {
            send.setSendTime(new Date());
            emailRecordMapper.updateRecord(send);
        }
        return send;
    }

    /**
     * 带文件发送
     * @param emailRecord
     * @param josnFile
     * @return
     */
    public EmailRecord sendSms(EmailRecord emailRecord,String josnFile){
        String templateCode = emailRecord.getTemplateCode();
        String sql = "select enclosures_status,enclosures_file from  email_module where module_code = '"+templateCode+"'";
        Map<String, Object> one = sqlService.getOne(sql);
        if(one!= null){
            if(ObjectUtils.oGetS(one.get("enclosures_status")).equals("1")){
                if(!josnFile.equals("") && !josnFile.equals("[]")){
                    List< Map<String, Object>> list = JSONUtil.parseArray(josnFile).toBean(List.class);
                    List<EmailRecordEnclosure> emailRecordEnclosures = emailRecord.getEmailRecordEnclosures();
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> map = list.get(i);
                        EmailRecordEnclosure emailRecordEnclosure =  new EmailRecordEnclosure();
                        emailRecordEnclosure.setEnclosureName(ObjectUtils.oGetS(map.get("name")));
                        emailRecordEnclosure.setEnclosurePath(
                                ObjectUtils.oGetS(
                                        sqlService.getOnef(
                                                "select url from sys_file where  id = '"+map.get("id")+"'")));
                        if(emailRecordEnclosures == null){
                            emailRecordEnclosures =  new ArrayList<EmailRecordEnclosure>();
                        }
                        emailRecordEnclosures.add(emailRecordEnclosure);
                    }
                    emailRecord.setEmailRecordEnclosures(emailRecordEnclosures);
                }
            }
        }
        EmailRecord send = this.send(emailRecord);
        Integer recordId=send.getId();
        try {
            SendEmailService sendEmailService = emailMap.getOrDefault(send.getSendMethod(), null);
            if(sendEmailService==null){
                send.setStatus(EmailStatusEnum.FAIL);
                LOGGER.error("未找到发送邮件的方式, record_id:" + recordId);
                throw new RuntimeException();
            }
            //发送邮件
            sendEmailService.sendEmail(send);
            send.setStatus(EmailStatusEnum.SUCCESS);
        } catch (Exception e) {
            send.setStatus(EmailStatusEnum.FAIL);
        } finally {
            send.setSendTime(new Date());
            emailRecordMapper.updateRecord(send);
        }
        return send;
    }

}
