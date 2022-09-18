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
import java.lang.reflect.Modifier;
import java.util.Properties;

public class NacosEnvironmentPostProcessor implements EnvironmentPostProcessor {

    public static ConfigService configService;

    private void init(ConfigurableEnvironment environment) throws NacosException, IOException, IllegalAccessException {
        final Properties properties = new Properties();
        properties.put("serverAddr", environment.getProperty("spring.cloud.nacos.config.server-addr"));
        properties.put("namespace", environment.getProperty("spring.cloud.nacos.config.namespace"));
        configService = NacosFactory.createConfigService(properties);
        NacosPropKit.init(configService, environment.getProperty("spring.cloud.nacos.config.group"));
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            init(environment);
        } catch (NacosException | IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        for (Field field : NacosPropKit.INSTANCE.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers())) continue;

            Properties o;
            try {
                o = (Properties) field.get(NacosPropKit.INSTANCE);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            propertySources.addFirst(new PropertySource<Properties>(field.getName(), o) {
                @Override
                public Object getProperty(@Nullable String name) {
                    return this.source.getProperty(name);
                }
            });
        }
    }
}
