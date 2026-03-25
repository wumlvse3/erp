package com.jcloud.system.controller;

import com.jcloud.system.entity.EmailModule;
import com.jcloud.system.mapper.EmailModuleMapper;
import com.jcloud.system.service.EmailModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/emailModule")
public class EmailModuleController {
//    @Autowired
//    private EmailModuleService emailModuleService;
//    @Autowired
//    private EmailModuleMapper emailModuleMapper;
//
//    @RequestMapping( method = RequestMethod.POST)
//    public EmailModule addObj(@RequestBody EmailModule bean) throws Exception {
//       return emailModuleService.addObj(bean);
//    }
//
//    @RequestMapping(method = RequestMethod.PUT)
//    public EmailModule updateObj(@RequestBody EmailModule bean){
//        return emailModuleService.updateObj(bean);
//    }
//
//    @RequestMapping(value="/{id}",method = RequestMethod.GET)
//    public EmailModule getSingleObj(@PathVariable("id") Long id){
//        return emailModuleMapper.selectById(id);
//    }
//
////    @RequestMapping(method = RequestMethod.GET)
////    public R getObjs(ListParam<EmailModule> listParam){
////        return emailModuleService.list(listParam);
////    }
//
//    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
//    public void deleteObj(@PathVariable("id")Long id){
//        emailModuleMapper.deleteById(id);
//    }
//
//    @RequestMapping(value = "/getEmailContentByParam/{modelCode}/{lan}",method = RequestMethod.POST)
//    public String getEmailContentByParam(@PathVariable("modelCode") String modelCode, @PathVariable("lan") String lan, @RequestBody Map map){
//        return emailModuleService.getEmailContentByParam(modelCode,lan,map);
//    }


}
