package com.holland.nacos.conf;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class NacosPropKit {
    private static final Logger logger = LoggerFactory.getLogger(NacosPropKit.class);

    static Class<?> INSTANCE;
    /* [namespace, ConfigService] */
    static Map<String, ConfigService> configServiceManager;
    static Set<NacosConfMeta> nacosConfMetas;

    public static void setInstance(Class<?> clazz) {
        INSTANCE = clazz;
    }

    /**
     * @param serverAddr 配置环境的值
     * @param namespace  配置环境的值
     * @param group      配置环境的值
     */
    static void init(String serverAddr, String namespace, String group) throws NacosException, IOException, IllegalAccessException {
        NacosPropKit.nacosConfMetas = NacosConfMeta.genConfigs(namespace, group);
        NacosPropKit.configServiceManager = new HashMap<>(8);
        for (NacosConfMeta meta : nacosConfMetas) {
            if (!NacosPropKit.configServiceManager.containsKey(meta.namespace)) {
                final Properties properties = new Properties();
                properties.put("serverAddr", serverAddr);
                properties.put("namespace", meta.namespace);
                ConfigService configService = NacosFactory.createConfigService(properties);
                NacosPropKit.configServiceManager.put(meta.namespace, configService);
            }
        }
        for (NacosConfMeta meta : nacosConfMetas) {
            ConfigService configService = configServiceManager.get(meta.namespace);
            final String content = configService.getConfig(meta.dataId, meta.group, 3000);
            if (content == null) {
                logger.error("NacosProp.{} is null", meta.dataId);
                continue;
            }

            final Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            meta.field.set(INSTANCE, properties);
            if (logger.isDebugEnabled()) logger.debug("load NacosProp.{}\n{}", meta, properties);
        }
    }

    /**
     * @apiNote environment中只在初始化的时候赋值，监听修改environment是没意义的
     */
    public static void listen(String fieldName, Consumer<Properties> consumer) throws NacosException, NoSuchFieldException {
        final NacosConfMeta meta = findConfigService(fieldName);
        final ConfigService configService = configServiceManager.get(meta.namespace);

        configService.addListener(meta.dataId, meta.group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                final Properties properties = new Properties();
                try {
                    properties.load(new ByteArrayInputStream(configInfo.getBytes(StandardCharsets.UTF_8)));
                    meta.field.set(INSTANCE, properties);
                    consumer.accept(properties);
                    if (logger.isDebugEnabled()) logger.debug("refresh NacosProp.{}\n{}", meta.dataId, configInfo);
                } catch (IOException | IllegalAccessException e) {
                    logger.error("更新配置异常", e);
                }
            }
        });
    }

    private static NacosConfMeta findConfigService(String fieldName) {
        final Field field;
        try {
            field = INSTANCE.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        Optional<NacosConfMeta> meta = nacosConfMetas.stream()
                .filter(p -> p.field.equals(field))
                .findFirst();
        if (!meta.isPresent())
            throw new RuntimeException("can not found NacosConfMeta from the filed: " + fieldName);
        return meta.get();
    }
}
