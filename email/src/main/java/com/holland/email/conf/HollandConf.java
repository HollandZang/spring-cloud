package com.holland.email.conf;

import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import com.holland.common.spring.configuration.NacosConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ComponentScan(
        basePackages = "com.holland.common.spring.configuration"
        , excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NacosConfig.class)
)
@Configuration
public class HollandConf {
    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public LocalValidatorFactoryBean validator(AutowireCapableBeanFactory springFactory) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        // 快速失败
        factoryBean.getValidationPropertyMap().put(BaseHibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString());
        return factoryBean;
    }
}
