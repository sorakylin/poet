package com.skypyb.poet.spring.boot.interceptor;

import com.skypyb.poet.spring.boot.autoconfigure.PoetHandlerInterceptorChainConfiguration;
import org.springframework.beans.factory.Aware;

/**
 * 实现此接口即可在 Spring Bean 创建完毕后将拦截器链条注入
 * 可在需要对附件的操作进行控制时使用
 */
public interface PoetInterceptorChainAware extends Aware {


    /**
     * 设置拦截器链,  将附件操作的拦截链条进行注入
     * 可针对话定制访问控制
     *
     * @see PoetHandlerInterceptorChainConfiguration#postProcessBeforeInitialization
     */
    void setInterceptorChain(PoetHandlerInterceptorChain chain);
}
