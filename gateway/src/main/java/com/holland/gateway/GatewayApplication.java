package com.holland.gateway;

import com.holland.gateway.conf.NacosProp;
import com.holland.nacos.conf.NacosPropKit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@EnableDiscoveryClient
@MapperScan("com.holland.gateway.mapper")
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        NacosPropKit.setInstance(NacosProp.class);
        SpringApplication.run(GatewayApplication.class, args);
    }

}
