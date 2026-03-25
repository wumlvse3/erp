package com.jcloud.timedTask.exception;

/**
 * @author banana
 * @date 2020/9/10
 * @time 10:54
 * 关于定时任务的异常类
 */
public class TaskException extends Exception{
    public TaskException(){
        super();
    }
    public TaskException(String s) {
        super(s);
    }
}
