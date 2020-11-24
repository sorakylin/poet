package com.skypyb.poet.spring.boot.core.interceptor;

import org.springframework.beans.factory.Aware;

public interface PoetInterceptorChainAware extends Aware {

    void setInterceptorChain(PoetHandlerInterceptorChain chain);
}
