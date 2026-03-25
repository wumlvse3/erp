package com.jcloud.system.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 读取文件配置参数
 * @Auther: yang
 * @Date: 2023/4/4 15:01
 * @Description:
 */
@Component
@ConfigurationProperties(prefix = "jcloud")
public class JcloudConfig {
    //上传路径
    private String uploadPath;

    private String dbtype;

    private String dbname;

    private String redistime;

    private String interfaceUrl;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getDbtype() { return dbtype; }

    public void setDbtype(String dbtype) { this.dbtype = dbtype; }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getRedistime() {
        return redistime;
    }

    public void setRedistime(String redistime) {
        this.redistime = redistime;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }
}
