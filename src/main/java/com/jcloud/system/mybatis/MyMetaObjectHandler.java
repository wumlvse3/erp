package com.jcloud.system.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jcloud.customer.WmsBaseData.service.MtlSheetTypesService;
import com.jcloud.system.annotation.SheetCode;
import com.jcloud.system.shiro.ShiroUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author banana
 * @date 2020/9/17
 * @time 9:33
 * 日期填充
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 判断是否存在进行填充，如果不为空，就不处理
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        Class entityClass = metaObject.getOriginalObject().getClass();
        Field[] fieldNames = entityClass.getDeclaredFields();
        for(Field fieldName:fieldNames){
            if(fieldName.isAnnotationPresent(SheetCode.class)){
                MtlSheetTypesService mtlSheetTypesService = applicationContext.getBean(MtlSheetTypesService.class);
                String sheet_type_id="";
                try{
                    sheet_type_id = (String) metaObject.getValue("sheet_type_id");
                }catch (ReflectionException e){
                    sheet_type_id = (String)metaObject.getValue("sheetTypeId");
                }

                String code = mtlSheetTypesService.generateCodeById(sheet_type_id);
                this.strictInsertFill(metaObject, fieldName.getName(), String.class, code);
            }
        }
        if (metaObject.hasGetter("create_time")){
            Class<?> create_time = metaObject.getGetterType("create_time");
            if(create_time.getName().equals(LocalDateTime.class.getName())){

                this.strictInsertFill(metaObject, "create_time", LocalDateTime.class, LocalDateTime.now());
            }else{
                this.strictInsertFill(metaObject, "create_time", Date.class, new Date());
            }

        }
        if(metaObject.hasGetter("create_date")){
            this.strictInsertFill(metaObject, "create_date", Date.class, new Date());
        }
            if(metaObject.hasGetter("create_by")){
                try{
                    if(ShiroUtils.getUserEntity()!=null){
                        String userId = ShiroUtils.getUserEntity().getUsername().toString();
                        if(userId != null){
                            this.strictInsertFill(metaObject, "create_by", String.class, userId);
                        }
                    }
                }catch (Exception e){

                }

            }
            if(metaObject.hasGetter("create_by_name")){
                try{
                    if(ShiroUtils.getUserEntity()!=null){
                        String userName = ShiroUtils.getUserEntity().getName();
                        if(userName != null){
                            this.strictInsertFill(metaObject, "create_by_name", String.class, userName);
                        }
                    }
                }catch (Exception e){

                }


            }
        if (metaObject.hasSetter("createTime")){
            Class<?> create_time = metaObject.getGetterType("createTime");
            if(create_time.getName().equals(LocalDateTime.class.getName())){

                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            }else{
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
            }

        }
        if(metaObject.hasSetter("createBy")){
            try{
                if(ShiroUtils.getUserEntity()!=null) {
                    String userId = ShiroUtils.getUserEntity().getUserId().toString();
                    if (userId != null) {
                        this.strictInsertFill(metaObject, "createBy", String.class, userId);
                    }
                }
            }catch (Exception e){

            }

        }
        if(metaObject.hasSetter("createByName")){
            try{
                if(ShiroUtils.getUserEntity()!=null){
                    String userName = ShiroUtils.getUserEntity().getName();
                    if(userName != null){
                        this.strictInsertFill(metaObject, "createByName", String.class, userName);
                    }
                }
            }catch (Exception e){

            }

        }
    }
    @Override
    public void updateFill(MetaObject metaObject) {

        if (metaObject.hasGetter("update_time")){
            Class<?> update_time = metaObject.getGetterType("update_time");
            if(update_time.getName().equals(LocalDateTime.class.getName())){
                this.strictUpdateFill(metaObject, "update_time", LocalDateTime.class, LocalDateTime.now());
            }else{
                this.strictUpdateFill(metaObject, "update_time", Date.class, new Date());
            }
        }
        if(metaObject.hasGetter("update_date")){
            this.strictUpdateFill(metaObject, "update_date", Date.class, new Date());
        }
        if(metaObject.hasGetter("update_by")){
            try{
                String userId = ShiroUtils.getUserEntity().getUsername().toString();
                if(userId != null){
                    this.strictUpdateFill(metaObject, "update_by", String.class, userId);
                }
            }catch (Exception e){

            }

        }
        if(metaObject.hasGetter("update_by_name")){
            try{
                String userName = ShiroUtils.getUserEntity().getName();
                if(userName != null){
                    this.strictUpdateFill(metaObject, "update_by_name", String.class, userName);
                }
            }catch (Exception e){

            }
        }


//        if (metaObject.hasSetter("updateTime")){
//            Class<?> update_time = metaObject.getGetterType("updateTime");
//            if(update_time.getName().equals(LocalDateTime.class.getName())){
//                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
//            }else{
//                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
//            }
//        }
//        if(metaObject.hasGetter("updateBy")&&ShiroUtils.getUserEntity()!=null){
//            String userId = ShiroUtils.getUserEntity().getUserId().toString();
//            if(userId != null){
//                this.strictUpdateFill(metaObject, "updateBy", String.class, userId);
//            }
//        }
//        if(metaObject.hasGetter("updateByName")&&ShiroUtils.getUserEntity()!=null){
//            String userName = ShiroUtils.getUserEntity().getName();
//            if(userName != null){
//                this.strictUpdateFill(metaObject, "updateByName", String.class, userName);
//            }
//        }
    }

    /**
     * @Description: 重写默认的strictFillStrategy，不论是否为null都更新。 默认为：null更新
     * @Param: [metaObject, fieldName, fieldVal]
     * @return: com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
     */
    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {

        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }

//    /**
//     * @Author: brbai
//     * @Create: 2020-09-08 14:58:19
//     * @Description: 获取用户
//     * @Param:
//     * @return: com.xjr.mvc.base.organization.xjr_base_user.Xjr_base_user
//     */
//    private Xjr_base_user getUser(){
//
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//
//        String token = request.getParameter("token");
//        String fMobile = request.getParameter("f_mobile");
//        RedisUtils redisUtils = new RedisUtils(stringRedisTemplate,objectRedisTemplate);
//
//        Xjr_base_user user = redisUtils.getObjectValue(fMobile + "_" + token, Xjr_base_user.class);
//        return user;
//    }
}
