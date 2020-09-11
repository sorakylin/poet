package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository;

import java.util.Optional;


public class DefaultPoetAnnexContext extends AbstractPoetAnnexContext {

    private PoetAnnexRepository repository;

    private PoetAnnexNameGenerator nameGenerator;

    @Override
    Optional<PoetAnnexNameGenerator> getNameGenerator() {
        return Optional.ofNullable(nameGenerator);
    }

    @Override
    Optional<PoetAnnexRepository> getRepository() {
        return Optional.ofNullable(repository);
    }

    public void setRepository(PoetAnnexRepository repository) {
        this.repository = repository;
    }

    public void setNameGenerator(PoetAnnexNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }
}
