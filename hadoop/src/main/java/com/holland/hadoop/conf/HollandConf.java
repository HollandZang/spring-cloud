package com.holland.hadoop.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;

@ComponentScan("com.holland.common.spring.configuration")
@Configuration
public class HollandConf {

    @Bean
    public HadoopConfig hadoopConfig(@Value("${hadoop.url}") String hdfsPath, @Value("${spring.application.name}") String appName) throws IOException, URISyntaxException {
        return new HadoopConfig(hdfsPath, appName);
    }
}
