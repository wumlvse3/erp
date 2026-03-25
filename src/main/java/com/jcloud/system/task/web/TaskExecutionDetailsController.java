package com.jcloud.system.task.web;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.jcloud.system.task.domain.TaskExecutionDetails;
import com.jcloud.system.task.service.TaskExecutionDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务执行明细
 * **/
@RestController
@RequestMapping("/taskExecutionDetails")
public class TaskExecutionDetailsController {
    @Autowired
    private TaskExecutionDetailsService service;
    /**
     * 获取任务执行明细
     * **/
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public TaskExecutionDetails getObj (@PathVariable("id") String id ) {
        return  service.getObj(id);
    }
    /**
     * 获取任务执行明细列表
     * **/
    @RequestMapping(method = RequestMethod.GET)
    public List<TaskExecutionDetails> getObjs(Wrapper<TaskExecutionDetails> param){
        return service.getObjs(param);
    }
    /**
     * 修改任务执行明细
     * **/
    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    public TaskExecutionDetails modifyObj(@PathVariable("id") String id, @RequestBody TaskExecutionDetails factory){
        return service.modifyObj(id,factory);
    }
    /**
     * 删除任务执行明细
     * **/
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public TaskExecutionDetails deleteObj(@PathVariable("id") String id){
        return  service.deleteObj(id);
    }
    /**
     * 新增任务执行明细
     * **/
    @RequestMapping(method = RequestMethod.POST)
    public TaskExecutionDetails addObj(@RequestBody TaskExecutionDetails taskExecutionDetails) {
        return  service.addObj(taskExecutionDetails);
    }
    /**
     * 获取单个任务的执行明细
     * **/
    @RequestMapping(value="/code/{code}",method = RequestMethod.GET)
    public List<TaskExecutionDetails> getObjsByTaskCode(@PathVariable("code")String taskCode){
        return service.getObjsByTaskCode(taskCode);
    }
}
