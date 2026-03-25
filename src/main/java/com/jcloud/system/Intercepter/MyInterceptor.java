package com.jcloud.system.Intercepter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jcloud.customer.base.IpKit;
import com.jcloud.customer.base.RedisUtils;
import com.jcloud.system.config.JcloudConfig;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.LoginService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Component
public class MyInterceptor implements HandlerInterceptor {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	RedisUtils redisUtils;

	@Autowired
	private JcloudConfig jcloudConfig;

	@Autowired
	private LoginService loginService;



	@Override
	public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object method, Exception e) throws Exception {
		String configPath = "submitCheck.properties";
		Properties properties = new Properties();
		properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(configPath));
		String urls = properties.getProperty("urls");
		String url =req.getRequestURI();
		if(!urls.contains(url)){
			return;
		}
		UserEntity user = ShiroUtils.getUserEntity();
		String ip= IpKit.getRealIp(req);
		String key = user.getUserId()+"-"+ip+"-"+url;
		redisUtils.deleteValue(key);


//		if(url.contains("onlinebiddinghead/vendorquote")){
//			BufferedReader reader = req.getReader();
//			StringBuilder body = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				body.append(line);
//			}
//			reader.close();
//
//			// 解析请求体的内容
//			Map<String, Object> map = JSONUtil.toBean(new JSONObject(body.toString()), Map.class);
//			String type  = map.get("type").toString();
//			String package_id = map.get("package_id").toString();
//			if(type.equals("H")){
//				key = url+"-"+package_id;
//				redisUtils.deleteValue(key);
//			}
//		}


	}


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String configPath = "submitCheck.properties";

		Properties properties = new Properties();
		properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(configPath));
		String urls = properties.getProperty("urls");
		String url =request.getRequestURI();

//		对外接口验证
		String[] split = jcloudConfig.getInterfaceUrl().split(",");
		for (int i = 0; i < split.length; i++) {
			if(url.startsWith(split[i])){
				String syskey = request.getHeader("token");
                if(syskey == null){
                    response.setCharacterEncoding("UTF-8"); // 设置字符集为 UTF-8
                    response.setContentType("application/json; charset=utf-8");
                    response.getWriter().write("{\n" +
                            "  \"data\": \"\",\n" +
                            "  \"status\": \"FAILED\",\n" +
                            "  \"reason\": \"Failed to verify key, please retrieve the key again\"\n" +
                            "}");
                    return false;
                }
				if(!loginService.verifyKe(syskey)){
					response.setCharacterEncoding("UTF-8"); // 设置字符集为 UTF-8
					response.setContentType("application/json; charset=utf-8");
					response.getWriter().write("{\n" +
							"  \"data\": \"\",\n" +
							"  \"status\": \"FAILED\",\n" +
							"  \"reason\": \"Failed to verify key, please retrieve the key again\"\n" +
							"}");
					return false;
				}
				break;
			}
		}
//		其他验证
		if(!urls.contains(url)){
			return true;
		}
		String ip= IpKit.getRealIp(request);
		UserEntity user = ShiroUtils.getUserEntity();
		String key = user.getUserId()+"-"+ip+"-"+url;
		Long time = new Date().getTime();
		Long last = redisUtils.getObjectValue(key,Long.class);
		if(last!=null){
			throw new Exception("重复请求:"+key);
		}
		redisUtils.setObjectValueWithDuration(key,time, Duration.ofSeconds(60));

//		if(url.contains("onlinebiddinghead/vendorquote")){
//			BufferedReader reader = request.getReader();
//			StringBuilder body = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				body.append(line);
//			}
////			reader.close();
//
//			// 解析请求体的内容
//			Map<String, Object> map = JSONUtil.toBean(new JSONObject(body.toString()), Map.class);
//			String type  = map.get("type").toString();
//			String package_id = map.get("package_id").toString();
//			if(type.equals("H")){
//				key = url+"-"+package_id;
//				Long lastReq = redisUtils.getObjectValue(key,Long.class);
//				if(lastReq!=null){
//					throw new Exception("失败");
//				}else{
//                    redisUtils.setObjectValueWithDuration(key,time, Duration.ofSeconds(5));
//                }
//			}
//
//
//		}


		return true;
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {
		logger.info(">>>MyInterceptor>>>>>>>请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）");

	}
}
