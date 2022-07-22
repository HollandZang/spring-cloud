package com.holland.gateway.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Profile("!pro")
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {
    private final Logger logger = LoggerFactory.getLogger(Knife4jConfiguration.class);

    @Value("${spring.application.name}")
    private String name;
    @Value("${server.port}")
    private String port;

    @Bean
    public Docket defaultApi2() {
        logger.info("enable swagger: http://localhost:{}/doc.html", port);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo(name))
                //分组名称  想要网关被记录到swagger就不要开分组
//                .groupName("2.X版本")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.holland." + name + ".controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo groupApiInfo(String name) {
        return new ApiInfoBuilder()
                .title("后端接口文档-" + name)
                .description("<div style='font-size:14px;color:red;'>description</div>")
                .termsOfServiceUrl("N")
                .contact(new Contact("HollanZang", "https://juejin.cn/user/352263461681214", "zhn.pop@gmail.com"))
                .version("1.0")
                .build();
    }
}
