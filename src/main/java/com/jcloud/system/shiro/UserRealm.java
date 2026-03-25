package com.jcloud.system.shiro;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcloud.system.dao.UserDao;
import com.jcloud.system.entity.UserEntity;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 认证
 */
@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserDao sysUserDao;



    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object primaryPrincipal = principals.getPrimaryPrincipal();
        System.out.println(primaryPrincipal);
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)authcToken;

        //查询用户信息
        UserEntity user = sysUserDao.selectOne(new QueryWrapper<UserEntity>().eq("username", token.getUsername()));
        //账号锁定
        if(user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getUsername()), getName());
        return info;
    }

    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher shaCredentialsMatcher = new HashedCredentialsMatcher();
        shaCredentialsMatcher.setHashAlgorithmName(ShiroUtils.hashAlgorithmName);
        shaCredentialsMatcher.setHashIterations(ShiroUtils.hashIterations);
        super.setCredentialsMatcher(shaCredentialsMatcher);
    }

//    /**
//     * 重写此方法，得到用户名缓存Key
//     */
//    @Override
//    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
//        UserEntity user = (UserEntity) principals.getPrimaryPrincipal();
//        return user.getName();
//    }
//
//    /**
//     * 重写此方法，得到用户名缓存Key
//     */
//    @Override
//    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
//        UserEntity user = (UserEntity) principals.getPrimaryPrincipal();
//        return user.getName();
//    }

    /**
     * 清除当前用户的权限认证缓存
     *
     * @param principals 权限信息 身份集合
     */
    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }
}