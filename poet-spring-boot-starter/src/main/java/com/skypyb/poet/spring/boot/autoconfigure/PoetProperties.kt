package com.skypyb.poet.spring.boot.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConfigurationProperties(prefix = "poet")
@Component("poetProperties")
data class PoetProperties(

        //默认储存位置
        var storageLocation: @NotBlank(message = "Storage location not be empty!") String? = null,

        //是否启用web端点
        var enableWebResource: Boolean = true,

        //是否使用DB储存附件信息
        var enableDBStore: Boolean = true,

        //web资源接口请求路径前缀
        var webUrlPrefix: String = "/poet",

        //默认模块, 在文件保存时若不指定则将直接保存到此模块之中
        var defaultModule: String? = null,

        //路径分隔符,  以本地文件系统作为附件储存库时可使用 '/', 适配 unix&win
        var pathDelimiter: String = "/",

        //储存附件信息的表名
        var tableName: String = "poet_annex"
)