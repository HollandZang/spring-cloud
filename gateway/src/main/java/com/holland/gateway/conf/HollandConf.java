package com.holland.gateway.conf;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
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
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Import(GlobalExceptionHandle.class)
@Configuration
public class HollandConf {

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.group}")
    private String group;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;

    @Resource
    private UserCache userCache;

    @Resource(name = "requestMappingHandlerMapping")
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private ConfigService configService;

    @PostConstruct
    public void init() throws NacosException, IOException, IllegalAccessException {
        final Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        configService = NacosFactory.createConfigService(properties);
        NacosProp.load(configService, group);
        NacosProp.listen(configService, group, "gateway");

        RequestUtil.init(userCache);
    }

    @Bean
    public ConfigService configService() {
        return configService;
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
