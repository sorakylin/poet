package com.skypyb.poet.spring.boot.core.client;

import com.skypyb.poet.spring.boot.core.model.Navigation;

/**
 * 附件的访问路由器, 职能为根据附件的名字&模块 精准定位到具体的附件信息
 */
public interface PoetAccessRouter {

    void setStorageLocation(String storageLocation);

    void setDefaultModule(String module);

    void setSlicer(PoetAnnexSlicer slicer);

    //路径分割符
    void setDelimiter(String delimiter);

    String getDelimiter();

    Navigation routing(String module, String name);

    /**
     * 组合文件存储路径+文件路径
     * 一般来说只有本地文件系统才能用到
     *
     * @param key 文件的key , 抽象路径
     * @return 文件的完整路径
     */
    String formatKey(String key);
}
