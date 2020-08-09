package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.store.PoetAnnexNameGenerator;

import java.io.InputStream;

/**
 * 具体对附件进行操作的接口
 * 基本的保存、预览、下载功能
 * <p>
 * 所有的操作若不指定模块会路由到默认的文件储存目录下
 * 保存附件时完毕后，所有流都会关掉
 */
public interface PoetAnnexClient extends PoetAnnexRouteSupport {

    PoetAnnex save(InputStream in, String name);

    PoetAnnex save(InputStream in, String name, String module);

    PoetAnnex save(byte[] data, String name);

    PoetAnnex save(byte[] data, String name, String module);

    boolean exist(String name);

    boolean exist(String name, String module);

    void delete(String name);

    void delete(String name, String module);

    byte[] getBytes(String name);

    byte[] getBytes(String name, String module);
}
