package com.skypyb.poet.spring.boot.core;


import com.skypyb.poet.spring.boot.core.route.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;

public interface PoetAnnexClientContext {

    void configure(PoetAccessRouter router);

    void configure(PoetAccessRouter router, PoetAnnexRepository repository);

    //路由器
    PoetAccessRouter getRouter();

    //储存器
    PoetAnnexRepository getRepository();

    //TODO 加密器

    //TODO ACL 访问控制器  write,read,info,admin

}
