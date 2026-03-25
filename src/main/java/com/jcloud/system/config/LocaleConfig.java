package com.jcloud.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.concurrent.Executor;

@Configuration
@EnableWebMvc
public class LocaleConfig implements WebMvcConfigurer {

    private final Executor taskExecutor;


    public LocaleConfig(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * 默认解析器 其中locale表示默认语言,当请求中未包含语种信息，则设置默认语种
     * 当前默认为CHINA,zh_CN
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }


    /**
     * 默认拦截器 其中lang表示切换语言的参数名
     * 拦截请求，获取请求参数lang种包含的语种信息并重新注册语种信息
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // 参数名
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }



    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor((AsyncTaskExecutor) taskExecutor);
        configurer.setDefaultTimeout(-1); // 设置超时时间，-1 表示不超时
    }
}
