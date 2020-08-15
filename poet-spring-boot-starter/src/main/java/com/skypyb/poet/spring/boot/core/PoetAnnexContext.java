package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.client.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Optional;

/**
 * 附件操作的上下文
 * 和 {@link PoetAnnexClient}{@link PoetAnnexClientHttpSupport} 的方法入参语义不相同
 * Client的语义为传入 key --> {@link PoetAnnex#getKey()}
 * Content的语义为: 传入附件的名字 name --> {@link PoetAnnex#getName()} ()} (名字应是全局唯一的)
 * 实际操作流程会根据 {@link com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository} 来获取附件具体的路径
 */
public interface PoetAnnexContext {


    PoetAnnex save(InputStream in, String name);


    PoetAnnex save(InputStream in, String name, String module);


    PoetAnnex save(byte[] data, String name);


    PoetAnnex save(byte[] data, String name, String module);


    void setRouter(PoetAccessRouter router);


    Optional<PoetAccessRouter> getRouter();


    boolean exist(String name);


    void delete(String name);


    byte[] getBytes(String name);


    void view(String name, HttpServletResponse response);


    void viewMedia(String name, HttpServletResponse response);


    void viewMedia(String name, HttpServletRequest request, HttpServletResponse response);


    void down(String name, HttpServletResponse response);


    void down(String name, String realName, HttpServletResponse response);

}
