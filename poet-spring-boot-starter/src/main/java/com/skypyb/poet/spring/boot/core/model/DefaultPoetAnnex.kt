package com.skypyb.poet.spring.boot.core.model

class DefaultPoetAnnex : PoetAnnex {

    private var name: String? = null
    private var realName: String? = null
    private var suffix: String? = null
    private var key: String? = null
    private var length: Long? = null


    override fun setName(name: String) {
        this.name = name
    }

    override fun setRealName(realName: String) {
        this.realName = realName
    }

    override fun setSuffix(suffix: String) {
        this.suffix = suffix
    }

    override fun setKey(key: String) {
        this.key = key
    }

    override fun setLength(length: Long) {
        this.length = length
    }

    override fun getName(): String {
        return name!!
    }

    override fun getRealName(): String {
        return realName!!
    }

    override fun getSuffix(): String {
        return suffix!!
    }

    override fun getKey(): String {
        return key!!
    }

    override fun getLength(): Long {
        return length!!
    }

    companion object {
        /**
         * 没有 realName&length
         */
        @JvmStatic
        fun of(n: Navigation): DefaultPoetAnnex {
            val name = n.name
            val defaultPoetAnnex = DefaultPoetAnnex()
            defaultPoetAnnex.setName(name!!)
            defaultPoetAnnex.setKey(n.path!!)
            val suffixPoint = name.lastIndexOf(".")
            if (suffixPoint > 0) { //后缀名保存时不保留 "."
                defaultPoetAnnex.setSuffix(name.substring(suffixPoint + 1))
            }
            return defaultPoetAnnex
        }
    }
}