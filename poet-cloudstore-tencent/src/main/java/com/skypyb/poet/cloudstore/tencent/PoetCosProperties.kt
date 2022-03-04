package com.skypyb.poet.cloudstore.tencent

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "xquan.openapi")
class PoetCosProperties {

    //ak和sk
    var secretId: String? = null
    var secretKey: String? = null

    //默认用哪个桶
    var defaultBucket: String? = null

    /**
     * COS地域：https://cloud.tencent.com/document/product/436/6224
     */
    var region: String? = null
}