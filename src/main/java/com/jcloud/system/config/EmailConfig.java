package com.jcloud.system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Data
@Configuration
public class EmailConfig {

    @Value("${email.host}")
    private String host;

    @Value("${email.port}")
    private Integer port;

    @Value("${email.user_name}")
    private String userName;

    @Value("${email.password}")
    private String password;

    @Value("${email.nick_name}")
    private String nickName;

    @Value("${timeOut:30000}")
    private String timeOut;

    @Value("${email.ssl}")
    private Boolean openSSL;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
