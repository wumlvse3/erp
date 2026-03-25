package com.jcloud.system.service;

import com.jcloud.system.entity.EmailRecord;
import org.springframework.stereotype.Service;

@Service
public interface SendEmailService {

    Boolean sendEmail(EmailRecord emailRecord);

}
