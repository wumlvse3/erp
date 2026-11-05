package com.jcloud.epd.Controller;

import com.jcloud.epd.entity.WoInfo;
import com.jcloud.epd.service.WoInfoService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 工作令优先级 Controller
 */
@RestController
@RequestMapping("/woInfo")
public class WoInfoController {

    @Autowired
    private WoInfoService woInfoService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public R addWo(@RequestBody WoInfo woInfo) {
        R userCheck = fillRecordInfo(woInfo);
        if (userCheck != null) {
            return userCheck;
        }
        return woInfoService.addWo(woInfo);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R updateWo(@RequestBody WoInfo woInfo) {
        R userCheck = fillRecordInfo(woInfo);
        if (userCheck != null) {
            return userCheck;
        }
        return woInfoService.updateWo(woInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R queryList(@RequestBody Map<String, Object> params) {
        String companyCode = params.get("companyCode") != null ? params.get("companyCode").toString() : null;
        return woInfoService.queryList(companyCode);
    }

    /**
     * 填充维护人信息
     */
    private R fillRecordInfo(WoInfo woInfo) {
        UserEntity user = ShiroUtils.getUserEntity();
        if (user == null) {
            return R.error("用户未登录");
        }
        woInfo.setRecord_code(user.getUsername());
        woInfo.setRecord_name(user.getName());
        woInfo.setRecord_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return null;
    }
}
