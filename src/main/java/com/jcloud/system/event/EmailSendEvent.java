package com.jcloud.system.event;

import org.springframework.context.ApplicationEvent;

public class EmailSendEvent extends ApplicationEvent {

    public EmailSendEvent(String emailId) {
        super(emailId);
    }
}
