package com.skypyb.poet.spring.boot.core.client;

import java.util.Optional;

public interface PoetAnnexRouteSupport {

    void setRouter(PoetAccessRouter router);

    Optional<PoetAccessRouter> getRouter();

}
