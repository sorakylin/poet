package com.skypyb.poet.cloudstore.tencent

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.region.Region
import com.skypyb.poet.core.client.PoetAccessRouter
import com.skypyb.poet.core.client.PoetAnnexClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import javax.validation.ValidationException
import javax.validation.Validator


@Configuration
@DependsOn("poetProperties")
@ConditionalOnProperty(name = ["poet.storeMode"], havingValue = "TENCENT")
@EnableConfigurationProperties(PoetCosProperties::class)
class PoetCosAutoConfiguration : InitializingBean {

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var poetCosProperties: PoetCosProperties

    companion object {
        private val logger = LoggerFactory.getLogger(PoetCosAutoConfiguration::class.java)
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val validate = validator.validate(poetCosProperties)

        if (validate != null && validate.isNotEmpty()) {
            val message = validate.map { it.message }.joinToString(" | ")
            throw ValidationException(message)
        }

        logger.info("======= Poet tencent cloud configure completed. =======")
    }


    @Bean
    @ConditionalOnMissingBean
    fun poetAnnexClient(poetAccessRouter: PoetAccessRouter): PoetAnnexClient {
        val prop = poetCosProperties

        val cred = BasicCOSCredentials(prop.secretId, prop.secretKey)

        val region = Region(prop.region)
        val clientConfig = ClientConfig(region)

        return TencentCosClient(
            router = poetAccessRouter,
            cosClient = COSClient(cred, clientConfig),
            defaultBucket = prop.defaultBucket!!
        )
    }
}