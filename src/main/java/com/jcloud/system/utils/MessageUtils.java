package com.jcloud.system.utils;
/**
 * 国际化多语言工具
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
public class MessageUtils {

    private static MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    /**
     * 获取单个国际化翻译值
     */
    public static String get(String msgKey,String otherMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(msgKey, null, locale)!=null ? messageSource.getMessage(msgKey, null, locale) : otherMessage;
        }catch (Exception e){
            return otherMessage;
        }
    }

    /**
     * 获取指定语言的单个国际化翻译值
     */
    public static String getValue(String msgKey, String language,String otherMessage) {
        try {
            //线上直接new Locale(language),会是小写en_us,调用本地方法读取资源的时候会识别不到，因为配置文件是大写的后缀en_US
            //所以这里拆分，然后拼装为大写的Locale的en_US
            String[] s = StringUtils.split(language, "_");
            Locale locale = new Locale(s[0], s[1]);
            return messageSource.getMessage(msgKey, null, locale)!=null ? messageSource.getMessage(msgKey, null, locale) : otherMessage;
        } catch (Exception e) {
            return otherMessage;
        }
    }
    /**
     * 带参数占位符替换
     */
    public static String get(String msgKey,String otherMessage,String...params) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            String result = messageSource.getMessage(msgKey, null, locale)!=null ? messageSource.getMessage(msgKey, null, locale) : otherMessage;
            if(params.length>0){
                MessageFormat temp = new MessageFormat(result);
                return temp.format(params);
            }else{
                return result;
            }
        }catch (Exception e){
            return otherMessage;
        }
    }


}
