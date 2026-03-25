package com.jcloud.customer.base;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisUtils {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	RedisTemplate redisTemplate;

	public String getStringValue(String key){
		if(redisTemplate==null){
			logger.error("objectRedisTemplate is null");
			return null;
		}
		logger.info("redis get value  : key ------ "+key);
		return stringRedisTemplate.opsForValue().get(key);
	}

	public <T> T getObjectValue(String key, Class<T> claz){
		if(redisTemplate==null){
			logger.error("objectRedisTemplate is null");
			return null;
		}
		logger.info("redis get value  : key ------ "+key);
		String obj = getStringValue(key);
		if (claz == null) {
			return (T) JSON.parse(obj);
		}
		return JSON.parseObject(obj, claz);
	}
	
	public <T> List<T> getListValue(String key, Class<T> claz) {
		if(redisTemplate==null){
			logger.error("objectRedisTemplate is null");
			return null;
		}
		logger.info("redis get value  : key ------ "+key);
		String obj = getStringValue(key);
		return JSON.parseArray(obj, claz);
	}

	public void setStringValueWithDuration(String key, String value, Duration duration){
		if(stringRedisTemplate==null){
			logger.error("stringRedisTemplate is null");
			return ;
		}
		logger.info("redis save string value : key ------ "+key+" value ----"+value);
		stringRedisTemplate.opsForValue().set(key, value,duration);
	}

	public void setStringValue(String key, String value){
		if(stringRedisTemplate==null){
			logger.error("stringRedisTemplate is null");
			return ;
		}
		logger.info("redis save string value : key ------ "+key+" value ----"+value);
		stringRedisTemplate.opsForValue().set(key, value);
	}
	public void setObjectValueWithDuration(String key, Object obj,Duration duration){
		if(redisTemplate==null){
			logger.error("objectRedisTemplate is null");
			return ;
		}
		String json = JSON.toJSONString(obj);
		logger.info("redis save object value : key ------ "+key+" value ----"+json);
		setStringValueWithDuration(key, json,duration);
	}

	public void setObjectValue(String key, Object obj){
		if(redisTemplate==null){
			logger.error("objectRedisTemplate is null");
			return ;
		}
		String json = JSON.toJSONString(obj);
		logger.info("redis save object value : key ------ "+key+" value ----"+json);
		setStringValue(key, json);
	}

	public Boolean deleteValue(String key){
		if(stringRedisTemplate==null){
			logger.error("stringRedisTemplate is null");
			return false;
		}
		logger.info("redis delete value  : key ------ "+key);
		return stringRedisTemplate.delete(key);
	}
}
