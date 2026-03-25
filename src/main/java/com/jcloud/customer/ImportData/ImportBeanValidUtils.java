package com.jcloud.customer.ImportData;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

/**
 * @ClassName: ImportBeanValidUtils
 * @Description TODO
 * @author: bianxs
 * @since: v1.0
 * @Date: 2023-11-15 15:21
 **/
@Component
public  class  ImportBeanValidUtils<T> {
    public String validNotBlankObj(T obj) throws Exception {
       Field[] fields =  obj.getClass().getDeclaredFields();
       StringBuffer result = new StringBuffer();
       for(Field field:fields){
           field.setAccessible(true);
           NotBlank annotation = field.getAnnotation(NotBlank.class);
           NotNull notNull = field.getAnnotation(NotNull.class);
           if(annotation!=null){
               if(field.getType().getClass().isInstance(String.class)){
                   String value = String.valueOf(field.get(obj));
                   if(StringUtils.isEmpty(value)||field.get(obj)==null){
                       result.append(field.getName());
                       result.append(":");
                       result.append(annotation.message());
                       result.append(";");
                   }
               }
           }
           if(notNull!=null){
               if(field.get(obj)==null){
                   result.append(field.getName());
                   result.append(":");
                   result.append(notNull.message());
                   result.append(";");
               }
           }
       }
        return result.toString();

    }
}
