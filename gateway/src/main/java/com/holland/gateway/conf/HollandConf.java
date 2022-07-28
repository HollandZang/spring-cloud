package com.holland.gateway.conf;

import com.alibaba.cloud.commons.lang.StringUtils;
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
import com.holland.redis.Redis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

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

        RequestUtil.init(userCache);
    }

    @Bean
    public Redis redis(@Value("${spring.redis.host}") String host
            , @Value("${spring.redis.port}") int port) {
        return new Redis(host, port);
    }

    @Bean
    public ConfigService configService() {
        return configService;
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }


    @Bean
    public AuthCheckMapping authCheckMapping() {
        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        final AuthCheckMapping authCheckMapping = new AuthCheckMapping(handlerMethods.size());
        handlerMethods.forEach(((requestMappingInfo, handlerMethod) -> {
            final String name = requestMappingInfo.getMethodsCondition().getMethods().stream().findFirst().get()
                    + " " +
                    requestMappingInfo.getPatternsCondition().getDirectPaths().stream().filter(StringUtils::isNotBlank).findFirst().orElse("/");
            final AuthCheck annotation = handlerMethod.getMethodAnnotation(AuthCheck.class);
            authCheckMapping.put(name, annotation == null ? null : Arrays.stream(annotation.values()).collect(Collectors.toList()));
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
