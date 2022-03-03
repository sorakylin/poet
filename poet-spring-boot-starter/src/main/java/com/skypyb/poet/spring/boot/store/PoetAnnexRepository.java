package com.skypyb.poet.spring.boot.store;

import com.skypyb.poet.core.model.PoetAnnex;

import javax.annotation.Nullable;
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

    List<PoetAnnex> findByNames(Collection<String> names);

    List<PoetAnnex> findByRoadSign(@Nullable String mainCategory, @Nullable Long instanceId, @Nullable String instanceModule);

    int updateInstanceId(Collection<String> names, Long instanceId);

    int neverExpire(Collection<String> names);

    List<String> findExpireAnnex();
}