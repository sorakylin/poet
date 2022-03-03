package com.skypyb.poet.core.util

import java.io.Closeable
import java.io.IOException


class StreamUtil {

    companion object {
        @JvmStatic
        fun close(closeable: Closeable) {
            if (closeable == null) return
            try {
                closeable.close()
            } catch (e: IOException) { //忽略
            }
        }

        @JvmStatic
        fun close(vararg closeable: Closeable) {
            if (closeable == null) return
            closeable.forEach { close(it) }
        }
    }

}