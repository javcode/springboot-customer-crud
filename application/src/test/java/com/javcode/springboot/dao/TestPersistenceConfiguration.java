package com.javcode.springboot.dao;

import org.jooq.ConnectionProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.javcode.springboot.config.DataSourceConfiguration;

@Configuration
@Import(value = { DataSourceConfiguration.class })
@ComponentScan
@EnableAutoConfiguration
public class TestPersistenceConfiguration {

    @Bean
    public org.jooq.Configuration jooqConfig(final ConnectionProvider connectionProvider,
                                             final ExecuteListenerProvider executeListenerProvider,
                                             final Settings settings) {
        return new DefaultConfiguration().derive(connectionProvider)
                .derive(executeListenerProvider).derive(settings)
                .derive(SQLDialect.H2);
    }

}
