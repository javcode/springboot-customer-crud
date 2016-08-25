package com.javcode.springboot.config;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DefaultTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

@Configuration
public class DataSourceConfiguration {

    @Bean
    public IdGenerator idGenerator() {
        return new AlternativeJdkIdGenerator();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @DependsOn("flyway")
    public DSLContext dsl(org.jooq.Configuration config) {
        return new DefaultDSLContext(config);
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public ExceptionTranslator exceptionTranslator() {
        return new ExceptionTranslator();
    }

    @Bean
    public ExecuteListenerProvider executeListenerProvider(ExceptionTranslator exceptionTranslator) {
        return new DefaultExecuteListenerProvider(exceptionTranslator);
    }

    @Bean
    public Settings jooqSettings() {
        return SettingsTools.defaultSettings().withRenderNameStyle(RenderNameStyle.AS_IS)
                .withExecuteWithOptimisticLocking(true);
    }

    @Bean
    public org.jooq.Configuration jooqConfig(DataSourceConnectionProvider connectionProvider,
            ExecuteListenerProvider executeListenerProvider, Settings settings) {
        return new DefaultConfiguration().derive(connectionProvider).derive(executeListenerProvider).derive(settings)
                .derive(SQLDialect.H2).derive(new DefaultTransactionProvider(connectionProvider));
    }
}

class ExceptionTranslator extends DefaultExecuteListener {

    private static final long serialVersionUID = -2450323227461061152L;

    @Override
    public void exception(ExecuteContext ctx) {
        final SQLDialect dialect = ctx.configuration().dialect();
        final SQLExceptionTranslator translator = (dialect != null) ? new SQLErrorCodeSQLExceptionTranslator(
                dialect.name()) : new SQLStateSQLExceptionTranslator();
        ctx.exception(translator.translate("jOOQ", ctx.sql(), ctx.sqlException()));
    }
}
