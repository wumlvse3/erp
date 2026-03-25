package com.jcloud.system.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * RedisSessionDao，以Redis持久化方式做Session共享，无需配置即可支持集群
 *
 * @Auther: yang
 * @Date: 2023/4/17 17:32
 * @Description:
 */
public class RedisSessionDao extends AbstractSessionDAO {

    /**
     * Session超时时间（秒）
     */
    private long expireTime;

    @Autowired
    private RedisTemplate sessionRedisTemplate;

    public RedisSessionDao(long expireTime) {
        this.expireTime = expireTime;
    }

    private static final String SESSION_KEY_PREFIX = "shiro:session:"; // 键前缀


    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        sessionRedisTemplate.opsForValue().set(
                buildKey(sessionId),
                session,
                expireTime,
                TimeUnit.SECONDS
        );
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        Object o = sessionRedisTemplate.opsForValue().get(buildKey(sessionId));
        if ((o != null) && o.equals("other login")) {
            throw new RuntimeException("other login");
        }
        return (Session) sessionRedisTemplate.opsForValue().get(buildKey(sessionId));
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            sessionRedisTemplate.opsForValue().set(
                    buildKey(session.getId()),
                    session,
                    expireTime,
                    TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void delete(Session session) {
        if (session != null && session.getId() != null) {
            sessionRedisTemplate.delete(buildKey(session.getId()));
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        // 使用前缀匹配所有会话键
        Collection<Serializable> keys = sessionRedisTemplate.keys(buildKey("*"));
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return sessionRedisTemplate.opsForValue().multiGet(keys);
    }

    // 生成带前缀的键，避免与其他 Redis 数据冲突
    private String buildKey(Serializable sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

}