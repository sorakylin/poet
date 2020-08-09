package com.skypyb.poet.spring.boot.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对于附件操作的支持
 * 包括下载、预览(指图片)、媒体播放支持
 */
public interface PoetAnnexClientHttpSupport extends PoetAnnexRouteSupport {

    void view(String name, HttpServletResponse response);

    void view(String name, String module, HttpServletResponse response);

    void viewMedia(String name, HttpServletResponse response);

    void viewMedia(String name, String module, HttpServletResponse response);

    void viewMedia(String name, HttpServletRequest request, HttpServletResponse response);

    void viewMedia(String name, String module, HttpServletRequest request, HttpServletResponse response);

    void down(String name, HttpServletResponse response);

    void down(String name, String module, HttpServletResponse response);
}
