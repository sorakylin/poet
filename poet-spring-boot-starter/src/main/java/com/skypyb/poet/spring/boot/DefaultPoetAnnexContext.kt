package com.skypyb.poet.spring.boot

import com.skypyb.poet.core.client.PoetAnnexNameGenerator
import com.skypyb.poet.spring.boot.store.PoetAnnexRepository

class DefaultPoetAnnexContext : AbstractPoetAnnexContext() {

    override var repository: PoetAnnexRepository? = null
    override var nameGenerator: PoetAnnexNameGenerator? = null
}