package com.skypyb.poet.spring.boot.core.client

import com.skypyb.poet.spring.boot.core.exception.AnnexOperationException
import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex
import com.skypyb.poet.spring.boot.core.model.Navigation
import com.skypyb.poet.spring.boot.core.util.HttpResourceViewUtils
import com.skypyb.poet.spring.boot.core.util.StreamUtil
import org.springframework.util.StringUtils
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 以本地文件系统作为服务基础来进行附件操作的客户端
 */
class LocalFileServerClient(router: PoetAccessRouter) : PoetAnnexClient, PoetAnnexClientHttpSupport {

    private val router: PoetAccessRouter = router

    //region AnnexClient

    override fun save(inputStream: InputStream, name: String) = save(inputStream, name, null)

    override fun save(inputStream: InputStream, name: String, module: String?): DefaultPoetAnnex {

        val routing: Navigation = router.routing(module, name)
        val path: Path = Paths.get(routing.fullPath)
        try {
            if (!Files.exists(path.parent)) {
                Files.createDirectories(path.parent)
            }
            //创建、覆盖
            Files.copy(inputStream, path)
        } catch (e: IOException) {
            val ex = AnnexOperationException("Failed to save file!", e)
            ex.path = routing.path
            throw ex
        } finally {
            StreamUtil.close(inputStream)
        }

        val annex = DefaultPoetAnnex.of(routing)
        try {
            annex.length = Files.size(path)
        } catch (e: IOException) {
            val ex = AnnexOperationException("Failed to read file length!", e)
            ex.path = routing.path
            throw ex
        }
        return annex
    }

    override fun save(data: ByteArray, name: String) = save(data, name, null)

    override fun save(data: ByteArray, name: String, module: String?): DefaultPoetAnnex {
        val routing: Navigation = router.routing(module, name)
        val path: Path = Paths.get(routing.fullPath)

        try {
            if (!Files.exists(path.parent)) {
                Files.createDirectories(path.parent)
            }
            //创建、覆盖
            Files.write(path, data)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val annex = DefaultPoetAnnex.of(routing)

        try {
            annex.length = Files.size(path)
        } catch (e: IOException) {
            val ex = AnnexOperationException("Failed to save file!", e)
            ex.path = routing.path
            throw ex
        }
        return annex
    }


    override fun exist(key: String): Boolean = Files.exists(Paths.get(router.formatKey(key)))


    override fun delete(key: String) {
        try {
            Files.deleteIfExists(Paths.get(router.formatKey(key)))
        } catch (e: IOException) {
            val ex = AnnexOperationException("Failed to delete file!", e)
            ex.path = key
            throw ex
        }
    }

    override fun getBytes(key: String): ByteArray {
        val path = Paths.get(router.formatKey(key))
        //exist check ?

        return try {
            Files.readAllBytes(path)
        } catch (e: IOException) {
            val ex = AnnexOperationException("Failed to read file bytes!", e)
            ex.path = key
            throw ex
        }
    }

    //endregion


    //region Implements PoetAnnexClientHttpSupport methods

    override fun view(key: String, response: HttpServletResponse) {
        val path = Paths.get(router.formatKey(key))
        val name = key.split(router.delimiter).last()

        try {
            response.reset()
            if (!Files.exists(path)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND)
                return
            }
            val length = Files.size(path)
            val lastModified = Files.getLastModifiedTime(path).toMillis()
            val expires = System.currentTimeMillis() + 604800000L
            response.addHeader("Content-Type", HttpResourceViewUtils.getContentTypeForSuffix(name))
            response.addHeader("Cache-Control", "max-age=315360000")
            response.addHeader("Accept-Ranges", "bytes")
            response.addHeader("ETag", HttpResourceViewUtils.getETag(lastModified, length))
            response.addHeader("Last-Modified", Date(lastModified).toString())
            response.addHeader("Expires", Date(expires).toString())
            response.addHeader("Content-Length", length.toString())
            response.outputStream.use { Files.copy(path, it) }
        } catch (e: Exception) {
            val ex = AnnexOperationException("Failed to view file!", e)
            ex.path = key
            throw ex
        }
    }

