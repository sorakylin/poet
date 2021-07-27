package com.skypyb.poet.spring.boot.core.store;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;

import java.util.Collection;
import java.util.List;

/**
 * 附件持久化操作
 * SQL、NoSQL 等...
 */
public interface PoetAnnexRepository {

    void save(PoetAnnex annex);

    void save(PoetAnnex annex, StoreRoadSign roadSign);

    int deleteByName(String name);

    PoetAnnex findByName(String name);

    List<? extends PoetAnnex> findByNames(Collection<String> names);

    List<? extends PoetAnnex> findByRoadSign(String mainCategory, Long instanceId, String instanceModule);
}
