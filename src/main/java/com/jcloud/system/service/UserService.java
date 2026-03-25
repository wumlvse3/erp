package com.jcloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.utils.R;

/**
 *
 *
 * 创 建: 超级管理员
 * 日 期: 2021-01-07
 * 描 述: 物料大小类, 业务接口
 *
 */
public interface UserService extends IService<UserEntity> {

    R saveUser(UserEntity Entity);

    R saveUser2(UserEntity Entity);
}