package com.skypyb.poet.spring.boot.core


import com.skypyb.poet.spring.boot.core.interceptor.PoetHandlerInterceptor
import com.skypyb.poet.spring.boot.core.interceptor.PoetHandlerInterceptorChain
import com.skypyb.poet.spring.boot.core.interceptor.PoetInterceptorChainAware
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport
import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException
import com.skypyb.poet.spring.boot.core.model.PoetAnnex
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository
import com.skypyb.poet.spring.boot.core.store.StoreRoadSign
import com.skypyb.poet.spring.boot.core.util.HttpResourceViewUtils
import java.io.InputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 附件操作的整体上下文
 * 除了拥有所有对附件操作的机能以外,还包含附件信息持久化、拦截器、自定义名称等功能
 */
abstract class AbstractPoetAnnexContext : PoetAnnexContext, PoetInterceptorChainAware {

    private lateinit var annexClient: PoetAnnexClient

    private lateinit var annexHttpClient: PoetAnnexClientHttpSupport

    private lateinit var interceptorChain: PoetHandlerInterceptorChain

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

    override fun setInterceptorChain(chain: PoetHandlerInterceptorChain) {
        this.interceptorChain = chain
    }

    private fun nameGenerator(realName: String): String {
        return nameGenerator?.generate()
                ?.let { StringBuilder(it).append(".").append(HttpResourceViewUtils.splitSuffix(realName)).toString() }
                ?: realName
    }

    override fun save(ins: InputStream, realName: String, roadSign: StoreRoadSign): PoetAnnex {
        val name = nameGenerator(realName)
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.SAVE, name, null)

        val result = annexClient.save(ins, name)
        result.realName = realName
        repository?.save(result, roadSign)
        return result
    }

    override fun save(ins: InputStream, realName: String, module: String, roadSign: StoreRoadSign): PoetAnnex {
        val name = nameGenerator(realName)
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.SAVE, name, module)

        val result = annexClient.save(ins, name, module)
        result.realName = realName
        repository?.save(result, roadSign)
        return result
    }

    override fun save(data: ByteArray, realName: String, roadSign: StoreRoadSign): PoetAnnex {
        val name = nameGenerator(realName)
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.SAVE, name, null)

        val result = annexClient.save(data, name)
        result.realName = realName
        repository?.save(result, roadSign)
        return result
    }

    override fun save(data: ByteArray, realName: String, module: String, roadSign: StoreRoadSign): PoetAnnex {
        val name = nameGenerator(realName)
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.SAVE, name, module)

        val result = annexClient.save(data, name, module)
        result.realName = realName
        repository?.save(result, roadSign)
        return result
    }

    override fun exist(name: String): Boolean {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        return repository?.findByName(name)?.key?.let { annexClient?.exist(it) } ?: false
    }

    override fun delete(name: String) {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.DELETE, name, null)

        repository?.findByName(name)?.let { repository!!.deleteByName(it.name); annexClient?.delete(it.key) }
    }

    /**
     * 根据名字去DB查附件信息
     * 如果禁用了DB之类的则会直接报错， 下边的所有方法都不能用
     */
    private fun findAnnex(name: String): PoetAnnex = repository?.findByName(name)
            ?: throw AnnexOperationException("File($name) does not exist!")

    override fun getBytes(name: String): ByteArray {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        val annex = findAnnex(name)
        return annexClient.getBytes(annex.key)
    }

    override fun view(name: String, response: HttpServletResponse) {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        val annex = findAnnex(name)

        annexHttpClient.view(annex.key, response)
    }

    override fun viewMedia(name: String, response: HttpServletResponse) {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        val annex = findAnnex(name)

        annexHttpClient.viewMedia(annex.key, response)
    }

    override fun viewMedia(name: String, request: HttpServletRequest, response: HttpServletResponse) {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        val annex = findAnnex(name)

        annexHttpClient.viewMedia(annex.key, request, response)
    }

    override fun down(name: String, response: HttpServletResponse) {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        val annex = findAnnex(name)

        annexHttpClient.down(annex.key, annex.realName, response)
    }

    override fun down(name: String, realName: String, response: HttpServletResponse) {
        interceptorChain.doInterception(PoetHandlerInterceptor.Mode.ACCESS, name, null)

        val annex = findAnnex(name)

        annexHttpClient.down(annex.key, realName, response)
    }
}