package com.holland.email.conf;

import com.holland.common.spring.configuration.NacosConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@ComponentScan(
        basePackages = "com.holland.common.spring.configuration"
        , excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NacosConfig.class)
)
@Configuration
public class HollandConf {
}
