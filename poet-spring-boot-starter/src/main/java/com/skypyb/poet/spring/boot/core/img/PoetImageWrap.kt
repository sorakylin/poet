package com.skypyb.poet.spring.boot.core.img

import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException
import com.skypyb.poet.spring.boot.core.model.PoetAnnex
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.resizers.configurations.ScalingMode
import java.io.ByteArrayOutputStream
import java.io.InputStream


/**
 * 图像处理的入口点
 * 可以在使用 [use] 时传入元信息 [K]
 * [K]将会传递到 [invoke] 方法被使用 , 可以借此实现生成自定义的名称等功能
 */
class PoetImageWrap<K> private constructor(private val bytes: ByteArray) {

    private val handlers: MutableMap<K, PoetImageHandler> = LinkedHashMap();

    companion object {

        @JvmStatic
        fun <K> warp(ins: InputStream): PoetImageWrap<K> = PoetImageWrap(ins.use { it.readBytes() })

        @JvmStatic
        fun <K> warp(bytes: ByteArray): PoetImageWrap<K> = PoetImageWrap(bytes)
    }


    /**
     * 传入一个 Key 和该 key 对应的 handler
     * 之后若执行 [invoke] 方法, 返回的 [Map.keys] 会和传入的一一对应
     */
    fun use(k: K, handler: PoetImageHandler): PoetImageWrap<K> {
        handlers.putIfAbsent(k, handler);
        return this
    }


    /**
     * 遍历执行所有的图片处理器
     * 将得到的所有已经过处理的图片结果应用到入参 [callback] 上
     * 一般 callback 为调用接口进行保存， 比如[com.skypyb.poet.spring.boot.core.PoetAnnexContext.save]
     *
     * @return String: use(k, ...)  value: 已经保存完毕的图片附件信息
     */
    fun invoke(callback: (K, ByteArray) -> PoetAnnex): Map<K, PoetAnnex> {
        return handlers.mapValues { e ->
            val newByte = e.value.handle(bytes)
            callback.invoke(e.key, newByte)
        }
    }

}


/**
 * 对图片无处理的处理器
 * 为调用者想要保留一份原图的需求使用
 */
class OriginalImageHandler() : PoetImageHandler {
    override fun handle(bytes: ByteArray) = bytes;
}


/**
 * 默认图片处理器, 使用的是 Thumbnails 库进行处理
 * 如果传入的图片是 Java 默认不支持的 (如 webp) 则会报错
 * 所以一定要万万注意
 */
class DefaultImageHandler(private val w: Int = Int.MAX_VALUE,
                          private val h: Int = Int.MAX_VALUE,
                          private val quality: Float = 1.0f,
                          private val format: String = "jpeg"
) : PoetImageHandler {

    private val suffix: String

    init {
        this.suffix = suffixMapping[format] ?: throw AnnexOperationException("Illegal format")
    }

    private companion object {
        //支持的图片类型:映射后缀   .xxx
        val suffixMapping = mapOf(
                "jpg" to "jpg",
                "jpeg" to "jpg",
                "png" to "png"
        )
    }

    override fun handle(bytes: ByteArray): ByteArray {

        val bao = ByteArrayOutputStream()

        return bytes.inputStream().use {
            Thumbnails.of(it)
                    .size(w, h)
                    .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                    .outputQuality(quality)
                    .outputFormat(format)
                    .toOutputStream(bao)
        }.let { bao.use { it.toByteArray() } }
    }

}

