package com.jcloud.system.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.jcloud.system.utils.RSAUtil.encipher;

/**
 * 待办任务链接生成
 *
 * @ClassName: TaskUrlUtils
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2024-11-18 18:08
 **/
@Component
public class TaskUrlUtils {
    @Value("${jcloud.localWebUrl}")
    private String localWebUrl;
    /**
    * 生成单点登录业务处理链接
    *
     * @param userId 用户登录账号
     * @param delayDay 有效期天数
     * @param businessUrl 业务跳转URL（注意URL中的？要替换成&）
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2024/11/19 9:51
    */
    public  String genTaskUrl(String userId,int delayDay,String businessUrl){
        if(delayDay<=0){
            delayDay = 3;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, delayDay);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        String disableDate = sp.format(calendar.getTime());
        String randomIndex = String.valueOf(calendar.getTimeInMillis());
        StringBuffer dataBuffer =new StringBuffer();
        dataBuffer.append(userId);
        dataBuffer.append(",");
        dataBuffer.append(disableDate);
        dataBuffer.append(",");
        dataBuffer.append(randomIndex);
        String encryptedData=encipher(dataBuffer.toString());
        String url = localWebUrl+"/login/thirdtjlogin?yangverify="+encryptedData+"&yangrandom="+randomIndex+"&url="+businessUrl;
        return url;
    }
}
