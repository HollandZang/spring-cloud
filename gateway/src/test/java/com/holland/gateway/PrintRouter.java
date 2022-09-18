package com.holland.gateway;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.AuthCheckMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PrintRouter {
    @Resource(name = "requestMappingHandlerMapping")
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    void contextLoads() {
        /* 打印注解提供的路由 */
        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        final AuthCheckMapping authCheckMapping = new AuthCheckMapping(handlerMethods.size());
        handlerMethods.forEach(((requestMappingInfo, handlerMethod) -> {
            //noinspection OptionalGetWithoutIsPresent
            final String name = requestMappingInfo.getMethodsCondition().getMethods().stream().findFirst().get()
                    + " " +
                    requestMappingInfo.getPatternsCondition().getDirectPaths().stream().filter(StringUtils::isNotBlank).findFirst().orElse("/");
            final AuthCheck annotation = handlerMethod.getMethodAnnotation(AuthCheck.class);
            authCheckMapping.put(name, annotation == null ? null : Arrays.stream(annotation.values()).collect(Collectors.toList()));
        }));

        authCheckMapping.forEach((k, v) -> {
            final String collect = v == null ? "" : v.stream().map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(","));
            final String r = k.replaceAll(" ", "\\\\u0020");
            System.err.printf("%s=%s\n", r, collect);
        });
    }
}
