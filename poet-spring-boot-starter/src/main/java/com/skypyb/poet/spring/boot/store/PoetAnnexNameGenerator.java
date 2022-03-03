package com.skypyb.poet.spring.boot.store;

import java.util.UUID;

@FunctionalInterface
public interface PoetAnnexNameGenerator {

    PoetAnnexNameGenerator DEFAULT_NAME_GENERATOR = () -> UUID.randomUUID().toString().replaceAll("-", "");

    /**
     * 名字生成的时点为附件储存之间
     *
     * @return 一个保证全局不会重名的名字, 不用带上后缀
     */
    String generate();
}
