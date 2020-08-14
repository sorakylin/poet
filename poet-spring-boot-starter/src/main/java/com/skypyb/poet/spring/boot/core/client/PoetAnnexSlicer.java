package com.skypyb.poet.spring.boot.core.client;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;

/**
 * 通过附件的元信息得到该附件的路径信息
 * 制定不同的策略来切成不同的路径，例:
 * model: (name='aaabbbxyz1.png', module='default/module1')
 * 1: ['default','module1','aaabbbxyz1.png']  -> /default/module1/aaabbbxyz1.png
 * 2: ['default','module1','aaa','bbb','aaabbbxyz1.png']  -> /default/module1/aaa/bbb/aaabbbxyz1.png
 * 3: ['default','module1','2020','0809','aaabbbxyz1.png']  -> /default/module1/2020/0809/aaabbbxyz1.png
 * ..... 等
 * <p>
 * 注: 最终名字的生成会依靠 {@link PoetAnnexNameGenerator}
 */
@FunctionalInterface
public interface PoetAnnexSlicer {

    String DELIMITER = "/";//default unix separator

    PoetAnnexSlicer DEFAULT_SLICER = (m, n) -> new StringBuilder(m)
            .append(DELIMITER)
            .append(n)
            .toString()
            .replaceAll(DELIMITER.concat(DELIMITER), DELIMITER)
            .split(DELIMITER);


    String[] slicePath(String module, String name);
}