    override fun viewMedia(key: String, response: HttpServletResponse) {
        val path = Paths.get(router.formatKey(key))
        val name = key.split(router.delimiter).last()

        try {
            response.reset()
            if (!Files.exists(path)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND)
                return
            }
            val length: Long = Files.size(path)
            val lastModified = Files.getLastModifiedTime(path).toMillis()
            val expires = System.currentTimeMillis() + 604800000L

            response.addHeader("Content-Type", HttpResourceViewUtils.getContentTypeForSuffix(name))
            response.addHeader("Connection", "keep-alive")
            response.addHeader("Cache-Control", "max-age=315360000")
            response.addHeader("Content-Disposition", "inline;filename=\"$name\"")
            response.addHeader("Accept-Ranges", "bytes")
            response.addHeader("ETag", HttpResourceViewUtils.getETag(lastModified, length))
            response.addHeader("Last-Modified", Date(lastModified).toString())
            response.addHeader("Expires", Date(expires).toString())
            response.addHeader("Content-Range", "bytes 0-${(length - 1L)}/$length")
            response.addHeader("Content-Length", length.toString())
            response.outputStream.use { Files.copy(path, it) }
        } catch (e: Exception) {
            val ex = AnnexOperationException("Failed to view media file!", e)
            ex.path = key
            throw ex
        }
    }

    override fun viewMedia(key: String, request: HttpServletRequest, response: HttpServletResponse) {
        val path = Paths.get(router.formatKey(key))
        val name = key.split(router.delimiter).last()

        try {
            response.reset()
            if (!Files.exists(path)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND)
                return
            }

            //多次断点请求的标识
            var range: String? = request.getHeader("Range")
            if (!StringUtils.hasText(range)) range = request.getHeader("range")

            val randomFile = RandomAccessFile(path.toFile(), "r") //只读模式
            val contentLength = randomFile.length()
            val lastModified = Files.getLastModifiedTime(path).toMillis()
            var start = 0
            var end = 0

            if (range != null && range.startsWith("bytes=")) {
                val values: List<String> = range.split("=")[1].split("-")
                start = values[0].toInt()
                if (values.size > 1) {
                    end = values[1].toInt()
                }
            }

            val requestSize = if (end != 0 && end > start) end - start + 1 else Int.MAX_VALUE

            response.contentType = HttpResourceViewUtils.getContentTypeForSuffix(name)
            response.setHeader("Accept-Ranges", "bytes")
            response.addHeader("ETag", HttpResourceViewUtils.getETag(lastModified, contentLength))
            response.addHeader("Last-Modified", Date(lastModified).toString())

            //第一次请求返回content length来让客户端请求多次实际数据
            if (range == null) {
                response.setHeader("Content-length", contentLength.toString())
            } else { //以后的多次以断点续传的方式来返回
                response.status = HttpServletResponse.SC_PARTIAL_CONTENT //206
                var requestStart: Long = 0
                var requestEnd: Long = 0
                val ranges = range.split("=")
                if (ranges.size > 1) {
                    val rangeDatas = ranges[1].split("-")
                    requestStart = rangeDatas[0].toInt().toLong()

                    if (rangeDatas.size > 1) requestEnd = rangeDatas[1].toInt().toLong()
                }

                var length: Long
                if (requestEnd > 0) {
                    length = requestEnd - requestStart + 1
                    response.setHeader("Content-length", length.toString())
                    response.setHeader("Content-Range", "bytes $requestStart-$requestEnd/$contentLength")
                } else {
                    length = contentLength - requestStart
                    response.setHeader("Content-length", length.toString())
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength)
                }
            }
            val out = response.outputStream
            val buffer = ByteArray(4096)
            var needSize = requestSize
            randomFile.seek(start.toLong())

            while (needSize > 0) {
                if (needSize < buffer.size) {
                    out.write(buffer, 0, needSize)
                } else {
                    val len = randomFile.read(buffer)

                    out.write(buffer, 0, len)
                    if (len < buffer.size) {
                        break
                    }
                }
                needSize -= buffer.size
            }
            StreamUtil.close(randomFile, out) //close...
        } catch (e: Exception) {
            val ex = AnnexOperationException("Failed to view media file!", e)
            ex.path = key
            throw ex
        }
    }

    override fun down(key: String, response: HttpServletResponse) = key.split(router.delimiter).last().let { down(key, it, response) }

    override fun down(key: String, realName: String, response: HttpServletResponse) {
        val path = Paths.get(router.formatKey(key))
        response.reset() // 清空输出流
        response.characterEncoding = "UTF-8"
        response.contentType = "application/octet-stream;charset=UTF-8"
        response.setHeader("Content-Disposition", "attachment;filename=$realName")
        response.addHeader("Pargam", "no-cache")
        response.addHeader("Cache-Control", "no-cache")
        try {
            response.outputStream.use { Files.copy(path, it) }
        } catch (e: IOException) {
            val ex = AnnexOperationException("File download failed!", e)
            ex.path = key
            throw ex
        }
    }


    //endregion
}