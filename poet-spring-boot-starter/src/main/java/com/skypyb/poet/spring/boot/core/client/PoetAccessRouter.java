package com.skypyb.poet.spring.boot.core.client;

import com.skypyb.poet.spring.boot.core.model.Navigation;

/**
 * 附件的访问路由器, 职能为根据附件的名字&模块 精准定位到具体的附件信息
 */
public interface PoetAccessRouter {

    void setStorageLocation(String storageLocation);

    void setDefaultModule(String module);

    void setSlicer(PoetAnnexSlicer slicer);

    Navigation routing(String name);

    Navigation routing(String module, String name);
}
