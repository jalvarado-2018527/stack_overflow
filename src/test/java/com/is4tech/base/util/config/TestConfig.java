package com.is4tech.base.util.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

@TestConfiguration
@ComponentScan(basePackages = "com.is4tech")
public class TestConfig {
    @Bean
    public DataSource getDatasource() {
        return new MockDataSource();
    }
}
