package com.skypyb.poet.cloudstore.tencent

import com.qcloud.cos.COSClient
import com.qcloud.cos.exception.CosClientException
import com.qcloud.cos.exception.CosServiceException
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.ObjectMetadata
import com.qcloud.cos.model.PutObjectRequest
import com.skypyb.poet.core.client.PoetAccessRouter
import com.skypyb.poet.core.client.PoetAnnexClient
import com.skypyb.poet.core.exception.AnnexOperationException
import com.skypyb.poet.core.model.DefaultPoetAnnex
import com.skypyb.poet.core.model.PoetAnnex
import org.springframework.beans.factory.DisposableBean
import java.io.InputStream


class TencentCosClient(
    private val router: PoetAccessRouter,
    private val cosClient: COSClient,
    private val defaultBucket: String,
) : PoetAnnexClient, DisposableBean {

    //捕获cosClient的几个运行时异常抛出
    private fun <R> handleWrap(handle: () -> R): R {
        try {
            return handle()
        } catch (e: CosServiceException) {
            throw AnnexOperationException(e.errorMessage);
        } catch (e: CosClientException) {
            throw AnnexOperationException(e.message);
        }
    }

    override fun save(inputStream: InputStream, name: String) = save(inputStream, name, null)

    override fun save(inputStream: InputStream, name: String, module: String?): PoetAnnex {
        val routing = router.routing(module, name)
        val key = routing.path

        val size = inputStream.use {
            val objectMetadata = ObjectMetadata()
            // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
            // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
//            objectMetadata.setContentLength(inputStreamLength);

            val req = PutObjectRequest(defaultBucket, key, inputStream, objectMetadata)
            val res = handleWrap { cosClient.putObject(req) }
            res.metadata.contentLength
        }

        val annex = DefaultPoetAnnex.of(routing)
        annex.length = size
        return annex
    }

    override fun save(data: ByteArray, name: String) = save(data, name, null)

    override fun save(data: ByteArray, name: String, module: String?): PoetAnnex {
        return save(data.inputStream(), name, module)
    }

    override fun exist(key: String): Boolean {
        return handleWrap { cosClient.doesObjectExist(defaultBucket, key) }
    }

    override fun delete(key: String) {
        cosClient.deleteObject(defaultBucket, key)
    }

    override fun getBytes(key: String): ByteArray {
        return handleWrap {
            val getObjectRequest = GetObjectRequest(defaultBucket, key)
            val cosObject = cosClient.getObject(getObjectRequest)

            val ins = cosObject.objectContent

            ins.use { ins.readBytes() }
        }
    }

    override fun destroy() {
        cosClient.shutdown()
    }
}