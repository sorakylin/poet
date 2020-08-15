package com.skypyb.poet.spring.boot.core.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对于附件操作的支持
 * 包括下载、预览(指图片)、媒体播放支持
 */
public interface PoetAnnexClientHttpSupport extends PoetAnnexRouteSupport {

    void view(String key, HttpServletResponse response);

    void viewMedia(String key, HttpServletResponse response);

    void viewMedia(String key, HttpServletRequest request, HttpServletResponse response);

    void down(String key, HttpServletResponse response);

    void down(String key, String realName, HttpServletResponse response);
}
