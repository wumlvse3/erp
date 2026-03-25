package com.jcloud.system.service;
/**
 * 带附件的方式发送邮件（附件id）
 */

import com.jcloud.customer.base.FileUtil;
import com.jcloud.system.config.EmailConfig;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.entity.EmailRecordEnclosure;
import com.jcloud.system.entity.EmailRecordParam;
import com.jcloud.system.minio.MinioUtil;
import com.jcloud.system.utils.EmailUtils;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service("enclosure")
public class SendEmailEnclosure implements SendEmailService {
    @Autowired
    private EmailConfig emailConfig;
    @Autowired
    private EmailModuleService emailModuleService;
    @Autowired
    SqlService sqlService;
    @Autowired
    MinioUtil minioUtil;

    @Async
    @Override
    public Boolean sendEmail(EmailRecord emailRecord) {
//        if(emailRecord.getSendMethod().equals("enclosure")){
//            emailRecord.getEmailRecordEnclosures().forEach(enclosure -> {
//                String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"email/file/"+ enclosure.getEnclosureName();
//                byte[] bytes = ComponentUtil.getAwsFileUrlInfo(template, enclosure.getEnclosureId());
//                FileUtils.byteToFile(bytes, filePath);
//                enclosure.setEnclosurePath(filePath);
//            });
//        }

        Map<String, String> toUsers = new HashMap<>();
        Map<String, String> copyUsers = new HashMap<>();
        Map<String, String> bccUsers = new HashMap<>();
        emailRecord.getReceivers().forEach(emailRecordReceiver -> {
            switch (emailRecordReceiver.getSendType()) {
                case T: {
                    toUsers.put(emailRecordReceiver.getEmail(), emailRecordReceiver.getName());
                    break;
                }
                case C: {
                    copyUsers.put(emailRecordReceiver.getEmail(), emailRecordReceiver.getName());
                    break;
                }
                case B: {
                    bccUsers.put(emailRecordReceiver.getEmail(), emailRecordReceiver.getName());
                    break;
                }
            }
        });

        Assert.isTrue(MapUtils.isNotEmpty(toUsers) || MapUtils.isNotEmpty(copyUsers) || MapUtils.isNotEmpty(bccUsers), "邮件接收人不能为空");

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", emailConfig.getHost());
        props.put("mail.smtp.port", emailConfig.getPort());
        props.put("mail.smtp.ssl.enable", emailConfig.getOpenSSL());

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUserName(), emailConfig.getPassword());
                    }
                });
        session.setDebug(true);
        try {
            System.getProperties().setProperty("mail.mime.splitlongparameters","false");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getUserName()));
            message.setRecipients(Message.RecipientType.TO, EmailUtils.getReceiver(toUsers));   // like inzi769@gmail.com
            if(!copyUsers.isEmpty()){
                message.setRecipients(Message.RecipientType.CC, EmailUtils.getReceiver(copyUsers));
            }
            if(!bccUsers.isEmpty()){
                message.setRecipients(Message.RecipientType.BCC, EmailUtils.getReceiver(bccUsers));
            }
            message.setSubject(emailRecord.getSubject());

            // 可以装载多个主体部件,可以把它当成是一个集合
            MimeMultipart partList = new MimeMultipart();
            // 把邮件的内容设置为多部件的集合对象

            // 创建一个部件
            MimeBodyPart part1 = new MimeBodyPart();
            // 给部件指定内容
            part1.setContent(emailRecord.getContent(),"text/html;charset=UTF-8");
            // 部件添加到集合中
            partList.addBodyPart(part1);
            if(emailRecord.getEmailRecordEnclosures()!=null && emailRecord.getEmailRecordEnclosures().size()!=0){
                for (EmailRecordEnclosure emailRecordEnclosure:emailRecord.getEmailRecordEnclosures()) {
                    // 又创建一个部件
                    MimeBodyPart part2 = new MimeBodyPart();
                    // 为部件指定附件
//                    InputStream input = minioUtil.getInput(emailRecordEnclosure.getEnclosurePath());
//                    File file = File.createTempFile(emailRecordEnclosure.getEnclosureName(), emailRecordEnclosure.getEnclosureName());
//                    FileOutputStream fos = new FileOutputStream(file);
//                    fos.write(input.read());
//                    fos.close();
                    File file = minioUtil.getFile(emailRecordEnclosure.getEnclosurePath());
                    part2.attachFile(file);
//                    // 为部件指定附件
//                    part2.attachFile(new File( sqlService.getUploadPath() + emailRecordEnclosure.getEnclosurePath()));
                    // 指定附件文件的名字,使用MimeUtility.encodeText()对中文进行编码
                    part2.setFileName(MimeUtility.encodeText(emailRecordEnclosure.getEnclosureName()));
                    partList.addBodyPart(part2);
                }
            }
            message.setContent(partList);
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            //无论发送是否成功删除文件
            try {
                for (EmailRecordEnclosure emailRecordEnclosure:emailRecord.getEmailRecordEnclosures()) {
                    File file=new File(emailRecordEnclosure.getEnclosurePath());
                    if(file!=null){
                        file.delete();
                    }
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
