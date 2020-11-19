package com.skypyb.poet.spring.boot.core

import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository
import org.springframework.util.Assert
import java.util.*

class DefaultPoetAnnexContext : AbstractPoetAnnexContext() {

    private var repository: Optional<PoetAnnexRepository>? = null
    private var nameGenerator: PoetAnnexNameGenerator? = null

    public override fun getNameGenerator(): Optional<PoetAnnexNameGenerator> {
        return Optional.ofNullable(nameGenerator)
    }

    public override fun getRepository(): Optional<PoetAnnexRepository> {
        return repository!!
    }

    fun setRepository(repository: PoetAnnexRepository?) {
        this.repository = Optional.ofNullable(repository)
    }

    fun setNameGenerator(nameGenerator: PoetAnnexNameGenerator?) {
        Assert.notNull(nameGenerator, "nameGenerator not be null!")
        this.nameGenerator = nameGenerator
    }
}