package com.skypyb.poet.spring.boot

import com.skypyb.poet.spring.boot.store.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.store.PoetAnnexRepository

class DefaultPoetAnnexContext : AbstractPoetAnnexContext() {

    override var repository: PoetAnnexRepository? = null
    override var nameGenerator: PoetAnnexNameGenerator? = null
}