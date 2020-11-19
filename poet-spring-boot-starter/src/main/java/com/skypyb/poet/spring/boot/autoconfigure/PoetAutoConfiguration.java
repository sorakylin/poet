package com.skypyb.poet.spring.boot.autoconfigure;

import com.skypyb.poet.spring.boot.core.DefaultPoetAnnexContext;
import com.skypyb.poet.spring.boot.core.PoetAnnexContext;
import com.skypyb.poet.spring.boot.core.client.*;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import com.skypyb.poet.spring.boot.core.store.PostgresPoetAnnexRepository;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(PoetProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class PoetAutoConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(PoetAutoConfiguration.class);
    private PoetProperties poetProperties;

    @Autowired
    private Validator validator;

    public PoetAutoConfiguration(PoetProperties poetProperties) {
        this.poetProperties = poetProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<ConstraintViolation<PoetProperties>> validate = validator.validate(poetProperties);
        if (validate != null && !validate.isEmpty()) {
            String message = validate.stream().map(v -> v.getMessage()).collect(Collectors.joining(" | "));
            throw new ValidationException(message);
        }

        //?
        Assert.notNull(poetProperties.getTableName(), "Table name not be null!");

        logger.info("======= Poet annex auto configuration success =======");
    }

    @Bean
    @ConditionalOnMissingBean
    public PoetAccessRouter poetAccessRouter(@Nullable PoetAnnexSlicer slicer) {
        PoetAccessRouter router = new DefaultPoetAccessRouter();
        router.setDefaultModule(poetProperties.getDefaultModule());
        router.setStorageLocation(poetProperties.getStorageLocation());
        router.setDelimiter(poetProperties.getPathDelimiter());
        router.setSlicer(Objects.isNull(slicer) ? PoetAnnexSlicer.DEFAULT_SLICER : slicer);
        return router;
    }

    @Bean
    @ConditionalOnMissingBean
    public PoetAnnexRepository poetAnnexRepository(@NotNull JdbcTemplate jdbcTemplate) {
        PostgresPoetAnnexRepository repository = new PostgresPoetAnnexRepository(jdbcTemplate);
        repository.setTableName(poetProperties.getTableName());
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
    @DependsOn({"poetAnnexClient", "poetAnnexClientHttpSupport"})
    public PoetAnnexContext poetAnnexContext(PoetAnnexClient poetAnnexClient,
                                             PoetAnnexClientHttpSupport poetAnnexClientHttpSupport,
                                             @Nullable PoetAnnexRepository poetAnnexRepository,
                                             @Nullable PoetAnnexNameGenerator nameGenerator) {
        DefaultPoetAnnexContext context = new DefaultPoetAnnexContext();
        context.configure(poetAnnexClient, poetAnnexClientHttpSupport);
        context.setRepository(Objects.isNull(poetAnnexRepository) ? null : poetAnnexRepository);
        context.setNameGenerator(Objects.isNull(nameGenerator) ? PoetAnnexNameGenerator.DEFAULT_NAME_GENERATOR : nameGenerator);
        return context;
    }
}
