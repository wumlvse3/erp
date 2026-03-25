package com.jcloud.system.service.impl;

import com.jcloud.system.config.JcloudConfig;
import com.jcloud.system.service.LoginService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import com.jcloud.system.vo.UserLoginRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JcloudConfig jcloudConfig;

    @Override
    public R userVerify(String name, String password) {
        String s = ShiroUtils.md5(password, name);
        String sql  = "select user_id from sys_user  where username = '"+name+"' and  password = '"+s+"'";
//        String sql  = "select user_id from sys_user  where username = 'admin' and  password ='a6d90e27dc45489484947a5efa894528'";
        List<Map<String, Object>> all = sqlService.getAll(sql);
        if(all.size()>0){
            String s1 = generateRandomCode();
            String s2 = generateRandomCode();
            redisTemplate.opsForValue().set(s1,s2,
                    Integer.valueOf(jcloudConfig.getRedistime()),TimeUnit.MINUTES);
            UserLoginRes res = new UserLoginRes();
            res.setToken(s1+s2);
            return R.ok().setdata(res);
        }else return R.error("账号密码错误");
    }

    @Override
    public Boolean verifyKe(String key) {
        if(key.length()<24)return false;
        String s1 = key.substring(0, 12);
        String s2 = key.substring(12);
        return ObjectUtils.oGetS(redisTemplate.opsForValue().get(s1)).equals(s2);
    }

    String generateRandomCode() {
        String randomCode = UUID.randomUUID().toString().replace("-", "");
        return randomCode.substring(0, 12);
    }


    public static void main(String[] args) {
        String pattern1 = "/aaa/";
        String pattern2 = "/aaa/aa1";

        if (pattern2.startsWith(pattern1)) {
            System.out.println("pattern1 matches pattern2");
        } else {
            System.out.println("pattern1 does not match pattern2");
        }
    }
}
