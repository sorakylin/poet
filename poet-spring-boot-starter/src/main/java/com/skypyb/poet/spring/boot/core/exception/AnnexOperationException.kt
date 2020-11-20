package com.skypyb.poet.spring.boot.core.exception

import java.nio.file.Path

/**
 * 附件操作相关 (CRUD) 报的异常
 */
class AnnexOperationException : RuntimeException {
    var path: String? = null
    var msg: String? = null

    constructor() {}

    constructor(message: String?) : super(message) {
        msg = message
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        msg = message
    }

    fun message(message: String?): AnnexOperationException {
        msg = message
        return this
    }

    companion object {

        fun ofPath(path: Path): AnnexOperationException {
            return ofPath(path.toString())
        }

        fun ofPath(path: String?): AnnexOperationException {
            val e = AnnexOperationException()
            e.path = path
            return e
        }
    }
}