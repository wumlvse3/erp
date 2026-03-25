package com.jcloud.system.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailUtils {
    public static InternetAddress[] getReceiver(Map<String, String> receiverMap) throws Exception{
        if (MapUtils.isEmpty(receiverMap)) {
            return new InternetAddress[0];
        }
        List<InternetAddress> receivers = new ArrayList<>();
        for (String toUserEmail : receiverMap.keySet()) {
            if(StringUtils.isEmpty(toUserEmail)) {
                continue;
            }
            if(StringUtils.isNotEmpty(receiverMap.get(toUserEmail))) {
                receivers.add(new InternetAddress(receiverMap.get(toUserEmail) + " <"+toUserEmail+">"));
            } else {
                receivers.add(new InternetAddress(toUserEmail + " <"+toUserEmail+">"));
            }
        }

        return receivers.toArray(new InternetAddress[receivers.size()]);
    }
}
