package com.skypyb.poet.core.util

internal object StrUtil {

    fun hasText(str: String?): Boolean {
        return str != null && str.isNotEmpty() && containsText(str)
    }

    private fun containsText(str: CharSequence): Boolean {
        val strLen = str.length
        for (i in 0 until strLen) {
            if (!Character.isWhitespace(str[i])) {
                return true
            }
        }
        return false
    }

}