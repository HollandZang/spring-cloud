package com.holland.filesystem.conf;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@EnableFeignClients
@ComponentScan("com.holland.common.spring.configuration")
@Configuration
public class HollandConf {
}
