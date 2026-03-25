package com.jcloud.system.config;


import com.jcloud.system.shiro.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro的配置文件
 * @Auther: yang
 * @Date: 2023/3/29 13:43
 * @Description:
 */
@Configuration
@AutoConfigureAfter(ShiroLifecycleBeanPostProcessorConfig.class)
public class ShiroConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxTotal;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

//    session超时时间 （秒）
    private Long expireTime = 3600L;

	@Bean(name = "redisSessionDAO")
	public RedisSessionDao redisSessionDao() {
		return new RedisSessionDao(expireTime);
	}

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        if (StringUtils.isNotBlank(password)) {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout,password);
            redisManager.setJedisPool(jedisPool);
            redisManager.setPassword(password);
        }else {
            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
            redisManager.setJedisPool(jedisPool);
        }
//        System.out.println(env);
        return redisManager;
    }

    @Bean("jsonSessionFactory")
    JsonSessionFactory jsonSessionFactory(){
        return new JsonSessionFactory();
    }


    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager();
        cacheManager.setRedisManager(redisManager());
        cacheManager.setPrincipalIdFieldName("userId");
        return cacheManager;
    }

    /**
     * 单机环境，session交给shiro管理
     * 会话管理器
     */
    @Bean("sessionManager")
    public DefaultWebSessionManager sessionManager() {
        CustomSessionManager sessionManager = new CustomSessionManager();
        // redis session
        sessionManager.setSessionDAO(redisSessionDao());
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionFactory(jsonSessionFactory());
        // 禁用重写url
         sessionManager.setSessionIdUrlRewritingEnabled(false);
        // 禁用session 放在cookie
        // sessionManager.setSessionIdCookieEnabled(false);
        sessionManager.setSessionValidationInterval(expireTime * 1000);
        // 会话过期时间，单位：毫秒(在无操作时开始计时)--->一分钟,用于测试
        sessionManager.setGlobalSessionTimeout(expireTime * 1000);

        return sessionManager;
    }

    /**
     * 集群环境，session交给spring-session管理
     */
    @Bean
    public ServletContainerSessionManager servletContainerSessionManager() {
        return new ServletContainerSessionManager();
    }


    private SimpleCookie rememberMeCookie() {
        // 设置 cookie 名称，对应 login.html 页面的 <input type="checkbox" name="rememberMe"/>
        SimpleCookie cookie = new SimpleCookie("jcloudCookie");
        // 设置 cookie 的过期时间，单位为秒 一个月
        cookie.setMaxAge(3600 * 30);
        return cookie;
    }


    private CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie 加密的密钥
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }


    @Bean("securityManager")
    public SecurityManager securityManager(UserRealm userRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//		securityManager.setCacheManager(new EhCacheManager());
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(rememberMeManager());

        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setUnauthorizedUrl("/");
        shiroFilter.setSuccessUrl("/index");
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/css/**", "anon");
        filterMap.put("/js/**", "anon");
        filterMap.put("/images/**", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/login/**", "anon");
        filterMap.put("/img/**", "anon");
        filterMap.put("/sys/**", "anon");
        filterMap.put("/systest/**", "anon");
        filterMap.put("/-resources/**","anon");
        filterMap.put("/swagger-ui/**","anon");
        filterMap.put("/v3/**","anon");
        filterMap.put("/vendorInfo/rigister", "anon");
        filterMap.put("/areaInfo/getList/**", "anon");
        filterMap.put("/websocket/**", "anon");
        filterMap.put("/myWebsocket/**", "anon");
        filterMap.put("/notice/rangeAll", "anon");
        filterMap.put("/notice/find/inviteTenders", "anon");
        filterMap.put("/itemsCategory/**", "anon");
        filterMap.put("/vendorCategoryFilesSetting/**", "anon");
        filterMap.put("/vendorInfo/getById", "anon");
        filterMap.put("/shareInfo/public", "anon");
        filterMap.put("/jd/**","anon");
        filterMap.put("/notice/bidding/public", "anon");
        filterMap.put("/doc.html**", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/thirdPart/**", "anon");
        filterMap.put("/screw/**", "anon");
        filterMap.put("/bas/orgapproval/**", "anon");//企业注册
        filterMap.put("/bas/orgshow/orgcodegetdata", "anon");//企业登录页面获取页面数据
        filterMap.put("/itsm/**", "anon");//运维系统
        filterMap.put("/noticeContent/**", "anon");//获取组织的
        filterMap.put("/commonBiddingContent/noLoginOrg", "anon");//通知公告
        filterMap.put("/registerAgreement/getData", "anon");//供应商注册协议
        filterMap.put("/**", "authc");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5"); //加密方式
        credentialsMatcher.setHashIterations(16);//散列次数
        return credentialsMatcher;

    }
}
