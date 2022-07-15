package com.holland.gateway.conf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.common.spring.configuration.GlobalExceptionHandle;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.kafka.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Import(GlobalExceptionHandle.class)
@Configuration
public class HollandConf {

    @Resource
    private UserCache userCache;

    @Resource(name = "requestMappingHandlerMapping")
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void init() {
        RequestUtil.init(userCache);
    }

    @Bean
    public AuthCheckMapping authCheckMapping() {
        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        final AuthCheckMapping authCheckMapping = new AuthCheckMapping(handlerMethods.size());
        handlerMethods.forEach(((requestMappingInfo, handlerMethod) -> {
            final String toString = requestMappingInfo.toString();
            final String name = toString.substring(1, toString.length() - 1);
            final AuthCheck annotation = handlerMethod.getMethodAnnotation(AuthCheck.class);
            authCheckMapping.put(name, annotation);
        }));
        return authCheckMapping;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public Producer kafkaProducer(@Value("${kafka.server}") String server
            , @Value("{kafka.groupId}") String groupId) {
        return new Producer(server, groupId);
    }
}
