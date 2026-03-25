package com.jcloud.system.service;

import com.jcloud.system.utils.R;
import java.util.Map;

/**
 * @Auther: yang
 * @Date: 2023/7/13
 * @Description: 流程方法
 */
public interface ActivtiService {



    //  .............流程操作.............

    /**
     * 流程标准保存与流程提交
     *
     * @param params
     * @return
     */
    R normsave(Map<String, Object> params);



    /**
     * 流程提交(指定表单)
     * @param region 域
     * @param unit 单元
     * @param params 数据
     * @param normsave 保存R
     * @param variables 改变或者新增流程变量
     * @return
     */
    R taskSubmitUnit(String region, String unit,Map<String, Object> params, R normsave,Map<String, Object> variables);


    /**
     * 流程提交
     * @param params 数据
     * @param normsave 保存R
     * @param variables 改变或者新增流程变量
     * @return
     */
    R taskSubmit(Map<String, Object> params, R normsave,Map<String, Object> variables);

    /**
     * 获取流程变量传入流程
     *
     * @param taskdate1 固定变量
     * @param taskdate2 界面变量
     * @param map       变量
     * @param vars      流程变量存入
     */
    public void getvars(String taskdate1, String taskdate2, Map<String, Object> map, Map<String, Object> vars);

    /**
     * 启动流程
     *
     * @param procDefKey    流程定义KEY
     * @param businessTable 业务表表名
     * @param businessId    业务表编号
     * @param title         流程标题，显示在待办任务标题
     * @param vars          流程变量
     * @param taskkeymap    流程节点属性变量
     * @return 流程实例ID
     */
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars, Map<String, Object> taskkeymap);

    /**
     * 流程提交
     *
     * @param taskId     流程实例id
     * @param vars       变量
     * @param feedback   意见
     * @param taskkeymap 流程节点属性变量
     */
    public void complete(String taskId, Map<String, Object> vars, String feedback, Map<String, Object> taskkeymap);

}
