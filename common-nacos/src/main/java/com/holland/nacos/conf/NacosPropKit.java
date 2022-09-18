package com.holland.nacos.conf;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public abstract class NacosPropKit {
    protected static final Logger logger = LoggerFactory.getLogger(NacosPropKit.class);

    static Class<?> INSTANCE;

    public static void setInstance(Class<?> clazz) {
        INSTANCE = clazz;
    }

    static void init(Map<String, ConfigService> configServiceMap, Set<NacosConfPo> nacosConfPos) throws NacosException, IOException, IllegalAccessException {
        for (NacosConfPo po : nacosConfPos) {
            ConfigService configService = configServiceMap.get(po.namespace);
            final String content = configService.getConfig(po.dataId, po.group, 3000);
            if (content == null) {
                logger.error("NacosProp.{} is null", po.dataId);
                continue;
            }

            final Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            po.field.set(INSTANCE, properties);
            if (logger.isDebugEnabled()) logger.debug("load NacosProp.{}\n{}", po, properties);
        }
    }

    /**
     * @apiNote environment中只在初始化的时候赋值，监听修改environment是没意义的
     */
    public static void listen(String fieldName, Consumer<Properties> consumer) throws NacosException, NoSuchFieldException {
        final Field field = INSTANCE.getDeclaredField(fieldName);
        //noinspection OptionalGetWithoutIsPresent
        final NacosConfPo po = NacosEnvironmentPostProcessor.nacosConfPos.stream()
                .filter(p -> p.field.equals(field))
                .findFirst()
                .get();
        final ConfigService configService = NacosEnvironmentPostProcessor.configServiceMap.get(po.namespace);

        configService.addListener(po.dataId, po.group, new Listener() {
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
                    if (logger.isDebugEnabled()) logger.debug("refresh NacosProp.{}\n{}", po.dataId, configInfo);
                } catch (IOException | IllegalAccessException e) {
                    logger.error("更新配置异常", e);
                }
            }
        });
    }
}
