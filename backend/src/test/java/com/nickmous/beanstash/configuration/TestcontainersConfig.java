package com.nickmous.beanstash.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:18-alpine")
            .withUsername("postgres")
            .withPassword("postgres")
            .withEnv("APP_USER", "app_user")
            .withEnv("APP_PASSWORD", "app_password")
            .withCopyFileToContainer(
                MountableFile.forHostPath("./db/init.sh"),
                "/docker-entrypoint-initdb.d/init.sh"
            );
        postgreSQLContainer.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.flyway.url=" + postgreSQLContainer.getJdbcUrl()
            ).applyTo(ctx.getEnvironment());
        }
    }
}
