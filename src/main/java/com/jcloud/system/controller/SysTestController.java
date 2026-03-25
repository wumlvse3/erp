package com.jcloud.system.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.system.entity.*;
import com.jcloud.system.enums.EmailSendTypeEnum;
import com.jcloud.system.service.EmailService;
import com.jcloud.system.service.FileService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.IPUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 测试接口
 *
 * @Auther: yang
 * @Date: 2023/05/12/9:36
 * @Description:
 */
@Controller
@RequestMapping("systest")
public class SysTestController {

    @Autowired
    SqlService sqlService;


    @ResponseBody
    @GetMapping("/test1")
    public R test1(HttpServletRequest request) {
        String clientIP = IPUtils.getIpAddr(request);
        return R.ok("连接成功").setdata(clientIP);
    }

    @ResponseBody
    @GetMapping("/test2")
    public R test2() {
        UserEntity userEntity = ShiroUtils.getUserEntity();
        System.out.println(userEntity);
        String orgId = ShiroUtils.getOrgId();
        System.out.println(orgId);
        Session session = ShiroUtils.getSession();
        Object test123 = session.getAttribute("test123");
        return R.ok("连接成功").setdata(test123);
    }



    /**
     * 自定义提交（模板）
     *
     * @param params 表单数据
     * @return
     */
    @ResponseBody
    @PostMapping("/normsave")
    public R save(@RequestBody Map<String, Object> params) {
        System.out.println(params.toString()); //表单数据
        Long regionId = Long.valueOf(params.get("regionId").toString());  //域id
        String unitCode = ObjectUtils.oGetS(params.get("unitCode"));  //单元编号
        String listCode = ObjectUtils.oGetS(params.get("listCode"));  //数列编号
        Map<String, Object> unitdatas = JSONUtil.toBean(ObjectUtils.oGetS(params.get("unitdatas")), Map.class);//单元数据
        List<List<Map<String, Object>>> listdatas = JSONUtil.parseArray(ObjectUtils.oGetS(params.get("listdatas"))).toBean(List.class);
        ; //列表数据
        UserEntity userEntity = ShiroUtils.getUserEntity();
        ShiroUtils.getOrgId();
        Session session = ShiroUtils.getSession();
        Object orgId = session.getAttribute("orgId");
        System.out.println(userEntity);
        return R.ok();
    }


    @Autowired
    FileService fileService;
    @Autowired
    private EmailService emailService;

    @ResponseBody
    @GetMapping("gettest3")
    public R gettest3() {
        EmailRecord record = new EmailRecord();
        record.setSubject("您有新的业务提醒（业务类型：完善档案，单据号 ：）");
        record.setTemplateCode("VENDOR_ENTER");
        record.setSendMethod("noEnclosure");
        record.setTemplateLanguage("zh");
        List<EmailRecordReceiver> recordReceivers = new ArrayList<>();
        EmailRecordReceiver receiver = new EmailRecordReceiver();
        receiver.setSendType(EmailSendTypeEnum.T);
        receiver.setEmail("329759830@qq.com");
        recordReceivers.add(receiver);
        record.setReceivers(recordReceivers);
        List<EmailRecordParam> recordParams = new ArrayList<>();
        EmailRecordParam recordParam = new EmailRecordParam();
        recordParam.setParamKey("vendorname");
        recordParam.setParamValue("11");
        recordParams.add(recordParam);
        record.setParam(recordParams);
        emailService.sendSms(record);
        return R.ok();
    }


    /**
     * 流程配置
     *
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("/tasktest")
    public R tasktest(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        System.out.println("yangaaaa");
        return R.ok();
    }

    /**
     * 添加人员
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getvendor")
    public R tasktest2() {
        String orgId = ShiroUtils.getOrgId();
        String orgId1 = ShiroUtils.getUserEntity().getOrgId();
        String sql = "select  vendor_id from  sys_org_vendor_relation  WHERE  vendor_org_id =" + orgId1 + "  and  org_id = " + orgId.toString();
        Session session = ShiroUtils.getSession();
        ShiroUtils.setSessionAttribute("orgId", orgId.toString());
        String vendor_id = ObjectUtils.oGetS(sqlService.getOnef(sql));
        session.setAttribute("vendor_id", vendor_id);
        return R.ok();
    }

    /**
     * 添加流程变量
     *
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("/tasktest3")
    public R tasktest3(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        params.get("unitData");//流程变量
        params.get("taskData");//流程实体参数
        Map<Object, Object> variables = new HashMap<>();
        variables.put("type", "1");
        return R.ok().put("variables", JSONUtil.toJsonStr(variables));
    }
}
