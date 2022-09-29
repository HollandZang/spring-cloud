package com.holland.nacos.conf;

import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class NacosEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String serverAddr = environment.getProperty("spring.cloud.nacos.config.server-addr");
        String namespace = environment.getProperty("spring.cloud.nacos.config.namespace");
        String group = environment.getProperty("spring.cloud.nacos.config.group");

        try {
            NacosPropKit.init(serverAddr, namespace, group);
        } catch (NacosException | IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        for (NacosConfMeta meta : NacosPropKit.nacosConfMetas) {
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
