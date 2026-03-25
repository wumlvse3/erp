package com.jcloud.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.system.dao.UserDao;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.UserService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 创 建: 超级管理员
 * 日 期: 2021-01-07
 * 描 述: 用户
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public R saveUser(UserEntity Entity) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        Entity.setUsername(Entity.getUsername().trim());
        queryWrapper.eq("username", Entity.getUsername());
        List<UserEntity> list = list(queryWrapper);
        if (list.size() > 0) {
            return R.error("账号已经存在");
        }
        QueryWrapper<UserEntity> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("status", 1);
        queryWrapper2.eq("mobile", Entity.getMobile());
        List<UserEntity> list2 = list(queryWrapper2);
        if (list2.size() > 0) {
            return R.error("电话号码重复");
        }
        QueryWrapper<UserEntity> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("status", 1);
        queryWrapper3.eq("email", Entity.getEmail());
        List<UserEntity> list3 = list(queryWrapper3);
        if (list3.size() > 0) {
            return R.error("邮箱重复");
        }
        String password = Entity.getPassword();
        if (StringUtils.isNotBlank(password)) {//密码就修改密码
//            R pwdAllRule = passwordrulesService.getPwdAllRule(password);
//            if (pwdAllRule.getCode().equals("0")) {
                Entity.setPassword(ShiroUtils.md5(password, Entity.getUsername()));
                Entity.setPassworddate(new Date());
//            } else return pwdAllRule;
        }
        Integer regionGrade = Entity.getRegionGrade();
//        if (Entity.getDeptId().equals("")) Entity.setDeptId(null);
        if (regionGrade == null) {
            Entity.setRegionGrade(3);
        }
//        Entity.setUserIdCreate(ShiroUtils.getId());
        Entity.setGmtCreate(new Date());
        if (Entity.getStatus() == null) {
            Entity.setStatus(1);
        }
        try {
            save(Entity);
        } catch (Exception e) {
            System.err.println(e);
            return R.error("数据错误");
        }
        return R.ok();
    }

    public R saveUser2(UserEntity Entity) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        Entity.setUsername(Entity.getUsername().trim());
        queryWrapper.eq("username", Entity.getUsername());
        List<UserEntity> list = list(queryWrapper);
        if (list.size() > 0) {
            return R.error("账号已经存在");
        }
        String password = Entity.getPassword();
        if (StringUtils.isNotBlank(password)) {//密码就修改密码
//            R pwdAllRule = passwordrulesService.getPwdAllRule(password);
//            if (pwdAllRule.getCode().equals("0")) {
            Entity.setPassword(ShiroUtils.md5(password, Entity.getUsername()));
            Entity.setPassworddate(new Date());
//            } else return pwdAllRule;
        }
        Integer regionGrade = Entity.getRegionGrade();
//        if (Entity.getDeptId().equals("")) Entity.setDeptId(null);
        if (regionGrade == null) {
            Entity.setRegionGrade(3);
        }
//        Entity.setUserIdCreate(ShiroUtils.getId());
        Entity.setGmtCreate(new Date());
        if (Entity.getStatus() == null) {
            Entity.setStatus(1);
        }
        try {
            save(Entity);
        } catch (Exception e) {
            System.err.println(e);
            return R.error("数据错误");
        }
        return R.ok();
    }
}