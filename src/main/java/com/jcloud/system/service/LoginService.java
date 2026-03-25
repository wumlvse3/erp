package com.jcloud.system.service;

import com.jcloud.system.utils.R;

public interface LoginService {

    /**
     * 账号验证
     * @param name  账号
     * @param password  密码
     * @return
     */
    R userVerify(String name, String password);


    Boolean verifyKe(String key);
}
