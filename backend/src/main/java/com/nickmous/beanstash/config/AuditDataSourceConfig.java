package com.nickmous.beanstash.config;

import javax.sql.DataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditDataSourceConfig implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
        if (bean instanceof DataSource ds && !(bean instanceof AuditAwareDataSource)) {
            return new AuditAwareDataSource(ds);
        }
        return bean;
    }
}
