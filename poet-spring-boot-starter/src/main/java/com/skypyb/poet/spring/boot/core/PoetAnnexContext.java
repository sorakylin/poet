package com.skypyb.poet.spring.boot.core;

import com.skypyb.poet.spring.boot.core.client.PoetAccessRouter;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClient;
import com.skypyb.poet.spring.boot.core.client.PoetAnnexClientHttpSupport;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import com.skypyb.poet.spring.boot.core.store.StoreRoadSign;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Optional;

/**
 * 附件操作的上下文
 * 和 {@link PoetAnnexClient}{@link PoetAnnexClientHttpSupport} 的方法入参语义不相同
 * Client的语义为传入 key --> {@link PoetAnnex#getKey()}
 * Context的语义为: 传入附件的名字 name --> {@link PoetAnnex#getName()} ()} (名字应是全局唯一的)
 * 实际操作流程会根据 {@link com.skypyb.poet.spring.boot.core.store.PoetAnnexRepository} 来获取附件具体的路径
 */
public interface PoetAnnexContext {


    default PoetAnnex save(InputStream in, String name) {
        return save(in, name, StoreRoadSign.Companion.empty());
    }

    default PoetAnnex save(InputStream in, String name, String module) {
        return save(in, name, module, StoreRoadSign.Companion.empty());
    }

    default PoetAnnex save(byte[] data, String name) {
        return save(data, name, StoreRoadSign.Companion.empty());
    }

    default PoetAnnex save(byte[] data, String name, String module) {
        return save(data, name, module, StoreRoadSign.Companion.empty());
    }

    PoetAnnex save(InputStream in, String name, StoreRoadSign roadSign);

    PoetAnnex save(InputStream in, String name, String module, StoreRoadSign roadSign);

    PoetAnnex save(byte[] data, String name, StoreRoadSign roadSign);

    PoetAnnex save(byte[] data, String name, String module, StoreRoadSign roadSign);

    boolean exist(String name);


    void delete(String name);


    byte[] getBytes(String name);


    void view(String name, HttpServletResponse response);


    void viewMedia(String name, HttpServletResponse response);


    void viewMedia(String name, HttpServletRequest request, HttpServletResponse response);


    void down(String name, HttpServletResponse response);


    void down(String name, String realName, HttpServletResponse response);

}
