package com.skypyb.poet.spring.boot.core.client;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;

import javax.annotation.Nullable;
import java.io.InputStream;

/**
 * 具体对附件进行操作的接口
 * 基本的保存、预览、下载功能
 * <p>
 * 所有的操作若不指定模块会路由到默认的文件储存目录下
 * 保存附件时完毕后，所有流都会关掉
 * <p>
 * 这里的 module 参数, 指的是指定的路径, 如 /temp/img。
 * 如果入参 module 为/temp/img, name 为123.jpg , 则默认会生成 /temp/img/123.jpg 的key
 * 具体生成策略 , 将配合 :{@link PoetAnnexSlicer}
 * 注: 最终名字的生成会依靠 {@link PoetAnnexNameGenerator}
 */
public interface PoetAnnexClient {

    PoetAnnex save(InputStream in, String name);

    PoetAnnex save(InputStream in, String name, @Nullable String module);

    PoetAnnex save(byte[] data, String name);

    PoetAnnex save(byte[] data, String name, @Nullable String module);

    boolean exist(String key);

    void delete(String key);

    byte[] getBytes(String key);
}
