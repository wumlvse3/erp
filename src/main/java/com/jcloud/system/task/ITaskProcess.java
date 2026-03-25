package com.jcloud.system.task;

import java.util.Map;

public interface ITaskProcess<T> {
    /**
    * 创建任务
    *
     * @param bean 任意类型得对象
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2022/11/30 11:55
    */
    public String createTask(T bean);
    /**
    * 删除任务
    * @param sourceBean 业务数据对象
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2022/11/30 11:55
    */
    public Map cancelTask(T sourceBean);
    /**
     * 完成任务
     * @param sourceBean 业务数据对象
     * @return: No such property: code for class: Script1
     * @author: CPL
     * @date: 2022/11/30 11:55
     */
    public Map finishTask(T sourceBean);

}
