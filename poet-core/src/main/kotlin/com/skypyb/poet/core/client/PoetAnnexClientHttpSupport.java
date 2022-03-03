package com.skypyb.poet.core.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对于附件操作的支持
 * 包括下载、预览(指图片)、媒体播放支持
 */
public interface PoetAnnexClientHttpSupport {

    /**
     * 查看文件, 比如预览图片等
     */
    void view(String key, HttpServletResponse response);

    /**
     * 一股脑返回媒体信息
     */
    void viewMedia(String key, HttpServletResponse response);

    /**
     * 根据请求分段返回媒体信息, 可用于视频、音频播放等
     */
    void viewMedia(String key, HttpServletRequest request, HttpServletResponse response);

    void down(String key, HttpServletResponse response);

    void down(String key, String realName, HttpServletResponse response);
}
