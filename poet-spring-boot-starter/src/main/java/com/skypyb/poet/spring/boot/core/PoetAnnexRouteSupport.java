package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.route.PoetAccessRouter;

import java.util.Optional;

public interface PoetAnnexRouteSupport {

    void setRouter(PoetAccessRouter router);

    Optional<PoetAccessRouter> getRouter();

}
