package com.holland.gateway.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SwaggerUtils {

    @Value("${spring.profiles.active}")
    private String springEnv;

    /**
     * 判断是否开启swagger了
     */
    public boolean enabledSwagger() {
        return !"pro".equals(springEnv);
    }

    public boolean isSwaggerRequest(String url) {
        return url.startsWith("/doc.html")
                || url.startsWith("/webjars/css")
                || url.startsWith("/webjars/js")
                || url.startsWith("/webjars/img")
                || url.endsWith("/v2/api-docs")
                || url.endsWith("/swagger-resources/configuration/security")
                || url.endsWith("/swagger-resources/configuration/ui")
                || url.endsWith("/swagger-resources");
    }
}
