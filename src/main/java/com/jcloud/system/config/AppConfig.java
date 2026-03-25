package com.jcloud.system.config;

import com.jcloud.system.Intercepter.MyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

//import org.apache.catalina.ssi.SSIServlet;

@Configuration
@ConditionalOnMissingClass
public class AppConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private MyInterceptor myInterceptor;
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
		/**
		 * 多个拦截器组成一个拦截器链 addPathPatterns 
		 * 用于添加拦截规则 excludePathPatterns 
		 * 用户排除拦截
		 */
    	 registry.addInterceptor(this.myInterceptor);// wps的回调接口
        super.addInterceptors(registry);
    }
    
}
