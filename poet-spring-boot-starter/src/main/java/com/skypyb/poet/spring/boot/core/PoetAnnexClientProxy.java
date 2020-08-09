package com.skypyb.poet.spring.boot.core;


import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import org.springframework.context.ApplicationContext;

import java.util.Optional;


interface PoetAnnexClientProxy extends PoetAnnexClient, PoetAnnexClientHttpSupport {

    //名字生成器
    void setNameGenerator(PoetAnnexNameGenerator nameGenerator);

    Optional<PoetAnnexNameGenerator> getNameGenerator();


    //TODO 加密器

    //TODO 储存器


    //TODO ACL 访问控制器  [write,read,info,delete,admin
}
