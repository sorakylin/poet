package com.skypyb.poet.spring.boot.autoconfigure

import com.skypyb.poet.spring.boot.core.DefaultPoetAnnexContext
import com.skypyb.poet.spring.boot.core.PoetAnnexContext
import com.skypyb.poet.spring.boot.core.client.*
import com.skypyb.poet.spring.boot.core.store.MySQLPoetAnnexRepository
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.lang.Nullable
import org.springframework.util.Assert
import java.util.stream.Collectors
import javax.sql.DataSource
import javax.validation.ValidationException
import javax.validation.Validator
import javax.validation.constraints.NotNull

@Configuration
@ConditionalOnSingleCandidate(DataSource::class)
@EnableConfigurationProperties(PoetProperties::class)
@AutoConfigureAfter(DataSourceAutoConfiguration::class)
open class PoetAutoConfiguration : InitializingBean {

    @Autowired
    private lateinit var validator: Validator
    @Autowired
    private lateinit var poetProperties: PoetProperties

    companion object {
        private val logger = LoggerFactory.getLogger(PoetAutoConfiguration::class.java)
    }


    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val validate = validator.validate(poetProperties)

        if (validate != null && !validate.isEmpty()) {
            val message = validate.stream().map { it.message }.collect(Collectors.joining(" | "))
            throw ValidationException(message)
        }

        if (poetProperties.enableDBStore) Assert.notNull(poetProperties.tableName, "Table name not be null!")
        logger.info("======= Poet annex auto configuration success =======")
    }

    @Bean
    @ConditionalOnMissingBean
    open fun poetAccessRouter(@Nullable slicer: PoetAnnexSlicer?): PoetAccessRouter {
        val router: PoetAccessRouter = DefaultPoetAccessRouter()
        router.setDefaultModule(poetProperties.defaultModule)
        router.setStorageLocation(poetProperties.storageLocation)
        router.delimiter = poetProperties.pathDelimiter
        router.setSlicer(slicer ?: PoetAnnexSlicer.DEFAULT_SLICER)
        return router
    }

    /**
     * 默认使用MySQL数据库
     * 使用其他数据库的需要自行配置
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = ["poet.enableDBStore"], havingValue = "true")
    open fun poetAnnexRepository(@NotNull jdbcTemplate: JdbcTemplate): PoetAnnexRepository {
        val repository = MySQLPoetAnnexRepository(jdbcTemplate)
        repository.tableName = poetProperties.tableName
        return repository
    }

    @Bean
    @ConditionalOnMissingBean
    open fun poetAnnexClient(poetAccessRouter: PoetAccessRouter): PoetAnnexClient {
        return LocalFileServerClient(poetAccessRouter)
    }

    @Bean
    @DependsOn("poetAnnexClient")
    @ConditionalOnMissingBean
    open fun poetAnnexClientHttpSupport(@NotNull client: PoetAnnexClient,
                                        @NotNull poetAccessRouter: PoetAccessRouter): PoetAnnexClientHttpSupport {
        return if (client is PoetAnnexClientHttpSupport) client else LocalFileServerClient(poetAccessRouter)
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("poetAnnexClient", "poetAnnexClientHttpSupport")
    open fun poetAnnexContext(poetAnnexClient: PoetAnnexClient,
                              poetAnnexClientHttpSupport: PoetAnnexClientHttpSupport,
                              @Nullable poetAnnexRepository: PoetAnnexRepository?,
                              @Nullable nameGenerator: PoetAnnexNameGenerator?): PoetAnnexContext {
        val context = DefaultPoetAnnexContext()
        context.configure(poetAnnexClient, poetAnnexClientHttpSupport)
        context.repository = poetAnnexRepository
        context.nameGenerator = nameGenerator ?: PoetAnnexNameGenerator.DEFAULT_NAME_GENERATOR
        return context
    }

}