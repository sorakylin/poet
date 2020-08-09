package com.skypyb.poet.spring.boot.core.store;

import java.util.UUID;

//生成的是不带斜杠的UUID
public class UUIDNameGenerator implements PoetAnnexNameGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
