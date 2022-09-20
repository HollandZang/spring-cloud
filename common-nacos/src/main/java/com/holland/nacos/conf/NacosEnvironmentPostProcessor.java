package com.holland.nacos.conf;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class NacosEnvironmentPostProcessor implements EnvironmentPostProcessor {

    public static Map<String, ConfigService> configServiceManager;
    public static Set<NacosConfMeta> nacosConfPos;
    private String serverAddr;

    private void init(Set<NacosConfMeta> nacosConfPos) throws NacosException, IOException, IllegalAccessException {
        configServiceManager = new HashMap<>(nacosConfPos.size());
        for (NacosConfMeta meta : nacosConfPos) {
            if (!configServiceManager.containsKey(meta.namespace)) {
                final Properties properties = new Properties();
                properties.put("serverAddr", serverAddr);
                properties.put("namespace", meta.namespace);
                ConfigService configService = NacosFactory.createConfigService(properties);
                configServiceManager.put(meta.namespace, configService);
            }
        }
        NacosPropKit.init(configServiceManager, nacosConfPos);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        serverAddr = environment.getProperty("spring.cloud.nacos.config.server-addr");
        String namespace = environment.getProperty("spring.cloud.nacos.config.namespace");
        String group = environment.getProperty("spring.cloud.nacos.config.group");
        nacosConfPos = NacosConfMeta.genConfigs(namespace, group);

        try {
            init(nacosConfPos);
        } catch (NacosException | IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        for (NacosConfMeta meta : nacosConfPos) {
            Field field = meta.field;

            Properties o;
            try {
                o = (Properties) field.get(NacosPropKit.INSTANCE);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            propertySources.addFirst(new PropertySource<Properties>(meta.toString(), o) {
                @Override
                public Object getProperty(@Nullable String name) {
                    return this.source.getProperty(name);
                }
            });
        }
    }
}
