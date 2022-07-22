package com.holland.gateway.conf;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executor;

public class NacosProp {
    private static final Logger logger = LoggerFactory.getLogger(NacosProp.class);

    public static Properties gateway;
    public static Properties gateway_router;

    static void load(ConfigService configService, String group) throws IllegalAccessException, NacosException, IOException {
        for (Field field : NacosProp.class.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers())) continue;

            final String name = field.getName();
            final String content = configService.getConfig(name, group, 3000);
            if (content == null) {
                logger.error("NacosProp.{} is null", name);
                continue;
            }

            final Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            field.set(NacosProp.class, properties);
        }
    }

    static void listen(ConfigService configService, String group, String dataId) throws NacosException {
        final Field field;
        try {
            field = NacosProp.class.getDeclaredField(dataId);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("NacosProp." + dataId + " is absent");
        }
        configService.addListener(dataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                final Properties properties = new Properties();
                try {
                    properties.load(new ByteArrayInputStream(configInfo.getBytes(StandardCharsets.UTF_8)));
                    field.set(NacosProp.class, properties);
                    if (logger.isDebugEnabled()) logger.debug("refresh NacosProp.{}\n{}", dataId, configInfo);
                } catch (IOException | IllegalAccessException e) {
                    logger.error("更新配置异常", e);
                }
            }
        });
    }
}
