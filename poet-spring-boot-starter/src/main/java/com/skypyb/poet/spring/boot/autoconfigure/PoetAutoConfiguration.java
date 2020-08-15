package com.skypyb.poet.spring.boot.autoconfigure;

import com.skypyb.poet.spring.boot.core.DefaultPoetAnnexContext;
import com.skypyb.poet.spring.boot.core.PoetAnnexContext;
import com.skypyb.poet.spring.boot.core.client.*;
import com.skypyb.poet.spring.boot.core.store.JdbcPoetAnnexRepository;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(PoetProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class PoetAutoConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(PoetAutoConfiguration.class);
    private PoetProperties poetProperties;


    public PoetAutoConfiguration(PoetProperties poetProperties) {
        this.poetProperties = poetProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(poetProperties.getStorageLocation(), "Storage location not be null!");
        Assert.notNull(poetProperties.getPathDelimiter(), "Path delimiter not be null!");
        Assert.notNull(poetProperties.getTableName(), "Table name not be null!");
        logger.info("======= Poet annex auto configuration success =======");
    }

    @Bean
    @ConditionalOnMissingBean
    public PoetAccessRouter poetAccessRouter() {
        PoetAccessRouter router = new DefaultPoetAccessRouter();
        router.setDefaultModule(poetProperties.getDefaultModule());
        router.setStorageLocation(poetProperties.getStorageLocation());
        router.setDelimiter(poetProperties.getPathDelimiter());
        router.setSlicer(PoetAnnexSlicer.slicer(poetProperties.getPathDelimiter()));
        return router;
    }

    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public PoetAnnexRepository poetAnnexRepository(JdbcTemplate jdbcTemplate) {
        JdbcPoetAnnexRepository repository = new JdbcPoetAnnexRepository();
        repository.setTableName(poetProperties.getTableName());
        repository.setJdbcTemplate(jdbcTemplate);
        return repository;
    }

    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public PoetAnnexRepository poetAnnexRepository(DataSource dataSource) {
        JdbcPoetAnnexRepository repository = new JdbcPoetAnnexRepository();
        repository.setTableName(poetProperties.getTableName());
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        repository.setJdbcTemplate(jdbcTemplate);
        return repository;
    }

    @Bean
    @ConditionalOnMissingBean
    public PoetAnnexClient poetAnnexClient(PoetAccessRouter poetAccessRouter) {
        return new LocalFileServerClient(poetAccessRouter);
    }

    @Bean
    @ConditionalOnMissingBean
    public PoetAnnexClientHttpSupport poetAnnexClientHttpSupport(@Nullable PoetAnnexClient client,
                                                                 PoetAccessRouter poetAccessRouter) {
        if (Objects.nonNull(client) && client instanceof PoetAnnexClientHttpSupport) {
            return (PoetAnnexClientHttpSupport) client;
        }
        return new LocalFileServerClient(poetAccessRouter);
    }


    @Bean
    @ConditionalOnMissingBean
    public PoetAnnexContext poetAnnexContext(PoetAnnexClient poetAnnexClient,
                                             PoetAnnexClientHttpSupport poetAnnexClientHttpSupport,
                                             PoetAnnexRepository poetAnnexRepository) {
        DefaultPoetAnnexContext context = new DefaultPoetAnnexContext();
        context.configure(poetAnnexClient, poetAnnexClientHttpSupport);
        context.setRepository(poetAnnexRepository);
        return context;
    }
}
