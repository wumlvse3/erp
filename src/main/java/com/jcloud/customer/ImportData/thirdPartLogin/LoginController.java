package com.jcloud.customer.ImportData.thirdPartLogin;

import com.jcloud.system.annotation.Log;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.LoginService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import com.jcloud.system.vo.UserLoginReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 登录请求
 * @Auther: yang
 * @Date: 2023/03/27/10:12
 * @Description:
 */
@RequestMapping("login")
@Api(tags = {"登录验证"},produces = "主要用于第三方获取token")
@Controller
public class LoginController {


    @Autowired
    private LoginService loginService;

    @GetMapping
    @ApiOperation(value = "内部系统登录",hidden = true)
    String page(Model model){
        return  "login/login";
    }

    @Log("登录请求")
    @ResponseBody
    @PostMapping("/login")
    @Ignore
    @ApiOperation(value = "内部系统登录",hidden = true)
    public R login(@RequestBody Map<String, String> params){
        try {
            String mobile = params.get("userName");
            String password = params.get("password");
            Subject subject = ShiroUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(mobile, password);
            subject.login(token);
            String orgId = params.get("orgId");
            if(StringUtils.isBlank(orgId))orgId = "1";
            ShiroUtils.setSessionAttribute("orgId",orgId);
        } catch (UnknownAccountException e) {
//			return R.error(e.getMessage());
            return  R.error("账号或密码不正确");
        } catch (IncorrectCredentialsException e) {
            return R.error("账号或密码不正确");
        } catch (LockedAccountException e) {
            return R.error("账号已被禁用,请联系管理员");
        } catch (AuthenticationException e) {
            return R.error("账户验证失败");
        }
        return R.ok("登录成功");
    }


    @Log("对外登录")
    @ResponseBody
    @PostMapping("/login/foreign")
    @ApiOperation("三方系统获取授权token")
    public R loginForeign(@RequestBody UserLoginReq req){
        return loginService.userVerify(req.getUserName(),req.getPassword());
    }



}
