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

    public static Map<String, ConfigService> configServiceMap;
    public static Set<NacosConfPo> nacosConfPos;
    private String serverAddr;

    private void init(Set<NacosConfPo> nacosConfPos) throws NacosException, IOException, IllegalAccessException {
        configServiceMap = new HashMap<>(nacosConfPos.size());
        for (NacosConfPo po : nacosConfPos) {
            if (!configServiceMap.containsKey(po.namespace)) {
                final Properties properties = new Properties();
                properties.put("serverAddr", serverAddr);
                properties.put("namespace", po.namespace);
                ConfigService configService = NacosFactory.createConfigService(properties);
                configServiceMap.put(po.namespace, configService);
            }
        }
        NacosPropKit.init(configServiceMap, nacosConfPos);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        serverAddr = environment.getProperty("spring.cloud.nacos.config.server-addr");
        String namespace = environment.getProperty("spring.cloud.nacos.config.namespace");
        String group = environment.getProperty("spring.cloud.nacos.config.group");
        nacosConfPos = NacosConfPo.genConfigs(namespace, group);

        try {
            init(nacosConfPos);
        } catch (NacosException | IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        for (NacosConfPo po : nacosConfPos) {
            Field field = po.field;

            Properties o;
            try {
                o = (Properties) field.get(NacosPropKit.INSTANCE);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            propertySources.addFirst(new PropertySource<Properties>(po.toString(), o) {
                @Override
                public Object getProperty(@Nullable String name) {
                    return this.source.getProperty(name);
                }
            });
        }
    }
}
