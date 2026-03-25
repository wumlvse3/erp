package com.jcloud.system.task.web;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.task.domain.TaskInfo;
import com.jcloud.system.task.service.TaskInfoService;
import com.jcloud.system.task.vo.TaskVo;
import com.jcloud.system.utils.R;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务基本信息
 * **/
@RestController
@RequestMapping("/taskInfo")
public class TaskInfoController {
    @Autowired
    private TaskInfoService service;

    /**
     * 获取任务基本信息
     * **/
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public TaskInfo getObj (@PathVariable("id") String id ) {
        return  service.getObj(id);
    }
    /**
     * 获取任务基本信息列表
     * **/
    @RequestMapping(method = RequestMethod.GET)
    public List<TaskInfo> getObjs(Wrapper<TaskInfo> wrapper){
        return service.getObjs(wrapper);
    }
    /**
     * 修改任务基本信息
     * **/
    @RequestMapping(method = RequestMethod.PUT)
    public TaskInfo modifyObj( @RequestBody TaskInfo factory){
        return service.modifyObj(factory);
    }
    /**
     * 删除任务基本信息
     * **/
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public TaskInfo deleteObj(@PathVariable("id") Long id){
        return  service.deleteObj(id);
    }

    /**
    *根据任务代码获取任务详情
    */
    @RequestMapping(value="/by/{code}",method = RequestMethod.GET)
    public TaskInfo getObjByCode(@PathVariable("code") String code) {
        return  service.getObjByCode(code);
    }
    /**
    * 拒绝任务
    */
    @RequestMapping(value="/reject",method = RequestMethod.POST)
    public R rejectTaskByCode(@RequestBody Map<String,Object> params) {
        String str = (String) params.get("unitdatas");
        TaskVo taskVo = JSON.parseObject(str, TaskVo.class);
        return  service.rejectTaskByCode(taskVo);
    }
    /**
     * 接受任务
     */
    @RequestMapping(value="/accept",method = RequestMethod.POST)
    public R acceptTaskByCode(@RequestBody Map<String,Object> params) {
        String str = (String) params.get("unitdatas");
        TaskVo taskVo = JSON.parseObject(str, TaskVo.class);
        return  service.acceptTaskByCode(taskVo);
    }
    /**
     * 暂停任务
     *
     */
    @RequestMapping(value="/pending",method = RequestMethod.POST)
    public R pendingTaskByCode(@RequestBody Map<String,Object> params) {
        String str = (String) params.get("unitdatas");
        TaskVo taskVo = JSON.parseObject(str, TaskVo.class);
        return  service.pendingTaskByCode(taskVo);
    }
    /**
     * 取消任务
     */
    @RequestMapping(value="/cancel",method = RequestMethod.POST)
    public R cancelTaskByCode(@RequestBody Map<String,Object> params) {
        String str = (String) params.get("unitdatas");
        TaskVo taskVo = JSON.parseObject(str, TaskVo.class);
        return  service.cancelTaskByCode(taskVo);
    }
    /**
     * 完成任务
     */
    @RequestMapping(value="/finish",method = RequestMethod.POST)
    public R finishTaskByCode(@RequestBody Map<String,Object> params) {
        String str = (String) params.get("unitdatas");
        TaskVo taskVo = JSON.parseObject(str, TaskVo.class);
        return  service.finishTaskByCode(taskVo);
    }
    /**
     * 派生任务
     */
    @RequestMapping(value="/derive",method = RequestMethod.POST)
    public R deriveTaskByCode(@RequestBody Map<String,Object> params) {
        String str = (String) params.get("unitdatas");
        TaskVo taskVo = JSON.parseObject(str, TaskVo.class);
        return  service.deriveTaskByCode(taskVo);
    }

    /**
    * 统计任务数量
    *
     * @param param
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2022/12/9 14:25
    */
//    @RequestMapping(value="/group/status",method = RequestMethod.GET)
//    public Map countTaskVo(ListParam<TaskInfo> param,@AuthenticationPrincipal UserInfo userInfo) {
//        return  service.countTaskVo(param,userInfo);
//    }

    /**
     * 首页查询代办
     */
    @RequestMapping("/needToHandle")
    public Map needToHandle(@Param("offset")Integer offset,@Param("size")Integer size){
        return service.needToHandle(offset,size);
    }

    /**
     * 首页查询已办
     */
    @RequestMapping("/finished")
    public Map finished(@Param("offset")Integer offset,@Param("size")Integer size){
        return service.finished(offset,size);
    }

    /**
     * 根据业务id（sheet_id）完成任务
     */
    @RequestMapping(value = "/finishBySheetId/{sheet_id}",method = RequestMethod.POST)
    public R finishBySheetId(@PathVariable("sheet_id") String sheet_id){
        return service.finishBySheetId(sheet_id);
    }

}
