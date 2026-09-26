package com.skypyb.poet.spring.boot.autoconfigure

import com.skypyb.poet.core.client.*
import com.skypyb.poet.spring.boot.DefaultPoetAnnexContext
import com.skypyb.poet.spring.boot.PoetAnnexContext
import com.skypyb.poet.spring.boot.store.MySQLPoetAnnexRepository
import com.skypyb.poet.core.client.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.store.PoetAnnexRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.lang.Nullable
import org.springframework.util.Assert
import java.util.stream.Collectors
import jakarta.validation.ValidationException
import jakarta.validation.Validator
import jakarta.validation.constraints.NotNull

@AutoConfiguration(after = [JdbcTemplateAutoConfiguration::class, ValidationAutoConfiguration::class])
@EnableConfigurationProperties
@Import(PoetProperties::class, PoetHandlerInterceptorChainConfiguration::class)
class PoetAutoConfiguration : InitializingBean {

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

        if (validate != null && validate.isNotEmpty()) {
            val message = validate.stream().map { it.message }.collect(Collectors.joining(" | "))
            throw ValidationException(message)
        }

        if (poetProperties.enableDBStore) Assert.notNull(poetProperties.tableName, "Table name not be null!")
        logger.info("======= Poet annex auto configuration success =======")
    }

    @Bean
    @ConditionalOnMissingBean
    fun poetAccessRouter(@Nullable slicer: PoetAnnexSlicer?): PoetAccessRouter {
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
    @ConditionalOnSingleCandidate(JdbcTemplate::class)
    @ConditionalOnProperty(prefix = "poet", name = ["enable-db-store"], havingValue = "true", matchIfMissing = true)
    fun poetAnnexRepository(@NotNull jdbcTemplate: JdbcTemplate): PoetAnnexRepository {
        val repository = MySQLPoetAnnexRepository(jdbcTemplate)
        repository.tableName = poetProperties.tableName
        return repository
    }

    @Bean
    @ConditionalOnMissingBean(PoetAnnexClient::class)
    @ConditionalOnProperty(
        prefix = "poet",
        name = ["store-mode"],
        havingValue = "LOCAL",
        matchIfMissing = true
    )
    fun poetAnnexClient(poetAccessRouter: PoetAccessRouter): PoetAnnexClient {
        return LocalFileServerClient(poetAccessRouter)
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "poet",
        name = ["store-mode"],
        havingValue = "LOCAL",
        matchIfMissing = true
    )
    fun poetAnnexClientHttpSupport(
        @NotNull client: PoetAnnexClient,
        @NotNull poetAccessRouter: PoetAccessRouter
    ): PoetAnnexClientHttpSupport {
        val support = if (client is PoetAnnexClientHttpSupport) client else LocalFileServerClient(poetAccessRouter)
        // 只暴露 HTTP 支持接口，避免同一个客户端被注册为两个附件客户端候选。
        return object : PoetAnnexClientHttpSupport by support {}
    }

    @Bean
    @ConditionalOnMissingBean
//    @DependsOn("poetAnnexClient", "poetAnnexClientHttpSupport")
    fun poetAnnexContext(
        poetAnnexClient: PoetAnnexClient,
        @Nullable poetAnnexClientHttpSupport: PoetAnnexClientHttpSupport?,
        @Nullable poetAnnexRepository: PoetAnnexRepository?,
        @Nullable nameGenerator: PoetAnnexNameGenerator?
    ): PoetAnnexContext {
        val context = DefaultPoetAnnexContext()
        context.configure(poetAnnexClient, poetAnnexClientHttpSupport)
        context.repository = poetAnnexRepository
        context.nameGenerator = nameGenerator ?: PoetAnnexNameGenerator.DEFAULT_NAME_GENERATOR
        return context
    }

}
