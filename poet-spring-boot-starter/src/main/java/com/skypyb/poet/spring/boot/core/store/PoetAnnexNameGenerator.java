package com.skypyb.poet.spring.boot.core.store;

@FunctionalInterface
public interface PoetAnnexNameGenerator {

    /**
     * 名字生成的时点为附件储存之间
     *
     * @return 一个保证全局不会重名的名字
     */
    String generate();
}
