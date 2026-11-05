package com.jcloud.epd.Controller;

import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 部门信息接口
 */
@RequestMapping("/sessiondept")
@Api(tags = {"部门信息"})
@RestController
public class LogindeptController {

    @Autowired
    private SqlService sqlService;

    @PostMapping("/loadToSession")
    @ApiOperation("根据登录人部门ID加载部门信息到session")
    public R loadDeptToSession() {
        UserEntity user = ShiroUtils.getUserEntity();
        if (user == null) {
            return R.error("用户未登录");
        }

        String deptId = user.getDeptId();
        if (deptId == null || deptId.isEmpty()) {
            return R.error("用户未绑定部门");
        }

        // 查询部门信息
        String sql = "select dept_code, name from sys_dept where dept_id = '" + deptId + "'";
        Map<String, Object> dept = sqlService.getOne(sql);

        if (dept == null) {
            return R.error("部门不存在");
        }

        // 保存到session
        ShiroUtils.setSessionAttribute("deptCode", dept.get("dept_code"));
        ShiroUtils.setSessionAttribute("deptName", dept.get("name"));

        return R.ok("部门信息已加载到session");
    }
}
