package com.holland.gateway;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;

public class PubConf {
    public static void main(String[] args) throws NacosException {
        final Properties properties = new Properties();
        properties.put("serverAddr", "114.115.212.83:8848");
        properties.put("namespace", "e45ec5af-12d8-4b45-a1f4-5eea3cf7a816");
        ConfigService configService = NacosFactory.createConfigService(properties);
        final String content = configService.getConfig("gateway", "DEFAULT_GROUP", 3000);
        System.out.println(content);

        final Properties properties1 = new Properties();
        properties1.put("serverAddr", "114.115.212.83:8848");
        properties1.put("namespace", "e45ec5af-12d8-4b45-a1f4-5eea3cf7a816");
        ConfigService configService1 = NacosFactory.createConfigService(properties);
        final boolean b = configService1.publishConfig("gateway", "DEFAULT_GROUP", content, ConfigType.PROPERTIES.getType());
        System.out.println(b);
    }
}
