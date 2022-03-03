package com.skypyb.poet.spring.boot.interceptor;

import javax.annotation.Nullable;

/**
 * 拦截链
 *
 * @see PoetHandlerInterceptor
 */
public interface PoetHandlerInterceptorChain {

    void doInterception(PoetHandlerInterceptor.Mode mode, String name, @Nullable String module);
}
