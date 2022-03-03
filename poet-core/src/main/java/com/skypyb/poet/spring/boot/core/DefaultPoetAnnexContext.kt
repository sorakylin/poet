package com.skypyb.poet.spring.boot.core

import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository

class DefaultPoetAnnexContext : AbstractPoetAnnexContext() {

    override var repository: PoetAnnexRepository? = null
    override var nameGenerator: PoetAnnexNameGenerator? = null
}