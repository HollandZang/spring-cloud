package com.holland.email.conf;

import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ComponentScan("com.holland.common.spring.configuration")
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
