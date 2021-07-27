package com.skypyb.poet.spring.boot.core.store

import java.time.LocalDateTime


class StoreRoadSign {
    var mainCategory: String? = null
    var instanceId: Long? = null
    var instanceModule: String? = null
    var expireTime: LocalDateTime? = null

    companion object {
        fun empty() = StoreRoadSign();
    }
}