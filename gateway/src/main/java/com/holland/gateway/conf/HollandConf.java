package com.holland.gateway.conf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.holland.common.spring.configuration.GlobalExceptionHandle;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.kafka.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Import(GlobalExceptionHandle.class)
@Configuration
public class HollandConf {

    @Resource
    private UserCache userCache;

    @PostConstruct
    public void init() {
        RequestUtil.init(userCache);
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
