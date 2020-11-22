package com.skypyb.poet.spring.boot.core

import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport
import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException
import com.skypyb.poet.spring.boot.core.model.PoetAnnex
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository
import com.skypyb.poet.spring.boot.core.util.HttpResourceViewUtils
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import java.io.InputStream
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 附件操作上下文, 拥有所有对附件操作的机能
 */
abstract class AbstractPoetAnnexContext : ApplicationContextAware, PoetAnnexContext {

    lateinit var annexClient: PoetAnnexClient

    lateinit var annexHttpClient: PoetAnnexClientHttpSupport
    
    /**
     * 是否启用Http支持， 若为false, 则Http相关的操作接口无法使用。 调用时会抛出 [UnsupportedOperationException]
     *
     * @see PoetAnnexClientHttpSupport
     */
    var isEnableHttpSupport = true

    //名字生成器
    abstract var nameGenerator: PoetAnnexNameGenerator?

    //储存器
    abstract var repository: PoetAnnexRepository?

    /**
     * 启动功能前必须要调用的核心配置方法
     *
     * @param annexClient     附件基础操作支持
     * @param annexHttpClient 附件Http相关操作支持
     */
    fun configure(annexClient: PoetAnnexClient, annexHttpClient: PoetAnnexClientHttpSupport) {
        this.annexClient = annexClient
        this.annexHttpClient = annexHttpClient
    }

    private fun checkHttpClientEnableState() = if (!isEnableHttpSupport || Objects.isNull(annexHttpClient))
        throw UnsupportedOperationException("HTTP support is not enabled!") else Unit


    private fun nameGenerator(realName: String): String {
        return nameGenerator?.generate()
                ?.let { StringBuilder(it).append(".").append(HttpResourceViewUtils.splitSuffix(realName)).toString() }
                ?: realName
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        if (eventPublisher == null) {
            eventPublisher = applicationContext
        }
    }

    override fun save(`in`: InputStream, name: String): PoetAnnex {
        val result = annexClient.save(`in`, nameGenerator(name))
        result.realName = name
        repository?.save(result)
        return result
    }

    override fun save(`in`: InputStream, name: String, module: String): PoetAnnex {
        val result = annexClient.save(`in`, nameGenerator(name), module)
        result.realName = name
        repository?.save(result)
        return result
    }

    override fun save(data: ByteArray, name: String): PoetAnnex {
        val result = annexClient.save(data, nameGenerator(name))
        result.realName = name
        repository?.save(result)
        return result
    }

    override fun save(data: ByteArray, name: String, module: String): PoetAnnex {
        val result = annexClient.save(data, nameGenerator(name), module)
        result.realName = name
        repository?.save(result)
        return result
    }

    override fun exist(name: String): Boolean {
        return repository?.findByName(name)?.key?.let { annexClient?.exist(it) ?: false } ?: false
    }

    override fun delete(name: String) {
        repository?.findByName(name)?.let { repository!!.deleteByName(it.name); annexClient?.delete(it.key) }
    }

    /**
     * 根据名字去DB查附件信息
     * 如果禁用了DB之类的则会直接报错， 下边的所有方法都不能用
     */
    private fun findAnnex(name: String): PoetAnnex = repository?.findByName(name)
            ?: throw AnnexOperationException("File($name) does not exist!")

    override fun getBytes(name: String): ByteArray {
        val annex = findAnnex(name)
        return annexClient.getBytes(annex.key)
    }

    override fun view(name: String, response: HttpServletResponse) {
        checkHttpClientEnableState()
        val annex = findAnnex(name)

        annexHttpClient.view(annex.key, response)
    }

    override fun viewMedia(name: String, response: HttpServletResponse) {
        checkHttpClientEnableState()
        val annex = findAnnex(name)

        annexHttpClient.viewMedia(annex.key, response)
    }

    override fun viewMedia(name: String, request: HttpServletRequest, response: HttpServletResponse) {
        checkHttpClientEnableState()
        val annex = findAnnex(name)

        annexHttpClient.viewMedia(annex.key, request, response)
    }

    override fun down(name: String, response: HttpServletResponse) {
        checkHttpClientEnableState()
        val annex = findAnnex(name)

        annexHttpClient.down(annex.key, annex.realName, response)
    }

    override fun down(name: String, realName: String, response: HttpServletResponse) {
        checkHttpClientEnableState()
        val annex = findAnnex(name)

        annexHttpClient.down(annex.key, realName, response)
    }

    companion object {
        private var eventPublisher: ApplicationEventPublisher? = null
    }
}