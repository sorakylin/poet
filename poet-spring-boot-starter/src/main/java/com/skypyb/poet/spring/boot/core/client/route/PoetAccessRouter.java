package com.skypyb.poet.spring.boot.core.client.route;

/**
 * 附件的访问路由器, 职能为根据附件的名字&模块 精准定位到具体的附件信息
 */
public interface PoetAccessRouter {

    Navigation routing(String name);

    Navigation routing(String name, String module);
}
