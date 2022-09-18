package com.holland.nacos.conf;

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
import java.util.function.Consumer;

public abstract class NacosPropKit {
    protected static final Logger logger = LoggerFactory.getLogger(NacosPropKit.class);

    static Class<?> INSTANCE;

    public static void setInstance(Class<?> clazz) {
        INSTANCE = clazz;
    }

    static void init(ConfigService configService, String group) throws IllegalAccessException, NacosException, IOException {
        for (Field field : INSTANCE.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers())) continue;

            final String name = field.getName();
            final String content = configService.getConfig(name, group, 3000);
            if (content == null) {
                logger.error("NacosProp.{} is null", name);
                continue;
            }

            final Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            field.set(INSTANCE, properties);
        }
    }

    /**
     * @apiNote environment中只在初始化的时候赋值，监听修改environment是没意义的
     */
    public static void listen(ConfigService configService, String group, String dataId, Consumer<Properties> consumer) throws NacosException {
        final Field field;
        try {
            field = INSTANCE.getDeclaredField(dataId);
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
                    field.set(INSTANCE, properties);
                    consumer.accept(properties);
                    if (logger.isDebugEnabled()) logger.debug("refresh NacosProp.{}\n{}", dataId, configInfo);
                } catch (IOException | IllegalAccessException e) {
                    logger.error("更新配置异常", e);
                }
            }
        });
    }
}
