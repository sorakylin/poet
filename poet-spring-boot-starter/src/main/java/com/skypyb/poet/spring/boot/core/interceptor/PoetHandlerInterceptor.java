package com.skypyb.poet.spring.boot.core.interceptor;

import org.springframework.core.Ordered;

import javax.annotation.Nullable;

/**
 * 拦截器, 在{@link com.skypyb.poet.spring.boot.core.PoetAnnexContext} 的所有方法执行之前调用。 方便做鉴权、限流等
 * 可自己定义运行时异常抛出, 用以终止整个流程
 */
public interface PoetHandlerInterceptor extends Ordered {

    /**
     * 在调用时会传入当前上下文试图做的操作， 和其操作对应的目标附件名字、模块
     *
     * @param mode   操作模式  访问/保存/删除
     * @param name   附件名 全局唯一.  为 {@link com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator} 生成的名字
     * @param module 模块名, 不一定会有
     * @return false = 终止整个流程 抛出异常 => AnnexAccessException
     * @throws com.skypyb.poet.spring.boot.core.exception.AnnexAccessException
     */
    boolean preHandle(Mode mode, String name, @Nullable String module);

    enum Mode {
        ACCESS, SAVE, DELETE
    }
}
