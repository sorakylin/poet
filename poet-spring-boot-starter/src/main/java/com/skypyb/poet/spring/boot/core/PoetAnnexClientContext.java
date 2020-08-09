package com.skypyb.poet.spring.boot.core;


import com.skypyb.poet.spring.boot.core.route.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;

import java.util.Optional;

public interface PoetAnnexClientContext {

    //名字生成器
    /*void setNameGenerator(PoetAnnexNameGenerator nameGenerator);

    Optional<PoetAnnexNameGenerator> getNameGenerator();*/

    //TODO 加密器

    //TODO ACL 访问控制器  [write,read,info,delete,admin

    //TODO 储存器


}
