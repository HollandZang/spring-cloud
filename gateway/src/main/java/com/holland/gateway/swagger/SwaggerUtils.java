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
        return url.startsWith("/webjars")
                || "/doc.html".equals(url)
                || url.endsWith("/v2/api-docs")
                || url.startsWith("/swagger-")
                || "/favicon.ico".equals(url);
    }
}
