package com.skypyb.poet.cloudstore.tencent

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties(prefix = "poet.tencent")
class PoetCosProperties {

    //ak和sk
    @NotBlank
    var secretId: String? = null
    @NotBlank
    var secretKey: String? = null

    //默认用哪个桶
    @NotBlank
    var defaultBucket: String? = null

    /**
     * COS地域：https://cloud.tencent.com/document/product/436/6224
     */
    @NotBlank
    var region: String? = null
}