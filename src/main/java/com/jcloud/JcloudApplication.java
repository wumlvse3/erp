package com.jcloud;

import com.jcloud.system.utils.R;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@EnableAsync
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableTransactionManagement
@MapperScan({"com.jcloud.system.mapper","com.jcloud.system.dao","com.jcloud.customer.*.mapper","com.jcloud.system.task.mapper","com.jcloud.timedTask.mapper","com.jcloud.epd.mapper","com.jcloud.pur.mapper","com.jcloud.erp.epd.mapper","com.jcloud.erp.ord.mapper","com.jcloud.erp.pur.mapper","com.jcloud.easypoi.excel.config.mapper","com.jcloud.easypoi.excel.mapper"})
public class JcloudApplication  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(JcloudApplication.class, args);
    }

    /**
     * 打war包修修改启动方式  extends SpringBootServletInitializer
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        System.out.println("外部tomcat,chapter启动!");
        return application.sources(JcloudApplication.class);
    }
    
    // 添加根路径处理方法
    @RequestMapping("/")
    @ResponseBody
    public R index() {
        return R.ok("应用启动成功！").setdata("当前端口：8083");
    }
}
