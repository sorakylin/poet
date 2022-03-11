package com.skypyb.poet.cloudstore.tencent

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties(prefix = "poet.tencent")
data class PoetCosProperties(

    //ak和sk
    @field:NotBlank
    var secretId: String? = null,

    @field:NotBlank
    var secretKey: String? = null,

    //默认用哪个桶
    @field:NotBlank
    var defaultBucket: String? = null,

    /**
     * COS地域：https://cloud.tencent.com/document/product/436/6224
     */
    @field:NotBlank
    var region: String? = null
)