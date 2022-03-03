package com.skypyb.poet.spring.boot.autoconfigure

import com.skypyb.poet.core.exception.AnnexAccessException
import com.skypyb.poet.spring.boot.interceptor.PoetHandlerInterceptor
import com.skypyb.poet.spring.boot.interceptor.PoetHandlerInterceptorChain
import com.skypyb.poet.spring.boot.interceptor.PoetInterceptorChainAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration

@Configuration
open class PoetHandlerInterceptorChainConfiguration : InitializingBean, BeanPostProcessor {

    private val standardInterceptorChain = StandardInterceptorChain();

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {

        when (bean) {
            is PoetHandlerInterceptor -> standardInterceptorChain.standardInterceptors.add(bean)
            is PoetInterceptorChainAware -> bean.setInterceptorChain(standardInterceptorChain)
        }

        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return bean
    }

    override fun afterPropertiesSet() {
        standardInterceptorChain.standardInterceptors.sortBy { it.order }
    }
}

class StandardInterceptorChain :
    PoetHandlerInterceptorChain {

    val standardInterceptors = mutableListOf<PoetHandlerInterceptor>()

    override fun doInterception(mode: PoetHandlerInterceptor.Mode, name: String, module: String?) {
        var refuse = standardInterceptors.any { !it.preHandle(mode, name, module) }

        if (refuse) throw AnnexAccessException("Access denied with StandardInterceptorChain")
    }
}