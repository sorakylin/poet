package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.model.PoetAnnex;

import java.io.InputStream;

/**
 * 具体对附件进行操作的接口
 * 基本的保存、预览、下载功能
 * <p>
 * 所有的操作若不指定模块会路由到默认的文件储存目录下
 * 保存附件时完毕后，所有流都会关掉
 */
public interface PoetAnnexClient extends PoetAnnexClientContext {

    PoetAnnex save(InputStream in);

    PoetAnnex save(InputStream in, String module);

    PoetAnnex save(byte[] data);

    PoetAnnex save(byte[] data, String suffix);

    PoetAnnex save(byte[] data, String suffix, String module);

    boolean exist(String name);

    boolean exist(String name, String module);

    void delete(String name);

    void delete(String name, String module);

    byte[] getBytes(String name);

    byte[] getBytes(String name, String module);
}
