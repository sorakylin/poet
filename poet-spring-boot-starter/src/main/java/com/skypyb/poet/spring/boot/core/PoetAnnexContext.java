package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 附件操作的上下文
 * 实现 {@link PoetAnnexClient}和{@link PoetAnnexClientHttpSupport} 的方法除了保存以外, 入参实际语义和实际是不相同的
 * 接口的语义为传入 key --> {@link PoetAnnex#getKey()}
 * 操作附件上下文的语义为: 传入附件的名字 (名字应是全局唯一的)
 * 实际操作流程会根据 {@link com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository} 来获取附件具体的路径
 */
public interface PoetAnnexContext extends PoetAnnexClient, PoetAnnexClientHttpSupport {


    @Override
    boolean exist(String name);

    @Override
    void delete(String name);

    @Override
    byte[] getBytes(String name);

    @Override
    void view(String name, HttpServletResponse response);

    @Override
    void viewMedia(String name, HttpServletResponse response);

    @Override
    void viewMedia(String name, HttpServletRequest request, HttpServletResponse response);

    @Override
    void down(String name, HttpServletResponse response);

    @Override
    void down(String name, String realName, HttpServletResponse response);

}
