package com.jcloud.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 配置
 *
 * @ClassName: SwaggerConfig
 * @Description Swagger3.0集成，默认路径为 /swagger-ui/index.html
 * @author: bianxs
 * @since: v1.0
 * @Date: 2023-06-14 15:16
 **/
@Configuration
@EnableOpenApi
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createOAApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("商城内部接口")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.integration.oa.seeyon.vo")) // 设置扫描路径
//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createBiReportApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("BI报表")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.customer.bireport")) // 设置扫描路径
//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createTestApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("测试接口")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.customer.vendor.controller")) // 设置扫描路径
//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }

    @Bean
    public Docket createImportApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("第三方导入接口")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.customer.ImportData.thirdPartController")) // 设置扫描路径
//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createInnerImportApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("商城测试")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.customer.market.controller")) // 设置扫描路径
//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createJDApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("京东VSP调试")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.integration.jdvsp")) // 设置扫描路径

//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createBipApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("BIP调试")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.integration.bip")) // 设置扫描路径

//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createPurchaseApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("采购执行测试")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.customer.purchase.controller")) // 设置扫描路径

//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    @Bean
    public Docket createExternalApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")

                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                .groupName("第三方访问获取token")
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jcloud.customer.ImportData.thirdPartLogin")) // 设置扫描路径

//                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();

    }
    /**
     * 添加摘要信息
     */
    public ApiInfo apiInfo()
    {
        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
                // 设置标题
                .title("Api Document")
                // 描述
                .description("接口文档")
                // 作者信息
                .contact(new Contact("小佳", "", ""))
                // 版本
                .version("1.0.0")
                .build();
    }
}
