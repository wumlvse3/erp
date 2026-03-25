package com.jcloud.timedTask.taskSet;

import com.alibaba.fastjson.JSONObject;
import com.jcloud.timedTask.TimedTask;
import com.jcloud.timedTask.entity.InterfaceInfo;

/**
 * @author banana
 * @date 2020/9/10
 * @time 17:15
 */
public class TestTask extends TimedTask {

    @Override
    protected String preToken(InterfaceInfo interfaceInfo) {
        return null;
    }

    @Override
    protected String replyToken(InterfaceInfo interfaceInfo) {
        return null;
    }

    @Override
    protected JSONObject result(JSONObject preResponse) {

        System.out.println(preResponse.toJSONString());
        return null;
    }

    @Override
    protected void replyResult(JSONObject replyResponse) {

    }
}
