package com.skypyb.poet.spring.boot.core.img;

import javax.validation.constraints.NotNull;

/**
 * 图片处理器统一接口
 */
@FunctionalInterface
public interface PoetImageHandler {

    /**
     * 涉及到图片处理,  原名已经无意义了。
     * 这个图片的原名在流程中将会被抹去,  自己保存时定义新的名字
     *
     * @param bytes 图片字节数组
     * @return 新图片的字节数组
     */
    byte[] handle(@NotNull byte[] bytes);
}
