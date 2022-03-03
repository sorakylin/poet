package com.skypyb.poet.spring.boot.core.exception

import java.nio.file.Path

/**
 * 附件访问异常
 */
class AnnexAccessException : RuntimeException {

    var path: String? = null
    var msg: String? = null

    constructor()

    constructor(message: String?) : super(message) {
        msg = message
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        msg = message
    }

    fun message(message: String?): AnnexAccessException {
        msg = message
        return this
    }

    companion object {
        fun ofPath(path: Path): AnnexAccessException {
            return ofPath(path.toString())
        }

        fun ofPath(path: String?): AnnexAccessException {
            val e = AnnexAccessException()
            e.path = path
            return e
        }
    }
}