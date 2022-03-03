package com.skypyb.poet.spring.boot.core.client

import com.skypyb.poet.spring.boot.core.model.Navigation
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Matcher

class DefaultPoetAccessRouter : PoetAccessRouter {

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultPoetAccessRouter::class.java)
    }

    private var slicer = PoetAnnexSlicer.DEFAULT_SLICER
    private var delimiter = PoetAnnexSlicer.DELIMITER
    private var storageLocation: String? = null
    private var defaultModule: String? = null

    override fun setStorageLocation(storageLocation: String) {
        this.storageLocation = if (storageLocation.endsWith(delimiter)) storageLocation.substring(0, storageLocation.length - 1) else storageLocation
    }

    override fun setDefaultModule(module: String?) {
        this.defaultModule = module
    }

    override fun setSlicer(slicer: PoetAnnexSlicer) {
        this.slicer = slicer
    }

    override fun setDelimiter(delimiter: String) {
        this.delimiter = delimiter
    }

    override fun getDelimiter(): String {
        return delimiter
    }

    override fun routing(module: String?, name: String): Navigation {

        val practicalModule: String = module ?: defaultModule ?: ""

        val path = slicer.slicePath(practicalModule, name).joinToString(delimiter)

        val navigation = Navigation(name, practicalModule, path, formatKey(path))

        return navigation
    }

    /**
     * 文件存储路径+文件路径 转换分隔符
     * key = temp/123.img
     * formatKey => /usr/temp/123.img
     * formatKey => D:\temp\123.img
     *
     * @param key 文件标识
     * @return 可直接定位到本机具体文件路径的地址
     */
    override fun formatKey(key: String): String {

        val abstractPath: String = if (key.startsWith(delimiter)) key.substring(1) else key

        var path: String = storageLocation + delimiter + abstractPath

        path = path.replace(delimiter.toRegex(), Matcher.quoteReplacement(File.separator))
        path = path.replace(File.separator + File.separator, Matcher.quoteReplacement(File.separator))

        logger.debug("Poet format key result ==>  key:{} path:{}", key, path)
        return path
    }
}