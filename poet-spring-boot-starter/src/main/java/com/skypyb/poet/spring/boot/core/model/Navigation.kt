package com.skypyb.poet.spring.boot.core.model

import java.io.Serializable

data class Navigation(
        var name: String,

        var module: String,

        //path == key
        var path: String,
        /**
         * fullPath 和 path 不同的是： fullPath包含文件系统基础路径,即存储路径
         * 如果是unix系统, 则最前面带个斜杠 ‘/’
         */
        var fullPath: String
) : Serializable