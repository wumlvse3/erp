package com.jcloud.system.service;

import com.jcloud.system.entity.EmailModule;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.mapper.EmailModuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailModuleService{
    @Autowired
    private EmailModuleMapper emailModuleMapper;

    public EmailRecord getEmailContentByParam(EmailRecord emailRecord, Map param){
        EmailModule module =emailModuleMapper.findByModuleCode(emailRecord.getTemplateCode());
        if(module!=null){
            String content = emailRecord.getTemplateLanguage().equalsIgnoreCase("zh")?module.getModuleContent():module.getModuleContentEng();
            Object[] keys = param.keySet().toArray();
            for(Object obj:keys){
                String source=obj.toString();
                String traget=param.get(obj).toString();
                content=content.replace(source,traget);
            }
            emailRecord.setContent(content);
            String subject=module.getSubjectPrefix()+emailRecord.getSubject();
            emailRecord.setSubject(subject);
        }
        return emailRecord;
    }

//    public EmailModule updateObj(EmailModule bean){
//        Integer userId = ShiroUtils.getId();
//        bean.setUpdateBy(userId);
//        bean.setUpdateTime(new Date());
//        emailModuleMapper.updateById(bean);
//        return bean;
//    }
//
//    public EmailModule addObj(EmailModule bean) throws Exception {
//        EmailModule old = emailModuleMapper.findByModuleCode(bean.getModuleCode());
//        if(old!=null){
//            throw new Exception("模板编号重复");
//        }
//        Integer userId = ShiroUtils.getId();
//        bean.setCreateBy(userId);
//        bean.setCreateTime(new Date());
//        emailModuleMapper.insert(bean);
//        return bean;
//    }
}
